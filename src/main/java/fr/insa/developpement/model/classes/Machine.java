package fr.insa.developpement.model.classes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class Machine {
    private String nom;
    private String description;
    // HashMap permettant d'associer à chaque TypeOperation une durée (Double) en secondes
    private HashMap<TypeOperation,Double> operationsDurees;

//    public Machine() {
//        this.nom = "";
//        this.description = "";
//        this.operationsDurees = new HashMap<TypeOperation,Double>();
//    }
    private int id;
    private String des;
    private String ref;
    private double puissance;

    public Machine(int id, String des, String ref, double puissance) {
        this.id = id;
        this.des = des;
        this.ref = ref;
        this.puissance = puissance;
    }

    public Machine(String des, String ref, double puissance) {
        this(-1,des,ref,puissance);
    }
    
    
    public void save(Connection con) throws SQLException{
        
        try (PreparedStatement pst=con.prepareStatement(
                "insert into machine (ref,des,puissance) VALUES (?,?,?)")){
            pst.setString(1, this.ref);
            pst.setString(2, this.des);
            pst.setDouble(3, this.puissance);
            pst.executeUpdate();
        }
    }
    
    public void fillMachineTable(Connection con) throws SQLException {
    // Begin transaction by setting auto-commit to false
    con.setAutoCommit(false);
    try {
        // Prepare a statement to insert data into the machine table
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO machine (ref, des, puissance) VALUES (?, ?, ?)")) {
            
            // Insert machine 1
            ps.setString(1, "MCH001");
            ps.setString(2, "Drill");
            ps.setDouble(3, 1500);
            ps.executeUpdate();
            
            // Insert machine 2
            ps.setString(1, "MCH002");
            ps.setString(2, "Lathe");
            ps.setDouble(3, 5000);
            ps.executeUpdate();
            
            // ... Repeat for as many machines as you want to insert
            
            // Commit the transaction
            con.commit();
        }
    } catch (SQLException ex) {
        // If there is an exception, rollback the transaction
        con.rollback();
        throw ex;
    } finally {
        // Restore default auto-commit behavior
        con.setAutoCommit(true);
    }
}
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<TypeOperation, Double> getOperationsDurees() {
        return operationsDurees;
    }

    public void setOperationsDurees(HashMap<TypeOperation, Double> operationsDurees) {
        this.operationsDurees = operationsDurees;
    }
    
}
