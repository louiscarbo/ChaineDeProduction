package fr.insa.developpement.model.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Client)) {
            return false;
        }

        Client otherClient = (Client) obj;
        return id == otherClient.id;
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

    public static List<Client> getClients() throws SQLException {
        Connection conn = GestionBDD.getConnection();
        List<Client> clients = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM client")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Client client = getClientFromId(id);
                if (client != null) {
                    clients.add(client);
                }
            }
        }
        return clients;
    }

    public void changeNom(String nom) {
        setNom(nom);
        Connection connection = GestionBDD.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE client SET nom = ? WHERE id = ?"
            );
            preparedStatement.setString(1, nom);
            preparedStatement.setInt(2, this.id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            Notification error = Notification.show("Une erreur est survenue lors du changement du nom du client : " + e.getLocalizedMessage());
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    public void delete() throws SQLException {
        Connection con = GestionBDD.getConnection();
        PreparedStatement pst1 = con.prepareStatement("DELETE FROM client WHERE id = ?");
        pst1.setInt(1, this.id);
        pst1.executeUpdate();
    }

    public void save() throws SQLException{
        Connection con = GestionBDD.getConnection();        
        try (PreparedStatement pst = con.prepareStatement(
                "INSERT INTO client (nom) VALUES (?)")){
            pst.setString(1, this.nom);
            pst.executeUpdate();
        }
    }
}
