package core.util.exceptions;

public class NoAvailableTaskClassException extends Exception {

    public NoAvailableTaskClassException() {
        super();
    }

    public NoAvailableTaskClassException(String message) {
        super(message);
    }

    public NoAvailableTaskClassException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAvailableTaskClassException(Throwable cause) {
        super(cause);
    }
}
