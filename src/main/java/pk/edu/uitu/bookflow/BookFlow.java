package pk.edu.uitu.bookflow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BookFlow extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("pages/login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Login - BookFlow");
        stage.setScene(scene);
        stage.show();
    }
}