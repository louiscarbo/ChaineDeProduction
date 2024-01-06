package fr.insa.developpement.model.classes;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import fr.insa.developpement.model.GestionBDD;

public class Commande {
    private int id;
    private Date date;
    private Client client;
    private boolean termine;
    private Map<Produit, Integer> produitsQuantites = new HashMap<>();

    public boolean isTermine() {
        return termine;
    }

    public void setTermine(boolean termine) {
        this.termine = termine;
    }

    public Map<Produit, Integer> getProduitsQuantites() {
        return produitsQuantites;
    }
    
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
                boolean termine = rs.getBoolean("termine");
                commande.setTermine(termine);

                PreparedStatement ps = conn.prepareStatement("SELECT * FROM produit_commande WHERE idCommande = ?");
                ps.setInt(1, id);
                ResultSet rs2 = ps.executeQuery();
                while (rs2.next()) {
                    int idProduit = rs2.getInt("idProduit");
                    int quantite = rs2.getInt("quantite");
                    commande.addProduitQuantite(Map.entry(Produit.getProduitFromId(idProduit), quantite));
                }

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

    public void setProduitsQuantites(Map<Produit, Integer> produitsQuantites) {
        this.produitsQuantites = produitsQuantites;
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

    public void changeTermine(boolean termine) {
        setTermine(termine);
        Connection connection = GestionBDD.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE commande SET termine = ? WHERE id = ?"
            );
            preparedStatement.setBoolean(1, this.termine);
            preparedStatement.setInt(2, this.id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            Notification error = Notification.show("Une erreur est survenue lors du changement du statut de la commande : " + e.getLocalizedMessage());
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

    public void save() throws SQLException{
        Connection con = GestionBDD.getConnection();
        con.setAutoCommit(false);
        
        try (PreparedStatement pst = con.prepareStatement(
                "INSERT INTO commande (idClient, dateCommande) VALUES (?, ?)")){
            pst.setInt(1, this.client.getId());
            pst.setDate(2, this.date);
            pst.executeUpdate();
        }

        PreparedStatement pst = con.prepareStatement("SELECT MAX(id) AS latestId FROM commande");
        ResultSet rs = pst.executeQuery();
        rs.next();
        int commandeID = rs.getInt("latestId");

        // Sauvegarde du contenu de la commande
        for (Map.Entry<Produit, Integer> entry: produitsQuantites.entrySet()) {
            PreparedStatement realiseStatement = con.prepareStatement("INSERT INTO produit_commande (idCommande, idProduit, quantite) VALUES (?,?,?)");
            realiseStatement.setInt(1, commandeID);
            realiseStatement.setInt(2, entry.getKey().getId());
            realiseStatement.setInt(3, entry.getValue());
            realiseStatement.executeUpdate();
        }

        con.commit();
        con.setAutoCommit(true);
    }

    public void addProduitQuantite(Map.Entry<Produit, Integer> produitQuantite) {
        this.produitsQuantites.put(produitQuantite.getKey(), produitQuantite.getValue());
    }
}
