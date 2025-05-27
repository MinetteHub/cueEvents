package test;


import entities.Comment;
import services.CommentService;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        int postId = 5;  // L'ID du post que tu veux tester
        CommentService commentService = new CommentService();

        try {
            // Récupérer et afficher les commentaires pour ce post
            System.out.println("Commentaires pour le post ID " + postId + " :");
            for (Comment c : commentService.recupererParPost(postId)) {
                System.out.println(c.getContenu() + " (Posté par utilisateur ID " + c.getUserId() + ")");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commentaires : " + e.getMessage());
        }
    }
}

