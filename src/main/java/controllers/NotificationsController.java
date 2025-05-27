package controllers;

import entities.Notification;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import services.NotificationService;

import java.util.List;

public class NotificationsController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox notificationContainer;

    private final NotificationService notificationService = new NotificationService();

    private final int currentUserId = 1; // À remplacer par l’utilisateur connecté

    @FXML
    public void initialize() {
        try {
            List<Notification> notifications = notificationService.recupererParUtilisateur(currentUserId);
            for (Notification notification : notifications) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/notification_card.fxml"));
                Node card = loader.load();
                NotificationCardController controller = loader.getController();
                controller.setNotificationData(notification);
                notificationContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
