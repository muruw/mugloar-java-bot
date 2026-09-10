package mugloar.client;

import java.util.Base64;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Some ads arrive scrambled, and the {@code encrypted} field names the cipher: 1 is base64, 2 is ROT13.
 * <br>
 * The adId is scrambled along with the text, and the game answers a scrambled one with 400, so an ad we
 * cannot read is also an ad we cannot solve.
 */
final class MessageDecoder {

    private static final int BASE64 = 1;

    private static final int ROT13 = 2;

    private MessageDecoder() {
    }

    /**
     * Decode one ad, or drop it when the cipher is one we do not know. Only 1 and 2 have ever appeared on
     * the live API, but losing one ad beats losing the run.
     * <br>
     * The {@code encrypted} field is kept as it arrived. It no longer describes the fields next to it, it
     * only says how the ad came in, which is worth seeing in a log line.
     */
    static Optional<Message> decode(Message message) {
        Integer cipher = message.encrypted();

        if (cipher == null || cipher == 0) {
            return Optional.of(message);
        }

        if (cipher != BASE64 && cipher != ROT13) {
            return Optional.empty();
        }

        return Optional.of(new Message(
                decipher(cipher, message.adId()),
                decipher(cipher, message.message()),
                message.reward(),
                message.expiresIn(),
                decipher(cipher, message.probability()),
                message.encrypted()));
    }

    private static String decipher(int cipher, String text) {
        return cipher == BASE64 ? fromBase64(text) : rot13(text);
    }

    /**
     * The base64 holds the UTF-8 bytes of the ad text, so the bytes are read back as UTF-8 rather than one
     * character each - the quests are full of names like "Quan Scarbrough".
     */
    private static String fromBase64(String text) {
        return new String(Base64.getDecoder().decode(text), UTF_8);
    }

    /** Letters move by thirteen, everything else - the digits in an adId included - stays put. */
    private static String rot13(String text) {
        char[] characters = text.toCharArray();

        for (int i = 0; i < characters.length; i++) {
            char character = characters[i];

            if (character >= 'a' && character <= 'z') {
                characters[i] = (char) ('a' + (character - 'a' + 13) % 26);
            } else if (character >= 'A' && character <= 'Z') {
                characters[i] = (char) ('A' + (character - 'A' + 13) % 26);
            }
        }

        return new String(characters);
    }
}
