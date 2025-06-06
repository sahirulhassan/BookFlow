package pk.edu.uitu.bookflow;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.function.Predicate;

public class BookController {

    @FXML
    private ChoiceBox<String> searchChoiceBox;
    @FXML
    private TextField searchField;
    @FXML
    private Button refreshBtn;

    @FXML
    private TableView<Book> booksTable;
    @FXML
    private TableColumn<Book, String> isbnColumn; // ISBN is a string due to leading zeros
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> genreColumn;
    @FXML
    private TableColumn<Book, Number> availableColumn;
    @FXML
    private TableColumn<Book, Number> priceColumn;


    @FXML
    private TextField isbnField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private ComboBox<String> genreComboBox;
    @FXML
    private Spinner<Integer> availableSpinner;
    @FXML
    private Spinner<Double> priceSpinner;

    @FXML
    private Button addUpdateBtn;
    @FXML
    private Button deleteBtn;

    @FXML
    void handleRefresh() {
        ObservableList<Book> books = getBooks(); // All books
        FilteredList<Book> filteredBooks = getFilteredBooks(books); // Filtered books
        SortedList<Book> sortedBooks = new SortedList<>(filteredBooks); // Sorted books

        // Bind the comparator of SortedList to that of the TableView
        sortedBooks.comparatorProperty().bind(booksTable.comparatorProperty());

        // Set the sorted (and filtered) list to the table
        booksTable.setItems(sortedBooks);
    }

    @FXML
    void handleAddUpdate() {
        String isbn = isbnField.getText();
        String title = titleField.getText();
        String author = authorField.getText();
        String genre = genreComboBox.getValue();
        int available = availableSpinner.getValue();
        double price = priceSpinner.getValue();

        if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || genre.isEmpty()) {
            // Show an error message if any field is empty
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all fields.");
            alert.showAndWait();
            return;
        }
        if (bookExists(isbn)) {
            // If book exists, update it
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Book already exists. Do you want to update it?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    updateBook(isbn, title, author, genre, available, price);
                }
            });
        } else {
            // If book does not exist, add it
            addBook(isbn, title, author, genre, available, price);
        }
    }

    private void addBook(String isbn, String title, String author, String genre, int available, double price) {
        String query = "INSERT INTO books (ISBN, title, author, genre, available, price) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, isbn);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, author);
            preparedStatement.setString(4, genre);
            preparedStatement.setInt(5, available);
            preparedStatement.setDouble(6, price);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Book added successfully.");
                alert.showAndWait();
                handleRefresh(); // Refresh the table after adding
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add book.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBook(String isbn, String title, String author, String genre, int available, double price) {
        String query = "UPDATE books SET title = ?, author = ?, genre = ?, available = ?, price = ? WHERE ISBN = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, genre);
            preparedStatement.setInt(4, available);
            preparedStatement.setDouble(5, price);
            preparedStatement.setString(6, isbn);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Book updated successfully.");
                alert.showAndWait();
                handleRefresh(); // Refresh the table after update
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update book.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleDelete() {
        String isbn = isbnField.getText().trim();
        if (isbn.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a book to delete or enter a valid ISBN.");
            alert.showAndWait();
            return;
        }
        if (!bookExists(isbn)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Book with ISBN " + isbn + " does not exist.");
            alert.showAndWait();
            return;
        }

        String query = "DELETE FROM books WHERE ISBN = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, isbn);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Book deleted successfully.");
                alert.showAndWait();
                handleRefresh(); // Refresh the table after deletion
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete book.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean bookExists(String isbn) {
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM books WHERE ISBN = '" + isbn + "'")) {
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // If count is greater than 0, book exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    public void initialize() {
        searchChoiceBox.getItems().addAll("ISBN", "Title", "Author", "Genre");
        searchChoiceBox.setValue("ISBN");
        isbnColumn.setCellValueFactory(cellData -> cellData.getValue().ISBNProperty());
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
        availableColumn.setCellValueFactory(cellData -> cellData.getValue().availableProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        handleRefresh(); // Fetch all books, filter them, sort them, and set them to the table
        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                isbnField.setText(newSelection.getISBN());
                titleField.setText(newSelection.getTitle());
                authorField.setText(newSelection.getAuthor());
                genreComboBox.setValue(newSelection.getGenre());
                availableSpinner.getValueFactory().setValue(newSelection.getAvailable());
                priceSpinner.getValueFactory().setValue(newSelection.getPrice());
            }
        });
        genreComboBox.getItems().addAll(
                "Fantasy",
                "Science Fiction",
                "Mystery",
                "Thriller",
                "Romance",
                "Historical Fiction",
                "Horror",
                "Biography",
                "Self Help",
                "Young Adult",
                "Graphic Novel",
                "Classic",
                "Adventure",
                "Dystopian",
                "Literary Fiction"
        );
        genreComboBox.setValue("Fiction");
        availableSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0));
        priceSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10000.0, 0.0, 1.0));
    }

    private FilteredList<Book> getFilteredBooks(ObservableList<Book> books) {
        ObjectBinding<Predicate<Book>> binding = Bindings.createObjectBinding(
                this::createBookPredicate,
                searchField.textProperty(), // whenever this
                searchChoiceBox.valueProperty() // or this changes, the predicate method will be called
        );

        FilteredList<Book> filteredBooks = new FilteredList<>(books, p -> true); // initially no filter
        filteredBooks.predicateProperty().bind(binding); // bind the predicate to the filtered list, now whenever the
        // search field or choice box changes, the list will be filtered accordingly

        return filteredBooks;
    }

    private ObservableList<Book> getBooks() { // Fetch books from the database
        ObservableList<Book> books = FXCollections.observableArrayList();
        String query = "SELECT * FROM books";
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                books.add(new Book(
                        resultSet.getString("ISBN"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("genre"),
                        resultSet.getInt("available"),
                        resultSet.getInt("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    private Predicate<Book> createBookPredicate() {
        String searchText = searchField.getText();
        String choice = searchChoiceBox.getValue();

        if (searchText == null || searchText.isEmpty() || choice == null) {
            return (Book book) -> true;
        }

        return switch (choice) {
            case "ISBN" -> (Book book) -> String.valueOf(book.getISBN()).contains(searchText);
            case "Title" -> (Book book) -> book.getTitle().toLowerCase().contains(searchText.toLowerCase());
            case "Author" -> (Book book) -> book.getAuthor().toLowerCase().contains(searchText.toLowerCase());
            case "Genre" -> (Book book) -> book.getGenre().toLowerCase().contains(searchText.toLowerCase());
            default -> (Book book) -> true;
        };
    }
}