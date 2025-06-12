package pk.edu.uitu.bookflow;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.function.Predicate;

import static javafx.collections.FXCollections.observableArrayList;

public class LoanedBooksController {

    @FXML private TableView<LoanedBook> loanedBooksTable;

    @FXML private TableColumn<LoanedBook, Number> idColumn;
    @FXML private TableColumn<LoanedBook, Number> userIDColumn;
    @FXML private TableColumn<LoanedBook, String> isbnColumn;
    @FXML private TableColumn<LoanedBook, String> statusColumn;
    @FXML private TableColumn<LoanedBook, String> issueDateColumn;
    @FXML private TableColumn<LoanedBook, String> returnDateColumn;

    @FXML private ChoiceBox<String> searchChoiceBox;
    @FXML private TextField searchField;

    private static final String[] SEARCH_OPTIONS = {
        "ID", "User ID", "ISBN", "Issue Date", "Return Date", "Status"
    };

    @FXML
    public void initialize() {
        setupSearchOptions();
        setupTableColumns();

        ObservableList<LoanedBook> loanedBooks = loadLoanedBooks();
        FilteredList<LoanedBook> filtered = applyFiltering(loanedBooks);
        SortedList<LoanedBook> sorted = new SortedList<>(filtered);

        sorted.comparatorProperty().bind(loanedBooksTable.comparatorProperty());
        loanedBooksTable.setItems(sorted);
    }

    private void setupSearchOptions() {
        searchChoiceBox.getItems().addAll(SEARCH_OPTIONS);
        searchChoiceBox.setValue("ID");
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cell -> cell.getValue().idProperty());
        userIDColumn.setCellValueFactory(cell -> cell.getValue().userIDProperty());
        isbnColumn.setCellValueFactory(cell -> cell.getValue().isbnProperty());
        issueDateColumn.setCellValueFactory(cell -> cell.getValue().issueDateProperty());
        returnDateColumn.setCellValueFactory(cell -> cell.getValue().returnDateProperty());
        statusColumn.setCellValueFactory(cell -> cell.getValue().statusProperty());
    }

    private ObservableList<LoanedBook> loadLoanedBooks() {
        ObservableList<LoanedBook> books = observableArrayList();
        String query = "SELECT * FROM loaned_Books";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                books.add(new LoanedBook(
                        rs.getInt("id"),
                        rs.getInt("user_ID"),
                        rs.getString("isbn"),
                        rs.getString("status"),
                        rs.getString("issue_Date"),
                        rs.getString("return_Date")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    private FilteredList<LoanedBook> applyFiltering(ObservableList<LoanedBook> books) {
        ObjectBinding<Predicate<LoanedBook>> predicateBinding = Bindings.createObjectBinding(
            this::createPredicate,
            searchField.textProperty(),
            searchChoiceBox.valueProperty()
        );

        FilteredList<LoanedBook> filteredList = new FilteredList<>(books, b -> true);
        filteredList.predicateProperty().bind(predicateBinding);
        return filteredList;
    }

    private Predicate<LoanedBook> createPredicate() {
        String text = searchField.getText();
        String choice = searchChoiceBox.getValue();

        if (text == null || text.isBlank() || choice == null) {
            return book -> true;
        }

        String lowerText = text.toLowerCase();

        return switch (choice) {
            case "ID" -> book -> String.valueOf(book.getId()).contains(lowerText);
            case "User ID" -> book -> String.valueOf(book.getUserID()).contains(lowerText);
            case "ISBN" -> book -> book.getIsbn().toLowerCase().contains(lowerText);
            case "Issue Date" -> book -> book.getIssueDate().contains(lowerText);
            case "Return Date" -> book -> book.getReturnDate().contains(lowerText);
            case "Status" -> book -> book.getStatus().toLowerCase().contains(lowerText);
            default -> book -> true;
        };
    }
}
