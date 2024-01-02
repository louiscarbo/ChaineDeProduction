package fr.insa.developpement.model.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fr.insa.developpement.model.GestionBDD;

public class Client {
    private int id;
    private String nom;

    public static Client getClientFromId(int id) throws SQLException {
        Connection conn = GestionBDD.getConnection();
        try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM client WHERE id = ?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String nom = rs.getString("nom");
                int idClient = rs.getInt("id");
                Client client = new Client(idClient, nom);
                return client;
            }
        }
        return null;
    }
    
    public Client(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Client() {
        this(0, "");
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
}
