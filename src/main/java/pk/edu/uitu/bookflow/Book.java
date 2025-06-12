package pk.edu.uitu.bookflow;

import javafx.beans.property.*;

public class Book {
    private final StringProperty ISBN;
    private final StringProperty title;
    private final StringProperty author;
    private final StringProperty genre;
    private final IntegerProperty available;
    private final DoubleProperty price;

    public Book(String ISBN, String title, String author, String genre, int available, double price) {
        this.ISBN = new SimpleStringProperty(ISBN);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.genre = new SimpleStringProperty(genre);
        this.available = new SimpleIntegerProperty(available);
        this.price = new SimpleDoubleProperty(price);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getISBN() {
        return ISBN.get();
    }

    public StringProperty ISBNProperty() {
        return ISBN;
    }

    public String getAuthor() {
        return author.get();
    }

    public StringProperty authorProperty() {
        return author;
    }

    public String getGenre() {
        return genre.get();
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public int getAvailable() {
        return available.get();
    }

    public IntegerProperty availableProperty() {
        return available;
    }

    public Double getPrice() {
        return price.get();
    }

    public DoubleProperty priceProperty() {
        return price;
    }
}
