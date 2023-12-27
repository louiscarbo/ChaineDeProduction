package fr.insa.developpement.views.operations;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.insa.developpement.model.GestionBDD;
import fr.insa.developpement.model.classes.Machine;
import fr.insa.developpement.model.classes.TypeOperation;

public class NewOperationTypeDialog extends Dialog {

    private TextField nomTextField;
    private TextField desTextField;
    private Button saveButton;

    private TypeOperationsView parentView;
    
    public NewOperationTypeDialog(TypeOperationsView typeOperationsView) {
        this.parentView = typeOperationsView;

        this.setHeaderTitle("Nouveau Type d'Opération");

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
        this.nomTextField.setValueChangeMode(ValueChangeMode.EAGER);
        this.nomTextField.addValueChangeListener(e -> {
            boolean allFieldsFilled = !nomTextField.isEmpty() && !desTextField.isEmpty();
            saveButton.setEnabled(allFieldsFilled);
        });
        this.desTextField.setValueChangeMode(ValueChangeMode.EAGER);
        this.desTextField.addValueChangeListener(e -> {
            boolean allFieldsFilled = !nomTextField.isEmpty() && !desTextField.isEmpty();
            saveButton.setEnabled(allFieldsFilled);
        });
    }

    private VerticalLayout createDialogLayout() {
        this.nomTextField = new TextField("Nom");
        this.desTextField = new TextField("Description");
        
        // Ne fait rien pour l'instant
        Component listeMachines = createMachinesList();

        VerticalLayout dialogLayout = new VerticalLayout(
            nomTextField,
            desTextField,
            listeMachines
        );
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
                TypeOperation newTypeOperation = createTypeOperation();
                try {
                    newTypeOperation.save(GestionBDD.connectSurServeurM3());
                    Notification.show("Type d'opération ajouté avec succès");
                } catch (SQLException e1) {
                    Notification.show(
                        "Une erreur est survenue lors de l'enregistrement du type d'opération sur le serveur :\n" + e1.getLocalizedMessage()
                    );
                } finally {
                    setFormValuesToNull();
                }
                dialog.close();
                parentView.refreshGrid();
            }
        );
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);

        return saveButton;
    }

    private void setFormValuesToNull() {
        this.nomTextField.setValue("");
        this.desTextField.setValue("");
    }

    private TypeOperation createTypeOperation() {
        String nom = this.nomTextField.getValue();
        String des = this.desTextField.getValue();
    
        return new TypeOperation(nom, des);
    }

    // TODO Rendre le lien entre les machines et les types d'opération actif
    private static CheckboxGroup<Machine> createMachinesList() {
        CheckboxGroup<Machine> listeMachines = new CheckboxGroup<>();
        List<Machine> machines = new ArrayList<Machine>();
        try {
            machines = Machine.getMachinesFromServer();
        } catch (SQLException e) {
            listeMachines.setEnabled(false);
            Notification.show("Erreur lors de la récupération des machines à sélectionner. " + e.getLocalizedMessage());
        }

        listeMachines.setItems(machines);
        listeMachines.setLabel("Machines réalisant l'opération");
        listeMachines.setRenderer(new ComponentRenderer<>(machine -> new Text(machine.getRef())));
        listeMachines.setHeight("200px");

        return listeMachines;
    }
}
