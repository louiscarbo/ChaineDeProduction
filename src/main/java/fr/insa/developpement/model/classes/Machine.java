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

        if(this.idTypeOperationAssocie.isPresent()) {
            try(PreparedStatement pst = con.prepareStatement(
                "INSERT INTO realise (idMachine, idType, duree) VALUES (?,?,?)"
            )) {
                pst.setInt(1, nextId);
                pst.setDouble(2, this.idTypeOperationAssocie.get());
                pst.setDouble(3, this.dureeTypeOperation);
                pst.executeUpdate();
            }
        } else {
            try(PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO realise (idMachine, duree) VALUES (?,?)"
            )) {
                pst.setInt(1, nextId);
                pst.setDouble(2, 30);
                pst.executeUpdate();
            }
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

                    // Récupération des idMachines associées à ce type d'opération
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT idType FROM realise WHERE idMachine = ?")) {
                        ps.setInt(1, machine.getId());
                        ResultSet rs2 = ps.executeQuery();
                        while (rs2.next()) {
                            int possibleID = rs2.getInt("idType");
                            if(possibleID != 0) {
                                machine.setIdTypeOperationAssocie(possibleID);
                            }
                            break;
                        }
                    }

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

                    // Récupération des idMachines associées à ce type d'opération
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT idType FROM realise WHERE idMachine = ?")) {
                        ps.setInt(1, machine.getId());
                        ResultSet rs2 = ps.executeQuery();
                        while (rs2.next()) {
                            int possibleID = rs2.getInt("idType");
                            if(possibleID != 0) {
                                machine.setIdTypeOperationAssocie(possibleID);
                            }
                            break;
                        }
                    }
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

    public int getIdTypeOperationAssocie() {
        if(idTypeOperationAssocie != null){
            return idTypeOperationAssocie.get();
        } else {
            return -1;
        }
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
