package com.dyescape.bot.discord.domain;

public class Suggestion {

    private final String url;

    public Suggestion(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}
