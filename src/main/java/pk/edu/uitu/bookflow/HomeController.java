package pk.edu.uitu.bookflow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private Button IssueReturnBtn;

    @FXML
    private Button analyticsBtn;

    @FXML
    private Button booksBtn;

    @FXML
    private Button dashBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private StackPane mainContentPane;

    @FXML
    private Button membersBtn;

    @FXML
    private Button settingsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        switchViews("books.fxml");
    }

    @FXML
    private void handleDashboardTab() {
        switchViews("dashboard.fxml");
    }

    @FXML
    private void handleBooksTab() {
        switchViews("books.fxml");
    }

    @FXML
    private void handleMembersTab() {
        switchViews("members.fxml");
    }

    @FXML
    private void handleIssueReturnTab() {
        switchViews("issue_return.fxml");
    }

    @FXML
    private void handleAnalyticsTab() {
        switchViews("analytics.fxml");
    }

    @FXML
    private void handleSettingsTab() {
        switchViews("settings.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pages/login.fxml"));
            Parent root = loader.load();

            // Get current stage and set new scene
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 720, 480));
            stage.setTitle("Login - BookFlow");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method to switch views dynamically
    public void switchViews(String fxmlFile) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("pages/" + fxmlFile));
            mainContentPane.getChildren().setAll(view);
        } catch (IOException e) {
            System.out.println("Error loading view: " + fxmlFile);
        }
    }
}
