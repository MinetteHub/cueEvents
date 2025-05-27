package services;

import entities.Event;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class EventService {
    private Connection cnx;

    public EventService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    // Ajouter un événement
    public void ajouter(Event event) throws SQLException {
        String sql = "INSERT INTO event (nom, date, type, description, lien) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, event.getNom());
        ps.setTimestamp(2, Timestamp.valueOf(event.getDate()));
        ps.setString(3, event.getType());
        ps.setString(4, event.getDescription());
        ps.setString(5, event.getLien());
        ps.executeUpdate();
    }

    // Modifier un événement
    public void modifier(Event event) throws SQLException {
        String sql = "UPDATE event SET nom = ?, date = ?, type = ?, description = ?, lien = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, event.getNom());
        ps.setTimestamp(2, Timestamp.valueOf(event.getDate()));
        ps.setString(3, event.getType());
        ps.setString(4, event.getDescription());
        ps.setString(5, event.getLien());
        ps.setInt(6, event.getId());
        ps.executeUpdate();
    }

    // Supprimer un événement
    public void supprimer(Event event) throws SQLException {
        String sql = "DELETE FROM event WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, event.getId());
        ps.executeUpdate();
    }

    // Récupérer tous les événements
    public List<Event> recuperer() throws SQLException {
        List<Event> evenements = new ArrayList<>();
        String sql = "SELECT * FROM event";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int id = rs.getInt("id");
            String nom = rs.getString("nom");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            String type = rs.getString("type");
            String description = rs.getString("description");
            String lien = rs.getString("lien");
            Event event = new Event(id, nom, date, type, description, lien);
            evenements.add(event);
        }

        return evenements;
    }
}
