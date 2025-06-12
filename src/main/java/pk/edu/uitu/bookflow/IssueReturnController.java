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

    @FXML
    void handleIsbnSearch() {
        String isbn = isbnField.getText().trim().toLowerCase();
        if (isbn.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter an ISBN to search.");
            alert.showAndWait();
        }
        String query = "SELECT * FROM books WHERE isbn = ?";
        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int availableCopies = resultSet.getInt("available");
                int price = resultSet.getInt("price");
                String issuelabel1Text =
                        "Title: " + title + "\nAuthor: " + author + "\nAvailable Copies: " + availableCopies + "\nPrice: " + price + " PKR";
                issueLabel1.setText(issuelabel1Text);
                if (availableCopies > 0) {
                    userIDField.setDisable(false);
                    issueBtn.setDisable(false);
                    isbnField.textProperty().addListener((obs, oldText, newText) -> {
                        issueBtn.setDisable(true); // locks the button when user edits the field
                        userIDField.setDisable(true); // enables the userID field
                        issueLabel1.setText(""); // clears the issue label
                    });
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No book found with the provided ISBN.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void handleIssueBtn() {
        String userId = userIDField.getText().trim();
        if (userId.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a User ID to issue the book.");
            alert.showAndWait();
            return;
        }
        String query = "SELECT * FROM users WHERE id = ?";
        String username = "";
        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No user found with the provided User ID.");
                alert.showAndWait();
                return;
            }
            username = resultSet.getString("name");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String isbn = isbnField.getText().trim().toLowerCase();
        if (isbn.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter an ISBN to issue the book.");
            alert.showAndWait();
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        Date sqlDate = Date.valueOf(nextWeek); // return date

        query = "INSERT INTO issued_books (user_id, isbn, issue_date, return_date) VALUES (?, ?, ?, ?)";
        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, userId);
            statement.setString(2, isbn);
            statement.setDate(3, Date.valueOf(today));
            statement.setDate(4, sqlDate);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Book issued successfully!");
                alert.showAndWait();
                String issueLabel2Text =
                        "Book issued to User ID: " + userId + "\nUser Name: " + username + "\nReturn Date: " + nextWeek;
                issueLabel2.setText(issueLabel2Text);
                issueBtn.setDisable(true);
                userIDField.setDisable(true);
                query = "UPDATE books SET available = available - 1 WHERE isbn = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(query)) {
                    updateStatement.setString(1, isbn);
                    updateStatement.executeUpdate();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to issue the book. Please try again.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void handleCopyIdSearch(ActionEvent event) {
        String copyID = CopyIDField.getText().trim().toLowerCase();
        if (copyID.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter an Copy or Receipt ID to search.");
            alert.showAndWait();
            return;
        }
        String query = "SELECT * FROM issued_books WHERE id = ?";
        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, copyID);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No issued book found with this Copy ID.");
                alert.showAndWait();
                return;
            }
            LocalDate issue_date = resultSet.getDate("issue_date").toLocalDate();
            LocalDate returnDate = resultSet.getDate("return_date").toLocalDate();
            String isbn = resultSet.getString("isbn");
            String userId = resultSet.getString("user_id");
            String status = resultSet.getString("status").toLowerCase();
            if (!status.equals("issued")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "This book is not currently issued.");
                alert.showAndWait();
                return;
            }
            String returnLabel1Text =
                    "Copy ID: " + copyID + "\nISBN: " + isbn + "\nUser ID: " + userId + "\nIssue Date: " + issue_date + "\nReturn Date: " + returnDate + "\nStatus: " + status;
            returnLabel1.setText(returnLabel1Text);
            returnBtn.setDisable(false);
            CopyIDField.textProperty().addListener((obs, oldText, newText) -> {
                returnBtn.setDisable(true); // locks the button when user edits the field
                returnLabel1.setText(""); // clears the return label
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void handleReturnBtn() {
        String copyID = CopyIDField.getText().trim().toLowerCase();
        if (copyID.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a Copy ID to return the book.");
            alert.showAndWait();
            return;
        }
        String query = "UPDATE issued_books SET status = 'RETURNED' WHERE id = ?";
        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, copyID);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Book returned successfully!");
                alert.showAndWait();
                returnLabel2.setText("Book with Copy ID: " + copyID + " has been returned successfully.");
                returnBtn.setDisable(true);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to return the book. Please try again.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        query = "SELECT isbn FROM issued_books WHERE id = ?";
        String isbn = "";
        try (
            Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, copyID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                isbn = resultSet.getString("isbn");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No issued book found with this Copy ID.");
                alert.showAndWait();
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        query = "UPDATE books SET available = available + 1 WHERE isbn = ?";
        try (
            Connection connection = Database.getConnection();
            PreparedStatement updateStatement = connection.prepareStatement(query)
        ) {
            updateStatement.setString(1, isbn);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
