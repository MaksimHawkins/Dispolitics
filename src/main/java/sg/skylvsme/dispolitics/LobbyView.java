package sg.skylvsme.dispolitics;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.shared.Registration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.List;

@Push
@Route("lobby")
@PageTitle("Лобби | Dispolitics")
public class LobbyView extends HorizontalLayout implements PageConfigurator {

    Registration broadcasterRegistration;

    public Game game;
    public static List<OAuth2User> oAuth2Users = new ArrayList<>();

    private VerticalLayout usersLayout;

    public LobbyView() {
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
                .set("border" , "1px solid #ccc")
                .set("border-radious", "3px");

        RadioButtonGroup<String> countriesRadio = new RadioButtonGroup<>();
        countriesRadio.setItems("Польша", "США", "Россия");
        countriesRadio.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        countriesRadio.addValueChangeListener(event -> {
            Notification.show(event.getValue());
        });

        countriesLayout.add(countriesRadio);

        middleLayout.add(countriesLayout);

        add(middleLayout);

        addDetachListener(e -> {
            updateUsers();
        });

    }

    public HorizontalLayout userLayout(OAuth2User user) {
        HorizontalLayout layout = new HorizontalLayout();

        layout.setWidthFull();

        Label label = new Label(user.getName());
        label.getStyle().set("font-size", "x-large");
        layout.add(label);

        Div div = new Div();
        div.getStyle()
                .set("background", "lightgreen")
                .set("border-radius", "50%");
        div.setWidth("40px");

        Image image = new Image();
        if (user.getAttribute("avatar") != null)
            image.setSrc(user.getAttribute("avatar").toString());
        layout.add(image);

        layout.add(div);
        layout.expand(label);

        return layout;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();

        oAuth2Users.add((OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        LobbyBroadcaster.broadcast("123");

        broadcasterRegistration = LobbyBroadcaster.register(newMessage -> ui.access(() -> {
            updateUsers();
        }));

        updateUsers();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        oAuth2Users.remove((OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        broadcasterRegistration.remove();
        broadcasterRegistration = null;

        LobbyBroadcaster.broadcast("123");
    }

    private void updateUsers() {
        usersLayout.removeAll();
        for (OAuth2User oAuth2User : oAuth2Users) {
            usersLayout.add(userLayout(oAuth2User));
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
}
