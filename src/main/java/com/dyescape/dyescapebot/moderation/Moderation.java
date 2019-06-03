package com.dyescape.dyescapebot.moderation;

public interface Moderation {

    void warn(long serverId, long userId, String reason) throws Exception;

    void kick(long serverId, long userId, String reason) throws Exception;

    void mute(long serverId, long userId, String reason) throws Exception;
    void tempmute(long serverId, long userId, String reason, long punishmentTime) throws Exception;
    void unmute(long serverId, long userId) throws Exception;

    void channelMute(long serverId, long userId, long channelId, String reason) throws Exception;
    void channelTempMute(long serverId, long userId, long channelId, String reason, long punishmentTime) throws Exception;
    void channelBan(long serverId, long userId, long channelId, String reason) throws Exception;
    void channelTempBan(long serverId, long userId, long channelId, String reason, long punishmentTime) throws Exception;

    void ban(long serverId, long userId, String reason) throws Exception;
    void tempban(long serverId, long userId, String reason, long punishmentTime) throws Exception;
    void unban(long serverId, long userId) throws Exception;
}
