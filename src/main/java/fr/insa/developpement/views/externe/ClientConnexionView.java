package fr.insa.developpement.views.externe;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Client Connexion")
@Route(value = "client-connexion")
@Uses(Icon.class)
public class ClientConnexionView extends Div {

    public ClientConnexionView() {
        LoginI18n i18n = LoginI18n.createDefault();

        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setTitle("Connexion");
        i18nForm.setUsername("Identifiant d'entreprise");
        i18nForm.setPassword("Mot de passe");
        i18nForm.setSubmit("Connexion");
        i18n.setForm(i18nForm);

        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle("Identifiant ou mot de passe incorrect");
        i18nErrorMessage.setMessage(
                "Votre identifiant ou votre mot de passe est incorrect. En cas de difficultés, contactez votre représentant.");
        i18n.setErrorMessage(i18nErrorMessage);

        LoginOverlay loginOverlay = new LoginOverlay();
        loginOverlay.setTitle("Nom de l'entreprise");
        loginOverlay.setDescription("Espace de commande");
        
        loginOverlay.setForgotPasswordButtonVisible(false);
        loginOverlay.setI18n(i18n);
        loginOverlay.addLoginListener(e -> {
            UI.getCurrent().navigate("test");
        });

        add(loginOverlay);
        loginOverlay.setOpened(true);
    }
}
