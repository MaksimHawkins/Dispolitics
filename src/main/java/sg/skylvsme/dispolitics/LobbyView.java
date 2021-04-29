package sg.skylvsme.dispolitics;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
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
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.List;

@Push
@Route("lobby")
@PageTitle("Лобби | Dispolitics")
public class LobbyView extends VerticalLayout implements PageConfigurator {

    Registration broadcasterRegistration;

    public Game game;
    public static List<Player> unassignedPlayers = new ArrayList<>();
    public static boolean allReady;

    private VerticalLayout usersLayout;
    private HorizontalLayout countriesLayout;
    private Checkbox isReadyCheckBox;

    private Player currentPlayer;

    private static final String UPDATE_USERS = "UpdateUsers";
    private static final String GOTO_GAME = "GotoGame";

    public LobbyView() {
        currentPlayer = new Player(SecurityConfiguration.getCurrentUser());

        game = Game.INSTANCE;

        setId("LobbyView");

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);

        setAlignItems(Alignment.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        usersLayout = new VerticalLayout();
        usersLayout.getStyle()
                .set("border" , "1px solid #ccc")
                .set("border-radious", "3px");
        usersLayout.setWidth("100%");
        usersLayout.setMaxWidth("450px");

        countriesLayout = new HorizontalLayout();
        add(countriesLayout);

        //middleLayout.add(usersLayout);

        /*for (Country country : game.getCountries()) {
            add(new H3(country.getName()), country.getFlagImage());
        }*/

        VerticalLayout countriesLayout = new VerticalLayout();
        countriesLayout.setWidth("15%");
        countriesLayout.getStyle()
                .set("border", "1px solid #ccc")
                .set("border-radious", "3px");

        add(countriesLayout);

        //add(middleLayout);

        isReadyCheckBox = new Checkbox("Я готов");
        isReadyCheckBox.addValueChangeListener(changeEvent -> changeReady());
        add(isReadyCheckBox);
    }

    private VerticalLayout countryLayout(Country country) {
        VerticalLayout countryLayout = new VerticalLayout();
        countryLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        countryLayout.setPadding(true);

        Image flag = country.getFlagImage();
        flag.setWidth("128px");
        flag.setHeight("128px");
        flag.addClickListener(event -> event.getSource().getAlt().ifPresent(name -> pickCountry(game.getCountryByName(name))));
        countryLayout.add(flag);
        countryLayout.add(new H3(country.getName()));

        for (Player player : country.getPlayers()) {
            countryLayout.add(userLayout(player));
        }

        return countryLayout;
    }

    public HorizontalLayout userLayout(Player user) {
        HorizontalLayout layout = new HorizontalLayout();

        layout.setWidthFull();
        layout.setHeight("40px");
        layout.setPadding(true);
        layout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Label label = new Label(user.getName());
        label.getStyle().set("font-size", "large");
        layout.add(label);

        Div div = new Div();
        div.getStyle()
                .set("background", user.isReady() ? "lightgreen" : "lightcoral")
                .set("border-radius", "50%");
        div.setWidth("40px");
        div.setHeight("40px");

        Image image = new Image();
        image.setWidth("40px");
        image.getStyle()
                .set("border-radius", "50%");
        if (user.getAvatarLocation() != null)
            image.setSrc(user.getAvatarLocation());
        layout.add(image);

        layout.add(div);
        layout.expand(label);

        return layout;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();

        unassignedPlayers.add(currentPlayer);

        sendMessage(UPDATE_USERS);

        broadcasterRegistration = LobbyBroadcaster.register(newMessage -> ui.access(() -> {
            if (newMessage.equals(UPDATE_USERS)) handleBroadcast();
        }));

        handleBroadcast();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (unassignedPlayers.contains(currentPlayer))
            unassignedPlayers.remove(currentPlayer);

        for (Country country : game.getCountries()) {
            if (country.getPlayers().contains(currentPlayer))
                country.getPlayers().remove(currentPlayer);
        }

        if (!allReady)
            sendMessage(UPDATE_USERS);

        unsubscribeBroadcaster();
    }

    private void handleBroadcast() {
        usersLayout.removeAll();
        countriesLayout.removeAll();
        for (Player user : unassignedPlayers) {
            usersLayout.add(userLayout(user));
        }

        for (Country country : game.getCountries()) {
            countriesLayout.add(countryLayout(country));
        }

        if (allReady) {
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
        currentPlayer.setReady(isReadyCheckBox.getValue());
        allReady = allReady();
        sendMessage(UPDATE_USERS);
    }

    private boolean allReady() {
        for (Country country : game.getCountries()) {
            for (Player player : country.getPlayers()) {
                if (!player.isReady()) return false;
            }
        }
        for (Player lobbyUser : unassignedPlayers) {
            if (!lobbyUser.isReady()) return false;
        }
        return true;
    }

    private void gotoGame() {
        getUI().ifPresent(ui -> {
            ui.navigate(GameView.class);
        });
    }

    private void unsubscribeBroadcaster() {
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
            broadcasterRegistration = null;
        }
    }

    private void sendMessage(String message) {
        LobbyBroadcaster.broadcast(message);
    }

    private void pickCountry(Country country) {
        if (country != null) {
            OAuth2User currentOAuth2User = currentPlayer.getOAuth2User();
            for (Country gameCountry : game.getCountries()) {
                if (gameCountry.getPlayers().contains(currentPlayer)) {
                    gameCountry.getPlayers().remove(currentPlayer);
                    break;
                }
            }
            for (Player lobbyUser : unassignedPlayers) {
                if (unassignedPlayers.contains(currentPlayer)) {
                    unassignedPlayers.remove(currentPlayer);
                    break;
                }
            }
            country.getPlayers().add(currentPlayer);
            sendMessage(UPDATE_USERS);
        }
    }

}
