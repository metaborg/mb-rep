package org.metaborg.scopegraph.experimental.path;


public class ScopeMismatchPathException extends PathException {

    private static final long serialVersionUID = 1L;

    public ScopeMismatchPathException() {
        super();
    }

    public ScopeMismatchPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScopeMismatchPathException(String message) {
        super(message);
    }

    public ScopeMismatchPathException(Throwable cause) {
        super(cause);
    }

}
