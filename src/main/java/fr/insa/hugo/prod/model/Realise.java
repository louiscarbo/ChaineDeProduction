/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.hugo.prod.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author hugog
 */
public class Realise {

    private int idMachine;
    private int idType;
    private int duree;

    public Realise() {
    }

    public Realise(int idMachine, int idType, int duree) {
        this.idMachine = idMachine;
        this.idType = idType;
        this.duree = duree;
    }
        public void save(Connection con) throws SQLException{
        
        try (PreparedStatement pst=con.prepareStatement(
                "INSERT INTO machine (idMachine,idType,duree) VALUES (?,?,?)")){
            pst.setInt(1, this.idMachine);
            pst.setInt(2, this.idType);
            pst.setInt(3, this.duree);
            pst.executeUpdate();
        }
    }

 

    public int getIdMachine() {
        return idMachine;
    }

    public void setIdMachine(int idMachine) {
        this.idMachine = idMachine;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    @Override
    public String toString() {
        return "Realise{" +           
                ", idMachine=" + idMachine +
                ", idType=" + idType +
                ", duree=" + duree +
                '}';
    }
}

