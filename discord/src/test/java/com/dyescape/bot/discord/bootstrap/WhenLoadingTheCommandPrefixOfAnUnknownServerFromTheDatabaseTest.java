package com.dyescape.bot.discord.bootstrap;

import com.dyescape.bot.data.repository.ServerRepository;
import com.dyescape.bot.discord.command.ServerPrefixProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("When loading the command prefix from an unknown server")
public class WhenLoadingTheCommandPrefixOfAnUnknownServerFromTheDatabaseTest {

    @Test
    @DisplayName("It should return the hardcoded default command prefix")
    public void itShouldReturnTheHardcodedDefaultCommandPrefix() {
        ServerRepository repository = Mockito.mock(ServerRepository.class);
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        ServerPrefixProvider prefixProvider = new ServerPrefixProvider(repository);
        String actual = prefixProvider.getPrefix("286476446338252800");

        assertEquals("!", actual, "Prefix provider did not return the configured prefix");
    }
}
