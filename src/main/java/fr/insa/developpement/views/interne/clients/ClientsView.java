package fr.insa.developpement.views.interne.clients;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import fr.insa.developpement.model.classes.Client;
import fr.insa.developpement.views.HasRefreshGrid;
import fr.insa.developpement.views.interne.MainLayout;

@PageTitle("Clients")
@Route(value = "clients", layout = MainLayout.class)
@Uses(Icon.class)
public class ClientsView extends Div implements HasRefreshGrid {

    private Grid<Client> grid;
    private List<Client> clients = new ArrayList<>();

    public ClientsView() {
        setSizeFull();
        addClassNames("clients-view");

        HorizontalLayout hlayout = new HorizontalLayout(
            createAddClientButton(),
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
        grid = new Grid<>(Client.class, false);

        // Créations des colonnes de la grille
        grid.addColumn("id").setAutoWidth(true).setHeader("Identifiant");
        Grid.Column<Client> nomColumn = grid.addColumn("nom").setHeader("Nom");
        grid.addColumn(
            new ComponentRenderer<Button, Client>(Button::new, (button, client) -> {
                button.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_TERTIARY);
                button.addClickListener(e -> {
                    Dialog dialog = createDeleteClientDialog(client);
                    dialog.open();
                });
                button.setIcon(new Icon(VaadinIcon.TRASH));
            })
        ).setHeader("Supprimer");

        // Création d'un Binder et d'un Editor permettant l'édition de la grille
        Binder<Client> binder = new Binder<>(Client.class);
        Editor<Client> editor = grid.getEditor();
        editor.setBinder(binder);

        // TextField d'édition du nom du client
        TextField textField = new TextField();
        textField.setWidthFull();
        addCloseHandler(textField, editor);
        binder.forField(textField)
                .asRequired("Un nom doit être renseigné.")
                .bind(Client::getNom, Client::changeNom);
        nomColumn.setEditorComponent(textField);

        // Ecoute du double clic pour activer l'édition de la ligne
        grid.addItemDoubleClickListener(e -> {
            editor.editItem(e.getItem());
            Component editorComponent = e.getColumn().getEditorComponent();
            if (editorComponent instanceof Focusable) {
                ((Focusable<?>) editorComponent).focus();
            }
        });

        refreshCommandes();
        grid.setItems(clients);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private static void addCloseHandler(Component textField, Editor<Client> editor) {
        textField.getElement().addEventListener("keydown", e -> editor.cancel())
                .setFilter("event.code === 'Escape'");
    }

    private Dialog createDeleteClientDialog(Client client) {
        Dialog dialog = new Dialog("Êtes vous sûr ?");
        dialog.setMaxWidth("400px");
        dialog.add("Vous êtes sur le point de supprimer un client. Êtes-vous sûr ?");

        Button confirmationButton = new Button(
            "Oui, supprimer",
            e -> {
                handleDeletion(client);
                dialog.close();
                this.refreshGrid();
            }
        );
        confirmationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        dialog.getFooter().add(new Button("Annuler", e-> dialog.close()));
        dialog.getFooter().add(confirmationButton);

        return dialog;
    }

    private static void handleDeletion(Client client) {
        try {
            client.delete();
            Notification succes = Notification.show("Client supprimé avec succès.");
            succes.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch(SQLException e) {
            Notification error = Notification.show("Une erreur est survenue lors de la suppression du client : " + e.getLocalizedMessage());
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private Component createAddClientButton() {
        Button button = new Button(
            "Ajouter un client",
            new Icon(VaadinIcon.PLUS),
            e -> {
                Dialog dialog = new NewClientDialog(this);
                dialog.open();
            }
        );
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
            this.clients = Client.getClients();
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la récupération des clients depuis le serveur : " + exception.getLocalizedMessage());
        }
    }

    public void refreshGrid() {
        try {
            this.clients = Client.getClients();
            grid.setItems(clients);
            Notification.show("Liste des clients mise à jour avec succès.");
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la mise à jour de la liste des clients : " + exception.getLocalizedMessage());
        }
    }

}