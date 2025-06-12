package pk.edu.uitu.bookflow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.*;
import java.time.LocalDate;

public class IssueReturnController {

    @FXML
    private TextField CopyIDField;

    @FXML
    private Button copyIdSearchBtn;

    @FXML
    private TextField isbnField;

    @FXML
    private Button isbnSearchBtn;

    @FXML
    private Button issueBtn;

    @FXML
    private Label issueLabel1;

    @FXML
    private Label issueLabel2;

    @FXML
    private Button returnBtn;

    @FXML
    private Label returnLabel1;

    @FXML
    private Label returnLabel2;

    @FXML
    private TextField userIDField;

    // SQL queries as constants
    private static final String SELECT_BOOK_BY_ISBN = "SELECT title, author, available, price FROM books WHERE isbn = ?";
    private static final String SELECT_USER_BY_ID = "SELECT name FROM users WHERE id = ?";
    private static final String INSERT_ISSUED_BOOK = "INSERT INTO loaned_books (user_id, isbn, issue_date, " +
            "return_date, " +
            "status) VALUES (?, ?, ?, ?, 'ISSUED')";
    private static final String UPDATE_BOOK_AVAILABILITY_DECREMENT = "UPDATE books SET available = available - 1 WHERE isbn = ?";
    private static final String SELECT_ISSUED_BY_ID = "SELECT issue_date, return_date, isbn, user_id, status FROM " +
            "loaned_books WHERE id = ?";
    private static final String UPDATE_ISSUED_STATUS_RETURNED = "UPDATE loaned_books SET status = 'RETURNED' WHERE id" +
            " = ?";
    private static final String UPDATE_BOOK_AVAILABILITY_INCREMENT = "UPDATE books SET available = available + 1 WHERE isbn = ?";

    @FXML
    public void initialize() {
        // Default states: disable issue/return buttons and related fields
        disableIssueControls();
        disableReturnControls();

        // Listeners to reset UI when fields change
        isbnField.textProperty().addListener((obs, oldText, newText) -> resetIssueUI());
        userIDField.textProperty().addListener((obs, oldText, newText) -> issueBtn.setDisable(newText.trim().isEmpty()));
        CopyIDField.textProperty().addListener((obs, oldText, newText) -> resetReturnUI());

        // Optionally, disable copyIdSearchBtn / isbnSearchBtn when their fields are empty
        isbnField.textProperty().addListener((obs, oldText, newText) -> isbnSearchBtn.setDisable(newText.trim().isEmpty()));
        CopyIDField.textProperty().addListener((obs, oldText, newText) -> copyIdSearchBtn.setDisable(newText.trim().isEmpty()));
    }

