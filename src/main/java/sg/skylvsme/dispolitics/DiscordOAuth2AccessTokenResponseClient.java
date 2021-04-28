package sg.skylvsme.dispolitics;

import com.jagrosh.jdautilities.oauth2.OAuth2Client;
import com.jagrosh.jdautilities.oauth2.Scope;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

import java.util.Collections;

@AllArgsConstructor
public class DiscordOAuth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private OAuth2Client client;

    @SneakyThrows
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) throws OAuth2AuthenticationException {

        val session = client.startSession(
                authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode(),
                client.getStateController().generateNewState("http://localhost:8080/login/oauth2/code/discord"),
                "",
                Scope.IDENTIFY
        ).complete();

        val user = client.getUser(session).complete();

        return OAuth2AccessTokenResponse.withToken(session.getAccessToken())
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(session.getExpiration().getSecond())
                .scopes(Collections.singleton("identify"))
                .additionalParameters(Collections.singletonMap("user", user))
                .build();
    }
}
