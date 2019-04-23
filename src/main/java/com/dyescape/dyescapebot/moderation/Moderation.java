package com.dyescape.dyescapebot.moderation;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface Moderation {

    void warn(long serverId, long userId, String reason, Handler<AsyncResult<Void>> handler);

    void kick(long serverId, long userId, String reason, Handler<AsyncResult<Void>> handler);

    void mute(long serverId, long userId, String reason, Handler<AsyncResult<Void>> handler);
    void tempmute(long serverId, long userId, String reason, long punishmentTime, Handler<AsyncResult<Void>> handler);

    void ban(long serverId, long userId, String reason, Handler<AsyncResult<Void>> handler);
    void tempban(long serverId, long userId, String reason, long punishmentTime, Handler<AsyncResult<Void>> handler);
}