    @FXML
    void handleIsbnSearch() {
        String isbn = isbnField.getText().trim().toLowerCase();
        if (isbn.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter an ISBN to search.");
            return;
        }

        // Query book
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BOOK_BY_ISBN)) {

            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    showAlert(Alert.AlertType.ERROR, "No book found with the provided ISBN.");
                    return;
                }
                String title = rs.getString("title");
                String author = rs.getString("author");
                int availableCopies = rs.getInt("available");
                int price = rs.getInt("price");

                issueLabel1.setText(String.format(
                        "Title: %s%nAuthor: %s%nAvailable Copies: %d%nPrice: %d PKR",
                        title, author, availableCopies, price));

                if (availableCopies > 0) {
                    userIDField.setDisable(false);
                    // issueBtn remains disabled until userIDField non-empty (listener handles that)
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "No copies currently available to issue.");
                    disableIssueControls();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void handleIssueBtn() {
        String userId = userIDField.getText().trim();
        if (userId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter a User ID to issue the book.");
            return;
        }
        String isbn = isbnField.getText().trim().toLowerCase();
        if (isbn.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter an ISBN to issue the book.");
            return;
        }

        // Verify user exists
        String username = fetchUserName(userId);
        if (username == null) {
            showAlert(Alert.AlertType.ERROR, "No user found with the provided User ID.");
            return;
        }

        // Issue book: insert into loaned_books and decrement availability
        LocalDate today = LocalDate.now();
        LocalDate returnDate = today.plusDays(7);

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            // Insert loaned_books
            try (PreparedStatement insertStmt = conn.prepareStatement(INSERT_ISSUED_BOOK)) {
                insertStmt.setString(1, userId);
                insertStmt.setString(2, isbn);
                insertStmt.setDate(3, Date.valueOf(today));
                insertStmt.setDate(4, Date.valueOf(returnDate));
                int inserted = insertStmt.executeUpdate();
                if (inserted <= 0) {
                    conn.rollback();
                    showAlert(Alert.AlertType.ERROR, "Failed to issue the book. Please try again.");
                    return;
                }
            }

            // Update books.available
            try (PreparedStatement updateStmt = conn.prepareStatement(UPDATE_BOOK_AVAILABILITY_DECREMENT)) {
                updateStmt.setString(1, isbn);
                updateStmt.executeUpdate();
            }
            conn.commit();

            showAlert(Alert.AlertType.INFORMATION, "Book issued successfully!");
            issueLabel2.setText(String.format(
                    "Book issued to User ID: %s%nUser Name: %s%nReturn Date: %s",
                    userId, username, returnDate));
            disableIssueControls();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void handleCopyIdSearch(ActionEvent event) {
        String copyID = CopyIDField.getText().trim().toLowerCase();
        if (copyID.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter a Copy or Receipt ID to search.");
            return;
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ISSUED_BY_ID)) {

            stmt.setString(1, copyID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    showAlert(Alert.AlertType.ERROR, "No issued book found with this Copy ID.");
                    return;
                }
                LocalDate issueDate = rs.getDate("issue_date").toLocalDate();
                LocalDate returnDate = rs.getDate("return_date").toLocalDate();
                String isbn = rs.getString("isbn");
                String userId = rs.getString("user_id");
                String status = rs.getString("status");
                if (!"ISSUED".equalsIgnoreCase(status)) {
                    showAlert(Alert.AlertType.ERROR, "This copy has already been returned.");
                    return;
                }
                boolean isOverdue = LocalDate.now().isAfter(returnDate);
                String overdueNote = isOverdue ? "This book is OVERDUE!" : "";

                returnLabel1.setText(String.format(
                        "Copy ID: %s%nISBN: %s%nUser ID: %s%nIssue Date: %s%nReturn Date: %s%nStatus: %s%n%s",
                        copyID, isbn, userId, issueDate, returnDate, status, overdueNote));
                returnBtn.setDisable(false);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void handleReturnBtn() {
        String copyID = CopyIDField.getText().trim().toLowerCase();
        if (copyID.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter a Copy ID to return the book.");
            return;
        }

        // First update loaned_books.status
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            // Update status
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_ISSUED_STATUS_RETURNED)) {
                stmt.setString(1, copyID);
                int updated = stmt.executeUpdate();
                if (updated <= 0) {
                    conn.rollback();
                    showAlert(Alert.AlertType.ERROR, "Failed to return the book. Please try again.");
                    return;
                }
            }
            // Fetch ISBN for availability increment
            String isbn = fetchIssuedIsbn(conn, copyID);
            if (isbn == null) {
                conn.rollback();
                showAlert(Alert.AlertType.ERROR, "No issued book found with this Copy ID.");
                return;
            }
            // Update books.available
            try (PreparedStatement updBook = conn.prepareStatement(UPDATE_BOOK_AVAILABILITY_INCREMENT)) {
                updBook.setString(1, isbn);
                updBook.executeUpdate();
            }
            conn.commit();

            showAlert(Alert.AlertType.INFORMATION, "Book returned successfully!");
            returnLabel2.setText("Book with Copy ID: " + copyID + " has been returned successfully.");
            returnBtn.setDisable(true);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Helper: fetch user name or return null if not found
    private String fetchUserName(String userId) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_ID)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // Helper: fetch ISBN from loaned_books given copyID, using existing connection
    private String fetchIssuedIsbn(Connection conn, String copyID) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT isbn FROM loaned_books WHERE id = ?")) {
            stmt.setString(1, copyID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("isbn");
                }
            }
        }
        return null;
    }

    // UI helper methods
    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void disableIssueControls() {
        issueBtn.setDisable(true);
        userIDField.setDisable(true);
    }

    private void resetIssueUI() {
        disableIssueControls();
        issueLabel1.setText("");
        issueLabel2.setText("");
    }

    private void disableReturnControls() {
        returnBtn.setDisable(true);
    }

    private void resetReturnUI() {
        disableReturnControls();
        returnLabel1.setText("");
        returnLabel2.setText("");
    }
}
