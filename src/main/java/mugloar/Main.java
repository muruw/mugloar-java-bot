package mugloar;

import mugloar.client.Message;
import mugloar.client.MugloarClient;
import mugloar.client.NewGame;
import mugloar.client.ShopItem;

/** Temporary, until there is a loop to run. */
public class Main {

    public static void main(String[] args) {
        MugloarClient client = new MugloarClient();

        NewGame game = client.startGame();
        System.out.println("game " + game.gameId()
                + " | lives " + game.lives()
                + " | gold " + game.gold()
                + " | level " + game.level()
                + " | score " + game.score()
                + " | turn " + game.turn());

        System.out.println();
        System.out.println("board:");
        for (Message message : client.getMessages(game.gameId())) {
            System.out.printf("  %-16s %4d gold  %2d turns  %-20s %s%s%n",
                    message.adId(),
                    message.reward(),
                    message.expiresIn(),
                    message.probability(),
                    message.message(),
                    message.encrypted() == null ? "" : "  [encrypted " + message.encrypted() + "]");
        }

        System.out.println();
        System.out.println("shop:");
        for (ShopItem item : client.getShopItems(game.gameId())) {
            System.out.printf("  %-8s %4d gold  %s%n", item.id(), item.cost(), item.name());
        }
    }
}
