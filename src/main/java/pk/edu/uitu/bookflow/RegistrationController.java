package pk.edu.uitu.bookflow;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static javafx.util.Duration.seconds;

public class RegistrationController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField pwField;

    @FXML
    private Button regButton;

    @FXML
    private Label regStatusLabel;

    @FXML
    void handleReg() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim().toLowerCase();
        String password = pwField.getText().trim();

        if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            regStatusLabel.setText("Please fill all fields.");
        } else if (!email.contains("@")) {
            regStatusLabel.setText("Invalid email format.");
        } else if (password.length() < 6) {
            regStatusLabel.setText("Password must be at least 6 characters.");
        } else if (registerUser(name, email, password)) {
            regStatusLabel.setText("Registration successful!");
            // Delay before switching scenes
            PauseTransition pause = new PauseTransition(seconds(1.5));
            pause.setOnFinished(event -> backToLogin(email, password));
            pause.play();
        } else {
            regStatusLabel.setText("Registration failed. Please try again.");
        }
    }

    private boolean registerUser(String name, String email, String password) {
        String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            regStatusLabel.setText("Database error or user already exists: " + e.getMessage());
            return false;
        }
    }

    private void backToLogin(String email, String password) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("pages/login.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            regStatusLabel.setText("Failed to load login page.");
            return;
        }

        // Get the controller instance
        LoginController loginController = loader.getController();
        loginController.prefillFields(email, password);

        // Show the scene
        Stage stage = (Stage) regButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Login");
        stage.show();
    }
}
