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
    private Date date;
    private Client client;

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
                Date dateCommande = rs.getDate("dateCommande");
                int idClient = rs.getInt("idClient");
                Commande commande = new Commande(id, dateCommande);
                commande.setClient(Client.getClientFromId(idClient));
                return commande;
            }
        }
        return null;
    }

    public static List<Commande> getCommandesForClient(Client client) throws SQLException {
        List<Commande> commandes = getCommandes();
        commandes.removeIf(commande -> !commande.getClient().equals(client));
        return commandes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    public Commande(int id, Date dateCommande) {
        this.id = id;
        this.date = dateCommande;
    }

    public Commande() {
        this.id = 0;
        this.date = Date.valueOf(LocalDate.now());
    }    
}
