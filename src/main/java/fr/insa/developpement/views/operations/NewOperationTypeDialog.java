package fr.insa.developpement.views.operations;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.ArrayList;
import java.util.List;

import fr.insa.developpement.model.classes.Machine;

public class NewOperationTypeDialog extends Dialog {
    
    public NewOperationTypeDialog() {
        this.setHeaderTitle("Nouveau Type d'Opération");

        VerticalLayout dialogLayout = createDialogLayout();
        this.add(dialogLayout);

        Button saveButton = createSaveButton(this);
        Button cancelButton = new Button("Annuler", e -> this.close());
        this.getFooter().add(cancelButton);
        this.getFooter().add(saveButton);
    }

    private static VerticalLayout createDialogLayout() {
        TextField firstNameField = new TextField("Nom");
        TextField lastNameField = new TextField("Description");
        Component listeMachines = createMachinesList();

        VerticalLayout dialogLayout = new VerticalLayout(
            firstNameField,
            lastNameField,
            listeMachines
        );
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private static Button createSaveButton(Dialog dialog) {
        Button saveButton = new Button("Ajouter", e -> dialog.close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return saveButton;
    }

    private static CheckboxGroup<Machine> createMachinesList() {
        List<Machine> machines = new ArrayList<Machine>();

        CheckboxGroup<Machine> listeMachines = new CheckboxGroup<>();
        // listeMachines.setItems(machines)
        listeMachines.setLabel("Machines réalisant l'opération");
        listeMachines.setRenderer(new ComponentRenderer<>(machine -> new Text(machine.getNom())));
        listeMachines.setHeight("200px");

        return listeMachines;
    }
}
