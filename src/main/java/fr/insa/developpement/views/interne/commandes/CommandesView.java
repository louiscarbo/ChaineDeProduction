package fr.insa.developpement.views.interne.commandes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import fr.insa.developpement.model.classes.Client;
import fr.insa.developpement.model.classes.Commande;
import fr.insa.developpement.views.HasRefreshGrid;
import fr.insa.developpement.views.interne.MainLayout;

@PageTitle("Commandes")
@Route(value = "commandes", layout = MainLayout.class)
@Uses(Icon.class)
public class CommandesView extends Div implements HasRefreshGrid {

    private Grid<Commande> grid;
    private List<Commande> commandes = new ArrayList<>();

    public CommandesView() {
        setSizeFull();
        addClassNames("commandes-view");

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
        grid.addColumn(new ComponentRenderer<Text, Commande>(commande -> {
            return new Text(commande.getClient().getNom());
        })).setHeader("Client");
        grid.addColumn(new ComponentRenderer<Text, Commande>(commande -> {
            return new Text(String.valueOf(commande.getClient().getId()));
        })).setHeader("Identifiant Client");
        grid.addColumn(new ComponentRenderer<Span, Commande>(commande -> {
            Span span = new Span();
            if (commande.isTermine()) {
                span.add("Terminée");
                span.getElement().getThemeList().add("badge success");
                return span;
            } else {
                span.add("Non traitée");
                span.getElement().getThemeList().add("badge contrast");
                return span;
            }
        })).setHeader("Etat");
        grid.addColumn(
            new ComponentRenderer<Button, Commande>(commande -> {
                return createConfirmerCommandeButton(commande);
            })
        ).setHeader("Terminer");
        grid.addColumn(
            new ComponentRenderer<Button, Commande>(Button::new, (button, commande) -> {
                button.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_TERTIARY);
                button.addClickListener(e -> {
                    Dialog deleteCommandeDialog = createDeleteCommandeDialog(commande);
                    deleteCommandeDialog.open();
                });
                button.setIcon(new Icon(VaadinIcon.TRASH));
            })
        ).setHeader("Supprimer");

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

    private Button createConfirmerCommandeButton(Commande commande) {
        Button button = new Button();
        button.addThemeVariants(ButtonVariant.LUMO_ICON,
            ButtonVariant.LUMO_SUCCESS,
            ButtonVariant.LUMO_TERTIARY);
        button.addClickListener(e -> {
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Marquer comme terminée");
            dialog.setText("Vous êtes sur le point de marquer cette commande comme terminée. Êtes-vous sûr ?");

            dialog.setCancelable(true);
            dialog.setCancelText("Annuler");

            dialog.setConfirmText("Oui");
            dialog.addConfirmListener(event -> {
                commande.changeTermine(true);
                refreshGrid();
            });

            dialog.open();
        });
        button.setIcon(new Icon(VaadinIcon.CHECK));
        if(!commande.isTermine()) {
            return button;
        } else {
            button.setVisible(false);
            button.setEnabled(false);
            return button;
        }
    }

    private static void addCloseHandler(Component textField, Editor<Commande> editor) {
        textField.getElement().addEventListener("keydown", e -> editor.cancel())
                .setFilter("event.code === 'Escape'");
    }

    private Dialog createDeleteCommandeDialog(Commande commande) {
        Dialog dialog = new Dialog("Êtes vous sûr ?");
        dialog.setMaxWidth("400px");
        dialog.add("Vous êtes sur le point de supprimer une commande. Êtes-vous sûr ?");

        Button confirmationButton = new Button(
            "Oui, supprimer",
            e -> {
                handleDeletion(commande);
                dialog.close();
                this.refreshGrid();
            }
        );
        confirmationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        dialog.getFooter().add(new Button("Annuler", e-> dialog.close()));
        dialog.getFooter().add(confirmationButton);

        return dialog;
    }

    private static void handleDeletion(Commande commande) {
        try {
            commande.delete();
            Notification succes = Notification.show("Commande supprimée avec succès.");
            succes.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch(SQLException e) {
            Notification error = Notification.show("Une erreur est survenue lors de la suppression de la commande : " + e.getLocalizedMessage());
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private Component createAddCommandeButton() {

        Button button = new Button(
            "Ajouter une commande",
            new Icon(VaadinIcon.PLUS),
            e -> {
                Optional<Client> optionalClient = Optional.empty();
                Dialog dialog = new NewCommandeDialog(this, optionalClient);
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
            this.commandes = Commande.getCommandes();
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la récupération des commandes depuis le serveur : " + exception.getLocalizedMessage());
        }
    }

    public void refreshGrid() {
        try {
            this.commandes = Commande.getCommandes();
            grid.setItems(commandes);
            Notification.show("Liste des commandes mise à jour avec succès.");
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la mise à jour de la liste des commandes : " + exception.getLocalizedMessage());
        }
    }

}