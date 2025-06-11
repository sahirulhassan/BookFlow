package pk.edu.uitu.bookflow;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.function.Predicate;

import static javafx.collections.FXCollections.observableArrayList;

public class UserController {
    @FXML
    private ChoiceBox<String> searchChoiceBox;
    @FXML
    private TextField searchField;
    @FXML
    private Button refreshBtn;

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Number> idColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, LocalDate> joinedColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;
    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TextField emailField;
    @FXML
    private TextField idField;
    @FXML
    private DatePicker joinedDatePicker;
    @FXML
    private TextField nameField;
    @FXML
    private TextField pwField;
    @FXML
    private ToggleGroup roleToggleGroup;

    @FXML
    private Button addUpdateBtn;
    @FXML
    private Button deleteBtn;

    @FXML
    void handleAddUpdate() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim().toLowerCase();
        String password = pwField.getText().trim();
        LocalDate joined = joinedDatePicker.getValue();
        String role = ((RadioButton) roleToggleGroup.getSelectedToggle()).getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || joined == null) {
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill all fields.");
            alert.showAndWait();
            return;
        }
        int id = Integer.parseInt(idField.getText().trim());
        if (id > 0 && userExists(id)) {
            // If user exists, update it
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "User already exists. Do you want to update it?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    updateUser(id, name, email, password, role, joined);
                }
            });
        } else {
            // If user does not exist, add it
            addUser(id, name, email, password, role, joined);
        }
    }

    private void addUser(int id, String name, String email, String password, String role, LocalDate joined) {
        String query = "INSERT INTO users (ID, Name, Email, Password, Role, Created At) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, role);
            preparedStatement.setObject(6, joined);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "User added successfully.");
                alert.showAndWait();
                handleRefresh(); // Refresh the table after adding
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add User.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUser(int id, String name, String email, String password, String role, LocalDate joined) {
        String query = "UPDATE users SET Name = ?, Email = ?, Password = ?, Role = ?, Joined = ? WHERE ID = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, role);
            preparedStatement.setObject(5, joined);
            preparedStatement.setInt(6, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "User updated successfully.");
                alert.showAndWait();
                handleRefresh(); // Refresh the table after updating
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update User.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleDelete() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a user to delete or enter a valid ID.");
            alert.showAndWait();
            return;
        }

        int id = Integer.parseInt(idText);

        if (!userExists(id)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "User with ID " + id + " does not exist.");
            alert.showAndWait();
            return;
        }

        String query = "DELETE FROM users WHERE ID = ?";
        try (Connection connection = Database.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "User deleted successfully.");
                alert.showAndWait();
                handleRefresh(); // Refresh the table after deletion
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete user.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleRefresh() {
        ObservableList<User> users = getUsers(); // All users
        FilteredList<User> filteredUsers = getFilteredUsers(users); // Filtered users
        SortedList<User> sortedUsers = new SortedList<>(filteredUsers); // Sorted users

        // Bind the comparator of SortedList to that of the TableView
        sortedUsers.comparatorProperty().bind(userTable.comparatorProperty());

        // Set the sorted (and filtered) list to the table
        userTable.setItems(sortedUsers);
    }

    private FilteredList<User> getFilteredUsers(ObservableList<User> users) {
        ObjectBinding<Predicate<User>> binding = Bindings.createObjectBinding(this::createPredicate, searchField.textProperty(), // whenever this
                searchChoiceBox.valueProperty() // or this changes, the predicate method will be called
        );

        FilteredList<User> filteredUsers = new FilteredList<>(users, p -> true); // initially no filter
        filteredUsers.predicateProperty().bind(binding); // bind the predicate to the filtered list, now whenever the
        // search field or choice box changes, the list will be filtered accordingly

        return filteredUsers;
    }

    private ObservableList<User> getUsers() {
        ObservableList<User> users = observableArrayList();
        String query = "SELECT * FROM users";
        try (Connection connection = Database.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                users.add(new User(resultSet.getInt("ID"), resultSet.getString("Name"), resultSet.getString("Email"), resultSet.getString("Password"), resultSet.getString("Role"), resultSet.getDate("Joined").toLocalDate()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private boolean userExists(int id) {
        try (Connection connection = Database.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users WHERE ID = '" + id + "'")) {
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // If count is greater than 0, user exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    public void initialize() {
        searchChoiceBox.getItems().addAll("ID", "Name", "Email", "Role");
        searchChoiceBox.setValue("ID"); // Default selection
        // Initialize the user table and columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        joinedColumn.setCellValueFactory(cellData -> cellData.getValue().joinedProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        // Load initial data into the table
        handleRefresh();
        userTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                idField.setText(String.valueOf(newValue.getId()));
                emailField.setText(newValue.getEmail());
                nameField.setText(newValue.getName());
                pwField.setText(newValue.getPassword());
                joinedDatePicker.setValue(newValue.getJoined());
            }
        });

    }

    private Predicate<User> createPredicate() {
        String searchText = searchField.getText();
        String choice = searchChoiceBox.getValue();

        if (searchText == null || searchText.isEmpty() || choice == null) {
            return (User user) -> true;
        }

        return switch (choice) {
            case "ID" -> (User user) -> String.valueOf(user.getId()).contains(searchText);
            case "Name" -> (User user) -> user.getName().toLowerCase().contains(searchText.toLowerCase());
            case "Email" -> (User user) -> user.getEmail().toLowerCase().contains(searchText.toLowerCase());
            case "Role" -> (User user) -> user.getRole().toLowerCase().contains(searchText.toLowerCase());
            default -> (User user) -> true;
        };
    }
}
