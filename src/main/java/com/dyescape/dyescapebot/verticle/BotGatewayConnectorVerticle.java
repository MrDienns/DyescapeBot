package com.dyescape.dyescapebot.verticle;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import com.dyescape.dyescapebot.constant.Config;
import com.dyescape.dyescapebot.core.connectivity.BotConnection;
import com.dyescape.dyescapebot.exception.DyescapeBotConfigurationException;

/**
 * Verticle used to initiate a connection to the gateway.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
@Singleton
public class BotGatewayConnectorVerticle extends AbstractVerticle {

    // -------------------------------------------- //
    // LOGGER
    // -------------------------------------------- //

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // -------------------------------------------- //
    // DEPENDENCIES
    // -------------------------------------------- //

    private final BotConnection botConnection;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    @Inject
    public BotGatewayConnectorVerticle(BotConnection botConnection) {
        this.botConnection = botConnection;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void start(Future<Void> future) {

        // Let's load the configuration
        this.logger.debug("Loading configuration to retrieve token...");
        ConfigRetriever configRetriever = ConfigRetriever.create(this.getVertx());
        configRetriever.getConfig(configHandler -> {

            // Check if the configuration could be retrieved at all
            if (!configHandler.succeeded()) {
                future.fail(configHandler.cause());
                return;
            }

            // Parse the configuration and validate the token
            JsonObject config = configHandler.result();
            String apiToken = config.getString(Config.API_TOKEN);
            if (apiToken == null || apiToken.isEmpty()) {
                future.fail(new DyescapeBotConfigurationException("No API token is configured."));
                return;
            }

            // Initiate the connection
            this.logger.debug("Token loaded, connecting to the gateway...");
            this.botConnection.connect(apiToken, handler -> {

                // Let's check the result
                if (handler.succeeded()) {

                    this.logger.info("Successfully connected to the gateway.");
                    future.complete();
                } else {

                    future.fail(handler.cause());
                }
            });
        });
    }
}
