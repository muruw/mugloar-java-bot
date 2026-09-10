package mugloar.client;

/**
 * Message board ad
 * <br>
 * probability represents the chance to solve the task in text form. ex: "Piece of cake" / "Gamble"
 * <br>
 * encrypted "1" means base64, "2" means rot13 and null/ "0" means no encryption. If encryption exists, then
 * adId, message and probability are all scrambled. MugloarClient#getMessages hands them over decoded.
 */
public record Message(
        String adId,
        String message,
        int reward,
        int expiresIn,
        String probability,
        Integer encrypted) {
}
