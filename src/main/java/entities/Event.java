package entities;

import java.time.LocalDateTime;

public class Event {
    private int id;
    private String nom;
    private LocalDateTime date;
    private String type;
    private String description;
    private String lien;

    // Constructeur pour création d'un événement
    public Event(String nom, LocalDateTime date, String type, String description, String lien) {
        this.nom = nom;
        this.date = date;
        this.type = type;
        this.description = description;
        this.lien = lien;
    }

    // Constructeur pour récupération depuis la base de données
    public Event(int id, String nom, LocalDateTime date, String type, String description, String lien) {
        this.id = id;
        this.nom = nom;
        this.date = date;
        this.type = type;
        this.description = description;
        this.lien = lien;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLien() { return lien; }
    public void setLien(String lien) { this.lien = lien; }



    @Override
    public String toString() {
        return nom; // ✅ Ceci s'affichera dans le ComboBox
    }
}



