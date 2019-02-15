package com.dyescape.dyescapebot.binder;

import com.google.inject.AbstractModule;

import com.dyescape.dyescapebot.provider.InjectorProvider;
import com.dyescape.dyescapebot.provider.InjectorProviderImpl;

/**
 * InjectorProviderBinder is an extension of Guice's AbstractModule which
 * is used to bind the {@link InjectorProvider} interface to the
 * implementation of {@link InjectorProviderImpl} when injecting the
 * {@link InjectorProvider} interface.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
public class InjectorProviderBinder extends AbstractModule {

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    protected void configure() {
        this.bind(InjectorProvider.class).to(InjectorProviderImpl.class);
    }
}
