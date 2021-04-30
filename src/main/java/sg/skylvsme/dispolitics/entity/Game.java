package sg.skylvsme.dispolitics.entity;

import lombok.Getter;
import lombok.val;
import sg.skylvsme.dispolitics.security.SecurityConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Game {

    public static Game INSTANCE = new Game();

    private int currentTurn;
    private List<Country> countries;
    private boolean isStarted = false;

    private Game() {
        this.currentTurn = 0;
        this.countries = new ArrayList<>();

        val usa = new Country("США", "usa.png");
        val russia = new Country("Россия", "russia.png");
        val china = new Country("Китай", "china.png");

        usa.addCity(new City("Нью-Йорк"));
        usa.addCity(new City("Лос-Анджелес"));
        usa.addCity(new City("Вашингтон"));

        russia.addCity(new City("Москва"));
        russia.addCity(new City("Санкт-Петербург"));
        russia.addCity(new City("Краснодар"));

        china.addCity(new City("Пекин"));
        china.addCity(new City("Шанхай"));
        china.addCity(new City("Гуанчжоу"));

        this.countries.add(usa);
        this.countries.add(russia);
        this.countries.add(china);
    }

    public Country getCountryByName(String name) {
        for (Country country : countries) {
            if (country.getName().equals(name)) return country;
        }
        return null;
    }

    public static List<City> getCities(PlayerFilter owner) {
        return null;
    }

    public static Player getCurrentPlayer() {
        for (Country country : INSTANCE.getCountries()) {
            for (Player player : country.getPlayers()) {
                if (player.getOAuth2User().equals(SecurityConfiguration.getCurrentUser())) {
                    return player;
                }
            }
        }
        return null;
    }

    public void start() {
        if (!isStarted) {
            isStarted = true;
            currentTurn = 1;
        }
    }

    public static Country getCountryByPlayer(Player player) {
        for (Country country : INSTANCE.getCountries()) {
            for (Player countryPlayer : country.getPlayers()) {
                if (countryPlayer.equals(player)) {
                    return country;
                }
            }
        }
        return null;
    }

    public static Country getCurrentCountry() {
        return getCountryByPlayer(getCurrentPlayer());
    }

}
