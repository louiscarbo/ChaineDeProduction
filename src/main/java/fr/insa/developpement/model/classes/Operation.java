package fr.insa.developpement.model.classes;

public class Operation {    private int id;
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
//    private TypeOperation type;
//    private Machine[] machines;
//
//    public Operation() {
//        this.type = new TypeOperation();
//        this.machines = new Machine[]{};
//    }
//
//    public TypeOperation getType() {
//        return type;
//    }
//
//    public void setType(TypeOperation type) {
//        this.type = type;
//    }
//
//    public Machine[] getMachines() {
//        return machines;
//    }
//
//    public void setMachines(Machine[] machines) {
//        this.machines = machines;
//    }
}
