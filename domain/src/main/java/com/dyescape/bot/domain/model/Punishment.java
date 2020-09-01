package com.dyescape.bot.domain.model;

import java.time.Instant;

public interface Punishment {

    String getAction();
    Instant getExpiresAt();
}
