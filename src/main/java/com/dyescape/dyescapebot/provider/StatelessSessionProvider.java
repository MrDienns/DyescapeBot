package com.dyescape.dyescapebot.provider;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import org.hibernate.StatelessSession;

public interface StatelessSessionProvider {

    void getStatelessSession(Vertx vertx, Handler<AsyncResult<StatelessSession>> resultHandler);
}
