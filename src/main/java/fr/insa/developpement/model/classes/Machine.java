package fr.insa.developpement.model.classes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.insa.developpement.model.GestionBDD;

public class Machine {
    private int id;
    private String des;
    private String ref;
    private double puissance;
    private Optional<TypeOperation> typeOperation;
    private double dureeTypeOperation;

    public Machine(int id, String des, String ref, double puissance) {
        this(id, des, ref, puissance, null, 30);
    }

    public Machine(String des, String ref, double puissance) {
        this(-1, des, ref, puissance);
    }

    public Machine(String des, String ref, double puissance, TypeOperation typeOperation, double dureeTypeOperation) {
        this(-1, des, ref, puissance, typeOperation, dureeTypeOperation);
    }

    public Machine(int id, String des, String ref, double puissance, TypeOperation typeOperation, double dureeTypeOperation) {
        this.id = id;
        this.des = des;
        this.ref = ref;
        this.puissance = puissance;
        this.typeOperation = Optional.ofNullable(typeOperation);
        this.dureeTypeOperation = dureeTypeOperation;
    }

    public Machine() {
        this(-1, "", "", 0, null, 30);
    }

    public void save() throws SQLException{
        Connection con = GestionBDD.getConnection();
        con.setAutoCommit(false);
        
        try (PreparedStatement pst = con.prepareStatement(
                "INSERT INTO machine (ref, des, puissance) VALUES (?, ?, ?)")){
            pst.setString(1, this.ref);
            pst.setString(2, this.des);
            pst.setDouble(3, this.puissance);
            pst.executeUpdate();
        }

        // Si l'utilisateur a renseigné l'opération réalisée par la machine, on l'enregistre
        if(this.typeOperation.isPresent()) {
            PreparedStatement pst = con.prepareStatement("SELECT MAX(id) AS latestId FROM machine");
            ResultSet rs = pst.executeQuery();
            rs.next();
            int machineID = rs.getInt("latestId");

            PreparedStatement realiseStatement = con.prepareStatement("INSERT INTO realise (idMachine, idType, duree) VALUES (?,?,?)");
            realiseStatement.setInt(1, machineID);
            realiseStatement.setInt(2, this.typeOperation.get().getId());
            realiseStatement.setDouble(3, this.dureeTypeOperation);
            realiseStatement.executeUpdate();
        }

        con.commit();
        con.setAutoCommit(true);
    }

    public void delete(Connection con) throws SQLException {
        PreparedStatement pst1 = con.prepareStatement("DELETE FROM machine WHERE id = ?");
        pst1.setInt(1, this.id);
        pst1.executeUpdate();
    }
    
    public static void fillMachineTable(Connection con) throws SQLException {
        Machine newMachine = new Machine("Drill", "MCH001", 1500);
        newMachine.setDureeTypeOperation(20);
        newMachine.save();
        Machine newMachine2 = new Machine("Lathe", "MCH002", 5000);
        newMachine2.save();
    }

    public static List<Machine> getMachines() throws SQLException {
        Connection conn = GestionBDD.getConnection();
        List<Machine> machines = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM machine")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Machine machine = getMachineFromId(id);
                if (machine != null) {
                    machines.add(machine);
                }
            }
        }
        return machines;
    }

    public static Machine getMachineFromId(int id) throws SQLException {
        Connection conn = GestionBDD.getConnection();
        try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM machine WHERE id = ?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Machine machine = new Machine();
                machine.setId(rs.getInt("id"));
                machine.setDes(rs.getString("des"));
                machine.setRef(rs.getString("ref"));
                machine.setPuissance(rs.getDouble("puissance"));

                // Récupération du typeOperation
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT idType FROM realise WHERE idMachine = ?")) {
                    ps.setInt(1, machine.getId());
                    ResultSet rs2 = ps.executeQuery();
                    while (rs2.next()) {
                        int possibleID = rs2.getInt("idType");
                        if(possibleID != 0) {
                            machine.setTypeOperation(TypeOperation.getSimpleTypeOperationFromId(possibleID));
                        }
                    }
                }

                // Récupération de la durée
                try(PreparedStatement ps = conn.prepareStatement(
                    "SELECT duree FROM realise WHERE idMachine = ?"
                )) {
                    ps.setInt(1, machine.getId());
                    ResultSet rs2 = ps.executeQuery();
                    while(rs2.next()) {
                        machine.setDureeTypeOperation(rs2.getDouble("duree"));
                    }
                }

                return machine;
            }
        }
        return null;
    }

    public static List<Machine> getMachinesWithoutOperationType() throws SQLException {
        Connection conn = GestionBDD.getConnection();
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(
                "SELECT machine.*\n" +
                    "FROM machine\n" +
                    "LEFT JOIN realise ON realise.idMachine = machine.id\n" +
                    "WHERE realise.idMachine IS NULL");

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

    public TypeOperation getTypeOperation() {
        return this.typeOperation.get();
    }

    public void setTypeOperation(TypeOperation typeOperation) {
        this.typeOperation = Optional.of(typeOperation);
    }
    
    public double getDureeTypeOperation() {
        return dureeTypeOperation;
    }

    public void setDureeTypeOperation(double dureeTypeOperation) {
        this.dureeTypeOperation = dureeTypeOperation;
    }
    
    public boolean hasTypeOperation() {
        return this.typeOperation.isPresent();
    }

}
