package fr.insa.developpement.model.classes;

import java.sql.Date;
import java.time.LocalDate;

public class Commande {
    private int id;
    private String nomClient;

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

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    private Date dateCommande;

    public Commande(int id, String nomClient, Date dateCommande) {
        this.id = id;
        this.nomClient = nomClient;
        this.dateCommande = dateCommande;
    }

    public Commande() {
        this.id = 0;
        this.nomClient = "";
        this.dateCommande = Date.valueOf(LocalDate.now());
    }    
}
