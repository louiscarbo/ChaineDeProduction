package fr.insa.developpement.views.modele.produits;

import fr.insa.developpement.model.GestionBDD;
import fr.insa.developpement.model.classes.Produit;
import fr.insa.developpement.views.MainLayout;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Produits")
@Route(value = "produits", layout = MainLayout.class)
@Uses(Icon.class)
public class ProduitsView extends Div {

    private Grid<Produit> grid;
    private List<Produit> produits = new ArrayList<>();

    public ProduitsView() {
        setSizeFull();
        addClassNames("produits-view");

        HorizontalLayout hlayout = new HorizontalLayout(
            createAddProduitButton(),
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
        grid = new Grid<>(Produit.class, false);
        grid.addColumn("ref").setAutoWidth(true).setHeader("Référence");
        grid.addColumn("des").setAutoWidth(true).setHeader("Description");
        grid.addColumn(
            new ComponentRenderer<>(Button::new, (button, produit) -> {
                button.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_TERTIARY);
                button.addClickListener(e -> {
                    Dialog deleteProduitDialog = deleteProduitDialog(produit);
                    deleteProduitDialog.open();
                });
                button.setIcon(new Icon(VaadinIcon.TRASH));
            })
        ).setHeader("Supprimer");
        grid.addColumn(
        new ComponentRenderer<>(Button::new, (button, produit) -> {
            button.addThemeVariants(
                ButtonVariant.LUMO_CONTRAST,
                ButtonVariant.LUMO_PRIMARY);
            button.addClickListener(e -> {
                PlanFabricationIdealDialog dialog = new PlanFabricationIdealDialog();
                dialog.open();
            });
            button.setText("Calcul du plan");
        })
        ).setHeader("Plan de fabrication idéal");

        refreshProduits();
        grid.setItems(produits);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private Dialog deleteProduitDialog(Produit produit) {
        Dialog dialog = new Dialog("Êtes vous sûr ?");
        dialog.add("Vous êtes sur le point de supprimer un produit. En êtes vous sûr ?");

        Button confirmationButton = new Button(
            "Oui, supprimer",
            e -> {
                try {
                    produit.delete(GestionBDD.connectSurServeurM3());
                    dialog.close();
                    Notification.show("Machine supprimée avec succès.");
                    this.refreshGrid();
                } catch (SQLException e1) {
                    Notification.show(
                        "Une erreur est survenue lors de la suppresion de la machine : \n" + e1.getLocalizedMessage()
                    );
                }
            }
        );
        confirmationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        dialog.getFooter().add(new Button("Annuler", e-> dialog.close()));
        dialog.getFooter().add(confirmationButton);

        return dialog;
    }

    private Component createAddProduitButton() {

        Button button = new Button(
            "Ajouter un produit",
            new Icon(VaadinIcon.PLUS),
            e -> {
                Dialog dialog = new NewProduitDialog(this);
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

    private void refreshProduits() {
        try {
            this.produits = Produit.getProduitsFromServer();
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la récupération des produits depuis le serveur : " + exception.getLocalizedMessage());
        }
    }

    public void refreshGrid() {
        try {
            this.produits = Produit.getProduitsFromServer();
            grid.setItems(produits);
            Notification.show("Liste des produits mise à jour avec succès.");
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la mise à jour de la liste des produits : " + exception.getLocalizedMessage());
        }
    }

}
