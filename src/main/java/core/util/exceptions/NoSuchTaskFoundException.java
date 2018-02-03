package core.util.exceptions;

public class NoSuchTaskFoundException extends Exception {

    public NoSuchTaskFoundException() {
        super();
    }

    public NoSuchTaskFoundException(String message) {
        super(message);
    }

    public NoSuchTaskFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchTaskFoundException(Throwable cause) {
        super(cause);
    }
}
