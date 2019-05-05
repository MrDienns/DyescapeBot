package com.dyescape.dyescapebot.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import com.dyescape.dyescapebot.provider.StatelessSessionProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DatabaseConnectorVerticle extends AbstractVerticle {

    // -------------------------------------------- //
    // LOGGER
    // -------------------------------------------- //

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // -------------------------------------------- //
    // DEPENDENCIES
    // -------------------------------------------- //

    private final StatelessSessionProvider statelessSessionProvider;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    @Inject
    public DatabaseConnectorVerticle(StatelessSessionProvider statelessSessionProvider) {
        this.statelessSessionProvider = statelessSessionProvider;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void start(Future<Void> future) {

        this.logger.debug("Initiating database connection...");
        this.statelessSessionProvider.getStatelessSession(this.vertx, handler -> {

            if (handler.succeeded()) {
                future.complete();
            } else {
                future.fail(handler.cause());
            }
        });
    }
}

