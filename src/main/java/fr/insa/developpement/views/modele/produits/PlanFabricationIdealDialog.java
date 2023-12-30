package fr.insa.developpement.views.modele.produits;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

// TODO a finir
public class PlanFabricationIdealDialog extends Dialog {
    
    public PlanFabricationIdealDialog() {
        this.setHeaderTitle("Plan de fabrication optimal");

        Button cancelButton = new Button("Annuler", e -> this.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button confirmationButton = new Button("OK", e -> this.close());
        confirmationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        this.getFooter().add(cancelButton, confirmationButton);

        add(createLayout());
    }

    private static VerticalLayout createLayout() {
        VerticalLayout vlayout = new VerticalLayout();
        vlayout.add(new Text("Fenêtre à réaliser"));
        
        return vlayout;
    }

}
