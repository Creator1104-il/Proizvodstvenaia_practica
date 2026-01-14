// Library.java - класс управления библиотекой

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Library {
    private List<Book> books;
    private static final String DATA_FILE = "library_data.dat";

    public Library(){
        books = new ArrayList<>();
        loadFromFile();
    }

    //Добавление книги
    public void addBook(Book book) {
        books.add(book);
        saveToFile();
    }

    // Обновление книги
    public void updateBook(int index, Book book) {
        if (index >= 0 && index < books.size()) {
            books.set(index, book);
            saveToFile();
        }
    }

    // Удаление книги
    public boolean removeBook(Book book) {
        boolean removed = books.remove(book);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    // Удаление книги по индексу
    public boolean removeBook(int index) {
        if (index >= 0 && index < books.size()) {
            books.remove(index);
            saveToFile();
            return true;
        }
        return false;
    }

    // Поиск книг по названию
    public List<Book> searchByTitle(String title) {
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Поиск книг по автору
    public List<Book> searchByAuthor(String author) {
        return books.stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Поиск книг по жанру
    public List<Book> searchByGenre(String genre) {
        return books.stream()
                .filter(book -> book.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
    }

    // Получение всех книг
    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    // Получение книги по индексу
    public Book getBook(int index) {
        if (index >= 0 && index < books.size()) {
            return books.get(index);
        }
        return null;
    }

    // Сохранение в файл
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(books);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузка из файла
    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                books = (List<Book>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                books = new ArrayList<>();
            }
        }
    }

    // Получение всех жанров
    public List<String> getAllGenres() {
        List<String> genres = books.stream()
                .map(Book::getGenre)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Добавляем популярные жанры, если список пуст
        if (genres.isEmpty()) {
            genres.add("Художественная литература");
            genres.add("Фантастика");
            genres.add("Детектив");
            genres.add("Роман");
            genres.add("Научная литература");
            genres.add("Биография");
            genres.add("Поэзия");
            genres.add("Приключения");
        }

        return genres;
    }

    // Получение статистики
    public String getStatistics() {
        int totalBooks = books.size();
        if (totalBooks == 0) return "В библиотеке нет книг";

        int totalPages = books.stream().mapToInt(Book::getPages).sum();
        double avgRating = books.stream().mapToDouble(Book::getRating).average().orElse(0);
        long uniqueAuthors = books.stream().map(Book::getAuthor).distinct().count();

        return String.format(
                "<html><b>Статистика библиотеки:</b><br>" +
                        "Всего книг: %d<br>" +
                        "Количество авторов: %d<br>" +
                        "Общее количество страниц: %d<br>" +
                        "Средний рейтинг: %.1f/5.0</html>",
                totalBooks, uniqueAuthors, totalPages, avgRating
        );
    }
}
