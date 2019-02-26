package com.dyescape.dyescapebot.unit.provider;

import io.vertx.junit5.VertxExtension;

import com.dyescape.dyescapebot.provider.ApplicationVerticleProviderImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
@DisplayName("When using the ApplicationVerticleProvider")
public class WhenUsingTheApplicationVerticleProviderTest {

    @Test
    @DisplayName("It should not result in any exceptions being thrown")
    public void itShouldNotResultInAnyExceptionsBeingThrown() {
        new ApplicationVerticleProviderImpl().getApplicationVerticles();
    }
}
