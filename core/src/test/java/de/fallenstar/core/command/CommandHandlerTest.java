package de.fallenstar.core.command;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für den CommandHandler.
 *
 * <p>Testet Command-Registrierung, Ausführung, Permission-Checks,
 * Tab-Completion und Fehlerbehandlung.</p>
 */
class CommandHandlerTest {

    private CommandHandler handler;
    private CommandSender sender;
    private org.bukkit.command.Command bukkitCommand;

    @BeforeEach
    void setUp() {
        handler = new CommandHandler("plot");
        sender = mock(CommandSender.class);
        bukkitCommand = mock(org.bukkit.command.Command.class);
    }

    @Test
    void testRegisterSubcommand_RegistersSuccessfully() {
        // Arrange
        Command command = mock(Command.class);

        // Act
        handler.registerSubcommand("set", command);

        // Assert
        assertTrue(handler.hasSubcommand("set"), "Subcommand sollte registriert sein");
        assertEquals(1, handler.getSubcommandCount());
    }

    @Test
    void testRegisterSubcommand_OverwritesExisting() {
        // Arrange
        Command command1 = mock(Command.class);
        Command command2 = mock(Command.class);

        // Act
        handler.registerSubcommand("set", command1);
        handler.registerSubcommand("set", command2);

        // Assert
        assertEquals(1, handler.getSubcommandCount(), "Sollte nur einen Subcommand haben");
        assertEquals(command2, handler.getSubcommand("set").orElse(null),
            "Zweiter Command sollte ersten überschreiben");
    }

    @Test
    void testRegisterSubcommand_ThrowsExceptionForNull() {
        // Arrange
        Command command = mock(Command.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            handler.registerSubcommand(null, command);
        }, "Sollte IllegalArgumentException bei null name werfen");

