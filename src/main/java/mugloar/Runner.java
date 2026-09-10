package mugloar;

import mugloar.client.ApiException;
import mugloar.client.Message;
import mugloar.client.MugloarClient;
import mugloar.client.SolveResult;
import mugloar.strategy.AdPicker;

import java.util.List;
import java.util.Optional;

/** Plays one game from start to death, printing a line a turn. */
public class Runner {

    private static final int GAME_OVER = 410;
    private static final int MESSAGE_WIDTH = 60;

    private final MugloarClient client;

    public Runner(MugloarClient client) {
        this.client = client;
    }

    /**
     * Fetch the msg board, pick the one with AdPicker class strategy and run til lives are out.
     */
    public void play() {
        GameState state = GameState.of(client.startGame());
        System.out.printf("game %s | lives %d | gold %d | level %d%n",
                state.gameId(), state.lives(), state.gold(), state.level());

        try {
            while (state.lives() > 0) {
                List<Message> board = client.getMessages(state.gameId());
                Optional<Message> choice = AdPicker.pickAd(board);

                if (choice.isEmpty()) {
                    System.out.println("No messages, thus nothing to solve.");
                    break;
                }

                Message ad = choice.get();
                SolveResult result = client.solveMessage(state.gameId(), ad.adId());
                int reward = result.gold() - state.gold();
                state = state.withSolve(result);

                System.out.printf("turn %3d | lives %d | gold %5d | score %5d | %-7s %-18s %+5d  %s%n",
                        state.turn(),
                        state.lives(),
                        state.gold(),
                        state.score(),
                        result.success() ? "solved" : "failed",
                        ad.probability(),
                        reward,
                        shorten(ad.message()));
            }
        } catch (ApiException e) {
            if (e.status() != GAME_OVER) {
                throw e;
            }
            System.out.println("Game over!");
        }

        System.out.printf("final score %d after %d turns (high score %d)%n",
                state.score(), state.turn(), state.highScore());

        // return state;
    }

    private static String shorten(String message) {
        return message.length() <= MESSAGE_WIDTH ? message : message.substring(0, MESSAGE_WIDTH - 1) + "…";
    }
}
