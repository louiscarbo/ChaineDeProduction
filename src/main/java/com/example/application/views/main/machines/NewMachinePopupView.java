package com.example.application.views.main.machines;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = "nouvellemachine")
public class NewMachinePopupView extends Div {

    public void NewMachinePopupView() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Nouvelle Machine");

        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(dialogLayout);

        Button saveButton = createSaveButton(dialog);
        Button cancelButton = new Button("Annuler", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        Button button = new Button("Ajouter une machine", e -> dialog.open());

        add(dialog, button);

        dialog.open();
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
