package fr.insa.developpement.views.interne.commandes;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import fr.insa.developpement.model.classes.Client;
import fr.insa.developpement.model.classes.Commande;
import fr.insa.developpement.model.classes.Produit;
import fr.insa.developpement.views.HasRefreshGrid;
import fr.insa.developpement.views.externe.EspaceDeCommande;

public class NewCommandeDialog extends Dialog {

    private CheckboxGroup<Produit> produitsCheckbox;
    private RadioButtonGroup<Client> clientRadioButtonGroup;
    private DatePicker datePicker;
    private Button saveButton;

    private Map<Produit, Integer> produitsQuantite = new HashMap<>();

    private HasRefreshGrid parentView;

    private Optional<Client> clientConnecte;

    public NewCommandeDialog(HasRefreshGrid parentView, Optional<Client> clientConnecte) {
        this.parentView = parentView;
        this.clientConnecte = clientConnecte;

        this.setHeaderTitle("Nouvelle Commande");

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

    public NewCommandeDialog(EspaceDeCommande commandesView, Optional<Client> clientConnecte) {
        this.parentView = commandesView;
        this.clientConnecte = clientConnecte;

        this.setHeaderTitle("Nouvelle Commande");

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
        if(clientConnecte.isPresent()) {
            this.produitsCheckbox.addValueChangeListener(e -> {
                boolean allFieldsFilled = !produitsCheckbox.isEmpty();
                saveButton.setEnabled(allFieldsFilled);
            });
        } else {
            this.produitsCheckbox.addValueChangeListener(e -> {
                boolean allFieldsFilled = !datePicker.isEmpty() && !produitsCheckbox.isEmpty() && !clientRadioButtonGroup.isEmpty();
                saveButton.setEnabled(allFieldsFilled);
            });
            this.datePicker.addValueChangeListener(e -> {
                boolean allFieldsFilled = !datePicker.isEmpty() && !produitsCheckbox.isEmpty() && !clientRadioButtonGroup.isEmpty();
                saveButton.setEnabled(allFieldsFilled);
            });
            this.clientRadioButtonGroup.addValueChangeListener(e -> {
                boolean allFieldsFilled = !datePicker.isEmpty() && !produitsCheckbox.isEmpty() && !clientRadioButtonGroup.isEmpty();
                saveButton.setEnabled(allFieldsFilled);
            });
        }
    }

    private VerticalLayout createDialogLayout() {
        this.produitsCheckbox = createProduitsCheckboxGroup();
        this.clientRadioButtonGroup = createClientButtonGroup();
        this.datePicker = createDatePicker();

        VerticalLayout dialogLayout = new VerticalLayout();

        if(clientConnecte.isPresent()) {
            dialogLayout.add(produitsCheckbox);
        } else {
            dialogLayout.add(datePicker, produitsCheckbox, clientRadioButtonGroup);
        }

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
        Date date = Date.valueOf(clientConnecte.isPresent() ? LocalDate.now() : datePicker.getValue()) ;
        Client client = clientConnecte.isPresent() ? clientConnecte.get() : clientRadioButtonGroup.getValue();

        Commande newCommande = new Commande();
        newCommande.setDate(date);
        newCommande.setClient(client);
        newCommande.setProduitsQuantites(produitsQuantite);

        try {
            newCommande.save();
            Notification succesNotification = Notification.show("Nouvelle commande ajoutée avec succès.");
            succesNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (SQLException e1) {
            Notification errorNotification = Notification.show(
                "Une erreur est survenue lors de l'enregistrement de la commande sur le serveur :\n" + e1.getLocalizedMessage()
            );
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private static RadioButtonGroup<Client> createClientButtonGroup() {
        RadioButtonGroup<Client> listeClients = new RadioButtonGroup<Client>();
        List<Client> clients = new ArrayList<Client>();

        try {
            clients = Client.getClients();
        } catch(SQLException exc) {
            listeClients.setEnabled(false);
            Notification notification = Notification.show(
                "Erreur lors de la récupération des clients : " + exc.getLocalizedMessage()
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

        listeClients.setItems(clients);
        listeClients.setLabel("Client");
        listeClients.setRenderer(new ComponentRenderer<>(typeOperation -> new Text(typeOperation.getNom())));
        listeClients.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        return listeClients;
    }

    private CheckboxGroup<Produit> createProduitsCheckboxGroup() {
        CheckboxGroup<Produit> listeProduits = new CheckboxGroup<Produit>();
        List<Produit> produits = new ArrayList<Produit>();

        try {
            produits = Produit.getProduits();
        } catch(SQLException exc) {
            listeProduits.setEnabled(false);
            Notification notification = Notification.show(
                "Erreur lors de la récupération des produits : " + exc.getLocalizedMessage()
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

        listeProduits.setItems(produits);
        listeProduits.setLabel("Produits");
        listeProduits.setRenderer(new ComponentRenderer<HorizontalLayout, Produit>(produit -> {
            HorizontalLayout hlayout = new HorizontalLayout();

            Text name = new Text(produit.getDes() + " - " + produit.getRef());

            Select<Integer> quantitySelect = new Select<Integer>();
            List<Integer> quantityList = new ArrayList<Integer>();
            for (int i = 1; i < 21; i++) {
                quantityList.add(i);
            }
            quantitySelect.setItems(quantityList);
            quantitySelect.setMaxWidth("100px");
            quantitySelect.addValueChangeListener(e -> {
                if(!quantitySelect.isEmpty()) {
                    produitsQuantite.put(produit, quantitySelect.getValue());
                }
            });

            Span hSpacer = new Span();
            hSpacer.setMinWidth("50px");

            hlayout.add(name, hSpacer, quantitySelect);
            hlayout.setAlignItems(Alignment.BASELINE);
            return hlayout;
        }));
        listeProduits.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        listeProduits.setWidthFull();
        return listeProduits;
    }

    private static DatePicker createDatePicker() {
        DatePicker datePicker = new DatePicker("Date de commande");
        return datePicker;
    }
}
