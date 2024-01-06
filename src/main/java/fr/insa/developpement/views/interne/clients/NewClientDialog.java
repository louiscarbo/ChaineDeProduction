package fr.insa.developpement.views.interne.clients;

import java.sql.SQLException;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import fr.insa.developpement.model.classes.Client;
import fr.insa.developpement.views.HasRefreshGrid;

public class NewClientDialog extends Dialog {

    private TextField nomTextField;
    private Button saveButton;

    private HasRefreshGrid parentView;

    public NewClientDialog(HasRefreshGrid parentView) {
        this.parentView = parentView;

        this.setHeaderTitle("Nouveau Client");

        VerticalLayout dialogLayout = createDialogLayout();
        this.add(dialogLayout);

        this.saveButton = createSaveButton(this);
        this.saveButton.setEnabled(false);
        
        Button cancelButton = new Button("Annuler", e -> this.close());
        this.getFooter().add(cancelButton);
        this.getFooter().add(saveButton);

        enableOrDisableSaveButton();
        this.setWidthFull();
        this.setMaxWidth("500px");
    }

    private void enableOrDisableSaveButton() {
        this.nomTextField.setValueChangeMode(ValueChangeMode.EAGER);
        this.nomTextField.addValueChangeListener(e -> {
            boolean allFieldsFilled = !nomTextField.isEmpty();
            saveButton.setEnabled(allFieldsFilled);
        });
    }

    private VerticalLayout createDialogLayout() {
        this.nomTextField = new TextField("Nom du client");

        VerticalLayout dialogLayout = new VerticalLayout();

        dialogLayout.add(nomTextField);

        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        dialogLayout.setSizeFull();

        return dialogLayout;
    }

    private Button createSaveButton(Dialog dialog) {
        Button saveButton = new Button(
            "Ajouter",
            e -> {
                createAndSaveCommande();
                dialog.close();
                parentView.refreshGrid();
            }
        );
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);

        return saveButton;
    }

    private void createAndSaveCommande() {
        Client newClient = new Client();
        newClient.setNom(nomTextField.getValue());

        try {
            newClient.save();
            Notification succesNotification = Notification.show("Nouveau client ajouté avec succès.");
            succesNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (SQLException e1) {
            Notification errorNotification = Notification.show(
                "Une erreur est survenue lors de l'enregistrement du client sur le serveur :\n" + e1.getLocalizedMessage()
            );
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
