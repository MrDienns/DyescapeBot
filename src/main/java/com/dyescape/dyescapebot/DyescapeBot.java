package com.dyescape.dyescapebot;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
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
 * @author Dennis van der Veeke - Owner & Lead Developer of Dyescape
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
        List<Future> deploymentFutures = new ArrayList<>();

        for (Class<? extends Verticle> clazz : this.applicationVerticleProvider.getApplicationVerticles()) {
            Future deploymentFuture = Future.future();
            deploymentFutures.add(deploymentFuture);
            this.getVertx().deployVerticle(this.injectorProvider.getInjector().getInstance(clazz), deploymentFuture);
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
    }
}
