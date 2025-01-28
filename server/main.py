import logging
import os
from datetime import datetime
from fastapi import FastAPI, Depends, HTTPException, UploadFile, File, Form
from pydantic import BaseModel, Field
from sqlalchemy import String, Integer, Float, DateTime, select, text


from sqlalchemy.ext.asyncio import AsyncSession, async_sessionmaker, create_async_engine
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column
from starlette.responses import FileResponse

# Настройка логгирования
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Конфигурация базы данных
DATABASE_URL = "sqlite+aiosqlite:///books.db"
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
engine = create_async_engine(DATABASE_URL, future=True, echo=True)

# Фабрика для создания асинхронных сессий
new_session = async_sessionmaker(engine, expire_on_commit=False)

# Инициализация FastAPI приложения
app = FastAPI()

# Генератор сессий для работы с базой данных
async def get_session() -> AsyncSession:
    async with new_session() as session:
        yield session

# Базовый класс для всех моделей базы данных
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
    image_id: Mapped[str] = mapped_column(String, nullable=True)

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
    phone_number: str = Field(..., description="Номер телефона в международном формате")
    username: str = Field(None, max_length=50, description="Имя пользователя")
    image_id: str = Field(None, max_length=255, description="URL изображения книги")

# Функция для преобразования текста с заглавной буквы
def capitalize_first_letter(text: str) -> str:
    if text:
        return ' '.join([word.capitalize() for word in text.split()])
    return text

# Функция для сокращения слова "город" до "г."
def shorten_city_name(city: str) -> str:
    return city.lower().replace("город ", "г. ", 1)

# Добавление книги в базу данных
@app.post("/books/")
async def create_book(book: BookRequest, session: AsyncSession = Depends(get_session)):
    title = capitalize_first_letter(book.title)
    author = capitalize_first_letter(book.author)
    username = capitalize_first_letter(book.username)
    city = capitalize_first_letter(shorten_city_name(book.city))
    district = capitalize_first_letter(book.district)

    new_book = BookModel(
        title=title,
        author=author,
        username=username,
        city=city,
        phone_number=book.phone_number,
        description=book.description,
        longitude=book.longitude,
        latitude=book.latitude,
        district=district,
    )

    session.add(new_book)
    await session.commit()
    logger.info(f"Добавлена книга: {new_book.title}")
    return {"status": "Book added successfully", "book_id": new_book.id}

# Получение списка всех книг
@app.get("/books/")
async def get_all_books(session: AsyncSession = Depends(get_session)):
    query = select(BookModel)
    result = await session.execute(query)
    books = result.scalars().all()

    if not books:
        raise HTTPException(status_code=404, detail="No books found.")

    return {"books": [book.__dict__ for book in books]}

@app.delete("/books/{book_id}")
async def delete_book(book_id: int, session: AsyncSession = Depends(get_session)):
    # Выполним запрос на получение книги по ID
    query = select(BookModel).filter(BookModel.id == book_id)
    result = await session.execute(query)
    book = result.scalar_one_or_none()

    if not book:
        raise HTTPException(status_code=404, detail="Book not found")

    # Пытаемся удалить связанный файл, если он есть
    if book.image_id:
        file_path = os.path.join(BASE_DIR, "imgs", str(book.image_id))
        file_path = file_path+".jpg"
        if os.path.exists(file_path):
            os.remove(file_path)


    # Удаляем книгу из базы данных
    await session.delete(book)
    await session.commit()

    return {"status": "Книга и изображение удалены успешно"}

# Загрузка изображений
@app.post("/upload")
async def upload_file(file: UploadFile = File(...), book_id: str = Form(...)):
    images_dir = os.path.join(BASE_DIR, "imgs")
    os.makedirs(images_dir, exist_ok=True)

    file_extension = os.path.splitext(file.filename)[1]
    file_path = os.path.join(images_dir, f"{book_id}{file_extension}")

    with open(file_path, "wb") as f:
        f.write(await file.read())

    return {"message": "Файл успешно загружен"}

# Получение изображения по ID
@app.get("/images/{image_id}")
async def get_image(image_id: str):
    images_dir = os.path.join(BASE_DIR, "imgs")
    file_path = os.path.join(images_dir, f"{image_id}.jpg")

    if not os.path.exists(file_path):
        raise HTTPException(status_code=404, detail="Image not found")

    return FileResponse(file_path)

# Создание таблиц базы данных при запуске приложения
@app.on_event("startup")
async def startup():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
