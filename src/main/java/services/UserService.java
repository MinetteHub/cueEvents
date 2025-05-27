package services;

import entities.User;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final Connection cnx;

    public UserService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    // Ajouter un utilisateur
    public void ajouter(User user) throws SQLException {
        String sql = "INSERT INTO user (username, email, password, avatar_url, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getAvatarUrl());
            ps.setString(5, user.getRole());
            ps.executeUpdate();
        }
    }

    // Modifier un utilisateur
    public void modifier(User user) throws SQLException {
        String sql = "UPDATE user SET username = ?, email = ?, password = ?, avatar_url = ?, role = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getAvatarUrl());
            ps.setString(5, user.getRole());
            ps.setInt(6, user.getId());
            ps.executeUpdate();
        }
    }

    // Supprimer un utilisateur
    public void supprimer(User user) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            ps.executeUpdate();
        }
    }

    // Récupérer tous les utilisateurs
    public List<User> recuperer(int userId) throws SQLException {
        List<User> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                utilisateurs.add(mapResultSetToUser(rs));
            }
        }
        return utilisateurs;
    }

    // Récupérer un utilisateur par ID
    public User recupererParId(int id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    // Méthode privée pour mapper un ResultSet en objet User
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String avatarUrl = rs.getString("avatar_url");
        String role = rs.getString("role");
        return new User(id, username, email, password, avatarUrl, role);
    }
}
