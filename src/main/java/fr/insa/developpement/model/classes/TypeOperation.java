package fr.insa.developpement.model.classes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class TypeOperation {
    private int id;
    private String des;

    public TypeOperation(int id, String des) {
        this.id = id;
        this.des = des;
    }
    public void save(Connection con) throws SQLException{
        
        try (PreparedStatement pst=con.prepareStatement(
                "INSERT INTO machine (id,des) VALUES (?,?)")){
            
            pst.setInt(1, this.id);
           pst.setString(2 ,this.des);
            pst.executeUpdate();
        }
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getdes() {
        return des;
    }

    public void setdes(String des) {
        this.des = des;
    }

    @Override
    public String toString() {
        return "TypeOperation{" +
                "id=" + id +
                ", des='" + des + '\'' +
                '}';
    }

    

}
