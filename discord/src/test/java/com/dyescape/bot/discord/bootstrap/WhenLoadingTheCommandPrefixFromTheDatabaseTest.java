package com.dyescape.bot.discord.bootstrap;

import com.dyescape.bot.data.entity.data.ServerEntity;
import com.dyescape.bot.data.repository.ServerRepository;
import com.dyescape.bot.discord.command.ServerPrefixProvider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("When loading the command prefix from a chat message")
public class WhenLoadingTheCommandPrefixFromTheDatabaseTest {

    @Test
    @DisplayName("It should return the configured prefix")
    public void itShouldReturnTheConfiguredPrefix() {
        ServerEntity mockedQueryResult = new ServerEntity("286476446338252800", "?", null, null, null);
        ServerRepository repository = Mockito.mock(ServerRepository.class);
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.of(mockedQueryResult));

        ServerPrefixProvider prefixProvider = new ServerPrefixProvider(repository);
        String actual = prefixProvider.getPrefix("286476446338252800");

        assertEquals("?", actual, "Prefix provider did not return the configured prefix");
    }
}
