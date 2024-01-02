package fr.insa.developpement.views.externe;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Espace de Commande")
@Route("client-commande")
public class EspaceDeCommande extends VerticalLayout {

    public EspaceDeCommande() {
        this.add("Espace de commande à venir");
    }
}
