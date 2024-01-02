package fr.insa.developpement.views.interne.machines;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import fr.insa.developpement.model.classes.Machine;
import fr.insa.developpement.model.classes.TypeOperation;

public class NewMachineDialog extends Dialog {

    private TextField referenceField;
    private TextField descriptionField;
    private NumberField puissanceField;
    private RadioButtonGroup<TypeOperation> typesOperationsRadioButtonGroup;
    private NumberField dureeField;
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
        this.typesOperationsRadioButtonGroup = createTypeOperationsButtonGroup();
        this.dureeField = createDureeField();
        enableOrDisableDureeField(dureeField);

        VerticalLayout dialogLayout = new VerticalLayout(
            referenceField,
            descriptionField,
            puissanceField,
            typesOperationsRadioButtonGroup,
            dureeField
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
                createAndSaveMachine();
                dialog.close();
                parentView.refreshGrid();
            }
        );
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);

        return saveButton;
    }

    private void createAndSaveMachine() {
        String ref = this.referenceField.getValue();
        String des = this.descriptionField.getValue();
        double puissance = this.puissanceField.getValue();

        Machine newMachine = new Machine(des, ref, puissance);
        
        if(!this.typesOperationsRadioButtonGroup.isEmpty()) {
            TypeOperation typeOperation = this.typesOperationsRadioButtonGroup.getValue();
            double dureeTypeOperation = this.dureeField.getValue();
            newMachine.setTypeOperation(typeOperation);
            newMachine.setDureeTypeOperation(dureeTypeOperation);
        }

        try {
            newMachine.save();
            Notification succesNotification = Notification.show("Nouvelle machine ajoutée avec succès.");
            succesNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (SQLException e1) {
            Notification errorNotification = Notification.show(
                "Une erreur est survenue lors de l'enregistrement de la machine sur le serveur :\n" + e1.getLocalizedMessage()
            );
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private static RadioButtonGroup<TypeOperation> createTypeOperationsButtonGroup() {
        RadioButtonGroup<TypeOperation> listeTypeOperations = new RadioButtonGroup<TypeOperation>();
        List<TypeOperation> typesOperations = new ArrayList<TypeOperation>();

        try {
            typesOperations = TypeOperation.getTypesOperations();
        } catch(SQLException exc) {
            listeTypeOperations.setEnabled(false);
            Notification notification = Notification.show(
                "Erreur lors de la récupération des types d'opérations à sélectionner : " + exc.getLocalizedMessage()
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

        listeTypeOperations.setItems(typesOperations);
        listeTypeOperations.setLabel("Type d'opération réalisé");
        listeTypeOperations.setRenderer(new ComponentRenderer<>(typeOperation -> new Text(typeOperation.getNom())));
        listeTypeOperations.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);


        return listeTypeOperations;
    }

    private static NumberField createDureeField() {
        NumberField dureeField = new NumberField();
        dureeField.setLabel("Durée");
        dureeField.setSuffixComponent(new Div(new Text("min")));
        dureeField.setEnabled(false);
        dureeField.setValue(Double.valueOf(30));

        return dureeField;
    }

    private void enableOrDisableDureeField(NumberField numberField) {
        // Permet d'écouter les changements de valeur dans la sélection de types d'opérations
        // pour activer/désactiver le champ de sélection de durée
        this.typesOperationsRadioButtonGroup.addValueChangeListener(e -> {
            boolean machineSelected = !typesOperationsRadioButtonGroup.isEmpty();
            numberField.setEnabled(machineSelected);
        });
    }
}
