package fr.insa.developpement.views.operations;

import fr.insa.developpement.model.classes.TypeOperation;
import fr.insa.developpement.views.main.MainLayout;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Opérations")
@Route(value = "operations", layout = MainLayout.class)
@Uses(Icon.class)

public class OperationsView extends Div {

    private Grid<TypeOperation> grid;
    private List<TypeOperation> typeOperations = new ArrayList<>();

    public OperationsView() {
        setSizeFull();
        addClassNames("operations-view");

        VerticalLayout layout = new VerticalLayout(createButton(), createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        add(layout);
    }

    public void refreshTypeOperations() {
        try {
            this.typeOperations = TypeOperation.getTypeOperationsFromServer();
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la récupération des types d'opérations depuis le serveur : " + exception.getLocalizedMessage());
        }
    }

    private Component createGrid() {
        grid = new Grid<>(TypeOperation.class, false);
        grid.addColumn("nom").setAutoWidth(true);
        grid.addColumn("des").setAutoWidth(true);

        refreshTypeOperations();
        grid.setItems(typeOperations);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private void refreshGrid() {
        try {
            this.typeOperations = TypeOperation.getTypeOperationsFromServer();
            grid.setItems(typeOperations);
            Notification.show("Liste des types d'opérations mise à jour avec succès.");
        } catch(SQLException exception) {
            Notification.show("Erreur lors de la mise à jour de la liste des machines : " + exception.getLocalizedMessage());
        }
    }

    private Component createButton() {
        Dialog dialog = new NewOperationTypeDialog();

        Button button = new Button(
            "Ajouter un type d'opération",
            new Icon(VaadinIcon.PLUS),
            e -> dialog.open()
        );

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getStyle().set("margin-left", "10px");
                
        return button;
    }

}
