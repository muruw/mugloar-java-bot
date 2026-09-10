package mugloar;

import mugloar.client.NewGame;
import mugloar.client.ShopItemPurchase;
import mugloar.client.SolveResult;

public record GameState(
        String gameId,
        int lives,
        int gold,
        int level,
        int score,
        int highScore,
        int turn) {

    public static GameState of(NewGame game) {
        return new GameState(
                game.gameId(),
                game.lives(),
                game.gold(),
                game.level(),
                game.score(),
                game.highScore(),
                game.turn());
    }

    public GameState withSolve(SolveResult result) {
        return new GameState(
                gameId,
                result.lives(),
                result.gold(),
                level,
                result.score(),
                result.highScore(),
                result.turn());
    }

    public GameState withPurchase(ShopItemPurchase purchase) {
        return new GameState(
                gameId,
                purchase.lives(),
                purchase.gold(),
                purchase.level(),
                score,
                highScore,
                purchase.turn());
    }
}
