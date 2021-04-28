package sg.skylvsme.dispolitics;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.shared.Registration;
import lombok.SneakyThrows;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.List;

@Push
@Route("lobby")
@PageTitle("Лобби | Dispolitics")
public class LobbyView extends HorizontalLayout implements PageConfigurator {

    Registration broadcasterRegistration;

    public Game game;
    public static List<LobbyUser> lobbyUsers = new ArrayList<>();

    private VerticalLayout usersLayout;
    private Checkbox isReadyCheckBox;

    private LobbyUser currentLobbyUser;

    public LobbyView() {
        currentLobbyUser = new LobbyUser(SecurityConfiguration.getCurrentUser());

        game = Game.INSTANCE;

        setId("LobbyView");

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout middleLayout = new HorizontalLayout();
        middleLayout.setAlignItems(Alignment.CENTER);
        middleLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        usersLayout = new VerticalLayout();
        usersLayout.getStyle()
                .set("border" , "1px solid #ccc")
                .set("border-radious", "3px");
        usersLayout.setWidth("100%");
        usersLayout.setMaxWidth("450px");

        middleLayout.add(usersLayout);

        VerticalLayout countriesLayout = new VerticalLayout();
        countriesLayout.setWidth("15%");
        countriesLayout.getStyle()
                .set("border", "1px solid #ccc")
                .set("border-radious", "3px");

        middleLayout.add(countriesLayout);

        add(middleLayout);

        isReadyCheckBox = new Checkbox("Я готов");
        isReadyCheckBox.addValueChangeListener(changeEvent -> changeReady());
        middleLayout.add(isReadyCheckBox);

        addDetachListener(e -> {
            sendUpdateUsers();
        });

    }

    public HorizontalLayout userLayout(LobbyUser user) {
        HorizontalLayout layout = new HorizontalLayout();

        layout.setWidthFull();

        Label label = new Label(user.getOAuth2User().getName());
        label.getStyle().set("font-size", "x-large");
        layout.add(label);

        Div div = new Div();
        div.getStyle()
                .set("background", user.isReady() ? "lightgreen" : "lightcoral")
                .set("border-radius", "50%");
        div.setWidth("40px");

        Image image = new Image();
        image.setWidth("40px");
        image.getStyle()
                .set("border-radius", "50%");
        if (user.getOAuth2User().getAttribute("avatar") != null)
            image.setSrc(user.getOAuth2User().getAttribute("avatar").toString());
        layout.add(image);

        layout.add(div);
        layout.expand(label);

        return layout;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();

        lobbyUsers.add(currentLobbyUser);

        sendUpdateUsers();

        broadcasterRegistration = LobbyBroadcaster.register(newMessage -> ui.access(() -> {
            updateUsers();
        }));

        updateUsers();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        lobbyUsers.remove(currentLobbyUser);

        sendUpdateUsers();

        unsubscribeBroadcaster();
    }

    //@SneakyThrows
    private void updateUsers() {
        usersLayout.removeAll();
        for (LobbyUser user : lobbyUsers) {
            usersLayout.add(userLayout(user));
        }

        if (allReady()) {
            unsubscribeBroadcaster();
            gotoGame();
        }
    }

    @Override
    public void configurePage(InitialPageSettings settings) {
        String script = "window.onbeforeunload = function (e) { var e = e || window.event; document.getElementById(\"LobbyView\").$server.browserIsLeaving(); return; };";
        settings.addInlineWithContents(InitialPageSettings.Position.PREPEND, script, InitialPageSettings.WrapMode.JAVASCRIPT);
    }

    @ClientCallable
    public void browserIsLeaving() {
        onDetach(null);
    }

    private void changeReady() {
        currentLobbyUser.setReady(isReadyCheckBox.getValue());
        sendUpdateUsers();
    }

    private boolean allReady() {
        for (LobbyUser lobbyUser : lobbyUsers) {
            if (!lobbyUser.isReady()) return false;
        }
        return true;
    }

    private boolean allReadyExceptMe() {
        for (LobbyUser lobbyUser : lobbyUsers) {
            if (lobbyUser != currentLobbyUser)
                if (!lobbyUser.isReady())
                return false;
        }
        return true;
    }

    private void gotoGame() {
        getUI().ifPresent(ui -> {
            ui.navigate("game");
        });
    }

    private LobbyUser findByOAuthUser(OAuth2User user) {
        for (LobbyUser lobbyUser : lobbyUsers) {
            if (lobbyUser.getOAuth2User() == user) return lobbyUser;
        }
        return null;
    }

    private void unsubscribeBroadcaster() {
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
            broadcasterRegistration = null;
        }
    }

    private void sendUpdateUsers() {
        //if (!allReadyExceptMe()) {
            LobbyBroadcaster.broadcast("123");
        //}
    }
}
