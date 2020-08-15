package com.dyescape.bot.domain.model;

public interface Moderatable {

    void warn(Server server, int points, String reason, User givenBy);
    int getActiveWarningPoints(Server server);

    void kick(Server server, String reason);

    void mute(Server server, TimeFrame timeFrame, String reason);
    boolean isMuted(Server server);

    void ban(Server server, TimeFrame timeFrame, String reason);
    boolean isBanned(Server server);
}
