package com.dyescape.bot.discord.command.resolver.processor;

import com.dyescape.bot.domain.model.TimeFrame;

public class TimeFrameProcessor implements ArgumentProcessor<TimeFrame> {

    @Override
    public TimeFrame process(String argument) {
        return new TimeFrame(argument);
    }
}
