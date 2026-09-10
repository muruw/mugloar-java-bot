package mugloar.client;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageDecoderTest {

    @Test
    void plainAdIsLeftAlone() {
        Message plain = new Message("OfLlY7gT", "Help Seren Appleton to transport a magic squirrel", 22, 7,
                "Piece of cake", null);

        assertSame(plain, MessageDecoder.decode(plain).orElseThrow());
    }

    @Test
    void decodesBase64Ad() {
        Message decoded = decode(new Message(
                "b0pqQkFXamE=",
                "SW52ZXN0aWdhdGUgSXNpIEdhcnJhcmQgYW5kIGZpbmQgb3V0IHRoZWlyIHJlbGF0aW9uIHRvIHRoZSBtYWdpYyBiZWVyIG11Zy4=",
                141, 3, "UXVpdGUgbGlrZWx5", 1));

        assertEquals("oJjBAWja", decoded.adId());
        assertEquals("Investigate Isi Garrard and find out their relation to the magic beer mug.", decoded.message());
        assertEquals("Quite likely", decoded.probability());
    }

    /** Base64 that carries more than ASCII: the bytes have to be read back as UTF-8, not one per character. */
    @Test
    void decodesBase64AdWithNonAsciiName() {
        Message decoded = decode(new Message("b0pqQkFXamE=", "S2lsbCBRdcOibiBTY2FyYnJvdWdo", 99, 2,
                "R2FtYmxl", 1));

        assertEquals("Kill Quân Scarbrough", decoded.message());
    }

    @Test
    void decodesRot13AdAndLeavesTheDigitsInTheIdAlone() {
        Message decoded = decode(new Message("c6jmu7IG", "Xvyy Gbzb Pyvsgba jvgu pybgurf", 108, 4,
                "Vzcbffvoyr", 2));

        assertEquals("p6wzh7VT", decoded.adId());
        assertEquals("Kill Tomo Clifton with clothes", decoded.message());
        assertEquals("Impossible", decoded.probability());
    }

    @Test
    void dropsAdWithUnknownCipher() {
        Optional<Message> decoded = MessageDecoder.decode(
                new Message("c6jmu7IG", "Xvyy Gbzb Pyvsgba", 108, 4, "Vzcbffvoyr", 3));

        assertTrue(decoded.isEmpty());
    }

    @Test
    void keepsTheRestOfTheAdAsItArrived() {
        Message decoded = decode(new Message("c6jmu7IG", "Xvyy Gbzb Pyvsgba", 108, 4, "Vzcbffvoyr", 2));

        assertEquals(108, decoded.reward());
        assertEquals(4, decoded.expiresIn());
        assertEquals(2, decoded.encrypted());
    }

    private static Message decode(Message message) {
        return MessageDecoder.decode(message).orElseThrow();
    }
}
