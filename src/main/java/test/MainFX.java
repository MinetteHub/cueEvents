package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Configuration pour réduire les warnings
        System.setProperty("prism.verbose", "false");
        System.setProperty("javafx.preloader.verbose", "false");
        System.setProperty("enable.native.access", "javafx.graphics");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Forum Sportif");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Paramètres JVM pour JavaFX
        System.setProperty("javafx.preload", "true");
        launch(args);
    }
}