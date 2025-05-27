package controllers;

import entities.Post;
import entities.User;
import entities.Comment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import services.CommentService;
import java.io.IOException;
import java.util.List;

public class PostDetailController {
    @FXML private VBox commentsContainer;

    private Post post;
    private User currentUser;
    private final CommentService commentService = new CommentService();

    public void setPostData(Post post, User user) {
        this.post = post;
        this.currentUser = user;
        loadComments();
    }

    private void loadComments() {
        commentsContainer.getChildren().clear();

        // Add loading indicator
        Label loadingLabel = new Label("Chargement des commentaires...");
        loadingLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        commentsContainer.getChildren().add(loadingLabel);

        try {
            List<Comment> comments = commentService.recupererParPost(post.getId());
            commentsContainer.getChildren().clear(); // Remove loading indicator

            if (comments.isEmpty()) {
                Label noCommentsLabel = new Label("Aucun commentaire pour le moment");
                noCommentsLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
                commentsContainer.getChildren().add(noCommentsLabel);
            } else {
                for (Comment comment : comments) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/commentCard.fxml"));
                        VBox commentCard = loader.load();
                        CommentCardController controller = loader.getController();
                        // Passer currentUserId comme troisième paramètre
                        controller.setCommentData(comment, this::loadComments, currentUser.getId());
                        commentsContainer.getChildren().add(commentCard);
                    } catch (IOException e) {
                        showAlert("Erreur", "Impossible de charger un commentaire");
                    }
                }
            }

            // Add comment form at the bottom
            setupCommentForm();

        } catch (Exception e) {
            commentsContainer.getChildren().clear();
            showAlert("Erreur", "Impossible de charger les commentaires");
        }
    }

    private void setupCommentForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commentForm.fxml"));
            VBox commentForm = loader.load();
            CommentFormController controller = loader.getController();
            controller.setData(post.getId(), currentUser.getId(), this::loadComments);
            VBox.setVgrow(commentForm, Priority.ALWAYS);
            commentsContainer.getChildren().add(commentForm);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger le formulaire de commentaire");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}