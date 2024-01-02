package fr.insa.developpement.views.interne;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import fr.insa.developpement.model.GestionBDD;

@JsModule("prefers-color-scheme.js")
@PageTitle("Main")
@Route(value = "")
public class MainLayout extends AppLayout {
    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Chaîne de Production");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);

        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav modeleSideNav = new SideNav();
        modeleSideNav.setLabel("Usine");
        modeleSideNav.setCollapsible(true);
        modeleSideNav.addItem(new SideNavItem("Machines", "machines", VaadinIcon.FACTORY.create()));
        modeleSideNav.addItem(new SideNavItem("Types d'Opérations", "type-operations", VaadinIcon.COGS.create()));
        modeleSideNav.addItem(new SideNavItem("Produits", "produits", VaadinIcon.PACKAGE.create()));
        modeleSideNav.addItem(new SideNavItem("Opérateurs", "operateurs", VaadinIcon.GROUP.create()));
        modeleSideNav.addItem(new SideNavItem("Stock", "stock", VaadinIcon.STOCK.create()));
        modeleSideNav.addItem(new SideNavItem("Commandes", "commandes", VaadinIcon.INVOICE.create()));

        return modeleSideNav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.add(createResetButton());

        return layout;
    }

    private Button createResetButton() {
        Button resetButton = new Button("Réinitialiser");
        resetButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        resetButton.addSingleClickListener(clickEvent -> {
            createResetDialog().open();
        });
        resetButton.getStyle().set("margin-left", "10px");
        resetButton.getStyle().set("margin-bottom", "10px");


        return resetButton;
    }

    private Dialog createResetDialog() {
        Dialog resetDialog = new Dialog();

        Button cancelButton = new Button("Annuler", event -> resetDialog.close());

        Button confirmationButton = new Button("Oui, réinitialiser");
        confirmationButton.addSingleClickListener(event -> {
            resetDialog.close();
            GestionBDD.razBDD();
            UI.getCurrent().getPage().reload();
        });
        confirmationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        confirmationButton.addClickShortcut(Key.ENTER);

        resetDialog.setHeaderTitle("Êtes vous sûr ?");
        resetDialog.add(new Text("Vous êtes sur le point de réinitialiser toute la base de données. Êtes vous sûr ?"));
        resetDialog.getFooter().add(cancelButton);
        resetDialog.getFooter().add(confirmationButton);

        return resetDialog;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
