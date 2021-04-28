package sg.skylvsme.dispolitics;

import lombok.AllArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class DiscordOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private JDA jda;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> attributes = new HashMap<>();

        val user = (com.jagrosh.jdautilities.oauth2.entities.OAuth2User) userRequest.getAdditionalParameters().get("user");

        if (user != null) {
            attributes.put("name", user.getName());
            attributes.put("id", user.getId());
            attributes.put("avatar", user.getAvatarUrl());
        }

        return new DefaultOAuth2User(
                Collections.singletonList(new OAuth2UserAuthority(attributes)),
                attributes,
                "name"
        );
    }

}
