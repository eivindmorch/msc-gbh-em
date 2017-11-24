package util.exceptions;

public class IllegalArgumentCombinationException extends Exception{

    public IllegalArgumentCombinationException() {
        super();
    }

    public IllegalArgumentCombinationException(String message) {
        super(message);
    }

    public IllegalArgumentCombinationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalArgumentCombinationException(Throwable cause) {
        super(cause);
    }
}
