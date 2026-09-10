package mugloar.strategy;

import mugloar.GameState;
import mugloar.client.Message;
import mugloar.client.ShopItem;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** How the bot plays */
public final class Strategy {

    // This could be improved by comparing ad expire dates etc. When there is time to solve ads,
    // we could instead buy multiple lives at once but this works well enough for now.
    private static final int HEAL_AT_LIVES = 1;

    private static final String HEALING_POTION = "healing potion";

    /**
     * These were at first picked randomly based on the phrases, but after letting Claude run this game for over 700 turns, then
     * these are the probabilities it came up. Surprisingly, "sure thing" isn't that of a sure thing.
     * All results are stored/visible in `probability-log.csv`.
     */
    private static final Map<String, Double> SUCCESS_RATES = Map.ofEntries(
            Map.entry("Walk in the park", 0.90),
            Map.entry("Piece of cake", 0.80),
            Map.entry("Quite likely", 0.74),
            Map.entry("Hmmm....", 0.65),
            Map.entry("Sure thing", 0.55),
            Map.entry("Gamble", 0.51),
            Map.entry("Risky", 0.39),
            Map.entry("Playing with fire", 0.33),
            Map.entry("Rather detrimental", 0.16),
            Map.entry("Suicide mission", 0.04),
            Map.entry("Impossible", 0.005));

    /**
     * this is used when we get a new or unknown probability phrase
     */
    private static final double UNKNOWN_SUCCESS_RATE = 0.001;

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

        Optional<Action> chosen = pickAd(board).map(Action.SolveAd::new);
        return chosen
                .orElseGet(() -> new Action.Stop("The board came back empty, so there is nothing to solve."));
    }

    /**
     * picks the ad worth the most,
     * if scores are the same, choose the one that expires first.
     */
    public static Optional<Message> pickAd(List<Message> board) {
        return board.stream()
                .max(Comparator.comparingDouble(Strategy::score)
                        .thenComparing(Comparator.comparingInt(Message::expiresIn).reversed()));
    }

    /**
     * calculates how much is it worth to solve the ad by taking its reward and multiplying it with the probability
     */
    static double score(Message ad) {
        return successRate(ad.probability()) * ad.reward();
    }

    static double successRate(String probability) {
        if (probability == null) {
            return UNKNOWN_SUCCESS_RATE;
        }

        return SUCCESS_RATES.getOrDefault(probability, UNKNOWN_SUCCESS_RATE);
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
