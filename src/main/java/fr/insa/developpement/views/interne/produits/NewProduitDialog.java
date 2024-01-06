package fr.insa.developpement.views.interne.produits;

import java.sql.SQLException;
import java.util.List;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import fr.insa.developpement.model.classes.Produit;
import fr.insa.developpement.model.classes.TypeOperation;

public class NewProduitDialog extends Dialog {

    private TextField referenceField;
    private TextField descriptionField;
    private Button saveButton;
    private OperationsGrids operationsGrids;

    private ProduitsView parentView;

    public NewProduitDialog(ProduitsView produitsView) {
        this.parentView = produitsView;
        this.setWidth("10000px");
        this.setHeaderTitle("Nouveau Produit");

        VerticalLayout dialogLayout = createDialogLayout();
        this.add(dialogLayout);

        this.saveButton = createSaveButton(this);
        this.saveButton.setEnabled(false);

        Button cancelButton = new Button("Annuler", e -> this.close());
        this.getFooter().add(cancelButton);
        this.getFooter().add(saveButton);
        this.setDraggable(true);

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
        this.referenceField = new TextField("Référence");
        this.descriptionField = new TextField("Description");
        Text label = new Text("Étapes de fabrication");
        this.operationsGrids = new OperationsGrids();

        VerticalLayout dialogLayout = new VerticalLayout(
            referenceField,
            descriptionField,
            label,
            operationsGrids
        );
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
                Produit newProduit = createProduit();
                try {
                    newProduit.save();
                    Notification succes = Notification.show("Produit ajouté avec succès");
                    succes.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (SQLException e1) {
                    Notification error = Notification.show(
                        "Une erreur est survenue lors de l'enregistrement du produit sur le serveur :\n" + e1.getLocalizedMessage()
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

    private Produit createProduit() {
        String ref = this.referenceField.getValue();
        String des = this.descriptionField.getValue();
        List<TypeOperation> planFabrication = this.operationsGrids.getOperationsFabrication();
    
        return new Produit(ref, des, planFabrication);
    }
    
}
