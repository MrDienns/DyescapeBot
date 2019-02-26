package com.dyescape.dyescapebot.unit.verticle.helper;

import io.vertx.core.AbstractVerticle;

public class SucceedingBlankVerticle extends AbstractVerticle {

    public static final String DEPLOYED_VERTICLE_ADDRESS = "test_verticle_deployed";

    @Override
    public void start() {
        this.getVertx().eventBus().publish(DEPLOYED_VERTICLE_ADDRESS, this.getClass().getSimpleName());
    }
}
