package fr.insa.developpement.model.classes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.insa.developpement.model.GestionBDD;

public class TypeOperation {
    private int id;
    private String des;
    private String nom;
    private List<Machine> machinesAssociees;

    public TypeOperation(int id, String nom, String des) {
        this.id = id;
        this.des = des;
        this.nom = nom;
        this.machinesAssociees = new ArrayList<Machine>();
    }

    public TypeOperation(String nom, String des) {
        this(0, nom, des);
    }

    public TypeOperation() {
        this(0, "", "");
    }

    public void save() throws SQLException{
        Connection con = GestionBDD.getConnection();
        con.setAutoCommit(false);

        try (PreparedStatement pst=con.prepareStatement(
                "INSERT INTO typeoperation (nom,des) VALUES (?,?)"
        )){
            pst.setString(1, this.nom);
            pst.setString(2 ,this.des);
            pst.executeUpdate();
        }

        int newTypeOperationId;
        try (PreparedStatement pst = con.prepareStatement(
            "SELECT MAX(id) AS latestId FROM typeoperation"
        )) {
            ResultSet rs = pst.executeQuery();
            rs.next();
            newTypeOperationId = rs.getInt("latestId");

            // Pour chaque machine qui réalise l'opération, crée l'objet réalise
            for (Machine machine : machinesAssociees) {
                PreparedStatement pst2 = con.prepareStatement(
                    "INSERT INTO realise (idType, idMachine, duree) VALUES (?,?,?)"
                );
                pst2.setInt(1, newTypeOperationId);
                pst2.setInt(2, machine.getId());
                pst2.setDouble(3, machine.getDureeTypeOperation());
                pst2.executeUpdate();
            }
        }

        con.commit();
        con.setAutoCommit(true);
    }

    public void delete() throws SQLException {
        Connection con = GestionBDD.getConnection();
        try (PreparedStatement pst = con.prepareStatement(
            "DELETE FROM typeoperation WHERE id = ?"
        )) {
            pst.setInt(1, this.id);
            pst.executeUpdate();
        }
    }

    public static List<TypeOperation> getTypesOperations() throws SQLException {
        Connection conn = GestionBDD.getConnection();

        List<TypeOperation> typesOperations = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM typeoperation")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                TypeOperation typeOperation = getTypeOperationFromId(id);
                if (typeOperation != null) {
                    typesOperations.add(typeOperation);
                }
            }
        }
        return typesOperations;
    }

    public static TypeOperation getTypeOperationFromId(int id) throws SQLException {
        Connection conn = GestionBDD.getConnection();

        try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM typeoperation WHERE id = ?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                TypeOperation typeOperation = new TypeOperation();
                typeOperation.setId(rs.getInt("id"));
                typeOperation.setNom(rs.getString("nom"));
                typeOperation.setDes(rs.getString("des"));

                // Récupération des machines associées à ce type d'opération
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT idMachine FROM realise WHERE idType = ?")) {
                    ps.setInt(1, typeOperation.getId());
                    ResultSet rs2 = ps.executeQuery();
                    while (rs2.next()) {
                        int idMachine = rs2.getInt("idMachine");
                        typeOperation.addMachine(Machine.getMachineFromId(idMachine));
                    }
                }

                return typeOperation;
            }
        }

        return null;
    }

    // Renvoie une version simple du typeOperation sans les machines pour éviter une boucle infinie
    public static TypeOperation getSimpleTypeOperationFromId(int id) throws SQLException {
        Connection conn = GestionBDD.getConnection();

        try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM typeoperation WHERE id = ?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                TypeOperation typeOperation = new TypeOperation();
                typeOperation.setId(rs.getInt("id"));
                typeOperation.setNom(rs.getString("nom"));
                typeOperation.setDes(rs.getString("des"));
                return typeOperation;
            }
        }

        return new TypeOperation();
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

    public void addMachine(Machine newMachine) {
        this.machinesAssociees.add(newMachine);
    }

    public void setMachinesAssociees(List<Machine> machines) {
        this.machinesAssociees = machines;
    }

    public List<Machine> getMachinesAssociees() {
        return machinesAssociees;
    }

    public boolean hasMachines() {
        return !this.machinesAssociees.isEmpty();
    }

    @Override
    public String toString() {
        return "TypeOperation{" +
                "id=" + id +
                ", des='" + des + '\'' +
                '}';
    }

}
