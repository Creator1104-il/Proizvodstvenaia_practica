// Book.java - класс модели книги

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String author;
    private String genre;
    private LocalDate publicationDate;
    private String filePath;
    private int pages;
    private double rating;
    private String description;

    public Book(String title, String author, String genre, LocalDate publicationDate,
                String filePath, int pages, double rating, String description) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationDate = publicationDate;
        this.filePath = filePath;
        this.pages = pages;
        this.rating = rating;
        this.description = description;
    }
    // Геттеры и сеттеры
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFormattedDate() {
        return publicationDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Override
    public String toString() {
        return title + " - " + author;
    }
}
