package com.dyescape.dyescapebot.provider;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import com.dyescape.dyescapebot.binder.ApplicationVerticlesBinder;
import com.dyescape.dyescapebot.binder.BotConnectionBinder;
import com.dyescape.dyescapebot.binder.InjectorProviderBinder;
import com.dyescape.dyescapebot.binder.StatelessSessionProviderBinder;

/**
 * Base implementation of the {@link InjectorProvider}
 * interface. This is the base implementation of a deployed scenario
 * as all known application specific binders are registered in this
 * {@link Injector}.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
@Singleton
public class InjectorProviderImpl implements InjectorProvider {

    // -------------------------------------------- //
    // STATIC
    // -------------------------------------------- //

    private final static Injector injector = Guice.createInjector(
            new ApplicationVerticlesBinder(),
            new InjectorProviderBinder(),
            new BotConnectionBinder(),
            new StatelessSessionProviderBinder()
    );

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public Injector getInjector() {
        return injector;
    }
}
