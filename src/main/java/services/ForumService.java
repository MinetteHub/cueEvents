package services;

import entities.Post;
import entities.User;
import utils.MyDatabase;


import java.util.ArrayList;
import java.util.List;

public class ForumService {

    // Simule une base de données en mémoire
    private static final List<Post> posts = new ArrayList<>();
    private static int nextId = 1; // Pour générer les IDs automatiquement

    // 🔧 Récupère un post par son ID
    public static Post getPostById(int id) {
        for (Post post : posts) {
            if (post.getId() == id) {
                return post;
            }
        }
        return null;
    }

    // ✅ Ajout d’un nouveau post
    public static boolean addPost(String titre, String contenu, int userId, int eventId) {
        Post post = new Post(eventId, titre, contenu, userId);
        post.setId(nextId++); // Génère un ID unique
        posts.add(post);
        return true;
    }

    // ✅ Modification d’un post existant
    public static boolean updatePost(int id, String titre, String contenu) {
        Post post = getPostById(id);
        if (post != null) {
            post.setTitre(titre);     // ✅ Utilise bien setTitre
            post.setContenu(contenu); // ✅ Utilise bien setContenu
            return true;
        }
        return false;
    }

    // ✅ Suppression d’un post par ID
    public static boolean deletePost(int id) {
        return posts.removeIf(p -> p.getId() == id);
    }

    // ✅ Liste de tous les posts (à des fins de test ou affichage)
    public static List<Post> getAllPosts() {
        return posts;
    }
}
