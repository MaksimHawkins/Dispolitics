package sg.skylvsme.dispolitics.model;

import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Objects;

@Getter
public class Player {

    private boolean isReady;
    private final OAuth2User oAuth2User;

    public Player(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public String getName() {
        return oAuth2User.getName();
    }

    public String getAvatarLocation() {
        if (oAuth2User.getAttribute("avatar") != null)
            return Objects.requireNonNull(oAuth2User.getAttribute("avatar")).toString();
        else
            return null;
    }

    public String getDiscordId() {
        return oAuth2User.getAttribute("id");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player)) return false;
        if (obj instanceof OAuth2User) return this.getDiscordId().equals(((OAuth2User) obj).getAttribute("id"));
        return this.getDiscordId().equals(((Player) obj).getDiscordId());
    }
}
