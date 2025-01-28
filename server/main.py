import logging
from datetime import datetime
from fastapi import FastAPI, Depends, HTTPException
from pydantic import BaseModel, Field
from sqlalchemy import String, Integer, Float, DateTime, select
from sqlalchemy.ext.asyncio import AsyncSession, async_sessionmaker, create_async_engine
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column

# Настройка логгирования
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Конфигурация базы данных
DATABASE_URL = "sqlite+aiosqlite:///books.db"
engine = create_async_engine(DATABASE_URL, future=True, echo=True)

# Фабрика для создания асинхронных сессий
new_session = async_sessionmaker(engine, expire_on_commit=False)

# Инициализация FastAPI приложения
app = FastAPI()


# Генератор сессий для работы с базой
async def get_session() -> AsyncSession:
    async with new_session() as session:
        yield session


# Базовый класс для всех моделей
class Base(DeclarativeBase):
    pass


# Модель таблицы "books"
class BookModel(Base):
    __tablename__ = "books"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    title: Mapped[str] = mapped_column(String, nullable=False)
    author: Mapped[str] = mapped_column(String, nullable=False)
    description: Mapped[str] = mapped_column(String, nullable=True)
    city: Mapped[str] = mapped_column(String, nullable=False)
    district: Mapped[str] = mapped_column(String, nullable=True)
    latitude: Mapped[float] = mapped_column(Float, nullable=False)
    longitude: Mapped[float] = mapped_column(Float, nullable=False)
    timestamp: Mapped[datetime] = mapped_column(DateTime, default=datetime.utcnow, nullable=False)
    phone_number: Mapped[str] = mapped_column(String(20), nullable=False)
    username: Mapped[str] = mapped_column(String, nullable=True)
    image_url: Mapped[str] = mapped_column(String, nullable=True)


# Инициализация базы данных
@app.on_event("startup")
async def setup_database():
    async with engine.begin() as conn:
        logger.info("Инициализация базы данных...")
        await conn.run_sync(Base.metadata.create_all)
        logger.info("База данных успешно инициализирована.")


# Pydantic модель для валидации запросов на добавление книги
class BookRequest(BaseModel):
    title: str = Field(..., max_length=100, description="Название книги")
    author: str = Field(..., max_length=100, description="Автор книги")
    description: str = Field(..., max_length=500, description="Описание книги")
    city: str = Field(..., max_length=100, description="Город, где находится книга")
    district: str = Field(None, max_length=100, description="Район города")
    latitude: float = Field(0.0, ge=-90.0, le=90.0, description="Широта местоположения книги")
    longitude: float = Field(0.0, ge=-180.0, le=180.0, description="Долгота местоположения книги")
    timestamp: datetime = Field(default_factory=datetime.utcnow, description="Дата и время добавления")
    phone_number: str = Field(...,  description="Номер телефона в международном формате")
    username: str = Field(None, max_length=50, description="Имя пользователя")
    image_url: str = Field(None, max_length=255, description="URL изображения книги")


# Маршрут для добавления книги
@app.post("/books/")
async def create_book(book: BookRequest, session: AsyncSession = Depends(get_session)):
    new_book = BookModel(
        title=book.title,
        author=book.author,
        username=book.username,
        city=book.city,
        phone_number=book.phone_number,
        description=book.description,
        longitude=book.longitude,
        latitude=book.latitude,
        district=book.district
    )
    session.add(new_book)
    await session.commit()
    logger.info(f"Добавлена книга: {new_book.title}")
    return {"status": "Book added successfully", "book_id": new_book.id}


# Маршрут для поиска книг
@app.get("/books/")
async def search_books(title: str, session: AsyncSession = Depends(get_session)):
    # Используем ORM-запрос вместо "raw SQL" для безопасности
    query = select(BookModel).where(BookModel.title.ilike(f"%{title}%"))
    result = await session.execute(query)
    books = result.scalars().all()

    if not books:
        raise HTTPException(status_code=404, detail="No books found matching the title.")

    return {"books": [book.__dict__ for book in books]}
