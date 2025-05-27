package controllers;

import entities.Comment;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import services.CommentService;
import services.NotificationService;
import services.PostService;
import entities.Notification;

public class CommentFormController {

    @FXML
    private TextArea commentTextArea;

    @FXML
    private Button btnSubmit;

    @FXML
    private Button btnCancel;

    @FXML
    private Label confirmationLabel;

    private int postId;
    private int userId;
    private Runnable onCommentAdded;

    private final CommentService commentService = new CommentService();
    private final PostService postService = new PostService();
    private final NotificationService notificationService = new NotificationService();

    public void setData(int postId, int userId, Runnable onCommentAdded) {
        this.postId = postId;
        this.userId = userId;
        this.onCommentAdded = onCommentAdded;
    }

    @FXML
    private void initialize() {
        if (btnSubmit != null) btnSubmit.setOnAction(e -> handleSubmit());
        if (btnCancel != null) btnCancel.setOnAction(e -> handleCancel());
    }

    @FXML
    private void handleSubmit() {
        String content = commentTextArea.getText().trim();
        if (content.isEmpty()) {
            showAlert("Erreur", "Le commentaire ne peut pas être vide.");
            return;
        }

        try {
            Comment comment = new Comment(postId, content, userId);
            commentService.ajouter(comment);

            // Envoyer une notification si l'auteur du post est différent du commentateur
            int postOwnerId = postService.getAuteurPostId(postId);
            if (postOwnerId != userId) {
                String msg = "Nouvel utilisateur a commenté votre post.";
                String lien = "/post?id=" + postId;
                notificationService.ajouter(new Notification(postOwnerId, msg, lien));
            }

            if (confirmationLabel != null) {
                confirmationLabel.setText("Commentaire ajouté !");
                confirmationLabel.setVisible(true);
            }

            if (onCommentAdded != null) onCommentAdded.run();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec de l'ajout du commentaire.");
        }
    }

    @FXML
    private void handleCancel() {
        if (onCommentAdded != null) onCommentAdded.run();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
