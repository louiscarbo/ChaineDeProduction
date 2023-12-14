package com.example.application.views.main.operations;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;

public class NewOperationDialog extends Dialog {
    
    public NewOperationDialog() {
        this.setHeaderTitle("Nouvelle Opération");

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
        //TODO Ajouter un picker pour le type d'opération
        //TODO Ajouter un truc pour la durée de réalisation du type d'opération

        VerticalLayout dialogLayout = new VerticalLayout(firstNameField, lastNameField);
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
}
