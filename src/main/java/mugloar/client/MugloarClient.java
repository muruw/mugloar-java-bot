package mugloar.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Dragons of Mugloar public api endpoints
 * <br>
 * API documentation is at https://dragonsofmugloar.com/doc/
 */
public class MugloarClient {

    private static final String API_BASE_URL = "https://dragonsofmugloar.com/api/v2";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper json = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /** Start a new game. */
    public NewGame startGame() {
        return post("/game/start", NewGame.class);
    }

    /** Run an investigation about your reputation. Costs a turn. */
    public Reputation investigateReputation(String gameId) {
        return post("/%s/investigate/reputation".formatted(gameId), Reputation.class);
    }

    /** Get all messages from the message board. */
    public List<Message> getMessages(String gameId) {
        return get("/%s/messages".formatted(gameId), Message.class);
    }

    /** Try to solve one of the messages from the message board. */
    public SolveResult solveMessage(String gameId, String adId) {
        return post("/%s/solve/%s".formatted(gameId, adId), SolveResult.class);
    }

    /** Get the listing of items available in the shop. */
    public List<ShopItem> getShopItems(String gameId) {
        return get("/%s/shop".formatted(gameId), ShopItem.class);
    }

    /** Purchase an item. Costs a turn even when the purchase fails. */
    public ShopItemPurchase purchaseShopItem(String gameId, String itemId) {
        return post("/%s/shop/buy/%s".formatted(gameId, itemId), ShopItemPurchase.class);
    }

    private <T> T post(String path, Class<T> type) {
        return parse(send("POST", path), json.getTypeFactory().constructType(type));
    }

    private <T> List<T> get(String path, Class<T> element) {
        return parse(send("GET", path), json.getTypeFactory().constructCollectionType(List.class, element));
    }

    private String send(String method, String path) {
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("%s%s".formatted(API_BASE_URL, path)))
                .method(method, HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response;
        try {
            response = http.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new UncheckedIOException("The call to %s %s did not go through.".formatted(method, path), e);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted while calling %s %s".formatted(method, path), e);
        }

        if (response.statusCode() != 200) {
            throw new ApiException(response.statusCode(),
                    "The game server said %d to %s %s".formatted(response.statusCode(), method, path));
        }

        return response.body();
    }

    private <T> T parse(String body, JavaType type) {
        try {
            return json.readValue(body, type);
        } catch (IOException e) {
            throw new UncheckedIOException("The game server sent something that is not %s.".formatted(type), e);
        }
    }
}
