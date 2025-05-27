package controllers;

import entities.Post;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.PostService;

public class PostController {

    private final PostService postService = new PostService();

    public void showPostForm(Post post, Runnable refreshCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/postForm.fxml"));
            Parent root = loader.load();

            PostFormController controller = loader.getController();
            if (post != null) controller.setPostData(post);

            controller.setOnSubmit(formPost -> {
                try {
                    if (post == null) {
                        postService.ajouter(formPost);
                    } else {
                        formPost.setId(post.getId());
                        postService.modifier(formPost);
                    }
                    if (refreshCallback != null) refreshCallback.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setTitle(post == null ? "Créer un Post" : "Modifier le Post");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
