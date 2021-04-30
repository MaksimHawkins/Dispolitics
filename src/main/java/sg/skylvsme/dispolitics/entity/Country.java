package sg.skylvsme.dispolitics.entity;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.VaadinServlet;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Country {

    private final String name;
    private final String flagLocation;

    private List<Player> players;
    private Player leader;

    private int money;

    private List<City> cities;

    public Country(String name, String flagLocation) {
        this.name = name;
        this.players = new ArrayList<Player>();
        this.cities = new ArrayList<City>();
        this.flagLocation = flagLocation;
        this.money = 0;
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

    public void addMoney(int count) {
        this.money += count;
    }
}
