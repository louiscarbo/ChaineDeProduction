package fr.insa.developpement.model.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import fr.insa.developpement.model.GestionBDD;

public class Realise {

    public static void deleteRealiseFromIdMachine(int idMachine) throws SQLException {
        Connection con = GestionBDD.getConnection();
        PreparedStatement pst1 = con.prepareStatement("DELETE FROM realise WHERE idMachine = ?");
        pst1.setInt(1, idMachine);
        pst1.executeUpdate();
    }

    public static void deleteRealiseFromIdTypeOperation(int idTypeOperation) throws SQLException {
        Connection con = GestionBDD.getConnection();
        PreparedStatement pst1 = con.prepareStatement("DELETE FROM realise WHERE idType = ?");
        pst1.setInt(1, idTypeOperation);
        pst1.executeUpdate();
    }
}
