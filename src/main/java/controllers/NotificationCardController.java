package controllers;

import entities.Notification;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import java.awt.Desktop;
import java.net.URI;

public class NotificationCardController {

    @FXML
    private Label messageLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Hyperlink linkLabel;

    private Notification notification;

    public void setNotificationData(Notification notification) {
        this.notification = notification;
        messageLabel.setText(notification.getMessage());
        dateLabel.setText(notification.getDateCreation().toString());

        // Affichage du lien si présent
        if (notification.getLienVers() != null && !notification.getLienVers().isEmpty()) {
            linkLabel.setText("Ouvrir");
            linkLabel.setOnAction(e -> handleOpenLink());
        } else {
            linkLabel.setVisible(false);
        }
    }

    private void handleOpenLink() {
        if (notification.getLienVers() == null || notification.getLienVers().isEmpty()) return;
        try {
            Desktop.getDesktop().browse(new URI(notification.getLienVers()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
