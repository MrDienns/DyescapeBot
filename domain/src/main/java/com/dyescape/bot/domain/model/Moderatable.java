package com.dyescape.bot.domain.model;

public interface Moderatable {

    void unwarn(Server server);
    void warn(Server server, int points, String reason, User givenBy);
    int getActiveWarningPoints(Server server);

    void kick(Server server, String reason, User givenBy);

    void unmute(Server server);
    void mute(Server server, TimeFrame timeFrame, String reason, User givenBy);
    boolean isMuted(Server server);

    void unban(Server server);
    void ban(Server server, TimeFrame timeFrame, String reason, User givenBy);
    boolean isBanned(Server server);
}
