package fr.insa.developpement.views.modele.operations;

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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import fr.insa.developpement.model.GestionBDD;
import fr.insa.developpement.model.classes.Machine;
import fr.insa.developpement.model.classes.TypeOperation;
import fr.insa.developpement.views.MainLayout;

@PageTitle("Types d'Opérations")
@Route(value = "type-operations", layout = MainLayout.class)
@Uses(Icon.class)

public class TypeOperationsView extends Div {

    private Grid<TypeOperation> grid;
    private List<TypeOperation> typeOperations = new ArrayList<>();

    public TypeOperationsView() {
        setSizeFull();
        addClassNames("operations-view");

        HorizontalLayout hlayout = new HorizontalLayout(createAddTypeOperationButton(), createRefreshGridButton());

        VerticalLayout layout = new VerticalLayout(hlayout, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        add(layout);
    }

    public void refreshTypeOperations() {
        try {
            this.typeOperations = TypeOperation.getTypesOperations();
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la récupération des types d'opérations depuis le serveur : " + exception.getLocalizedMessage());
        }
    }

    private Component createGrid() {
        grid = new Grid<>(TypeOperation.class, false);
        grid.addColumn("nom").setAutoWidth(true);
        grid.addColumn("des").setAutoWidth(true).setHeader("Description");
        grid.addColumn(new ComponentRenderer<>(typeOperation -> {
            return getMachineNamesLayoutForGrid(typeOperation);
        })).setHeader("Machines associées");
        grid.addColumn(
            new ComponentRenderer<>(Button::new, (button, typeOperation) -> {
                button.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_TERTIARY);
                button.addClickListener(e -> {
                    Dialog deleteTypeOperationDialog = deleteTypeOperationDialog(typeOperation);
                    deleteTypeOperationDialog.open();
                });
                button.setIcon(new Icon(VaadinIcon.TRASH));
            })
        ).setHeader("Supprimer");

        refreshTypeOperations();
        grid.setItems(typeOperations);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private static HorizontalLayout getMachineNamesLayoutForGrid(TypeOperation typeOperation) {
        String theme = String.format("badge %s","contrast");
        HorizontalLayout layout = new HorizontalLayout();

        List<String> listeNomsMachines = new ArrayList<String>();
        for(Machine machine : typeOperation.getMachinesAssociees()) {
            listeNomsMachines.add(machine.getRef());
        }

        for(String nomMachine: listeNomsMachines){
            Span newSpan = new Span();
            newSpan.getElement().setAttribute("theme", theme);
            newSpan.setText(nomMachine);
            layout.add(newSpan);
        }

        return layout;
    }

    private Dialog deleteTypeOperationDialog(TypeOperation typeOperation) {
        Dialog dialog = new Dialog("Êtes vous sûr ?");
        dialog.add("Vous êtes sur le point de supprimer un type d'opération. En êtes vous sûr ?");

        Button confirmationButton = new Button(
            "Oui, supprimer",
            e -> {
                try {
                    typeOperation.delete(GestionBDD.connectSurServeurM3());
                    dialog.close();
                    Notification.show("Type d'opération supprimé avec succès.");
                    this.refreshGrid();
                } catch (SQLException e1) {
                    Notification.show(
                        "Une erreur est survenue lors de la suppresion du type d'opération : \n" + e1.getLocalizedMessage()
                    );
                }
            }
        );
        confirmationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        dialog.getFooter().add(new Button("Annuler", e-> dialog.close()));
        dialog.getFooter().add(confirmationButton);

        return dialog;
    }

    public void refreshGrid() {
        try {
            this.typeOperations = TypeOperation.getTypesOperations();
            grid.setItems(typeOperations);
            Notification notification = Notification.show("Liste des types d'opérations mise à jour avec succès.");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch(SQLException exception) {
            Notification notification = Notification.show("Erreur lors de la mise à jour de la liste des machines : " + exception.getLocalizedMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
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

    private Component createAddTypeOperationButton() {

        Button button = new Button(
            "Ajouter un type d'opération",
            new Icon(VaadinIcon.PLUS),
            e -> {
                Dialog dialog = new NewOperationTypeDialog(this);
                dialog.open();
            }
        );

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getStyle().set("margin-left", "10px");
                
        return button;
    }

}
