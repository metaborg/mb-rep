package org.metaborg.scopegraph.experimental.path;


public class OccurrenceMismatchPathException extends PathException {

    private static final long serialVersionUID = 1L;

    public OccurrenceMismatchPathException() {
        super();
    }

    public OccurrenceMismatchPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public OccurrenceMismatchPathException(String message) {
        super(message);
    }

    public OccurrenceMismatchPathException(Throwable cause) {
        super(cause);
    }

}
