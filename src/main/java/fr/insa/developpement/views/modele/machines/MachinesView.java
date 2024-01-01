package fr.insa.developpement.views.modele.machines;

import fr.insa.developpement.model.classes.Machine;
import fr.insa.developpement.model.classes.Realise;
import fr.insa.developpement.model.classes.TypeOperation;
import fr.insa.developpement.views.MainLayout;

import com.vaadin.flow.component.dialog.Dialog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
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

@PageTitle("Machines")
@Route(value = "machines", layout = MainLayout.class)
@Uses(Icon.class)
public class MachinesView extends Div {

    private Grid<Machine> grid;
    private List<Machine> machines = new ArrayList<>();

    public MachinesView() {
        setSizeFull();
        addClassNames("machines-view");

        HorizontalLayout hlayout = new HorizontalLayout(
            createAddMachineButton(),
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
        grid = new Grid<>(Machine.class, false);
        grid.addColumn("ref").setAutoWidth(true).setHeader("Référence");
        grid.addColumn("des").setAutoWidth(true).setHeader("Description");
        grid.addColumn("puissance").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(machine -> {
            return getOperationNameForGrid(machine);
        })).setHeader("Opération réalisée");
        grid.addColumn(
            new ComponentRenderer<>(Button::new, (button, machine) -> {
                button.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_TERTIARY);
                button.addClickListener(e -> {
                    Dialog deleteMachineDialog = createDeleteMachineDialog(machine);
                    deleteMachineDialog.open();
                });
                button.setIcon(new Icon(VaadinIcon.TRASH));
            })
        ).setHeader("Supprimer");

        refreshMachines();
        grid.setItems(machines);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private static Span getOperationNameForGrid(Machine machine) {
        Span span = new Span();
        span.setText("");

        if(machine.hasTypeOperation()) {
            TypeOperation typeOperation = machine.getTypeOperation();
            String nomTypeOperation = typeOperation.getNom();

            span.getElement().setAttribute("theme", String.format("badge %s","contrast"));
            span.setText(nomTypeOperation + " - " + machine.getDureeTypeOperation() + " min");
            return span;
        }

        return span;
    }

    private Dialog createDeleteMachineDialog(Machine machine) {
        Dialog dialog = new Dialog("Êtes vous sûr ?");
        dialog.setMaxWidth("400px");
        String body = machine.hasTypeOperation()?
            new String("Cette machine est liée à un type d'opération. En supprimant cette machine, vous supprimerez également le type d'opération lié à cette machine. Êtes-vous sûr ?")
            : new String("Vous êtes sur le point de supprimer une machine. En êtes vous sûr ?");
        dialog.add(body);

        Button confirmationButton = new Button(
            "Oui, supprimer",
            e -> {
                handleDeletion(machine);
                dialog.close();
                this.refreshGrid();
            }
        );
        confirmationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        dialog.getFooter().add(new Button("Annuler", e-> dialog.close()));
        dialog.getFooter().add(confirmationButton);

        return dialog;
    }

    private static void handleDeletion(Machine machine) {
        try {
            if(machine.hasTypeOperation()){
                Realise.deleteRealiseFromIdMachine(machine.getId());
                machine.getTypeOperation().delete();
                machine.delete();
            } else {
                machine.delete();
            }
            Notification succes = Notification.show("Machine supprimée avec succès.");
            succes.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch(SQLException e) {
            Notification error = Notification.show("Une erreur est survenue lors de la suppression de la machine : " + e.getLocalizedMessage());
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private Component createAddMachineButton() {

        Button button = new Button(
            "Ajouter une machine",
            new Icon(VaadinIcon.PLUS),
            e -> {
                Dialog dialog = new NewMachineDialog(this);
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

    private void refreshMachines() {
        try {
            this.machines = Machine.getMachines();
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la récupération des machines depuis le serveur : " + exception.getLocalizedMessage());
        }
    }

    public void refreshGrid() {
        try {
            this.machines = Machine.getMachines();
            grid.setItems(machines);
            Notification.show("Liste des machines mise à jour avec succès.");
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la mise à jour de la liste des machines : " + exception.getLocalizedMessage());
        }
    }

}
