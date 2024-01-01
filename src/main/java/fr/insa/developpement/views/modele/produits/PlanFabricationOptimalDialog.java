package fr.insa.developpement.views.modele.produits;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import fr.insa.developpement.model.classes.Machine;
import fr.insa.developpement.model.classes.Produit;

public class PlanFabricationOptimalDialog extends Dialog {
    Produit produit;
    
    public PlanFabricationOptimalDialog(Produit produit) {
        this.produit = produit;
        this.setHeaderTitle("Plan de fabrication optimal");

        Button cancelButton = new Button("Annuler", e -> this.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button confirmationButton = new Button("OK", e -> this.close());
        confirmationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        this.getFooter().add(cancelButton, confirmationButton);

        Scroller scroller = new Scroller(createLayout());

        add(scroller);
    }

    private VerticalLayout createLayout() {
        List<Machine> planDeFabricationOptimal = new ArrayList<Machine>();
        try {
            planDeFabricationOptimal = produit.calculPlanDeFabricationIdeal();
        } catch (SQLException e) {
            Notification errorNotification = Notification.show(
                "Une erreur est survenue lors du calcul du plan de fabrication idéal : " + e.getLocalizedMessage()
            );
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        VerticalLayout vlayout = new VerticalLayout();

        for(Machine machine: planDeFabricationOptimal) {
            Div machineView = createMachineSpan(machine);
            this.add(machineView);
        }

        vlayout.setPadding(true);
        
        return vlayout;
    }

    private Div createMachineSpan(Machine machine) {
        Div machineSpan = new Div();

        VerticalLayout vlayout = new VerticalLayout();

        // Nom de la machine
        HorizontalLayout designation = createPropertyHorizontalLayout("Machine :", machine.getDes());
        designation.setAlignItems(FlexComponent.Alignment.BASELINE);
        vlayout.add(designation);

        // Référence de la machine
        HorizontalLayout reference = createPropertyHorizontalLayout("Référence :", machine.getRef());
        reference.setAlignItems(FlexComponent.Alignment.BASELINE);
        vlayout.add(reference);

        // Ajouter un divider
        vlayout.add(new Hr());

        // Operation réalisée
        String operationName = machine.getTypeOperation().getNom();
        HorizontalLayout operation = createPropertyHorizontalLayout("Opération :", operationName);
        operation.setAlignItems(FlexComponent.Alignment.BASELINE);
        vlayout.add(operation);

        // Durée de l'opération
        Double duration = machine.getDureeTypeOperation();
        String durationText = String.format("%.2f", duration) + " min";
        HorizontalLayout duree = createPropertyHorizontalLayout("Durée :", durationText);
        duree.setAlignItems(FlexComponent.Alignment.BASELINE);
        vlayout.add(duree);

        machineSpan.add(vlayout);
        machineSpan.addClassName(LumoUtility.BorderRadius.LARGE);
        machineSpan.addClassName(LumoUtility.Background.CONTRAST_10);
        machineSpan.addClassName(LumoUtility.Margin.MEDIUM);

        return machineSpan;
    }

    private static HorizontalLayout createPropertyHorizontalLayout(String name, String value) {
        Span nameText = new Span(name);
        nameText.addClassName(LumoUtility.TextColor.SECONDARY);
        Span valueSpan = new Span(value);
        valueSpan.getElement().getThemeList().add("badge contrast");

        HorizontalLayout layout = new HorizontalLayout(
            nameText,
            valueSpan
        );

        return layout;
    }

}
