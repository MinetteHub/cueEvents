package services;

import entities.Ticket;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketService {

    private Connection cnx;

    public TicketService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    public void ajouter(Ticket t) throws SQLException {
        String sql = "INSERT INTO ticket(type, price, event_id) VALUES(?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, t.getType());
        ps.setDouble(2, t.getPrice());
        ps.setInt(3, t.getEventId());
        ps.executeUpdate();
    }

    public void modifier(Ticket t) throws SQLException {
        String sql = "UPDATE ticket SET type = ?, price = ?, event_id = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, t.getType());
        ps.setDouble(2, t.getPrice());
        ps.setInt(3, t.getEventId());
        ps.setInt(4, t.getId());
        ps.executeUpdate();
    }

    public void supprimer(Ticket t) throws SQLException {
        String sql = "DELETE FROM ticket WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, t.getId());
        ps.executeUpdate();
    }

    public List<Ticket> recuperer() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM ticket";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int id = rs.getInt("id");
            String type = rs.getString("type");
            double price = rs.getDouble("price");
            int eventId = rs.getInt("event_id");
            Ticket t = new Ticket(id, type, price, eventId);
            tickets.add(t);
        }

        return tickets;
    }
}
