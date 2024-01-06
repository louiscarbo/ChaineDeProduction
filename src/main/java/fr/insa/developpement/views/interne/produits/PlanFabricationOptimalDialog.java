package fr.insa.developpement.views.interne.produits;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Text;
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
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;
import com.vaadin.flow.theme.lumo.LumoUtility;

import fr.insa.developpement.model.classes.Commande;
import fr.insa.developpement.model.classes.Machine;
import fr.insa.developpement.model.classes.Produit;

public class PlanFabricationOptimalDialog extends Dialog {
    Produit produit;
    Commande commande;
    List<Machine> planDeFabricationOptimal = new ArrayList<Machine>();
    
    public PlanFabricationOptimalDialog(Produit produit) {
        this.produit = produit;

        this.setHeaderTitle("Plan de fabrication optimal");

        Button cancelButton = new Button("Annuler", e -> this.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button confirmationButton = new Button("OK", e -> this.close());
        confirmationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.getFooter().add(cancelButton, confirmationButton);

        Scroller scroller = new Scroller(createLayout());
        scroller.setScrollDirection(ScrollDirection.HORIZONTAL);
        scroller.setSizeFull();

        add(scroller);

        this.setMaxWidth("500px");
        this.setMaxHeight("600px");
    }

    public PlanFabricationOptimalDialog(Commande commande) {
        this.commande = commande;

        this.setHeaderTitle("Plan de fabrication optimal");

        Button cancelButton = new Button("Annuler", e -> this.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button confirmationButton = new Button("OK", e -> this.close());
        confirmationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.getFooter().add(cancelButton, confirmationButton);

        Scroller scroller = new Scroller(createLayoutForCommande());
        scroller.setScrollDirection(ScrollDirection.BOTH);
        scroller.setSizeFull();

        add(scroller);

        this.setMaxWidth("500px");
        this.setMaxHeight("600px");
    }

    private VerticalLayout createLayout() {
        try {
            planDeFabricationOptimal = produit.calculPlanDeFabricationIdeal();
        } catch (SQLException e) {
            Notification errorNotification = Notification.show(
                "Une erreur est survenue lors du calcul du plan de fabrication idéal : " + e.getLocalizedMessage()
            );
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        return createPlanDeFabricationLayout(planDeFabricationOptimal);
    }

    private VerticalLayout createLayoutForCommande() {
        VerticalLayout vlayout = new VerticalLayout();
        try {
            double dureeTotale = 0;
            for(Produit produit: new ArrayList<Produit>(commande.getProduitsQuantites().keySet())) {
                vlayout.add("Produit : " + produit.getDes() + " (" + produit.getRef() + ")");
                planDeFabricationOptimal = produit.calculPlanDeFabricationIdeal();
                vlayout.add(createPlanDeFabricationLayout(planDeFabricationOptimal));
                vlayout.add(new Hr());

                dureeTotale += Produit.calculDureeDePlanFabrication(planDeFabricationOptimal);
            }
            vlayout.add("Durée totale de fabrication de la commande : " + dureeTotale + " min");
        } catch (SQLException e) {
            Notification errorNotification = Notification.show(
                "Une erreur est survenue lors du calcul du plan de fabrication idéal : " + e.getLocalizedMessage()
            );
            errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        
        return vlayout;
    }

    public static VerticalLayout createPlanDeFabricationLayout(List<Machine> planDeFabricationOptimal) {
        HorizontalLayout hlayout = new HorizontalLayout();

        int numeroEtape = 1;
        for(Machine machine: planDeFabricationOptimal) {
            Div machineView = createMachineSpan(machine, numeroEtape);
            hlayout.add(machineView);
            numeroEtape ++;
        }

        VerticalLayout vLayout = new VerticalLayout(hlayout, new Text("Durée totale : " + String.valueOf(Produit.calculDureeDePlanFabrication(planDeFabricationOptimal) + " min")));
        vLayout.setSizeFull();

        return vLayout;
    }

    private static Div createMachineSpan(Machine machine, int numeroEtape) {
        Div machineSpan = new Div();

        VerticalLayout vlayout = new VerticalLayout();
        vlayout.setSizeFull();

        // Numéro de l'étape
        Span etape = new Span("Etape " + String.valueOf(numeroEtape));
        etape.getElement().getThemeList().add("badge");
        vlayout.add(etape);

        // Nom de la machine
        HorizontalLayout designation = createPropertyHorizontalLayout("Machine :", machine.getDes());
        designation.setAlignItems(FlexComponent.Alignment.BASELINE);
        designation.setWidthFull();
        vlayout.add(designation);

        // Référence de la machine
        HorizontalLayout reference = createPropertyHorizontalLayout("Référence :", machine.getRef());
        reference.setAlignItems(FlexComponent.Alignment.BASELINE);
        reference.setWidthFull();
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
        machineSpan.setMinWidth("120px");

        machineSpan.setSizeFull();

        return machineSpan;
    }

    private static HorizontalLayout createPropertyHorizontalLayout(String name, String value) {
        Span nameText = new Span(name);
        nameText.addClassName(LumoUtility.TextColor.SECONDARY);
        nameText.setWidthFull();
        Span valueSpan = new Span(value);
        valueSpan.getElement().getThemeList().add("badge contrast");

        HorizontalLayout layout = new HorizontalLayout(
            nameText,
            valueSpan
        );

        return layout;
    }

}
