package entities;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int userId;
    private String message;
    private LocalDateTime dateCreation;
    private String lienVers;

    // Constructeur pour création d'une notification
    public Notification(int userId, String message, String lienVers) {
        this.userId = userId;
        this.message = message;
        this.lienVers = lienVers;
        this.dateCreation = LocalDateTime.now();
    }

    // Constructeur pour récupération depuis la base de données
    public Notification(int id, int userId, String message, LocalDateTime dateCreation, String lienVers) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.dateCreation = dateCreation;
        this.lienVers = lienVers;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getLienVers() { return lienVers; }
    public void setLienVers(String lienVers) { this.lienVers = lienVers; }
}
