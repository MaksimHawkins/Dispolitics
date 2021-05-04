package sg.skylvsme.dispolitics.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sg.skylvsme.dispolitics.model.Country;

@Getter
@AllArgsConstructor
public class CountryNotification {

    private Country country;
    private String message;

}
