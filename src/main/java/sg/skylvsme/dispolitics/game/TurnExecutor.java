package sg.skylvsme.dispolitics.game;

import sg.skylvsme.dispolitics.messaging.NotificationBroadcaster;

public class TurnExecutor {

    private static final OrderExecutor orderExecutor = new OrderExecutor();

    static void nextTurn() {
        orderExecutor.init();

    }

    static void processTurn() {
        orderExecutor.execute();
    }

    static OrderExecutor getOrderExecutor() {
        return orderExecutor;
    }

}
