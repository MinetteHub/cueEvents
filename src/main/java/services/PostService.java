package services;

import entities.Post;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class PostService {
    private final Connection cnx;
    private final CommentService commentService = new CommentService();

    public PostService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    public void ajouter(Post post) throws SQLException {
        String sql = "INSERT INTO post (event_id, titre, contenu, user_id, categorie, date_publication) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, post.getEventId());
            ps.setString(2, post.getTitre());
            ps.setString(3, post.getContenu());
            ps.setInt(4, post.getUserId());

            String categorie = post.getCategorie();
            ps.setString(5, categorie != null ? categorie : "Non catégorisé");

            ps.setTimestamp(6, Timestamp.valueOf(post.getDatePublication()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    post.setId(rs.getInt(1));
                }
            }
        }
    }

    public void modifier(Post post) throws SQLException {
        String sql = "UPDATE post SET event_id = ?, titre = ?, contenu = ?, categorie = ? WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, post.getEventId());
            ps.setString(2, post.getTitre());
            ps.setString(3, post.getContenu());
            ps.setString(4, post.getCategorie());
            ps.setInt(5, post.getId());

            ps.executeUpdate();
        }
    }

    public boolean supprimer(Post post) throws SQLException {
        String sql = "DELETE FROM post WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, post.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Post> recupererTous() throws SQLException {
        String sql = "SELECT * FROM post ORDER BY date_publication DESC";
        return executeQueryAndGetPosts(sql);
    }

    public Post recupererParId(int id) throws SQLException {
        String sql = "SELECT * FROM post WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return createPostFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Post> recupererParUtilisateur(int userId) throws SQLException {
        String sql = "SELECT * FROM post WHERE user_id = ? ORDER BY date_publication DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return executeQueryAndGetPosts(ps);
        }
    }

    public List<Post> recupererParEvenement(int eventId) throws SQLException {
        String sql = "SELECT * FROM post WHERE event_id = ? ORDER BY date_publication DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            return executeQueryAndGetPosts(ps);
        }
    }

    public int compterTous() throws SQLException {
        String sql = "SELECT COUNT(*) FROM post";

        try (PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public CommentService getCommentService() {
        return commentService;
    }

    public boolean hasUserLikedPost(int postId, int userId) throws SQLException {
        String sql = "SELECT 1 FROM post_likes WHERE post_id = ? AND user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void toggleLike(int postId, int userId) throws SQLException {
        if (hasUserLikedPost(postId, userId)) {
            String sql = "DELETE FROM post_likes WHERE post_id = ? AND user_id = ?";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, postId);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO post_likes (post_id, user_id) VALUES (?, ?)";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, postId);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }
        }
    }

    public int getLikeCount(int postId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM post_likes WHERE post_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int getAuteurPostId(int postId) throws SQLException {
        String sql = "SELECT user_id FROM post WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("user_id");
        }
        throw new SQLException("Auteur du post introuvable.");
    }

    private List<Post> executeQueryAndGetPosts(String sql) throws SQLException {
        try (PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return getPostsFromResultSet(rs);
        }
    }

    private List<Post> executeQueryAndGetPosts(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            return getPostsFromResultSet(rs);
        }
    }

    private List<Post> getPostsFromResultSet(ResultSet rs) throws SQLException {
        List<Post> posts = new ArrayList<>();
        while (rs.next()) {
            posts.add(createPostFromResultSet(rs));
        }
        return posts;
    }

    private Post createPostFromResultSet(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setEventId(rs.getInt("event_id"));
        post.setTitre(rs.getString("titre"));
        post.setContenu(rs.getString("contenu"));
        post.setDatePublication(rs.getTimestamp("date_publication").toLocalDateTime());
        post.setUserId(rs.getInt("user_id"));

        String categorie = rs.getString("categorie");
        post.setCategorie(categorie != null ? categorie : "Non catégorisé");

        return post;
    }

    public List<Post> recupererParCategories(boolean includeEvent, boolean includeQuestion, boolean includeAnnonce) throws SQLException {
        List<String> conditions = new ArrayList<>();
        if (includeEvent) conditions.add("'Événements'");
        if (includeQuestion) conditions.add("'Questions'");
        if (includeAnnonce) conditions.add("'Annonces'");

        if (conditions.isEmpty()) return recupererTous();

        String sql = "SELECT * FROM post WHERE categorie IN (" + String.join(",", conditions) + ") ORDER BY date_publication DESC";
        return executeQueryAndGetPosts(sql);
    }
}
