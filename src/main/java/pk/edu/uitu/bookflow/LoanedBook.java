package pk.edu.uitu.bookflow;

import javafx.beans.property.*;

import java.time.LocalDate;

public class LoanedBook {
    private final IntegerProperty id;
    private final IntegerProperty userID;
    private final StringProperty isbn;
    private final StringProperty status;
    private final ObjectProperty<LocalDate> issueDate;
    private final ObjectProperty<LocalDate> returnDate;

    public LoanedBook(int id, int userID, String isbn, String status, LocalDate issueDate, LocalDate returnDate) {
        this.id = new SimpleIntegerProperty(id);
        this.userID = new SimpleIntegerProperty(userID);
        this.isbn = new SimpleStringProperty(isbn);
        this.status = new SimpleStringProperty(status);
        this.issueDate = new SimpleObjectProperty<>(issueDate);
        this.returnDate = new SimpleObjectProperty<>(returnDate);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public int getUserID() {
        return userID.get();
    }

    public IntegerProperty userIDProperty() {
        return userID;
    }

    public String getIsbn() {
        return isbn.get();
    }

    public StringProperty isbnProperty() {
        return isbn;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public LocalDate getIssueDate() {
        return issueDate.get();
    }

    public ObjectProperty<LocalDate> issueDateProperty() {
        return issueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate.get();
    }

    public ObjectProperty<LocalDate> returnDateProperty() {
        return returnDate;
    }
}
