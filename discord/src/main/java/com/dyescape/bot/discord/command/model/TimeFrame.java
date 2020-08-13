package com.dyescape.bot.discord.command.model;

public class TimeFrame {

    private final String originalFormat;

    public TimeFrame(String originalFormat) {
        this.originalFormat = originalFormat;
    }

    public String getOriginalFormat() {
        return this.originalFormat;
    }
}
