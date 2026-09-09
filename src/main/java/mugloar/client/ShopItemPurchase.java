package mugloar.client;

public record ShopItemPurchase(
        boolean shoppingSuccess,
        int lives,
        int gold,
        int level,
        int turn) {
}
