package fr.insa.developpement.model.classes;

public class TypeOperation {
    private String nom;
    private String description;
    private double duree;

    public TypeOperation() {
        this.nom = "";
        this.description = "";
        this.duree = 0.0;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDuree() {
        return duree;
    }

    public void setDuree(double duree) {
        this.duree = duree;
    }

    

}
