package sg.skylvsme.dispolitics;

import lombok.Data;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class LobbyUser {

    private boolean isReady;
    private OAuth2User oAuth2User;

    public LobbyUser(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
