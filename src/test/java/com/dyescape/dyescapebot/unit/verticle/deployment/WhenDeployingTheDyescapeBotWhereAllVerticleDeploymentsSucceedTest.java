package com.dyescape.dyescapebot.unit.verticle.deployment;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.dyescape.dyescapebot.DyescapeBot;
import com.dyescape.dyescapebot.binder.ApplicationVerticlesBinder;
import com.dyescape.dyescapebot.binder.InjectorProviderBinder;
import com.dyescape.dyescapebot.constant.Events;
import com.dyescape.dyescapebot.provider.ApplicationVerticleProvider;
import com.dyescape.dyescapebot.provider.InjectorProvider;
import com.dyescape.dyescapebot.unit.verticle.helper.SucceedingBlankVerticle;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(VertxExtension.class)
@DisplayName("When deploying the Dyescape Bot")
public class WhenDeployingTheDyescapeBotWhereAllVerticleDeploymentsSucceedTest {

    private static Injector injector;

    @BeforeAll
    public static void setup() {
        Verticle testVerticle = new SucceedingBlankVerticle();

        List<Class<? extends Verticle>> verticles = new ArrayList<>();
        verticles.add(testVerticle.getClass());

        injector = Guice.createInjector(
                new ApplicationVerticlesBinder(),
                new InjectorProviderBinder()
        );
    }

    @Test
    @DisplayName("It should complete the startup future successfully")
    public void itShouldCompleteTheStartupFutureSuccessfully(Vertx vertx, VertxTestContext testContext) {
        ApplicationVerticleProvider applicationVerticleProvider = Mockito.mock(ApplicationVerticleProvider.class);
        Mockito.when(applicationVerticleProvider.getApplicationVerticles()).thenReturn(new ArrayList<>());

        DyescapeBot dyescapeBot = new DyescapeBot(injector.getInstance(InjectorProvider.class),
                applicationVerticleProvider);
        vertx.deployVerticle(dyescapeBot, (handler) -> {
            assertTrue(handler.succeeded());
            testContext.completeNow();
        });
    }

    @Test
    @DisplayName("It should deploy the provided list of Verticles")
    public void itShouldDeployTheProvidedListOfVerticles(Vertx vertx, VertxTestContext testContext) {
        List<Class<? extends Verticle>> verticles = new ArrayList<>();
        verticles.add(SucceedingBlankVerticle.class);

        ApplicationVerticleProvider applicationVerticleProvider = Mockito.mock(ApplicationVerticleProvider.class);
        Mockito.when(applicationVerticleProvider.getApplicationVerticles()).thenReturn(verticles);

        DyescapeBot dyescapeBot = new DyescapeBot(injector.getInstance(InjectorProvider.class),
                applicationVerticleProvider);

        vertx.eventBus().consumer(Events.STARTUP_EVENT, handler -> {
            testContext.completeNow();
        });

        vertx.deployVerticle(dyescapeBot);
    }
}
