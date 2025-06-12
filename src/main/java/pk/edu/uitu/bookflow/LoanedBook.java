package pk.edu.uitu.bookflow;

import javafx.beans.property.*;

import java.time.LocalDate;

public class LoanedBook {
    private final IntegerProperty id;
    private final IntegerProperty userID;
    private final StringProperty isbn;
    private final StringProperty status;
    private final StringProperty issueDate;
    private final StringProperty returnDate;

    public LoanedBook(int id, int userID, String isbn, String status, String issueDate, String returnDate) {
        this.id = new SimpleIntegerProperty(id);
        this.userID = new SimpleIntegerProperty(userID);
        this.isbn = new SimpleStringProperty(isbn);
        this.status = new SimpleStringProperty(status);
        this.issueDate = new SimpleStringProperty(issueDate);
        this.returnDate = new SimpleStringProperty(returnDate);
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

    public String getIssueDate() {
        return issueDate.get();
    }

    public StringProperty issueDateProperty() {
        return issueDate;
    }

    public String getReturnDate() {
        return returnDate.get();
    }

    public StringProperty returnDateProperty() {
        return returnDate;
    }
}
