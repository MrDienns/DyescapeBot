package com.dyescape.dyescapebot.core.connectivity;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Generic, non-platform specific connection wrapper.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
public interface BotConnection {

    void connect(String token, Handler<AsyncResult<Void>> handler);
}
