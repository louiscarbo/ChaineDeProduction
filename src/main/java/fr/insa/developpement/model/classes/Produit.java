package fr.insa.developpement.model.classes;

public class Produit {
    private String nom;
    private String description;
    private TypeOperation[] operations;

    public Produit() {
        this.nom = "";
        this.description = "";
        this.operations = new TypeOperation[]{} ;
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

    public TypeOperation[] getOperations() {
        return operations;
    }

    public void setOperations(TypeOperation[] operations) {
        this.operations = operations;
    }

    
    
}
