package com.dyescape.dyescapebot.unit.verticle.helper;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class FailingBlankVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {
        future.fail("What is my purpose? You'll always fail! Oh, my God.");
    }
}
