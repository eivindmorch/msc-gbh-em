package core.util.exceptions;

public class NoSuchTasksFoundException extends Exception {

    public NoSuchTasksFoundException() {
        super();
    }

    public NoSuchTasksFoundException(String message) {
        super(message);
    }

    public NoSuchTasksFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchTasksFoundException(Throwable cause) {
        super(cause);
    }
}
