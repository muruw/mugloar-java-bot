package mugloar.client;

/**
 * The status code is kept because it decides what happens next:
 * <br> 410 means lives are gone,
 * and 400 on a quest/solve means the ad is no longer there.
 */
public class ApiException extends RuntimeException {

    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int status() {
        return status;
    }
}
