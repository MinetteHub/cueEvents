package services;

import entities.Forum;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForumService {

    private Connection cnx;

    public ForumService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    public void ajouter(Forum f) throws SQLException {
        String sql = "INSERT INTO forum(title, content) VALUES('" + f.getTitle() + "', '" + f.getContent() + "')";
        Statement st = cnx.createStatement();
        st.executeUpdate(sql);
    }

    public void modifier(Forum f) throws SQLException {
        String sql = "UPDATE forum SET title = ?, content = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, f.getTitle());
        ps.setString(2, f.getContent());
        ps.setInt(3, f.getId());
        ps.executeUpdate();
    }

    public void supprimer(Forum f) throws SQLException {
        String sql = "DELETE FROM forum WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, f.getId());
        ps.executeUpdate();
    }

    public List<Forum> recuperer() throws SQLException {
        List<Forum> forums = new ArrayList<>();
        String sql = "SELECT * FROM forum";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String content = rs.getString("content");
            Forum f = new Forum(id, title, content);
            forums.add(f);
        }

        return forums;
    }
}
