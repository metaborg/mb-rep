/*
 * Created on 17. sep.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms.util;

import jakarta.annotation.Nullable;

/**
 * Exception indicating that something was not implemented.
 */
public class NotImplementedException extends UnsupportedOperationException {

    private static final long serialVersionUID = -1028814795329444374L;
    private static final String DEFAULT_MESSAGE = "This operation has not been implemented.";

    /**
     * Initializes a new instance of the {@link NotImplementedException} class.
     *
     * @param message the message to display; or {@code null} to use the default message
     */
    public NotImplementedException(@Nullable String message) {
        super(message != null ? message : DEFAULT_MESSAGE, null);
    }

    /**
     * Initializes a new instance of the {@link NotImplementedException} class
     * with the default message.
     */
    public NotImplementedException() {
        this(null);
    }

}
