/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.hugo.prod.model;

/**
 *
 * @author hugog
 */
public class Operation {

    private int id;
    private int idType;
    private int idProduit;

    public Operation(int id, int idType, int idProduit) {
        this.id = id;
        this.idType = idType;
        this.idProduit = idProduit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id=" + id +
                ", idType=" + idType +
                ", idProduit=" + idProduit +
                '}';
    }
}