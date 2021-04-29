package sg.skylvsme.dispolitics;

import lombok.Data;
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
}
