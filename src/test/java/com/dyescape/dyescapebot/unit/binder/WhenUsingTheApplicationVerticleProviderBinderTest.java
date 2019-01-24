package com.dyescape.dyescapebot.unit.binder;

import javax.inject.Inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dyescape.dyescapebot.binder.ApplicationVerticlesBinder;
import com.dyescape.dyescapebot.provider.ApplicationVerticleProvider;
import com.dyescape.dyescapebot.provider.ApplicationVerticleProviderImpl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("When starting the application with the ApplicationVerticleBinder")
public class WhenUsingTheApplicationVerticleProviderBinderTest {

    @Inject
    private ApplicationVerticleProvider applicationVerticleProvider;

    @BeforeEach
    public void setup() {
        Injector injector = Guice.createInjector(new ApplicationVerticlesBinder());
        injector.injectMembers(this);
    }

    @Test
    @DisplayName("It should inject ApplicationVerticleProvider instances")
    public void itShouldInjectApplicationVerticleProviderInstances() {
        assertNotNull(this.applicationVerticleProvider);
    }

    @Test
    @DisplayName("It should use the correct implementation")
    public void itShouldUseTheCorrectImplementation() {
        assertTrue(this.applicationVerticleProvider instanceof ApplicationVerticleProviderImpl);
    }
}
