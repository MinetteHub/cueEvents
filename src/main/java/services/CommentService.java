package services;

import entities.Comment;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class CommentService {
    private Connection cnx;

    public CommentService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    public void ajouter(Comment comment) throws SQLException {
        String sql = "INSERT INTO commentaire (post_id, contenu, user_id) VALUES (?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, comment.getPostId());
        ps.setString(2, comment.getContenu());
        ps.setInt(3, comment.getUserId());
        ps.executeUpdate();
    }

    public List<Comment> recupererParPost(int postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM commentaire WHERE post_id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, postId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            String contenu = rs.getString("contenu");
            LocalDateTime datePublication = rs.getTimestamp("date_publication").toLocalDateTime();
            int userId = rs.getInt("user_id");
            Comment comment = new Comment(id, postId, contenu, datePublication, userId);
            comments.add(comment);
        }

        return comments;
    }

    public void modifier(Comment comment) throws SQLException {
        String sql = "UPDATE commentaire SET contenu = ?, date_publication = CURRENT_TIMESTAMP WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, comment.getContenu());
        ps.setInt(2, comment.getId());
        ps.executeUpdate();
    }


    public void supprimer(Comment comment) throws SQLException {
        String sql = "DELETE FROM commentaire WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, comment.getId());
        ps.executeUpdate();
    }

    public int compterCommentaires() throws SQLException {
        String sql = "SELECT COUNT(*) FROM commentaire";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    public int getLikeCount(int commentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM comment_likes WHERE comment_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public boolean hasUserLikedComment(int commentId, int userId) throws SQLException {
        String sql = "SELECT 1 FROM comment_likes WHERE comment_id = ? AND user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void toggleLike(int commentId, int userId) throws SQLException {
        if (hasUserLikedComment(commentId, userId)) {
            String sql = "DELETE FROM comment_likes WHERE comment_id = ? AND user_id = ?";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, commentId);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO comment_likes (comment_id, user_id) VALUES (?, ?)";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, commentId);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }
        }
    }

    public void incrementerLike(int commentId, int userId) throws SQLException {
        toggleLike(commentId, userId);
    }
}
