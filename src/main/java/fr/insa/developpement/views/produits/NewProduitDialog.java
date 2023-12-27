package fr.insa.developpement.views.produits;

import java.sql.SQLException;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import fr.insa.developpement.model.GestionBDD;
import fr.insa.developpement.model.classes.Produit;

public class NewProduitDialog extends Dialog {

    private TextField referenceField;
    private TextField descriptionField;
    private Button saveButton;

    private ProduitsView parentView;

    public NewProduitDialog(ProduitsView produitsView) {
        this.parentView = produitsView;

        this.setHeaderTitle("Nouveau Produit");

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
            boolean allFieldsFilled = !referenceField.isEmpty() && !descriptionField.isEmpty();
            saveButton.setEnabled(allFieldsFilled);
        });
        this.descriptionField.setValueChangeMode(ValueChangeMode.EAGER);
        this.descriptionField.addValueChangeListener(e -> {
            boolean allFieldsFilled = !referenceField.isEmpty() && !descriptionField.isEmpty();
            saveButton.setEnabled(allFieldsFilled);
        });
    }

    private VerticalLayout createDialogLayout() {
        this.referenceField = new TextField("Nom");
        this.descriptionField = new TextField("Description");

        VerticalLayout dialogLayout = new VerticalLayout(
            referenceField,
            descriptionField
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
                Produit newProduit = createProduit();
                try {
                    newProduit.save(GestionBDD.connectSurServeurM3());
                    Notification.show("Produit ajouté avec succès");
                } catch (SQLException e1) {
                    Notification.show(
                        "Une erreur est survenue lors de l'enregistrement du produit sur le serveur :\n" + e1.getLocalizedMessage()
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
        this.referenceField.setValue("");
        this.descriptionField.setValue("");
    }

    private Produit createProduit() {
        String ref = this.referenceField.getValue();
        String des = this.descriptionField.getValue();
    
        return new Produit(ref, des);
    }
    
}
