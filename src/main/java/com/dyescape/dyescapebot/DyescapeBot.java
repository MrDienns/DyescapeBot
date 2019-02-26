package com.dyescape.dyescapebot;

import java.util.ArrayList;
import java.util.List;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import com.google.inject.Inject;

import com.dyescape.dyescapebot.constant.Events;
import com.dyescape.dyescapebot.provider.ApplicationVerticleProvider;
import com.dyescape.dyescapebot.provider.InjectorProvider;

/**
 * Main startup Verticle of the Dyescape Bot, responsible for
 * the startup process of the application, such as deploying all
 * other application Verticles and making sure all are working as
 * they should.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
public class DyescapeBot extends AbstractVerticle {

    // -------------------------------------------- //
    // LOGGER
    // -------------------------------------------- //

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // -------------------------------------------- //
    // DEPENDENCIES
    // -------------------------------------------- //

    private final InjectorProvider injectorProvider;
    private final ApplicationVerticleProvider applicationVerticleProvider;

    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    @Inject
    public DyescapeBot(InjectorProvider injectorProvider,
                       ApplicationVerticleProvider applicationVerticleProvider) {
        this.injectorProvider = injectorProvider;
        this.applicationVerticleProvider = applicationVerticleProvider;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void start(Future<Void> future) {

        // We prepare a list of deployment futures
        List<Future> deploymentFutures = new ArrayList<>();

        // Let's load the configuration
        ConfigRetriever configRetriever = ConfigRetriever.create(this.getVertx());
        configRetriever.getConfig(configHandler -> {

            // Check if the configuration could be retrieved at all
            if (!configHandler.succeeded()) {
                future.fail(configHandler.cause());
                return;
            }

            // Get the actual configuration now
            JsonObject config = configHandler.result();

            // We loop over all Verticles that this application needs to run
            for (Class<? extends Verticle> clazz : this.applicationVerticleProvider.getApplicationVerticles()) {
                Future deploymentFuture = Future.future();
                deploymentFutures.add(deploymentFuture);
                this.getVertx().deployVerticle(this.injectorProvider.getInjector().getInstance(clazz),
                        new DeploymentOptions().setConfig(config), deploymentFuture);
            }

            CompositeFuture.all(deploymentFutures).setHandler(deploymentHandler -> {
                if (deploymentHandler.succeeded()) {

                    this.getVertx().eventBus().publish(Events.STARTUP_EVENT, null);
                    future.complete();
                    this.logger.info("Finished application startup");
                } else {

                    future.fail(deploymentHandler.cause());
                    this.logger.error("Failed application startup!");
                }
            });
        });
    }
}
