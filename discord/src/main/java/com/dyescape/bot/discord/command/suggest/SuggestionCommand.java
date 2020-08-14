package com.dyescape.bot.discord.command.suggest;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.CommandAlias;

public class SuggestionCommand extends BaseCommand {

    @CommandAlias("suggest")
    public void onTest(JDACommandEvent e) {
        e.getEvent().getChannel().sendMessage("Hello!").submit();
    }
}
