package org.metaborg.scopegraph.path;


public class PathException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PathException() {
        super();
    }

    public PathException(String message) {
        super(message);
    }

    public PathException(Throwable cause) {
        super(cause);
    }

    public PathException(String message, Throwable cause) {
        super(message, cause);
    }

}
