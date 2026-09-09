package mugloar.client;

public record SolveResult(
        boolean success,
        int lives,
        int gold,
        int score,
        int highScore,
        int turn,
        String message) {
}
