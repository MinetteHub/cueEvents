package controllers;

import entities.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import services.PostService;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ForumController {

    @FXML private VBox postContainer;
    @FXML private Label lblPostCount;
    @FXML private Label lblCommentCount;
    @FXML private TextField searchField;
    @FXML private ImageView imgAvatar;
    @FXML private CheckBox eventCheckBox;
    @FXML private CheckBox questionCheckBox;
    @FXML private CheckBox announcementCheckBox;

    private final PostService postService = new PostService();
    private final PostController postController = new PostController();

    @FXML
    public void initialize() {
        refreshPosts();

        Circle clip = new Circle(imgAvatar.getFitWidth() / 2, imgAvatar.getFitHeight() / 2, imgAvatar.getFitWidth() / 2);
        imgAvatar.setClip(clip);
        imgAvatar.setImage(new Image("/images/avatar_url.jpg"));

        eventCheckBox.setOnAction(e -> refreshPosts());
        questionCheckBox.setOnAction(e -> refreshPosts());
        announcementCheckBox.setOnAction(e -> refreshPosts());

        searchField.setOnAction(e -> handleSearch());
    }

    @FXML
    public void handleNewPost() {
        postController.showPostForm(null, this::refreshPosts);
    }

    @FXML
    public void refreshPosts() {
        postContainer.getChildren().clear();

        try {
            List<Post> posts;
            boolean eventSelected = eventCheckBox.isSelected();
            boolean questionSelected = questionCheckBox.isSelected();
            boolean announcementSelected = announcementCheckBox.isSelected();

            if (!eventSelected && !questionSelected && !announcementSelected) {
                posts = postService.recupererTous();
            } else {
                posts = postService.recupererParCategories(eventSelected, questionSelected, announcementSelected);
            }

            if (posts.isEmpty()) {
                Label emptyLabel = new Label("Aucun post disponible.");
                emptyLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic; -fx-padding: 10;");
                postContainer.getChildren().add(emptyLabel);
                lblPostCount.setText("0");
                lblCommentCount.setText("0");
                return;
            }

            String keyword = searchField.getText().trim().toLowerCase();
            if (!keyword.isEmpty()) {
                posts.removeIf(post -> !post.getTitre().toLowerCase().contains(keyword)
                        && !post.getContenu().toLowerCase().contains(keyword));
            }

            for (Post post : posts) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/postCard.fxml"));
                VBox postCard = loader.load();
                PostCardController controller = loader.getController();
                controller.setPostData(post);
                controller.setParentController(this);
                postContainer.getChildren().add(postCard);
            }

            lblPostCount.setText(String.valueOf(posts.size()));
            lblCommentCount.setText(String.valueOf(postService.getCommentService().compterCommentaires()));

        } catch (Exception e) {
            System.err.println("Erreur chargement posts : " + e.getMessage());
            showAlert("Erreur", "Impossible de charger les posts.");
        }
    }

    public PostController getPostController() {
        return postController;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void updateStats() {
        try {
            int totalComments = postService.getCommentService().compterCommentaires();
            lblCommentCount.setText(String.valueOf(totalComments));
        } catch (SQLException e) {
            lblCommentCount.setText("?");
            System.err.println("Erreur mise à jour des stats : " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        refreshPosts();
    }

    @FXML
    public void handleOpenNotifications() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notifications.fxml"));
            BorderPane notificationsView = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Mes Notifications");
            stage.setScene(new Scene(notificationsView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la vue des notifications.");
        }
    }
}
