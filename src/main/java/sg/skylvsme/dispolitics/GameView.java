package sg.skylvsme.dispolitics;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;

import static com.vaadin.flow.component.Tag.H3;

@Route("game")
public class GameView extends HorizontalLayout {

    public GameView() {
        add(new H3("Game"));
    }

}
