package sg.skylvsme.dispolitics;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.val;

import static com.vaadin.flow.component.Tag.H3;

@Route("game")
@PageTitle("Игра | Dispolitics")
@Push
public class GameView extends VerticalLayout {

    H3 header;
    VerticalLayout countriesLayout;
    Registration broadcasterRegistration;

    public GameView() {
        header = new H3();
        setHorizontalComponentAlignment(Alignment.CENTER, header);
        countriesLayout = new VerticalLayout();
        HorizontalLayout layout = new HorizontalLayout();
        layout.add(countriesLayout);
        add(header, layout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();

        if (!Game.INSTANCE.isStarted()) {
            ui.navigate(LobbyView.class);
            return;
        }

        sendMessage();

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
        header.setText("Ход " + Game.INSTANCE.getCurrentTurn());
        updateCountriesLayout();
    }

    private void updateCountriesLayout() {
        countriesLayout.removeAll();
        for (Country country : Game.INSTANCE.getCountries()) {
            countriesLayout.add(countryLayout(country));
        }
    }

    private Component countryLayout(Country country) {
        val layout = new VerticalLayout();
        layout.setWidth("");

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
        for (City city : country.getCities()) {
            citiesLayout.add(cityLayout(city));
        }
        layout.add(citiesLayout);
        return layout;
    }

    private Component cityLayout(City city) {
        val layout = new VerticalLayout();
        layout.add(new H4(city.getName()));

        val infoLayout = new HorizontalLayout();
        infoLayout.add(new Label("♥ Здоровье: " + city.getHealth()));
        infoLayout.add(new Label("Экономика: " + 1500 + "$"));

        layout.add(infoLayout);

        return layout;
    }

    private void sendMessage() {
        GameBroadcaster.broadcast("");
    }

}
