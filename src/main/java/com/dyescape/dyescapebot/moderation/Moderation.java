package com.dyescape.dyescapebot.moderation;

public interface Moderation {

    void warn(long serverId, long userId, String reason, long punisher) throws Exception;

    void kick(long serverId, long userId, String reason, long punisher) throws Exception;

    void mute(long serverId, long userId, String reason, long punisher) throws Exception;
    void tempmute(long serverId, long userId, String reason, long punishmentTime, long punisher) throws Exception;
    void unmute(long serverId, long userId, long punisher) throws Exception;

    void channelMute(long serverId, long userId, long channelId, String reason, long punisher) throws Exception;
    void channelTempMute(long serverId, long userId, long channelId, String reason, long punishmentTime, long punisher) throws Exception;
    void channelBan(long serverId, long userId, long channelId, String reason, long punisher) throws Exception;
    void channelTempBan(long serverId, long userId, long channelId, String reason, long punishmentTime, long punisher) throws Exception;

    void ban(long serverId, long userId, String reason, long punisher) throws Exception;
    void tempban(long serverId, long userId, String reason, long punishmentTime, long punisher) throws Exception;
    void unban(long serverId, long userId, long punisher) throws Exception;
}
