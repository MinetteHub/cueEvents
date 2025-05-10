package test;

import entities.Ticket;
import services.TicketService;
import services.TicketService;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        TicketService ps = new TicketService();
        try {
            System.out.println(ps.recuperer());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
