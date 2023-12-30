package fr.insa.developpement.views.modele.produits;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import fr.insa.developpement.model.classes.TypeOperation;

public class OperationsGrids extends SplitLayout {

    Grid<TypeOperation> firstGrid;
    Grid<TypeOperation> secondGrid;
    List<TypeOperation> typesOperationsSecondGrid = new ArrayList<TypeOperation>();

    public OperationsGrids() {
        firstGrid = createSelectionGrid(); 
        secondGrid = createOrderingGrid();

        this.addToPrimary(firstGrid);
        this.addToSecondary(secondGrid);
        this.setSplitterPosition(40);
    }

    private Grid<TypeOperation> createSelectionGrid() {
        List<TypeOperation> typeOperations = new ArrayList<TypeOperation>();
        try {
            typeOperations = TypeOperation.getTypeOperationsFromServer();
        } catch (SQLException e) {
            Notification errorNotification = Notification.show(
                "Erreur lors de la récupération des types d'opérations depuis la base de données. " + e.getLocalizedMessage()
            );
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

        Grid<TypeOperation> grid = new Grid<TypeOperation>(TypeOperation.class, false);
        grid.addColumn("nom").setAutoWidth(true);
        grid.addColumn("des").setAutoWidth(true).setHeader("Description");

        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addSelectionListener(selection -> {
            typesOperationsSecondGrid = new ArrayList<>(selection.getAllSelectedItems());
            secondGrid.setItems(typesOperationsSecondGrid);
        });

        grid.setItems(typeOperations);

        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        return grid;
    }

    private Grid<TypeOperation> createOrderingGrid() {
        Grid<TypeOperation> grid = new Grid<>(TypeOperation.class, false);

        grid.addColumn("nom").setAutoWidth(true);
        grid.addColumn("des").setAutoWidth(true).setHeader("Description");
        grid.addColumn(new ComponentRenderer<Span, TypeOperation>(typeOperation -> {
            return this.getOperationIndex(typeOperation);
        })).setHeader("Position");

        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        return grid;
    }

    private Span getOperationIndex(TypeOperation typeOperation) {
        Span span = new Span();
        span.setText("");
        
        for(int i = 0; i < this.typesOperationsSecondGrid.size(); i++) {
            if(typesOperationsSecondGrid.get(i) == typeOperation) {
                span.getElement().setAttribute("theme", String.format("badge %s","contrast"));
                span.setText(String.valueOf(i+1));
                return span;
            }
        }

        return span;
    }
}
