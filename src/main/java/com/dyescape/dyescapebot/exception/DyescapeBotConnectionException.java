package com.dyescape.dyescapebot.exception;

/**
 * Exception thrown when the bot had issues connecting to the gateway.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
public class DyescapeBotConnectionException extends Exception {

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public DyescapeBotConnectionException(Exception cause) {
        super(cause);
    }
}
