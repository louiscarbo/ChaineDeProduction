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
        this(-1,des,ref,puissance);
    }
    
    
    public void save(Connection con) throws SQLException{
        
        try (PreparedStatement pst=con.prepareStatement(
                "INSERT INTO machine (ref,des,puissance) VALUES (?,?,?)")){
            pst.setString(1, this.ref);
            pst.setString(2, this.des);
            pst.setDouble(3, this.puissance);
            pst.executeUpdate();
        }
    }    
}