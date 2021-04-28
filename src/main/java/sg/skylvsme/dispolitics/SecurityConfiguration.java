package sg.skylvsme.dispolitics;

import com.jagrosh.jdautilities.oauth2.OAuth2Client;
import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Stream;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .requestMatchers(SecurityConfiguration::isFrameworkInternalRequest).permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/").and()
                .oauth2Login()
                .tokenEndpoint().accessTokenResponseClient(new DiscordOAuth2AccessTokenResponseClient(oAuth2Client()))
                .and().userInfoEndpoint().userService(new DiscordOAuth2UserService(jda()))
                .and().loginPage("/login").permitAll();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                // client-side JS code
                "/VAADIN/**",

                // the standard favicon URI
                "/favicon.ico",

                // web application manifest
                "/manifest.webmanifest", "/sw.js", "/offline-page.html",

                // icons and images
                "/icons/**", "/images/**");
    }

    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request
                .getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
                && Stream.of(ServletHelper.RequestType.values()).anyMatch(
                r -> r.getIdentifier().equals(parameterValue));
    }

    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }

    @Bean
    public OAuth2Client oAuth2Client() {
        return new OAuth2Client.Builder()
                .setClientId(836701361297948673L)
                .setClientSecret("A9eoYjMhY2cj3p22TAhHfM1scCUSPKRY")
                .build();
    }

    @SneakyThrows
    @Bean
    public JDA jda() {
        return JDABuilder.createDefault("ODM2NzAxMzYxMjk3OTQ4Njcz.YIh03Q.G5sjrkcEo4g98MNPW8uFzf-DotI").build();
    }

    public static OAuth2User getCurrentUser() {
        return (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
