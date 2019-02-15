package com.dyescape.dyescapebot.exception;

/**
 * Exception thrown when there's an issue with the configuration. Causes
 * may be directed to missing configuration values, or values being
 * incorrect.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
public class DyescapeBotConfigurationException extends RuntimeException {

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public DyescapeBotConfigurationException(String message) {
        super(message);
    }
}
