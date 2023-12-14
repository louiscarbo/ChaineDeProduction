package fr.insa.developpement.model.classes;
import java.util.HashMap;

public class Machine {
    private String nom;
    private String description;
    // HashMap permettant d'associer à chaque TypeOperation une durée (Double) en secondes
    private HashMap<TypeOperation,Double> operationsDurees;

    public Machine() {
        this.nom = "";
        this.description = "";
        this.operationsDurees = new HashMap<TypeOperation,Double>();
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

    public HashMap<TypeOperation, Double> getOperationsDurees() {
        return operationsDurees;
    }

    public void setOperationsDurees(HashMap<TypeOperation, Double> operationsDurees) {
        this.operationsDurees = operationsDurees;
    }
    
}
