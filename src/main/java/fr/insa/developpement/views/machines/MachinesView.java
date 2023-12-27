package fr.insa.developpement.views.machines;

import fr.insa.developpement.model.classes.Machine;
import fr.insa.developpement.views.main.MainLayout;
import com.vaadin.flow.component.dialog.Dialog;

import java.sql.SQLException;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Machines")
@Route(value = "machines", layout = MainLayout.class)
@Uses(Icon.class)
public class MachinesView extends Div {

    private Grid<Machine> grid;
    private List<Machine> machines;

    public MachinesView() {
        setSizeFull();
        addClassNames("machines-view");

        VerticalLayout layout = new VerticalLayout(createButton(), createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        add(layout);
    }

    private Component createGrid() {
        grid = new Grid<>(Machine.class, false);
        grid.addColumn("ref").setAutoWidth(true);
        grid.addColumn("des").setAutoWidth(true);
        grid.addColumn("puissance").setAutoWidth(true);

        refreshMachines();
        grid.setItems(machines);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private Component createButton() {
        Dialog dialog = new NewMachineDialog();

        Button button = new Button(
            "Ajouter une machine",
            new Icon(VaadinIcon.PLUS),
            e -> dialog.open()
        );

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getStyle().set("margin-left", "10px");
                
        return button;
    }

    private void refreshMachines() {
        try {
            this.machines = Machine.getMachinesFromServer();
            Notification.show("Machines récupérées depuis le serveur avec succès.");
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la récupération des machines depuis le serveur : " + exception.getLocalizedMessage());
        }
    }

}
