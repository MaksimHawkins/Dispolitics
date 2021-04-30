package sg.skylvsme.dispolitics.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.shared.Registration;
import lombok.val;
import sg.skylvsme.dispolitics.entity.Player;
import sg.skylvsme.dispolitics.security.SecurityConfiguration;
import sg.skylvsme.dispolitics.entity.Country;
import sg.skylvsme.dispolitics.entity.Game;
import sg.skylvsme.dispolitics.messaging.LobbyBroadcaster;

import java.util.ArrayList;
import java.util.List;

@Push
@Route("lobby")
@PageTitle("Лобби | Dispolitics")
@CssImport("./styles/country-card.css")
public class LobbyView extends VerticalLayout implements PageConfigurator {

    Registration broadcasterRegistration;

    public Game game;
    public static List<Player> unassignedPlayers = new ArrayList<>();

    //private VerticalLayout usersLayout;
    private HorizontalLayout countriesLayout;
    private VerticalLayout unassignedPlayersLayout;
    private VerticalLayout unassignedPlayersContainer;
    private Checkbox isReadyCheckBox;
    private Label notEnoughPlayers;

    private Player currentPlayer;

    private static final String UPDATE_USERS = "UpdateUsers";
    private static final String GOTO_GAME = "GotoGame";

    public LobbyView() {
        currentPlayer = new Player(SecurityConfiguration.getCurrentUser());

        game = Game.INSTANCE;

        setId("LobbyView");

        //setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);

        setAlignItems(Alignment.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(new H2("Лобби"));

        unassignedPlayersLayout = new VerticalLayout();
        unassignedPlayersLayout.addClassName("panel");
        unassignedPlayersContainer = new VerticalLayout();
        unassignedPlayersLayout.add(new H4("Нераспределенные игроки"));
        unassignedPlayersLayout.add(unassignedPlayersContainer);

        add(unassignedPlayersLayout);

        countriesLayout = new HorizontalLayout();
        add(countriesLayout);

        isReadyCheckBox = new Checkbox("Я готов");
        isReadyCheckBox.addValueChangeListener(changeEvent -> changeReady());
        add(isReadyCheckBox);

        notEnoughPlayers = new Label("Недостаточно игроков");
        notEnoughPlayers.getStyle().set("color", "red");
        notEnoughPlayers.setVisible(false);
        add(notEnoughPlayers);
    }

    private VerticalLayout countryLayout(Country country) {
        val countryLayout = new VerticalLayout();
        countryLayout.addClassName("panel");
        countryLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        countryLayout.setPadding(true);
        countryLayout.setMinWidth("20em");

        Image flag = country.getFlagImage();
        flag.setWidth("128px");
        flag.setHeight("128px");
        //flag.addClickListener(event -> event.getSource().getAlt().ifPresent(name -> pickCountry(game.getCountryByName(name))));
        countryLayout.add(flag);
        countryLayout.add(new H3(country.getName()));

        for (Player player : country.getPlayers()) {
            countryLayout.add(userLayout(player));
        }

        countryLayout.addClickListener(event -> pickCountry(country));

        return countryLayout;
    }

    public HorizontalLayout userLayout(Player user) {
        val layout = new HorizontalLayout();

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
        if (game.isStarted()) {
            gotoGame();
            return;
        }

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
        if (!game.isStarted()) {
            if (unassignedPlayers.contains(currentPlayer))
                unassignedPlayers.remove(currentPlayer);

            for (Country country : game.getCountries()) {
                if (country.getPlayers().contains(currentPlayer))
                    country.getPlayers().remove(currentPlayer);
            }
        }

        if (!allReady())
            sendMessage(UPDATE_USERS);

        unsubscribeBroadcaster();
    }

    private void handleBroadcast() {
        unassignedPlayersContainer.removeAll();
        countriesLayout.removeAll();
        for (Player user : unassignedPlayers) {
            unassignedPlayersContainer.add(userLayout(user));
        }

        for (Country country : game.getCountries()) {
            countriesLayout.add(countryLayout(country));
        }

        if (allReady()) {
            if (!game.isStarted()) {
                game.start();
            }
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
            for (Country gameCountry : game.getCountries()) {
                if (gameCountry.getPlayers().contains(currentPlayer)) {
                    gameCountry.getPlayers().remove(currentPlayer);
                    break;
                }
            }
            for (Player player : unassignedPlayers) {
                if (unassignedPlayers.contains(currentPlayer)) {
                    unassignedPlayers.remove(currentPlayer);
                    break;
                }
            }
            country.getPlayers().add(currentPlayer);
            sendMessage(UPDATE_USERS);
        }
    }

    private int getPlayersCount() {
        int count = 0;
        for (Country country : game.getCountries()) {
            count += country.getPlayers().size();
        }
        count += unassignedPlayers.size();
        return count;
    }

}
