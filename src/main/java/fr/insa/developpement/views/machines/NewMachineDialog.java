package fr.insa.developpement.views.machines;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.sql.SQLException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import fr.insa.developpement.model.GestionBDD;
import fr.insa.developpement.model.classes.Machine;

public class NewMachineDialog extends Dialog {

    private TextField referenceField;
    private TextField descriptionField;
    private NumberField puissanceField;
    private Button saveButton;

    private MachinesView parentView;

    public NewMachineDialog(MachinesView machinesView) {
        this.parentView = machinesView;

        this.setHeaderTitle("Nouvelle Machine");

        VerticalLayout dialogLayout = createDialogLayout();
        this.add(dialogLayout);

        this.saveButton = createSaveButton(this);
        this.saveButton.setEnabled(false);
        
        Button cancelButton = new Button("Annuler", e -> this.close());
        this.getFooter().add(cancelButton);
        this.getFooter().add(saveButton);
        enableOrDisableSaveButton();
    }

    private void enableOrDisableSaveButton() {
        // Permet d'écouter les changements de valeur dans les champs pour activer/désactiver le bouton de validation
        this.referenceField.setValueChangeMode(ValueChangeMode.EAGER);
        this.referenceField.addValueChangeListener(e -> {
            boolean allFieldsFilled = !referenceField.isEmpty() && !descriptionField.isEmpty() && !puissanceField.isEmpty();
            saveButton.setEnabled(allFieldsFilled);
        });
        this.descriptionField.setValueChangeMode(ValueChangeMode.EAGER);
        this.descriptionField.addValueChangeListener(e -> {
            boolean allFieldsFilled = !referenceField.isEmpty() && !descriptionField.isEmpty() && !puissanceField.isEmpty();
            saveButton.setEnabled(allFieldsFilled);
        });
        this.puissanceField.setValueChangeMode(ValueChangeMode.EAGER);
        this.puissanceField.addValueChangeListener(e -> {
            boolean allFieldsFilled = !referenceField.isEmpty() && !descriptionField.isEmpty() && !puissanceField.isEmpty();
            saveButton.setEnabled(allFieldsFilled);
        });
    }

    private VerticalLayout createDialogLayout() {
        this.referenceField = new TextField("Référence");
        this.descriptionField = new TextField("Description");
        this.puissanceField = new NumberField("Puissance");

        //TODO Ajouter un picker pour le type d'opération
        //TODO Ajouter un truc pour la durée de réalisation du type d'opération

        VerticalLayout dialogLayout = new VerticalLayout(referenceField, descriptionField, puissanceField);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private Button createSaveButton(Dialog dialog) {
        Button saveButton = new Button(
            "Ajouter",
            e -> {
                Machine newMachine = createMachine();
                try {
                    newMachine.save(GestionBDD.connectSurServeurM3());
                    Notification.show("Machine ajoutée avec succès");
                } catch (SQLException e1) {
                    Notification.show(
                        "Une erreur est survenue lors de l'enregistrement de la machine sur le serveur :\n" + e1.getLocalizedMessage()
                    );
                } finally {
                    setFormValuesToNull();
                }
                dialog.close();
                parentView.refreshGrid();
            }
        );
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return saveButton;
    }

    private void setFormValuesToNull() {
        this.referenceField.setValue("");
        this.descriptionField.setValue("");
        this.puissanceField.setValue(null);
    }

    private Machine createMachine() {
        String ref = this.referenceField.getValue();
        String des = this.descriptionField.getValue();
        double puissance = this.puissanceField.getValue();
    
        return new Machine(des, ref, puissance);
    }
}
