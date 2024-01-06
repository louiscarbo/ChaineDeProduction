package fr.insa.developpement.views.externe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import fr.insa.developpement.model.classes.Client;
import fr.insa.developpement.model.classes.Commande;
import fr.insa.developpement.views.HasRefreshGrid;
import fr.insa.developpement.views.interne.commandes.NewCommandeDialog;

// TODO Adapter pour que ça colle à la partie "client"
@PageTitle("Espace de Commande")
@Route("client-commande")
public class EspaceDeCommande extends VerticalLayout implements HasRefreshGrid, HasUrlParameter<String> {
    
    private Client client;
    private Grid<Commande> grid;
    private List<Commande> commandes = new ArrayList<>();

    public EspaceDeCommande() {
        createLayout();        
    }

    private void createLayout() {
        this.removeAll();
        this.setSizeFull();
        this.setSpacing(false);
        this.setPadding(false);

        Div hSpacer = new Div();
        hSpacer.setSizeFull();

        String clientName = client == null ? "Client Inexistant" : client.getNom();
        Span clientNameBadge = new Span(clientName);
        clientNameBadge.getElement().getThemeList().add("badge");

        HorizontalLayout hlayout = new HorizontalLayout(
            createAddCommandeButton(),
            createRefreshGridButton(),
            hSpacer,
            clientNameBadge
        );
        hlayout.setWidthFull();
        hlayout.setPadding(true);

        this.add(hlayout, createGrid());
    }

    private Component createGrid() {
        grid = new Grid<>(Commande.class, false);

        // Créations des colonnes de la grille
        Grid.Column<Commande> dateColumn = grid.addColumn("date").setAutoWidth(true).setHeader("Date");
        grid.addColumn(new ComponentRenderer<Text, Commande>(commande -> {
            return new Text(commande.getClient().getNom());
        })).setHeader("Client");

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
            "Passer commande",
            new Icon(VaadinIcon.PLUS),
            e -> {
                Dialog dialog = new NewCommandeDialog(this, Optional.of(client));
                dialog.open();
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
            this.commandes = Commande.getCommandesForClient(client);
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la récupération des commandes depuis le serveur : " + exception.getLocalizedMessage());
        }
    }

    public void refreshGrid() {
        try {
            this.commandes = Commande.getCommandesForClient(client);
            grid.setItems(commandes);
            Notification.show("Liste des commandes mise à jour avec succès.");
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la mise à jour de la liste des commandes : " + exception.getLocalizedMessage());
        }
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        try {
            int idClient = Integer.parseInt(parameter);
            this.client = Client.getClientFromId(idClient);
            this.createLayout();
        } catch (Exception e) {
            Notification error = Notification.show("Impossible de récupérer les commandes. L'identifiant client doit être un nombre : " + e.getLocalizedMessage());
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        refreshGrid();
    }

}
