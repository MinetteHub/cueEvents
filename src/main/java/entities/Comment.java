package entities;

import java.time.LocalDateTime;

public class Comment {
    private int id;
    private int postId;
    private String contenu;
    private LocalDateTime datePublication;
    private int userId;

    // ✅ Constructeur vide requis par JavaFX / new Comment()
    public Comment() {
        this.datePublication = LocalDateTime.now();
    }

    // Constructeur pour création d'un commentaire
    public Comment(int postId, String contenu, int userId) {
        this.postId = postId;
        this.contenu = contenu;
        this.userId = userId;
        this.datePublication = LocalDateTime.now();
    }

    // Constructeur pour récupération depuis la base de données
    public Comment(int id, int postId, String contenu, LocalDateTime datePublication, int userId) {
        this.id = id;
        this.postId = postId;
        this.contenu = contenu;
        this.datePublication = datePublication;
        this.userId = userId;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDateTime datePublication) { this.datePublication = datePublication; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
