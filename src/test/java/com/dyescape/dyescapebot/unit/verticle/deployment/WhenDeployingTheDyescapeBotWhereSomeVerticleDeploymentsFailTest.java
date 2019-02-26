package com.dyescape.dyescapebot.unit.verticle.deployment;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import com.dyescape.dyescapebot.DyescapeBot;
import com.dyescape.dyescapebot.provider.ApplicationVerticleProvider;
import com.dyescape.dyescapebot.provider.InjectorProviderImpl;
import com.dyescape.dyescapebot.unit.verticle.helper.FailingBlankVerticle;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(VertxExtension.class)
@DisplayName("When deploying the DyescapeBot where some Verticle deployments fail")
public class WhenDeployingTheDyescapeBotWhereSomeVerticleDeploymentsFailTest {

    private static boolean succeeded;

    @BeforeAll
    public static void setup(Vertx vertx, VertxTestContext context) {

        ApplicationVerticleProvider verticleProvider = mock(ApplicationVerticleProvider.class);
        List<Class<? extends Verticle>> verticles = new ArrayList<>();
        verticles.add(FailingBlankVerticle.class);
        when(verticleProvider.getApplicationVerticles()).thenReturn(verticles);

        DyescapeBot bot = new DyescapeBot(new InjectorProviderImpl(), verticleProvider);
        vertx.deployVerticle(bot, handler -> {

            succeeded = handler.succeeded();

            context.completeNow();
        });
    }

    @Test
    @DisplayName("It should fail the DyescapeBot verticle deployment")
    public void itShouldFailTheDyescapeBotVerticleDeployment() {
        assertFalse(succeeded);
    }
}
