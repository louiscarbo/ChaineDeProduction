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

import fr.insa.developpement.model.classes.Machine;
import fr.insa.developpement.model.classes.Realise;
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
                    Dialog deleteTypeOperationDialog = createDeleteTypeOperationDialog(typeOperation);
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

    private Dialog createDeleteTypeOperationDialog(TypeOperation typeOperation) {
        Dialog dialog = new Dialog("Êtes vous sûr ?");
        dialog.setMaxWidth("400px");
        String body = typeOperation.hasMachines() ?
        new String("Ce type d'opération est lié à une machine. En supprimant ce type d'opération, vous supprimerez également toutes les machines liées à ce type d'opération. Êtes-vous sûr ?")
        : new String("Vous êtes sur le point de supprimer un type d'opération. En êtes vous sûr ?");
        dialog.add(body);

        Button confirmationButton = new Button(
            "Oui, supprimer",
            e -> {
                handleDeletion(typeOperation);
                dialog.close();
                this.refreshGrid();
            }
        );
        confirmationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        dialog.getFooter().add(new Button("Annuler", e-> dialog.close()));
        dialog.getFooter().add(confirmationButton);

        return dialog;
    }

    public static void handleDeletion(TypeOperation typeOperation) {
        try {
            if(typeOperation.hasMachines()) {
                Realise.deleteRealiseFromIdTypeOperation(typeOperation.getId());
                typeOperation.delete();
                for(Machine machine: typeOperation.getMachinesAssociees()) {
                    machine.delete();
                }
            } else {
                typeOperation.delete();
            }
            Notification succes = Notification.show("Type d'opération supprimé avec succès.");
            succes.addThemeVariants(NotificationVariant.LUMO_SUCCESS);    
        } catch(SQLException e) {
            Notification error = Notification.show("Une erreur est survenue lors de la suppression du type d'opération : " + e.getLocalizedMessage());
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    public void refreshGrid() {
        try {
            this.typeOperations = TypeOperation.getTypesOperations();
            grid.setItems(typeOperations);
            Notification.show("Liste des types d'opérations mise à jour avec succès.");
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
