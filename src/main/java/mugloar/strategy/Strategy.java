package mugloar.strategy;

import mugloar.GameState;
import mugloar.client.Message;
import mugloar.client.ShopItem;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** How the bot plays */
public final class Strategy {

    // This could be improved by comparing ad expire dates etc. When there is time to solve ads,
    // we could instead buy multiple lives at once but this works well enough for now.
    private static final int HEAL_AT_LIVES = 1;

    private static final String HEALING_POTION = "healing potion";

    private Strategy() {
    }

    /**
     * Basic turn strategy where bot prioritizes healing instead of solving ads.
     * <br>
     * A Stop ends the run and says why.
     */
    public static Action decide(GameState state, List<Message> board, List<ShopItem> shop) {
        Optional<ShopItem> potion = affordablePotion(state, shop);

        if (state.lives() <= HEAL_AT_LIVES && potion.isPresent()) {
            return new Action.BuyItem(potion.get());
        }

        return pickAd(board)
                .<Action>map(Action.SolveAd::new)
                .orElseGet(() -> new Action.Stop("The board came back empty, so there is nothing to solve."));
    }

    /**
     * basic strategy -> takes the ad with the biggest reward
     */
    public static Optional<Message> pickAd(List<Message> board) {
        return board.stream().max(Comparator.comparingInt(Message::reward));
    }

    /**
     * Checks whether we can afford a healing potion.
     */
    static Optional<ShopItem> affordablePotion(GameState state, List<ShopItem> shop) {
        return shop.stream()
                .filter(item -> item.name().toLowerCase(Locale.ROOT).contains(HEALING_POTION))
                .filter(item -> item.cost() <= state.gold())
                .findFirst();
    }
}
