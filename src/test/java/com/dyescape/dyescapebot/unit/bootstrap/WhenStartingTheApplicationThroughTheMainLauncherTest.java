package com.dyescape.dyescapebot.unit.bootstrap;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.dyescape.dyescapebot.DyescapeBotLauncher;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
@DisplayName("When starting Dyescape Bot through the main launcher")
public class WhenStartingTheApplicationThroughTheMainLauncherTest {

    @BeforeAll
    public static void setup() {
        DyescapeBotLauncher.main(new String[] {});
    }

    @Test
    @DisplayName("It should configure the Log4j system property")
    public void itShouldConfigureTheLog4jSystemProperty() {
        assertEquals(System.getProperty("vertx.logger-delegate-factory-class-name"),
                "io.vertx.core.logging.Log4jLogDelegateFactory");
    }

    @Test
    @DisplayName("It should register the Guice Verticle factory")
    public void itShouldRegisterTheGuiceVerticleFactory(Vertx vertx) {
        DyescapeBotLauncher launcher = new DyescapeBotLauncher();
        launcher.afterStartingVertx(vertx);
        assertEquals(vertx.verticleFactories().size(), 1);
    }
}
