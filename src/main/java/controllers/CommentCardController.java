package controllers;

import entities.Comment;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import services.CommentService;
import services.UserService;

import java.io.InputStream;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class CommentCardController {

    @FXML private Label authorLabel;
    @FXML private Label dateLabel;
    @FXML private Label contentLabel;
    @FXML private ImageView imgUserAvatar;
    @FXML private Button btnLike;
    @FXML private Button btnEdit;
    @FXML private Button btnReply;
    @FXML private Button btnDelete;

    // Nouveau champ d’édition inline
    @FXML private VBox commentBox;
    private final TextArea editTextArea = new TextArea();
    private final HBox editButtonBox = new HBox();

    private final UserService userService = new UserService();
    private final CommentService commentService = new CommentService();

    private Comment comment;
    private Runnable refreshCallback;
    private ForumController parentController;
    private int likeCount;
    private int currentUserId;

    public void setCommentData(Comment comment, Runnable refreshCallback, int currentUserId) {
        this.comment = comment;
        this.refreshCallback = refreshCallback;
        this.currentUserId = currentUserId;

        try {
            User user = userService.recupererParId(comment.getUserId());
            authorLabel.setText(user != null ? user.getUsername() : "Utilisateur inconnu");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            dateLabel.setText(comment.getDatePublication().format(formatter));
            contentLabel.setText(comment.getContenu());

            // Avatar
            Image avatar = null;
            try {
                if (user != null && user.getAvatarUrl() != null) {
                    String url = user.getAvatarUrl();
                    if (url.startsWith("http")) {
                        avatar = new Image(url, true);
                    } else {
                        InputStream stream = getClass().getResourceAsStream(url);
                        if (stream == null) throw new Exception("Avatar local introuvable : " + url);
                        avatar = new Image(stream);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur avatar personnalisé : " + e.getMessage());
            }

            if (avatar == null) {
                InputStream fallback = getClass().getResourceAsStream("/images/avatar_url.jpg");
                if (fallback != null) avatar = new Image(fallback);
            }

            imgUserAvatar.setImage(avatar);

            // Likes
            likeCount = commentService.getLikeCount(comment.getId());
            updateLikeButton();

            boolean isAuthor = (user != null && currentUserId == user.getId());
            btnEdit.setVisible(isAuthor);
            btnDelete.setVisible(isAuthor);

        } catch (Exception e) {
            System.err.println("Erreur affichage commentaire: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateLikeButton() throws SQLException {
        boolean liked = commentService.hasUserLikedComment(comment.getId(), currentUserId);
        btnLike.setText((liked ? "Unlike" : "Like") + " (" + likeCount + ")");
    }

    public void setParentController(ForumController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleLike() {
        try {
            commentService.toggleLike(comment.getId(), currentUserId);
            likeCount = commentService.getLikeCount(comment.getId());
            updateLikeButton();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de liker/déliker le commentaire.");
        }
    }

    @FXML
    private void handleEdit() {
        // Masquer le contenu actuel
        contentLabel.setVisible(false);

        // Préparer le champ de texte
        editTextArea.setText(comment.getContenu());
        editTextArea.setWrapText(true);

        // Boutons de sauvegarde/annulation
        Button btnSave = new Button("Enregistrer");
        Button btnCancel = new Button("Annuler");

        btnSave.setOnAction(e -> {
            try {
                String nouveauContenu = editTextArea.getText().trim();
                if (!nouveauContenu.isEmpty()) {
                    comment.setContenu(nouveauContenu);
                    commentService.modifier(comment);
                    if (refreshCallback != null) refreshCallback.run();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Impossible de modifier le commentaire.");
            }
        });

        btnCancel.setOnAction(e -> {
            commentBox.getChildren().remove(editTextArea);
            commentBox.getChildren().remove(editButtonBox);
            contentLabel.setVisible(true);
        });

        editButtonBox.getChildren().setAll(btnSave, btnCancel);
        editButtonBox.setSpacing(10);

        commentBox.getChildren().add(editTextArea);
        commentBox.getChildren().add(editButtonBox);
    }

   @FXML
    private void handleReply() {
        showAlert("Fonction non disponible", "La réponse aux commentaires sera bientôt disponible.");
    }

    @FXML
    private void handleDelete() {
        try {
            commentService.supprimer(comment);
            if (refreshCallback != null) refreshCallback.run();
            if (parentController != null) parentController.updateStats();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de supprimer le commentaire.");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
