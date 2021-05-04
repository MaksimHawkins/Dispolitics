package sg.skylvsme.dispolitics.model;

import lombok.Getter;

@Getter
public class City {

    private final String name;
    private int health;
    private int economyAmplifier;

    public City(String name) {
        this.name = name;
        this.health = 100;
        this.economyAmplifier = 5;
    }

    public void attackCity(int damage) {
        this.health -= damage;
    }

    public void increaseEconomyAmplifier(int increaseValue) {
        this.economyAmplifier += increaseValue;
    }

}
