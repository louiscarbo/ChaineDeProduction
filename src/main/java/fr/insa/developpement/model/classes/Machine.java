package fr.insa.developpement.model.classes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

import fr.insa.developpement.model.GestionBDD;

public class Machine {
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
        this.id = -1;
        this.des = des;
        this.ref = ref;
        this.puissance = puissance;
    }

    public Machine() {
        this.id = -1;
        this.des = "";
        this.ref = "";
        this.puissance = 0;
    }

    public void save(Connection con) throws SQLException{
        try (PreparedStatement pst = con.prepareStatement(
                "INSERT INTO machine (ref, des, puissance) VALUES (?, ?, ?)")){
            pst.setString(1, this.ref);
            pst.setString(2, this.des);
            pst.setDouble(3, this.puissance);
            pst.executeUpdate();
        }
    }
    
    public static void fillMachineTable(Connection con) throws SQLException {
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

    public static List<Machine> getMachinesFromServer() throws SQLException {
        try (Connection conn = GestionBDD.connectSurServeurM3()) {
            try (Statement st = conn.createStatement()) {
                ResultSet rs = st.executeQuery("SELECT * FROM machine");

                List<Machine> machines = new ArrayList<>();

                while (rs.next()) {
                    Machine machine = new Machine();
                    machine.setId(rs.getInt("id"));
                    machine.setDes(rs.getString("des"));
                    machine.setRef(rs.getString("ref"));
                    machine.setPuissance(rs.getDouble("puissance"));

                    machines.add(machine);
                }

                return machines;
            }
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

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public double getPuissance() {
        return puissance;
    }

    public void setPuissance(double puissance) {
        this.puissance = puissance;
    }
    
}
