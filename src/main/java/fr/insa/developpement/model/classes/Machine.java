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
        con.setAutoCommit(false);
        int nextId;

        try(PreparedStatement pst = con.prepareStatement(
                "SELECT AUTO_INCREMENT AS next_id\n" + 
                    "FROM information_schema.TABLES\n" + 
                    "WHERE TABLE_SCHEMA = 'm3_hgounon01'\n" +
                    "AND TABLE_NAME = 'machine'"
            )) {
            ResultSet resultSet = pst.executeQuery();
            resultSet.next();
            nextId = resultSet.getInt("next_id");
        }

        //TODO Changer la durée
        try(PreparedStatement pst = con.prepareStatement(
                "INSERT INTO realise (idMachine, duree) VALUES (?,?)")) {
            pst.setInt(1, nextId);
            pst.setDouble(2, 30);
            pst.executeUpdate();
        }

        try (PreparedStatement pst = con.prepareStatement(
                "INSERT INTO machine (ref, des, puissance) VALUES (?, ?, ?)")){
            pst.setString(1, this.ref);
            pst.setString(2, this.des);
            pst.setDouble(3, this.puissance);
            pst.executeUpdate();
        }

        con.commit();
        con.setAutoCommit(true);
    }

    public void delete(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
            "DELETE FROM machine WHERE id = ?"
        )) {
            pst.setInt(1, this.id);
            pst.executeUpdate();
        }
    }
    
    public static void fillMachineTable(Connection con) throws SQLException {
        Machine newMachine = new Machine("Drill", "MCH001", 1500);
        newMachine.save(con);
        Machine newMachine2 = new Machine("Lathe", "MCH002", 5000);
        newMachine2.save(con);
    }

    public static List<Machine> getMachines() throws SQLException {
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

    // TODO pas ouf à améliorer
    public static Machine getMachineFromId(int id) throws SQLException {
        try (Connection conn = GestionBDD.connectSurServeurM3()) {
            try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM machine WHERE id = ?")) {
                pst.setInt(1, id);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    Machine machine = new Machine();
                    machine.setId(rs.getInt("id"));
                    machine.setDes(rs.getString("des"));
                    machine.setRef(rs.getString("ref"));
                    machine.setPuissance(rs.getDouble("puissance"));
                    return machine;
                }
            }
        }
        return new Machine();
    }

    public static List<Machine> getMachinesWithoutOperationType() throws SQLException {
        try (Connection conn = GestionBDD.connectSurServeurM3()) {
            try (Statement st = conn.createStatement()) {
                ResultSet rs = st.executeQuery(
                    "SELECT * FROM machine\n" +
                        "WHERE id IN (\n" +
                        "    SELECT idMachine FROM realise\n" +
                        "    WHERE idType IS NULL\n" +
                        ");");

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
