package fr.insa.developpement.views.main;

import java.sql.Connection;
import java.sql.SQLException;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import fr.insa.developpement.model.GestionBDD;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.model.Dial;
import com.vaadin.flow.component.dialog.Dialog;

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
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Machines", "machines"));
        nav.addItem(new SideNavItem("Opérations", "operations"));
        nav.addItem(new SideNavItem("Produits", "produits"));

        return nav;
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

        return resetButton;
    }

    private Dialog createResetDialog() {
        Dialog resetDialog = new Dialog();

        Button cancelButton = new Button("Annuler", event -> resetDialog.close());
        Button confirmationButton = new Button("Oui, réinitialiser");
        confirmationButton.addSingleClickListener(event -> {
            resetDialog.close();
            try (Connection connection = GestionBDD.connectSurServeurM3()) {
                GestionBDD gestionBDD = new GestionBDD(connection);
                gestionBDD.razBDD();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        confirmationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

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
