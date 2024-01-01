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
    private Optional<Integer> idTypeOperationAssocie;
    private double dureeTypeOperation;

    public Machine(int id, String des, String ref, double puissance) {
        this.id = id;
        this.des = des;
        this.ref = ref;
        this.puissance = puissance;
        this.idTypeOperationAssocie = Optional.ofNullable(null);
        this.dureeTypeOperation = 30;
    }

    public Machine(String des, String ref, double puissance) {
        this(-1, des, ref, puissance);
    }

    public Machine(String des, String ref, double puissance, int idTypeOperationAssocie, double dureeTypeOperation) {
        this(des, ref, puissance);
        this.idTypeOperationAssocie = Optional.of(idTypeOperationAssocie);
        this.dureeTypeOperation = dureeTypeOperation;
    }

    public Machine() {
        this.id = -1;
        this.des = "";
        this.ref = "";
        this.puissance = 0;
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
        if(this.idTypeOperationAssocie.isPresent()) {
            PreparedStatement pst = con.prepareStatement("SELECT MAX(id) AS latestId FROM machine");
            ResultSet rs = pst.executeQuery();
            rs.next();
            int machineID = rs.getInt("latestId");

            PreparedStatement realiseStatement = con.prepareStatement("INSERT INTO realise (idMachine, idType, duree) VALUES (?,?,?)");
            realiseStatement.setInt(1, machineID);
            realiseStatement.setInt(2, this.idTypeOperationAssocie.get());
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

    public static List<Machine> getMachines(Connection conn) throws SQLException {
        List<Machine> machines = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM machine")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Machine machine = getMachineFromId(conn, id);
                if (machine != null) {
                    machines.add(machine);
                }
            }
        }
        return machines;
    }

    public static Machine getMachineFromId(Connection conn, int id) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM machine WHERE id = ?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Machine machine = new Machine();
                machine.setId(rs.getInt("id"));
                machine.setDes(rs.getString("des"));
                machine.setRef(rs.getString("ref"));
                machine.setPuissance(rs.getDouble("puissance"));

                // Récupération de l'idType
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT idType FROM realise WHERE idMachine = ?")) {
                    ps.setInt(1, machine.getId());
                    ResultSet rs2 = ps.executeQuery();
                    while (rs2.next()) {
                        int possibleID = rs2.getInt("idType");
                        if(possibleID != 0) {
                            machine.setIdTypeOperationAssocie(possibleID);
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
        return new Machine();
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

    public int getIdTypeOperationAssocie() {
        if(idTypeOperationAssocie != null){
            return idTypeOperationAssocie.get();
        } else {
            return -1;
        }
    }

    public TypeOperation getTypeOperationAssocie() throws SQLException {
        int idType = getIdTypeOperationAssocie();
        return TypeOperation.getTypeOperationFromId(idType);
    }

    public void setIdTypeOperationAssocie(int idTypeOperationAssocie) {
        this.idTypeOperationAssocie = Optional.of(idTypeOperationAssocie);
    }
    
    public double getDureeTypeOperation() {
        return dureeTypeOperation;
    }

    public void setDureeTypeOperation(double dureeTypeOperation) {
        this.dureeTypeOperation = dureeTypeOperation;
    }
    
    public boolean hasTypeOperationId() {
        return !(this.idTypeOperationAssocie == null);
    }

}
