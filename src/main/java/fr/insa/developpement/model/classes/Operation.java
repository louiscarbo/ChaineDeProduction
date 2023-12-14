package fr.insa.developpement.model.classes;

public class Operation {
    private TypeOperation type;
    private Machine[] machines;

    public Operation() {
        this.type = new TypeOperation();
        this.machines = new Machine[]{};
    }

    public TypeOperation getType() {
        return type;
    }

    public void setType(TypeOperation type) {
        this.type = type;
    }

    public Machine[] getMachines() {
        return machines;
    }

    public void setMachines(Machine[] machines) {
        this.machines = machines;
    }

    
    
}
