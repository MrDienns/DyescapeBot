package com.dyescape.dyescapebot.binder;

import com.dyescape.dyescapebot.provider.StatelessSessionProvider;
import com.dyescape.dyescapebot.provider.StatelessSessionProviderImpl;

import com.google.inject.AbstractModule;

public class StatelessSessionProviderBinder extends AbstractModule {

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    protected void configure() {
        this.bind(StatelessSessionProvider.class).to(StatelessSessionProviderImpl.class);
    }
}
