package org.metaborg.scopegraph.path;


public class RecursivePathException extends PathException {

    private static final long serialVersionUID = 1L;

    public RecursivePathException() {
        super();
    }

    public RecursivePathException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecursivePathException(String message) {
        super(message);
    }

    public RecursivePathException(Throwable cause) {
        super(cause);
    }

}
