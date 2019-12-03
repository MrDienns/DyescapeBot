package com.dyescape.dyescapebot.moderation;

import com.dyescape.dyescapebot.entity.discord.Warning;

import java.util.List;

public interface Moderation {

    void warn(long serverId, long userId, int points, String reason, long punisher, long time) throws Exception;
    void pardon(long serverId, long userId);
    void pardon(long serverId, long userId, long warningId, boolean message);
    List<Warning> getWarnings(long serverId, long userId);

    void kick(long serverId, long userId, String reason, long punisher) throws Exception;

    void mute(long serverId, long userId, String reason, long punisher) throws Exception;
    void tempmute(long serverId, long userId, String reason, long punishmentTime, long punisher) throws Exception;
    void unmute(long serverId, long userId) throws Exception;

    void channelMute(long serverId, long userId, long channelId, String reason, long punisher) throws Exception;
    void channelTempMute(long serverId, long userId, long channelId, String reason, long punishmentTime, long punisher) throws Exception;
    void unchannelMute(long serverId, long userId, long channelId);

    void channelBan(long serverId, long userId, long channelId, String reason, long punisher) throws Exception;
    void channelTempBan(long serverId, long userId, long channelId, String reason, long punishmentTime, long punisher) throws Exception;
    void unchannelBan(long serverId, long userId, long channelId);

    void ban(long serverId, long userId, String reason, long punisher) throws Exception;
    void tempban(long serverId, long userId, String reason, long punishmentTime, long punisher) throws Exception;
    void unban(long serverId, long userId) throws Exception;
}
