package pk.edu.uitu.bookflow;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static javafx.util.Duration.seconds;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private Button loginBtn;

    @FXML
    private PasswordField pwField;

    @FXML
    private Label statusLabel;

    @FXML
    private Hyperlink registerLink;


    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = pwField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both fields.");
            return;
        }

        boolean success = authenticate(email, password);
        if (success) {
            statusLabel.setText("Login successful!");
            PauseTransition pause = new PauseTransition(seconds(1.5));
            pause.setOnFinished(event -> toHomePage());
            pause.play();
        } else {
            statusLabel.setText("Invalid email or password or not an admin.");
        }
    }

    private void toHomePage() {
        try {
            // Load the home page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pages/home.fxml"));
            Parent root = loader.load();

            // Get current stage and set new scene
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 720, 480));
            stage.setTitle("Home");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load home page.");
        }
    }

    public boolean authenticate(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ? AND role = 'ADMIN'";
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // if a match is found
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void handleRegistration() {
        try {
            // Load the registration page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pages/register.fxml"));
            Parent root = loader.load();

            // Get current stage and set new scene
            Stage stage = (Stage) registerLink.getScene().getWindow();
            stage.setScene(new Scene(root, 720, 480));
            stage.setTitle("Register");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load registration page.");
        }
    }

    public void prefillFields(String email, String password) {
        emailField.setText(email);
        pwField.setText(password);
    }
}
