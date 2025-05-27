package controllers;

import entities.Event;
import entities.Post;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.EventService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class PostFormController {
    @FXML private TextField titleField;
    @FXML private TextArea contentField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<Event> eventComboBox;

    private Post post;
    private Stage stage;
    private Consumer<Post> onSubmit;
    private final EventService eventService = new EventService();

    @FXML
    public void initialize() {
        categoryComboBox.setItems(FXCollections.observableArrayList(
                "Événements", "Questions", "Annonces"
        ));
        loadEvents();
    }

    private void loadEvents() {
        try {
            List<Event> events = eventService.recuperer();
            eventComboBox.setItems(FXCollections.observableArrayList(events));

            // Pré-sélectionner l'événement "Sans événement" si présent
            Optional<Event> defaultEvent = events.stream()
                    .filter(e -> e.getNom().equalsIgnoreCase("Sans événement"))
                    .findFirst();
            defaultEvent.ifPresent(eventComboBox::setValue);

        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les événements");
            e.printStackTrace();
        }
    }

    public void setPostData(Post post) {
        this.post = post;
        if (post != null) {
            titleField.setText(post.getTitre());
            contentField.setText(post.getContenu());
            categoryComboBox.setValue(post.getCategorie());

            if (post.getEventId() > 0) {
                eventComboBox.getItems().stream()
                        .filter(e -> e.getId() == post.getEventId())
                        .findFirst()
                        .ifPresent(eventComboBox::setValue);
            }
        }
    }

    @FXML
    private void handleSubmit() {
        if (!validateFields()) return;

        Post formPost = createOrUpdatePost();

        if (onSubmit != null) {
            onSubmit.accept(formPost); // Ajout ou update géré par PostController
        }

        closeStage();
    }

    private boolean validateFields() {
        if (titleField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le titre ne peut pas être vide");
            return false;
        }

        if (contentField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Le contenu ne peut pas être vide");
            return false;
        }

        if (eventComboBox.getValue() == null) {
            showAlert("Erreur", "Aucun événement sélectionné, et aucun événement par défaut trouvé.");
            return false;
        }

        return true;
    }

    private Post createOrUpdatePost() {
        Post formPost = (post != null) ? post : new Post();

        formPost.setTitre(titleField.getText().trim());
        formPost.setContenu(contentField.getText().trim());

        String categorie = categoryComboBox.getValue();
        formPost.setCategorie(categorie != null ? categorie : "Non catégorisé");

        Event selectedEvent = eventComboBox.getValue();
        formPost.setEventId(selectedEvent.getId()); // jamais null, on le garantit via validateFields()

        if (post == null) {
            formPost.setUserId(1); // à remplacer avec utilisateur connecté
            formPost.setDatePublication(LocalDateTime.now());
        }

        return formPost;
    }

    @FXML
    private void handleCancel() {
        closeStage();
    }

    private void closeStage() {
        if (stage != null) {
            stage.close();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnSubmit(Consumer<Post> callback) {
        this.onSubmit = callback;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
