package fr.insa.developpement.views.externe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import fr.insa.developpement.model.classes.Commande;

// TODO Adapter pour que ça colle à la partie "client"
@PageTitle("Espace de Commande")
@Route("client-commande")
public class EspaceDeCommande extends VerticalLayout {

    private Grid<Commande> grid;
    private List<Commande> commandes = new ArrayList<>();

    public EspaceDeCommande() {
        setSizeFull();

        HorizontalLayout hlayout = new HorizontalLayout(
            createAddCommandeButton(),
            createRefreshGridButton()
        );

        VerticalLayout layout = new VerticalLayout(
            hlayout,
            createGrid()
        );
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        add(layout);
    }

    private Component createGrid() {
        grid = new Grid<>(Commande.class, false);

        // Créations des colonnes de la grille
        Grid.Column<Commande> dateColumn = grid.addColumn("date").setAutoWidth(true).setHeader("Date");
        Grid.Column<Commande> nomClientColumn = grid.addColumn("nomClient").setAutoWidth(true).setHeader("Client");

        // Création d'un Binder et d'un Editor permettant l'édition de la grille
        Binder<Commande> binder = new Binder<>(Commande.class);
        Editor<Commande> editor = grid.getEditor();
        editor.setBinder(binder);

        // DatePicker d'édition de la date de commande
        DatePicker datePicker = new DatePicker();
        datePicker.setWidthFull();
        addCloseHandler(datePicker, editor);
        binder.forField(datePicker)
                .asRequired("Une date doit être renseignée.")
                .bind(Commande::getLocalDate, Commande::changeDate);
        dateColumn.setEditorComponent(datePicker);

        // Textfield d'édition du nom du client
        TextField clientField = new TextField();
        clientField.setWidthFull();
        addCloseHandler(clientField, editor);
        binder.forField(clientField)
                .asRequired("Un nom doit être indiqué.")
                .bind(Commande::getNomClient, Commande::changeNomClient);
        nomClientColumn.setEditorComponent(clientField);

        // Ecoute du double clic pour activer l'édition de la ligne
        grid.addItemDoubleClickListener(e -> {
            editor.editItem(e.getItem());
            Component editorComponent = e.getColumn().getEditorComponent();
            if (editorComponent instanceof Focusable) {
                ((Focusable<?>) editorComponent).focus();
            }
        });

        refreshCommandes();
        grid.setItems(commandes);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private static void addCloseHandler(Component textField, Editor<Commande> editor) {
        textField.getElement().addEventListener("keydown", e -> editor.cancel())
                .setFilter("event.code === 'Escape'");
    }

    private Component createAddCommandeButton() {
        Button button = new Button(
            "Nouvelle commande",
            new Icon(VaadinIcon.PLUS),
            e -> {
                // TODO Créer un dialogue pour ajouter une nouvelle commande
                // Dialog dialog = new NewCommandeDialog(this);
                // dialog.open();
            }
        );
        button.addClassName("commandes-view-button-1");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getStyle().set("margin-left", "10px");
                
        return button;
    }

    private Component createRefreshGridButton() {
        Button button = new Button(
            "Actualiser la liste",
            new Icon(VaadinIcon.REFRESH),
            e -> refreshGrid()
        );
        button.getStyle().set("margin-left", "10px");

        return button;
    }

    private void refreshCommandes() {
        try {
            this.commandes = Commande.getCommandesForClient();
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la récupération des commandes depuis le serveur : " + exception.getLocalizedMessage());
        }
    }

    public void refreshGrid() {
        try {
            this.commandes = Commande.getCommandesForClient();
            grid.setItems(commandes);
            Notification.show("Liste des commandes mise à jour avec succès.");
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la mise à jour de la liste des commandes : " + exception.getLocalizedMessage());
        }
    }

}
