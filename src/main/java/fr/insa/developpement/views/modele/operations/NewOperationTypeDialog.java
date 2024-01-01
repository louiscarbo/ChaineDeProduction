package fr.insa.developpement.views.modele.operations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import fr.insa.developpement.model.classes.Machine;
import fr.insa.developpement.model.classes.TypeOperation;

public class NewOperationTypeDialog extends Dialog {

    private TextField nomTextField;
    private TextField desTextField;
    private Button saveButton;
    private CheckboxGroup<Machine> machinesCheckboxGroup;

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
        this.machinesCheckboxGroup = createMachinesList();

        VerticalLayout dialogLayout = new VerticalLayout(
            nomTextField,
            desTextField,
            machinesCheckboxGroup
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
                newTypeOperation.setMachinesAssociees(new ArrayList<Machine>(machinesCheckboxGroup.getValue()));
                try {
                    newTypeOperation.save();
                    Notification success = Notification.show("Type d'opération ajouté avec succès");
                    success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (SQLException e1) {
                    Notification error = Notification.show(
                        "Une erreur est survenue lors de l'enregistrement du type d'opération sur le serveur :\n" + e1.getLocalizedMessage()
                    );
                    error.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
                dialog.close();
                parentView.refreshGrid();
            }
        );
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);

        return saveButton;
    }

    private TypeOperation createTypeOperation() {
        String nom = this.nomTextField.getValue();
        String des = this.desTextField.getValue();
        
        TypeOperation newTypeOperation = new TypeOperation(nom, des);
    
        return newTypeOperation;
    }

    private static CheckboxGroup<Machine> createMachinesList() {
        CheckboxGroup<Machine> listeMachines = new CheckboxGroup<Machine>();
        listeMachines.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        List<Machine> machines = new ArrayList<Machine>();
        try {
            machines = Machine.getMachinesWithoutOperationType();
        } catch (SQLException e) {
            listeMachines.setEnabled(false);
            Notification.show("Erreur lors de la récupération des machines à sélectionner. " + e.getLocalizedMessage());
        }

        listeMachines.setItems(machines);
        listeMachines.setLabel("Machines sans type d'opération");
        listeMachines.setRenderer(new ComponentRenderer<>(machine -> createMachineCheckboxLayout(machine)));
        listeMachines.setHeight("200px");

        return listeMachines;
    }

    private static HorizontalLayout createMachineCheckboxLayout(Machine machine) {
        HorizontalLayout hlayout = new HorizontalLayout();

        hlayout.add(new Text(machine.getRef()));

        Span spacer = new Span();
        spacer.setWidthFull();
        hlayout.add(spacer);

        NumberField dureeField = new NumberField();
        dureeField.setSuffixComponent(new Div(new Text("min")));
        dureeField.setValue(Double.valueOf(30));
        dureeField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        dureeField.setValueChangeMode(ValueChangeMode.EAGER);
        hlayout.add(dureeField);

        dureeField.addValueChangeListener( event -> {
            if(dureeField.getValue() != null) {
                machine.setDureeTypeOperation(dureeField.getValue());
            }
        });

        hlayout.setAlignItems(Alignment.CENTER);
        return hlayout;
    }
}
