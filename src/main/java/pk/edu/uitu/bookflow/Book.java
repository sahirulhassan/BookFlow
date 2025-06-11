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

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getISBN() {
        return ISBN.get();
    }

    public void setISBN(String ISBN) {
        this.ISBN.set(ISBN);
    }

    public StringProperty ISBNProperty() {
        return ISBN;
    }

    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public StringProperty authorProperty() {
        return author;
    }

    public String getGenre() {
        return genre.get();
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public int getAvailable() {
        return available.get();
    }

    public void setAvailable(int available) {
        this.available.set(available);
    }

    public IntegerProperty availableProperty() {
        return available;
    }

    public Double getPrice() {
        return price.get();
    }

    public void setPrice(int price) {
        this.price.set(price);
    }

    public DoubleProperty priceProperty() {
        return price;
    }
}
