package fr.insa.developpement.model.classes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.insa.developpement.model.GestionBDD;

public class TypeOperation {
    private int id;
    private String des;
    private String nom;
    private List<Integer> idMachinesAssocies = new ArrayList<Integer>();

    public TypeOperation(int id, String nom, String des) {
        this.id = id;
        this.des = des;
        this.nom = nom;
        this.idMachinesAssocies = new ArrayList<>();
    }

    public TypeOperation(String nom, String des) {
        this(0, nom, des);
    }

    public TypeOperation() {
        this.id = 0;
        this.des = "";
        this.nom = "";
    }

    public void save(Connection con) throws SQLException{
        int nextIdType = 0;
        con.setAutoCommit(false);

        // Récupère le prochain ID attribué par la BDD au prochain type d'opération créé
        // Pour chaque machine qui réalise l'opération, modifie l'objet realise accordément
        try(PreparedStatement pst = con.prepareStatement(
                "SELECT AUTO_INCREMENT AS next_id\n" + 
                    "FROM information_schema.TABLES\n" + 
                    "WHERE TABLE_SCHEMA = 'm3_hgounon01'\n" +
                    "AND TABLE_NAME = 'typeoperation'"
            )) {
            ResultSet resultSet = pst.executeQuery();
            resultSet.next();
            nextIdType = resultSet.getInt("next_id");

            for (Integer idMachine : idMachinesAssocies) {
                PreparedStatement pst2 = con.prepareStatement(
                    "UPDATE realise SET idType = ? WHERE idMachine = ?"
                );
                pst2.setInt(1, nextIdType);
                pst2.setInt(2, idMachine);
                pst2.executeUpdate();
            }
        }

        try (PreparedStatement pst=con.prepareStatement(
                "INSERT INTO typeoperation (nom,des) VALUES (?,?)")){
            pst.setString(1, this.nom);
            pst.setString(2 ,this.des);
            pst.executeUpdate();
        }

        con.commit();
        con.setAutoCommit(true);
    }

    public void delete(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
            "DELETE FROM typeoperation WHERE id = ?"
        )) {
            pst.setInt(1, this.id);
            pst.executeUpdate();
        }
    }

    public static List<TypeOperation> getTypeOperationsFromServer() throws SQLException {
        try (Connection conn = GestionBDD.connectSurServeurM3()) {
            try (Statement st = conn.createStatement()) {
                ResultSet rs = st.executeQuery("SELECT * FROM typeoperation");

                List<TypeOperation> typeOperations = new ArrayList<>();

                while (rs.next()) {
                    // Récupération de l'ID, du nom et de la description du type d'opération
                    TypeOperation typeOperation = new TypeOperation();
                    typeOperation.setId(rs.getInt("id"));
                    typeOperation.setNom(rs.getString("nom"));
                    typeOperation.setDes(rs.getString("des"));

                    // Récupération des idMachines associées à ce type d'opération
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT idMachine FROM realise WHERE idType = ?")) {
                        ps.setInt(1, typeOperation.getId());
                        ResultSet rs2 = ps.executeQuery();
                        while (rs2.next()) {
                            typeOperation.addIdMachine(rs2.getInt("idMachine"));
                        }
                    }
                    typeOperations.add(typeOperation);
                }
                return typeOperations;
            }
        }
    }

    public static TypeOperation getTypeOperationFromId(int id) throws SQLException {
        try (Connection conn = GestionBDD.connectSurServeurM3()) {
            try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM typeoperation WHERE id = ?")) {
                pst.setInt(1, id);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    TypeOperation typeOperation = new TypeOperation();
                    typeOperation.setId(rs.getInt("id"));
                    typeOperation.setNom(rs.getString("nom"));
                    typeOperation.setDes(rs.getString("des"));

                    // Récupération des idMachines associées à ce type d'opération
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT idMachine FROM realise WHERE idType = ?")) {
                        ps.setInt(1, typeOperation.getId());
                        ResultSet rs2 = ps.executeQuery();
                        while (rs2.next()) {
                            typeOperation.addIdMachine(rs2.getInt("idMachine"));
                        }
                    }

                    return typeOperation;
                }
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

    public List<Integer> getIdMachinesAssocies() {
        return idMachinesAssocies;
    }

    public void setIdMachinesAssocies(List<Integer> idMachinesAssocies){
        this.idMachinesAssocies = idMachinesAssocies;
    }

    public void addIdMachine(int newIdMachine) {
        this.idMachinesAssocies.add(newIdMachine);
    }

    @Override
    public String toString() {
        return "TypeOperation{" +
                "id=" + id +
                ", des='" + des + '\'' +
                '}';
    }

}
