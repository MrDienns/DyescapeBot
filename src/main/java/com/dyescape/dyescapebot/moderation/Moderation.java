package com.dyescape.dyescapebot.moderation;

import java.time.LocalDateTime;

public interface Moderation {

    void warn(long serverId, long userId, String reason);

    void kick(long serverId, long userId, String reason);

    void mute(long serverId, long userId, String reason);
    void tempmute(long serverId, long userId, String reason, LocalDateTime tillDateTime);

    void ban(long serverId, long userId, String reason);
    void tempban(long serverId, long userId, String reason, LocalDateTime tillDateTime);
}
