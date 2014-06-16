package org.unitils.util;



/**
 * MissingKeyException: If one or more resourcebundles doesn't contain a key.
 * 
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
public class MissingKeysException extends Exception {
    
    public MissingKeysException(String message) {
        super(message);
    }

    /** */
    private static final long serialVersionUID = 7281526112203174186L;
    
}
