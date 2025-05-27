package services;

import entities.Notification;
import utils.MyDatabase;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.sql.*;

public class NotificationService {
    private Connection cnx;

    public NotificationService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    // Ajouter une notification
    public void ajouter(Notification notification) throws SQLException {
        String sql = "INSERT INTO notification (user_id, message, lien_vers) VALUES (?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, notification.getUserId());
        ps.setString(2, notification.getMessage());
        ps.setString(3, notification.getLienVers());
        ps.executeUpdate();
    }

    // Récupérer toutes les notifications d'un utilisateur
    public List<Notification> recupererParUtilisateur(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE user_id = ? ORDER BY date_creation DESC";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            String message = rs.getString("message");
            LocalDateTime dateCreation = rs.getTimestamp("date_creation").toLocalDateTime();
            String lienVers = rs.getString("lien_vers");
            Notification notification = new Notification(id, userId, message, dateCreation, lienVers);
            notifications.add(notification);
        }

        return notifications;
    }
}
