package mugloar.strategy;

import mugloar.client.Message;
import mugloar.client.ShopItem;

/**
 * What the bot does with its one turn.
 */
public sealed interface Action {

    record SolveAd(Message ad) implements Action {
    }

    record BuyItem(ShopItem item) implements Action {
    }

    record Stop(String reason) implements Action {
    }

}
