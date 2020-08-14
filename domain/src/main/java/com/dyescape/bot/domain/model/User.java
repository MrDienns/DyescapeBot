package com.dyescape.bot.domain.model;

public interface User extends Identified, Permissible, Moderatable {

    String getName();
}
