package com.dyescape.dyescapebot.moderation;

public interface Moderation {

    void warn(long serverId, long userId, String reason);

    void kick(long serverId, long userId, String reason);

    void mute(long serverId, long userId, String reason);
    void tempmute(long serverId, long userId, String reason, long punishmentTime);

    void ban(long serverId, long userId, String reason);
    void tempban(long serverId, long userId, String reason, long punishmentTime);
}