        assertThrows(IllegalArgumentException.class, () -> {
            handler.registerSubcommand("set", null);
        }, "Sollte IllegalArgumentException bei null command werfen");
    }

    @Test
    void testUnregisterSubcommand_RemovesCommand() {
        // Arrange
        Command command = mock(Command.class);
        handler.registerSubcommand("set", command);

        // Act
        boolean result = handler.unregisterSubcommand("set");

        // Assert
        assertTrue(result, "Sollte true zurückgeben bei erfolgreichem Entfernen");
        assertFalse(handler.hasSubcommand("set"), "Subcommand sollte nicht mehr registriert sein");
        assertEquals(0, handler.getSubcommandCount());
    }

    @Test
    void testUnregisterSubcommand_ReturnsFalseForNonexistent() {
        // Act
        boolean result = handler.unregisterSubcommand("nonexistent");

        // Assert
        assertFalse(result, "Sollte false zurückgeben wenn Command nicht existiert");
    }

    @Test
    void testOnCommand_ExecutesSubcommand() {
        // Arrange
        Command command = mock(Command.class);
        when(command.canExecute(sender)).thenReturn(true);
        when(command.execute(eq(sender), any(String[].class))).thenReturn(true);

        handler.registerSubcommand("set", command);

        // Act
        boolean result = handler.onCommand(sender, bukkitCommand, "plot", new String[]{"set", "arg1"});

        // Assert
        assertTrue(result);
        verify(command).execute(eq(sender), eq(new String[]{"arg1"}));
    }

    @Test
    void testOnCommand_ChecksPermission() {
        // Arrange
        Command command = mock(Command.class);
        when(command.canExecute(sender)).thenReturn(false);
        when(command.getPermission()).thenReturn("plot.set");

        handler.registerSubcommand("set", command);

        // Act
        handler.onCommand(sender, bukkitCommand, "plot", new String[]{"set"});

        // Assert
        verify(command, never()).execute(any(), any());
        verify(sender).sendMessage(contains("keine Berechtigung"));
    }

    @Test
    void testOnCommand_ShowsUsageOnFailure() {
        // Arrange
        Command command = mock(Command.class);
        when(command.canExecute(sender)).thenReturn(true);
        when(command.execute(any(), any())).thenReturn(false);
        when(command.getUsage()).thenReturn("/plot set <name>");

        handler.registerSubcommand("set", command);

        // Act
        handler.onCommand(sender, bukkitCommand, "plot", new String[]{"set"});

        // Assert
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(sender, atLeastOnce()).sendMessage(messageCaptor.capture());

        List<String> messages = messageCaptor.getAllValues();
        assertTrue(messages.stream().anyMatch(msg -> msg.contains("Usage")),
            "Sollte Usage-Message anzeigen");
    }

    @Test
    void testOnCommand_ShowsHelpForUnknownSubcommand() {
        // Act
        handler.onCommand(sender, bukkitCommand, "plot", new String[]{"unknown"});

        // Assert
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(sender, atLeastOnce()).sendMessage(messageCaptor.capture());

        List<String> messages = messageCaptor.getAllValues();
        assertTrue(messages.stream().anyMatch(msg -> msg.contains("Unbekannter")),
            "Sollte Fehler-Message für unbekannten Command anzeigen");
    }

    @Test
    void testOnCommand_ExecutesDefaultCommandWhenNoArgs() {
        // Arrange
        Command defaultCommand = mock(Command.class);
        when(defaultCommand.canExecute(sender)).thenReturn(true);
        when(defaultCommand.execute(eq(sender), any())).thenReturn(true);

        handler.setDefaultCommand(defaultCommand);

        // Act
        handler.onCommand(sender, bukkitCommand, "plot", new String[]{});

        // Assert
        verify(defaultCommand).execute(eq(sender), eq(new String[]{}));
    }

    @Test
    void testOnCommand_ShowsHelpWhenNoArgsAndNoDefault() {
        // Act
        handler.onCommand(sender, bukkitCommand, "plot", new String[]{});

        // Assert
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(sender, atLeastOnce()).sendMessage(messageCaptor.capture());

        List<String> messages = messageCaptor.getAllValues();
        assertTrue(messages.stream().anyMatch(msg -> msg.contains("Commands")),
            "Sollte Hilfe-Header anzeigen");
    }

    @Test
    void testOnTabComplete_SuggestsSubcommands() {
        // Arrange
        Command command1 = mock(Command.class);
        Command command2 = mock(Command.class);
        when(command1.canExecute(sender)).thenReturn(true);
        when(command2.canExecute(sender)).thenReturn(true);

        handler.registerSubcommand("set", command1);
        handler.registerSubcommand("list", command2);

        // Act
        List<String> suggestions = handler.onTabComplete(sender, bukkitCommand, "plot", new String[]{"s"});

        // Assert
        assertNotNull(suggestions);
        assertTrue(suggestions.contains("set"), "Sollte 'set' vorschlagen");
        assertFalse(suggestions.contains("list"), "Sollte 'list' nicht vorschlagen (startet nicht mit 's')");
    }

    @Test
    void testOnTabComplete_DelegatesToSubcommand() {
        // Arrange
        Command command = mock(Command.class);
        when(command.canExecute(sender)).thenReturn(true);
        when(command.tabComplete(eq(sender), any())).thenReturn(List.of("suggestion1", "suggestion2"));

        handler.registerSubcommand("set", command);

        // Act
        List<String> suggestions = handler.onTabComplete(sender, bukkitCommand, "plot",
            new String[]{"set", "arg"});

        // Assert
        assertNotNull(suggestions);
        assertTrue(suggestions.contains("suggestion1"));
        assertTrue(suggestions.contains("suggestion2"));
        verify(command).tabComplete(eq(sender), eq(new String[]{"arg"}));
    }

    @Test
    void testOnTabComplete_FiltersCommandsWithoutPermission() {
        // Arrange
        Command command1 = mock(Command.class);
        Command command2 = mock(Command.class);
        when(command1.canExecute(sender)).thenReturn(true);
        when(command2.canExecute(sender)).thenReturn(false); // Keine Permission

        handler.registerSubcommand("set", command1);
        handler.registerSubcommand("delete", command2);

        // Act
        List<String> suggestions = handler.onTabComplete(sender, bukkitCommand, "plot", new String[]{""});

        // Assert
        assertTrue(suggestions.contains("set"), "Sollte 'set' vorschlagen");
        assertFalse(suggestions.contains("delete"), "Sollte 'delete' nicht vorschlagen (keine Permission)");
    }

    @Test
    void testGetSubcommand_ReturnsOptional() {
        // Arrange
        Command command = mock(Command.class);
        handler.registerSubcommand("set", command);

        // Act
        var result = handler.getSubcommand("set");

        // Assert
        assertTrue(result.isPresent(), "Sollte Command zurückgeben");
        assertEquals(command, result.get());
    }

    @Test
    void testGetSubcommand_ReturnsEmptyForNonexistent() {
        // Act
        var result = handler.getSubcommand("nonexistent");

        // Assert
        assertTrue(result.isEmpty(), "Sollte leeres Optional zurückgeben");
    }

    @Test
    void testGetSubcommandNames_ReturnsAllNames() {
        // Arrange
        handler.registerSubcommand("set", mock(Command.class));
        handler.registerSubcommand("list", mock(Command.class));
        handler.registerSubcommand("delete", mock(Command.class));

        // Act
        var names = handler.getSubcommandNames();

        // Assert
        assertEquals(3, names.size());
        assertTrue(names.contains("set"));
        assertTrue(names.contains("list"));
        assertTrue(names.contains("delete"));
    }

    @Test
    void testClearSubcommands_RemovesAll() {
        // Arrange
        handler.registerSubcommand("set", mock(Command.class));
        handler.registerSubcommand("list", mock(Command.class));

        // Act
        handler.clearSubcommands();

        // Assert
        assertEquals(0, handler.getSubcommandCount());
        assertFalse(handler.hasSubcommand("set"));
        assertFalse(handler.hasSubcommand("list"));
    }

    @Test
    void testSubcommandIsCaseInsensitive() {
        // Arrange
        Command command = mock(Command.class);
        handler.registerSubcommand("SET", command);

        // Assert
        assertTrue(handler.hasSubcommand("set"), "Sollte case-insensitive sein");
        assertTrue(handler.hasSubcommand("SET"), "Sollte case-insensitive sein");
        assertTrue(handler.hasSubcommand("SeT"), "Sollte case-insensitive sein");
        assertEquals(command, handler.getSubcommand("set").orElse(null));
    }
}
