package entities;

import java.time.LocalDateTime;

public class Post {
    private int id;
    private int eventId;
    private String titre;
    private String contenu;
    private String categorie;
    private LocalDateTime datePublication;
    private int userId;

    // ✅ Constructeur vide requis pour JavaFX et la sérialisation
    public Post() {
        this.datePublication = LocalDateTime.now();
        this.categorie = "Non catégorisé"; // Valeur par défaut
    }

    // ✅ Constructeur pour création rapide (sans événement)
    public Post(String titre, String contenu, int userId) {
        this();
        this.titre = titre;
        this.contenu = contenu;
        this.userId = userId;
    }

    // ✅ Constructeur avec eventId
    public Post(int eventId, String titre, String contenu, int userId) {
        this();
        this.eventId = eventId;
        this.titre = titre;
        this.contenu = contenu;
        this.userId = userId;
    }

    // ✅ Constructeur complet
    public Post(int id, int eventId, String titre, String contenu,
                LocalDateTime datePublication, int userId, String categorie) {
        this.id = id;
        this.eventId = eventId;
        this.titre = titre;
        this.contenu = contenu;
        this.datePublication = datePublication;
        this.userId = userId;
        this.categorie = categorie;
    }

    // ======= Getters & Setters =======
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public LocalDateTime getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDateTime datePublication) { this.datePublication = datePublication; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    // ✅ Affichage utile dans une ComboBox
    @Override
    public String toString() {
        return titre + " - " + (datePublication != null ? datePublication.toLocalDate() : "non daté");
    }
}
