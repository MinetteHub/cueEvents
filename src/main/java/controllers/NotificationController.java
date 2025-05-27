package controllers;

import entities.Notification;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import services.NotificationService;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class NotificationController implements Initializable {

    @FXML
    private VBox notificationContainer;

    private final NotificationService notificationService = new NotificationService();

    private final int currentUserId = 1; // à remplacer par session actuelle

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerNotifications();
    }

    private void chargerNotifications() {
        try {
            List<Notification> notifications = notificationService.recupererParUtilisateur(currentUserId);
            notificationContainer.getChildren().clear();

            if (notifications.isEmpty()) {
                Label empty = new Label("Aucune notification disponible.");
                empty.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
                notificationContainer.getChildren().add(empty);
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Notification notif : notifications) {
                Label label = new Label("[" + notif.getDateCreation().format(formatter) + "] " + notif.getMessage());
                label.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 10; -fx-background-radius: 5;");
                notificationContainer.getChildren().add(label);
            }

        } catch (SQLException e) {
            Label error = new Label("Erreur lors du chargement des notifications.");
            error.setStyle("-fx-text-fill: red;");
            notificationContainer.getChildren().add(error);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        notificationContainer.getScene().getWindow().hide();
    }
}
