package fr.insa.developpement.views.produits;

import fr.insa.developpement.model.classes.Produit;
import fr.insa.developpement.views.main.MainLayout;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Produits")
@Route(value = "produits", layout = MainLayout.class)
@Uses(Icon.class)
public class ProduitsView extends Div {

    private Grid<Produit> grid;

    public ProduitsView() {
        setSizeFull();
        addClassNames("produits-view");

        VerticalLayout layout = new VerticalLayout(createButton(), createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private Component createGrid() {
        grid = new Grid<>(Produit.class, false);
        grid.addColumn("nom").setAutoWidth(true);
        grid.addColumn("description").setAutoWidth(true);

        grid.setItems();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private Component createButton() {
        Dialog dialog = new NewProduitDialog();

        Button button = new Button(
            "Ajouter un produit",
            new Icon(VaadinIcon.PLUS),
            e -> dialog.open()
        );

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getStyle().set("margin-left", "10px");
                
        return button;
    }

}
