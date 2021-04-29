package sg.skylvsme.dispolitics;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Game {

    public static Game INSTANCE = new Game();

    private int currentTurn;
    private List<Country> countries;

    private Game() {
        this.currentTurn = 0;
        this.countries = new ArrayList<>();

        this.countries.add(new Country("USA", "usa.png"));
        this.countries.add(new Country("Russia", "russia.png"));
        this.countries.add(new Country("China", "china.png"));
    }

    public Country getCountryByName(String name) {
        for (Country country : countries) {
            if (country.getName().equals(name)) return country;
        }
        return null;
    }

}
