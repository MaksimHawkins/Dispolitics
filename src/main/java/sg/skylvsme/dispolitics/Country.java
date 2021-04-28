package sg.skylvsme.dispolitics;

import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Country {

    private List<OAuth2User> players;
    private OAuth2User leader;
    private String name;

    public Country(String name) {
        this.name = name;
        this.players = new ArrayList<>();
    }

    public void addPlayer(OAuth2User user) {
        players.add(user);
    }

    public void setLeader(OAuth2User leader) {
        this.leader = leader;
    }

}
