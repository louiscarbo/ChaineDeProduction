package fr.insa.developpement.model.classes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TypeOperation {
    private int id;
    private String des;
    private String nom;

    public TypeOperation(int id, String nom, String des) {
        this.id = id;
        this.des = des;
        this.nom = nom;
    }

    public TypeOperation() {
        this.id = 0;
        this.des = "";
        this.nom = "";
    }

    public void save(Connection con) throws SQLException{
        try (PreparedStatement pst=con.prepareStatement(
                "INSERT INTO typeoperation (id,des) VALUES (?,?)")){
            pst.setInt(1, this.id);
            pst.setString(2 ,this.des);
            pst.executeUpdate();
        }
    }

    public void delete(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
            "DELETE FROM typeoperation WHERE id = ?"
        )) {
            pst.setInt(1, this.id);
            pst.executeUpdate();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "TypeOperation{" +
                "id=" + id +
                ", des='" + des + '\'' +
                '}';
    }

}
