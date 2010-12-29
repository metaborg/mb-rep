/*
 * Created on 17. sep.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU General Public License, v2
 */
package org.spoofax;

public class NotImplementedException extends RuntimeException {
    
    public NotImplementedException() {
        super("Not Implemented");
    }
    
    public NotImplementedException(String message) {
        super(message);
    }

    private static final long serialVersionUID = -1028814795329444374L;
}
