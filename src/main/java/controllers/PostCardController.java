package controllers;

import entities.Comment;
import entities.Post;
import entities.User;
import entities.Notification;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import services.CommentService;
import services.PostService;
import services.UserService;
import services.NotificationService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PostCardController {

    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label contentLabel;
    @FXML private Label lblPostDate;
    @FXML private ImageView imgUserAvatar;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnLike;
    @FXML private VBox commentsContainer;

    private Post post;
    private ForumController parentController;
    private final PostService postService = new PostService();
    private final UserService userService = new UserService();
    private final CommentService commentService = new CommentService();
    private final NotificationService notificationService = new NotificationService();

    private int postLikeCount;
    private int currentUserId;

    public void setPostData(Post post) {
        this.post = post;
        this.currentUserId = getCurrentUser().getId();

        try {
            User user = userService.recupererParId(post.getUserId());
            String username = (user != null) ? user.getUsername() : "Utilisateur inconnu";
            authorLabel.setText("Par " + username);

            Image avatar = null;
            try {
                String url = (user != null) ? user.getAvatarUrl() : null;
                if (url != null && url.startsWith("http")) {
                    avatar = new Image(url, true);
                } else if (url != null) {
                    InputStream stream = getClass().getResourceAsStream(url);
                    if (stream == null) throw new IOException("Avatar introuvable: " + url);
                    avatar = new Image(stream);
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement avatar personnalisé: " + e.getMessage());
            }

            if (avatar == null) {
                InputStream fallback = getClass().getResourceAsStream("/images/avatar_url.jpg");
                if (fallback != null) avatar = new Image(fallback);
            }

            imgUserAvatar.setImage(avatar);

        } catch (SQLException e) {
            authorLabel.setText("Par utilisateur inconnu");
            e.printStackTrace();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        lblPostDate.setText(post.getDatePublication().format(formatter));
        titleLabel.setText(post.getTitre());
        contentLabel.setText(post.getContenu());

        try {
            postLikeCount = postService.getLikeCount(post.getId());
            updateLikeButton();
        } catch (SQLException e) {
            postLikeCount = 0;
            btnLike.setText("Like (?)");
        }

        loadComments();
    }

    private void updateLikeButton() throws SQLException {
        boolean liked = postService.hasUserLikedPost(post.getId(), currentUserId);
        btnLike.setText((liked ? "Unlike" : "Like") + " (" + postLikeCount + ")");
    }

    public void setParentController(ForumController controller) {
        this.parentController = controller;
        btnEdit.setVisible(isCurrentUserOwner());
        btnDelete.setVisible(isCurrentUserOwner());
    }

    @FXML
    private void handleEdit() {
        if (parentController != null) {
            parentController.getPostController().showPostForm(post, parentController::refreshPosts);
        }
    }

    @FXML
    private void handleDelete() {
        if (parentController != null) {
            try {
                postService.supprimer(post);
                parentController.refreshPosts();
            } catch (Exception e) {
                System.err.println("Erreur suppression post : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleLike() {
        try {
            postService.toggleLike(post.getId(), currentUserId);
            postLikeCount = postService.getLikeCount(post.getId());
            updateLikeButton();

            // Notifier le propriétaire du post s'il n'est pas l'auteur du like
            if (currentUserId != post.getUserId()) {
                Notification notif = new Notification(
                        post.getUserId(),
                        "Votre post a été liké.",
                        "/post/" + post.getId()
                );
                notificationService.ajouter(notif);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du like/unlike du post.");
        }
    }

    @FXML
    private void handleComment() {
        setupCommentForm();
    }

    @FXML
    private void loadComments() {
        try {
            commentsContainer.getChildren().clear();
            List<Comment> comments = commentService.recupererParPost(post.getId());
            for (Comment comment : comments) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/commentCard.fxml"));
                Node card = loader.load();
                CommentCardController controller = loader.getController();
                controller.setCommentData(comment, this::loadComments, currentUserId);
                controller.setParentController(parentController);
                commentsContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des commentaires : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupCommentForm() {
        try {
            commentsContainer.getChildren().removeIf(node -> "commentForm".equals(node.getId()));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commentForm.fxml"));
            VBox commentForm = loader.load();
            commentForm.setId("commentForm");

            CommentFormController controller = loader.getController();
            controller.setData(post.getId(), currentUserId, () -> {
                loadComments();
                commentsContainer.getChildren().remove(commentForm);
                if (parentController != null) {
                    parentController.updateStats();
                }

                // Notifier l’auteur du post s’il ne commente pas lui-même
                if (currentUserId != post.getUserId()) {
                    try {
                        Notification notif = new Notification(
                                post.getUserId(),
                                "Quelqu'un a commenté votre post.",
                                "/post/" + post.getId()
                        );
                        notificationService.ajouter(notif);
                    } catch (SQLException e) {
                        System.err.println("Erreur notification commentaire : " + e.getMessage());
                    }
                }
            });

            commentsContainer.getChildren().add(commentForm);

        } catch (IOException e) {
            System.err.println("Erreur ouverture formulaire commentaire : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isCurrentUserOwner() {
        return post.getUserId() == currentUserId;
    }

    private User getCurrentUser() {
        return new User(1, "currentUser", "user@example.com", "pass", "/images/avatar_url.jpg", "user");
    }
}
