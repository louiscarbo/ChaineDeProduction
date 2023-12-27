package fr.insa.developpement.model.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.insa.developpement.model.GestionBDD;

public class Produit {
    private int id;
    private String ref;
    private String des;

    public Produit() {
        this.id = 0;
        this.ref = "";
        this.des = "";
    }

    public Produit(String ref, String des) {
        this.ref = ref;
        this.des = des;
    }

    public Produit(int id, String ref, String des) {
        this.id = id;
        this.ref = ref;
        this.des = des;
    }

    public void save(Connection con) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                "INSERT INTO produit (ref, des) VALUES (?, ?)")){
            pst.setString(1, this.ref);
            pst.setString(2, this.des);
            pst.executeUpdate();
        }
    }

    public void delete(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
            "DELETE FROM produit WHERE id = ?"
        )) {
            pst.setInt(1, this.id);
            pst.executeUpdate();
        }
    }

    public static List<Produit> getProduitsFromServer() throws SQLException {
        try (Connection conn = GestionBDD.connectSurServeurM3()) {
            try (Statement st = conn.createStatement()) {
                ResultSet rs = st.executeQuery("SELECT * FROM produit");

                List<Produit> produits = new ArrayList<>();

                while (rs.next()) {
                    Produit produit = new Produit();
                    produit.setId(rs.getInt("id"));
                    produit.setDes(rs.getString("des"));
                    produit.setRef(rs.getString("ref"));

                    produits.add(produit);
                }
                return produits;
            }
        }
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getRef() {
        return ref;
    }
    public void setRef(String ref) {
        this.ref = ref;
    }
    public String getDes() {
        return des;
    }
    public void setDes(String des) {
        this.des = des;
    }

    
}
