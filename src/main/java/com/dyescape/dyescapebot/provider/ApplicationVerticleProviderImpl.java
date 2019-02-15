package com.dyescape.dyescapebot.provider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import io.vertx.core.Verticle;

import com.dyescape.dyescapebot.verticle.BotGatewayConnectorVerticle;

/**
 * Base implementation of the {@link ApplicationVerticleProvider}
 * interface. In here, the actual production scenario application
 * Verticles are specified to the application can operate.
 * @author Dennis van der Veeke - Owner & Lead Developer of Dyescape
 * @since 0.1.0
 */
@Singleton
public class ApplicationVerticleProviderImpl implements ApplicationVerticleProvider {

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public List<Class<? extends Verticle>> getApplicationVerticles() {
        List<Class<? extends Verticle>> verticles = new ArrayList<>();

        verticles.add(BotGatewayConnectorVerticle.class);

        return verticles;
    }
}
