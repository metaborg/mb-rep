package org.metaborg.scopegraph.path;


public class CyclicPathException extends PathException {

    private static final long serialVersionUID = 1L;

    public CyclicPathException() {
        super();
    }

    public CyclicPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public CyclicPathException(String message) {
        super(message);
    }

    public CyclicPathException(Throwable cause) {
        super(cause);
    }

}
