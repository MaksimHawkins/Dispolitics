package sg.skylvsme.dispolitics.view;


import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.var;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import sg.skylvsme.dispolitics.messaging.LobbyBroadcaster;

@Route("login")
@PageTitle("Вход | Dispolitics")
public class LoginView extends VerticalLayout {

    private static final String OAUTH_URL = "/oauth2/authorization/discord";

    private Image image;
    private H3 welcome;

    public LoginView() {
        Anchor loginButton = new Anchor(OAUTH_URL, "Войти с помощью Discord");
        add(loginButton);

        Anchor logoutButton = new Anchor("/logout", "Выход");
        add(logoutButton);

        setSizeFull();

        image = new Image();
        welcome = new H3("Welcome...");
        add(welcome);
        add(image);

        Button sendNotification = new Button();
        sendNotification.addClickListener(buttonClickEvent -> {
            LobbyBroadcaster.broadcast("123");
        });

        add(sendNotification);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        var user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user instanceof DefaultOAuth2User) {
            DefaultOAuth2User user2 = (DefaultOAuth2User) user;
            welcome.setText("Welcome, " + user2.getName());

            String avatar = user2.getAttribute("avatar");
            if (avatar != null) {
                image.setSrc(avatar);
            }
        }
    }
}
