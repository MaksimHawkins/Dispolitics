package sg.skylvsme.dispolitics.messaging;

import com.vaadin.flow.shared.Registration;
import sg.skylvsme.dispolitics.game.CountryNotification;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class NotificationBroadcaster {

    static Executor executor = Executors.newSingleThreadExecutor();

    static LinkedList<Consumer<CountryNotification>> listeners = new LinkedList<>();

    public static synchronized Registration register(
            Consumer<CountryNotification> listener) {
        listeners.add(listener);

        return () -> {
            synchronized (NotificationBroadcaster.class) {
                listeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(CountryNotification message) {
        for (Consumer<CountryNotification> listener : listeners) {
            executor.execute(() -> listener.accept(message));
        }
    }

}
