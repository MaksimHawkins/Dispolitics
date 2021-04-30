package sg.skylvsme.dispolitics;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.VaadinServlet;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Country {

    private List<Player> players;
    private Player leader;
    private final String name;
    private final String flagLocation;
    private List<City> cities;

    public Country(String name, String flagLocation) {
        this.name = name;
        this.players = new ArrayList<Player>();
        this.cities = new ArrayList<City>();
        this.flagLocation = flagLocation;
    }

    public void addPlayer(Player user) {
        this.players.add(user);
    }

    public void addCity(City city) {
        this.cities.add(city);
    }

    public void setLeader(Player player) {
        if (players.contains(player))
            this.leader = player;
    }

    public Image getFlagImage() {
        return new Image(VaadinServlet.getCurrent().getServletContext().getContextPath() + "/flags/" + flagLocation, getName());
    }

}
