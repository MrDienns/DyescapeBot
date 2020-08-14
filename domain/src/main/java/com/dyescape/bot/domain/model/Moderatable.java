package com.dyescape.bot.domain.model;

import java.time.Instant;

public interface Moderatable {

    void warn(Server server, int points, String reason, User givenBy);
    int getActiveWarningPoints(Server server);

    void kick(Server server, String reason);

    void mute(Server server, Instant expiryTime, String reason);
    boolean isMuted(Server server);

    void ban(Server server, Instant expiryTime, String reason);
    boolean isBanned(Server server);
}
