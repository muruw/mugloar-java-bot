package mugloar;

import mugloar.client.ApiException;
import mugloar.client.Message;
import mugloar.client.MugloarClient;
import mugloar.client.ShopItem;
import mugloar.client.ShopItemPurchase;
import mugloar.client.SolveResult;
import mugloar.strategy.Action;
import mugloar.strategy.Strategy;

import java.util.List;

public class Runner {

    private static final int GAME_OVER = 410;
    private static final int MESSAGE_WIDTH = 60;

    private final MugloarClient client;

    public Runner(MugloarClient client) {
        this.client = client;
    }

    /**
     * Fetch the board, decide what the turn is spent on, carry it out, merge the reply, repeat.
     * <br>
     * The run ends when the lives are gone
     */
    public void play() {
        GameState state = GameState.of(client.startGame());
        System.out.printf("game %s | lives %d | gold %d | level %d%n",
                state.gameId(), state.lives(), state.gold(), state.level());

        List<ShopItem> shop = client.getShopItems(state.gameId());

        try {
            turns:
            while (state.lives() > 0) {
                List<Message> board = client.getMessages(state.gameId());

                switch (Strategy.decide(state, board, shop)) {
                    case Action.SolveAd(Message ad) -> state = solve(state, ad);
                    case Action.BuyItem(ShopItem item) -> state = buy(state, item);
                    case Action.Stop(String reason) -> {
                        System.out.println(reason);
                        break turns;
                    }
                }
            }
        } catch (ApiException e) {
            if (e.status() != GAME_OVER) {
                throw e;
            }
            System.out.println("Game over!");
        }

        System.out.printf("final score %d after %d turns%n", state.score(), state.turn());
    }

    private GameState solve(GameState before, Message ad) {
        SolveResult result = client.solveMessage(before.gameId(), ad.adId());
        GameState after = before.withSolve(result);

        log(after, result.success() ? "solved" : "failed", ad.probability(),
                after.gold() - before.gold(), shorten(ad.message()));

        return after;
    }

    private GameState buy(GameState before, ShopItem item) {
        ShopItemPurchase purchase = client.purchaseShopItem(before.gameId(), item.id());
        GameState after = before.withPurchase(purchase);

        log(after, purchase.shoppingSuccess() ? "bought" : "refused", item.name(),
                after.gold() - before.gold(), "");

        return after;
    }

    private static void log(GameState state, String what, String detail, int gold, String text) {
        System.out.printf("turn %3d | lives %d | gold %5d | score %5d | %-7s %-18s %+5d  %s%n",
                state.turn(), state.lives(), state.gold(), state.score(), what, detail, gold, text);
    }

    private static String shorten(String message) {
        return message.length() <= MESSAGE_WIDTH ? message : message.substring(0, MESSAGE_WIDTH - 1) + "…";
    }
}
