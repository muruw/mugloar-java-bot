package mugloar.strategy;

import mugloar.client.Message;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class AdPicker {

    private AdPicker() {
    }

    /**
     * basic strategy -> takes the one with the biggest reward
     */
    public static Optional<Message> pickAd(List<Message> board) {
        return board.stream().max(Comparator.comparingInt(Message::reward));
    }
}
