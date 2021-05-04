package sg.skylvsme.dispolitics.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.shared.communication.PushMode;
import lombok.val;
import sg.skylvsme.dispolitics.game.CountryNotification;
import sg.skylvsme.dispolitics.game.Game;
import sg.skylvsme.dispolitics.game.NotificationPool;
import sg.skylvsme.dispolitics.messaging.GameBroadcaster;
import sg.skylvsme.dispolitics.model.City;
import sg.skylvsme.dispolitics.model.Country;
import sg.skylvsme.dispolitics.model.Player;
import sg.skylvsme.dispolitics.model.order.OrderItem;

import java.util.List;

@Route("game")
@PageTitle("Игра | Dispolitics")
@Push(value = PushMode.AUTOMATIC)
@CssImport("./styles/country-card.css")
public class GameView extends VerticalLayout {

    H3 header;
    VerticalLayout countriesLayout, panelsLayout;
    Registration broadcasterRegistration;

    Player currentPlayer;

    public GameView() {
        addClassName("game-view");

        setHeightFull();

        header = new H3();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        HorizontalLayout contentLayout = new HorizontalLayout();
        contentLayout.setWidthFull();

        countriesLayout = new VerticalLayout();
        contentLayout.add(countriesLayout);
        contentLayout.setFlexGrow(2, countriesLayout);

        panelsLayout = new VerticalLayout();
        contentLayout.add(panelsLayout);
        contentLayout.setFlexGrow(0.5, panelsLayout);

        add(header, contentLayout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();

        if (!Game.isStarted()) {
            ui.navigate(LobbyView.class);
            return;
        }

        this.currentPlayer = Game.getCurrentPlayer();

        //sendMessage();

        broadcasterRegistration = GameBroadcaster.register(newMessage -> ui.access(() -> {
            updateGame();
        }));

        updateGame();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
            broadcasterRegistration = null;
        }
    }

    private void updateGame() {
        header.setText("Ход " + Game.getCurrentTurn());
        updateCountriesLayout();
        updatePanels();
        updateNotifications();
    }

    private void updateNotifications() {
        List<CountryNotification> notificationList = NotificationPool.getNotificationsByCountry(Game.getCountryByPlayer(currentPlayer));
        for (CountryNotification countryNotification : notificationList) {
            if (countryNotification.getCountry() == Game.getCountryByPlayer(currentPlayer)) {
                showNotification(countryNotification);
                NotificationPool.removeNotification(countryNotification);
            }
        }
    }

    private void showNotification(CountryNotification notification) {
        Notification.show(notification.getMessage());
    }

    private void updateCountriesLayout() {
        countriesLayout.removeAll();
        for (Country country : Game.getCountries()) {
            if (country == Game.getCurrentCountry(currentPlayer)) continue;
            countriesLayout.add(countryLayout(country));
        }
    }

    private void updatePanels() {
        panelsLayout.removeAll();
        panelsLayout.add(countryPanelLayout(Game.getCurrentCountry(currentPlayer)));

        val ordersPanel = ordersPanelLayout();
        panelsLayout.add(ordersPanel);
        panelsLayout.setFlexGrow(1, ordersPanel);

        val addOrderItemButton = new Button("Добавить пункт приказа");
        addOrderItemButton.setWidthFull();
        addOrderItemButton.addClickListener(event -> {
            updateGame();
            new OrderDialog(Game.getCurrentOrder(currentPlayer)).open();
        });

        val nextTurnButton = new Button("Следующий ход");
        nextTurnButton.addClickListener(event -> {
            Game.nextTurn();
            GameBroadcaster.broadcast("");
        });
        nextTurnButton.setWidthFull();
        nextTurnButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        panelsLayout.add(addOrderItemButton);
        panelsLayout.add(nextTurnButton);
    }

    private Component ordersPanelLayout() {
        val layout = new VerticalLayout();
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        layout.add(new H4("Приказ"));
        layout.addClassName("panel");

        val orderList = new Scroller();
        val orderListContent = new VerticalLayout();
        orderListContent.setAlignItems(Alignment.STRETCH);

        for (OrderItem orderItem : Game.getCurrentOrder(currentPlayer).getOrderItems()) {
            orderListContent.add(orderItem.getLayout());
        }

        orderList.setContent(orderListContent);
        layout.add(orderList);

        return layout;
    }

    private Component countryPanelLayout(Country country) {
        val layout = new VerticalLayout();
        layout.addClassName("panel");

        val headerLayout = new HorizontalLayout();
        headerLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        val image = country.getFlagImage();
        image.setWidth("96px");
        image.setHeight("96px");

        headerLayout.add(image);

        headerLayout.addAndExpand(new H3(country.getName()));

        layout.add(headerLayout);

        return layout;
    }

    private Component countryLayout(Country country) {
        val layout = new VerticalLayout();
        layout.addClassName("panel");
        layout.setWidthFull();

        val headerLayout = new HorizontalLayout();
        headerLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        val image = country.getFlagImage();
        image.setHeight("96px");
        image.setWidth("96px");
        headerLayout.add(image);
        val countryName = new H3(country.getName());
        headerLayout.addAndExpand(countryName);

        for (Player player : country.getPlayers()) {
            val avatar = new Image();
            avatar.setWidth("48px");
            avatar.setHeight("48px");
            if (player.getAvatarLocation() != null)
                avatar.setSrc(player.getAvatarLocation());
            avatar.getStyle().set("border-radius", "50%");
            headerLayout.add(avatar);
        }

        layout.add(headerLayout);

        val citiesLayout = new HorizontalLayout();
        citiesLayout.setSpacing(false);
        for (City city : country.getCities()) {
            citiesLayout.add(cityLayout(city));
        }
        layout.add(citiesLayout);
        return layout;
    }

    private Component cityLayout(City city) {
        val layout = new VerticalLayout();
        layout.add(new H4(city.getName()));

        val healthLayout = new HorizontalLayout();
        healthLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        healthLayout.add(IconManager.getHeartIcon());
        healthLayout.add(new Label(String.valueOf(city.getHealth())));

        val populationLayout = new HorizontalLayout();
        healthLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        healthLayout.add(IconManager.getPopulationIcon());
        healthLayout.add(new Label(String.valueOf(city.getEconomyAmplifier())));

        layout.add(healthLayout);
        layout.add(populationLayout);

        return layout;
    }

}
