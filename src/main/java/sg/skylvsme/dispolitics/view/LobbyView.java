package sg.skylvsme.dispolitics.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.shared.communication.PushMode;
import lombok.val;
import sg.skylvsme.dispolitics.game.Game;
import sg.skylvsme.dispolitics.messaging.LobbyBroadcaster;
import sg.skylvsme.dispolitics.model.Country;
import sg.skylvsme.dispolitics.model.Player;
import sg.skylvsme.dispolitics.security.SecurityConfiguration;

import java.util.ArrayList;
import java.util.List;

@Push(PushMode.MANUAL)
@Route("lobby")
@PageTitle("Лобби | Dispolitics")
@CssImport("./styles/country-card.css")
public class LobbyView extends VerticalLayout implements PageConfigurator {

    Registration broadcasterRegistration;

    public static List<Player> unassignedPlayers = new ArrayList<>();

    private HorizontalLayout countriesLayout;
    private VerticalLayout unassignedPlayersContainer;
    private Checkbox isReadyCheckBox;
    private Label notEnoughPlayers;

    private Player currentPlayer;

    public LobbyView() {
        currentPlayer = new Player(SecurityConfiguration.getCurrentUser());

        setId("LobbyView");

        setJustifyContentMode(JustifyContentMode.CENTER);

        setAlignItems(Alignment.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(new H2("Лобби"));

        VerticalLayout unassignedPlayersLayout = new VerticalLayout();
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
        if (Game.isStarted()) {
            gotoGame();
            return;
        }

        UI ui = attachEvent.getUI();

        unassignedPlayers.add(currentPlayer);

        sendMessage(LobbyBroadcaster.LobbyMessages.UPDATE);

        broadcasterRegistration = LobbyBroadcaster.register(newMessage -> ui.access(() -> {
            handleBroadcast(newMessage);
        }));

        handleBroadcast(LobbyBroadcaster.LobbyMessages.UPDATE);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (!Game.isStarted()) {
            unassignedPlayers.remove(currentPlayer);

            for (Country country : Game.getCountries()) {
                country.getPlayers().remove(currentPlayer);
            }
        }

        if (!allReady())
            sendMessage(LobbyBroadcaster.LobbyMessages.UPDATE);

        unsubscribeBroadcaster();
    }

    private void handleBroadcast(LobbyBroadcaster.LobbyMessages message) {
        if (message == LobbyBroadcaster.LobbyMessages.UPDATE) {
            unassignedPlayersContainer.removeAll();
            countriesLayout.removeAll();
            for (Player user : unassignedPlayers) {
                unassignedPlayersContainer.add(userLayout(user));
            }

            for (Country country : Game.getCountries()) {
                countriesLayout.add(countryLayout(country));
            }
        }

        if (message == LobbyBroadcaster.LobbyMessages.REDIRECT_TO_GAME) {
            unsubscribeBroadcaster();
            gotoGame();
        }

        getUI().ifPresent(UI::push);
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
        if (allReady()) {
            if (!Game.isStarted()) {
                Game.start();
            }

            sendMessage(LobbyBroadcaster.LobbyMessages.REDIRECT_TO_GAME);
        } else {
            sendMessage(LobbyBroadcaster.LobbyMessages.UPDATE);
        }
    }

    private boolean allReady() {
        for (Country country : Game.getCountries()) {
            for (Player player : country.getPlayers()) {
                if (!player.isReady()) return false;
            }
        }

        if (unassignedPlayers.size() > 0) return false;

        return true;
    }

    private void gotoGame() {
        getUI().ifPresent(ui -> {
            ui.getPage().setLocation("http://localhost:8080/game");
        });
    }

    private void unsubscribeBroadcaster() {
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
            broadcasterRegistration = null;
        }
    }

    private void sendMessage(LobbyBroadcaster.LobbyMessages message) {
        LobbyBroadcaster.broadcast(message);
    }

    private void pickCountry(Country country) {
        if (country != null) {
            for (Country gameCountry : Game.getCountries()) {
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
            sendMessage(LobbyBroadcaster.LobbyMessages.UPDATE);
        }
    }

    private int getPlayersCount() {
        int count = 0;
        for (Country country : Game.getCountries()) {
            count += country.getPlayers().size();
        }
        count += unassignedPlayers.size();
        return count;
    }

}
