package fr.insa.developpement.model.classes;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import fr.insa.developpement.model.GestionBDD;

public class Commande {
    private int id;
    private String nomClient;
    private Date date;

    public static List<Commande> getCommandes() throws SQLException {
        Connection conn = GestionBDD.getConnection();
        List<Commande> commandes = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM commande")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Commande commande = getCommandeFromId(id);
                if (commande != null) {
                    commandes.add(commande);
                }
            }
        }
        return commandes;
    }

    public static Commande getCommandeFromId(int id) throws SQLException {
        Connection conn = GestionBDD.getConnection();
        try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM commande WHERE id = ?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String nomClient = rs.getString("nomClient");
                Date dateCommande = rs.getDate("dateCommande");
                Commande commande = new Commande(id, nomClient, dateCommande);
                return commande;
            }
        }
        return null;
    }

    // TODO Créer la fonction getCommandesForClient()
    public static List<Commande> getCommandesForClient() throws SQLException {
        return new ArrayList<Commande>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public void changeNomClient(String nomClient) {
        setNomClient(nomClient);
        Connection connection = GestionBDD.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE commande SET nomClient = ? WHERE id = ?"
            );
            preparedStatement.setString(1, this.nomClient);
            preparedStatement.setInt(2, this.id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            Notification error = Notification.show("Une erreur est survenue lors du changement du nom du client de la commande : " + e.getLocalizedMessage());
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    public void delete() throws SQLException {
        Connection con = GestionBDD.getConnection();
        PreparedStatement pst1 = con.prepareStatement("DELETE FROM commande WHERE id = ?");
        pst1.setInt(1, this.id);
        pst1.executeUpdate();
    }

    public Date getDate() {
        return date;
    }

    public LocalDate getLocalDate() {
        return date.toLocalDate();
    }

    public void setDate(Date dateCommande) {
        this.date = dateCommande;
    }

    public void changeDate(LocalDate date) {
        setDate(Date.valueOf(date));
        Connection connection = GestionBDD.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE commande SET dateCommande = ? WHERE id = ?"
            );
            preparedStatement.setDate(1, this.date);
            preparedStatement.setInt(2, this.id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            Notification error = Notification.show("Une erreur est survenue lors du changement de la date de la commande : " + e.getLocalizedMessage());
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    public Commande(int id, String nomClient, Date dateCommande) {
        this.id = id;
        this.nomClient = nomClient;
        this.date = dateCommande;
    }

    public Commande() {
        this.id = 0;
        this.nomClient = "";
        this.date = Date.valueOf(LocalDate.now());
    }    
}
