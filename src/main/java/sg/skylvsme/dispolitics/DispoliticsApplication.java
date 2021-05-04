package sg.skylvsme.dispolitics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import sg.skylvsme.dispolitics.game.Game;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class DispoliticsApplication {

    public static void main(String[] args) {
        Game.init();
        SpringApplication.run(DispoliticsApplication.class, args);
    }

}
