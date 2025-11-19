package de.fallenstar.core.event;

import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für den EventHandler.
 *
 * <p>Testet grundlegende EventHandler-Funktionalität ohne Bukkit-Server-Dependency.</p>
 *
 * <p><b>Hinweis:</b> Tests die Bukkit.getPluginManager() benötigen (Listener-Registrierung)
 * werden nur in Integration-Tests mit MockBukkit oder einem laufenden Server getestet.</p>
 */
class EventHandlerTest {

    private EventHandler eventHandler;
    private Plugin plugin;

    @BeforeEach
    void setUp() {
        plugin = mock(Plugin.class);
        when(plugin.isEnabled()).thenReturn(true);
        when(plugin.getName()).thenReturn("FallenStarCore");

        eventHandler = new EventHandler(plugin);
    }

    @Test
    void testConstructor_ThrowsExceptionForNullPlugin() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new EventHandler(null);
        }, "Sollte IllegalArgumentException bei null Plugin werfen");
    }

    @Test
    void testGetListenerCount_InitiallyZero() {
        // Assert
        assertEquals(0, eventHandler.getListenerCount(), "Initial sollten 0 Listener registriert sein");
    }

    @Test
    void testGetEventStatistics_ReturnsEmptyInitially() {
        // Act
        Map<String, Integer> statistics = eventHandler.getEventStatistics();

        // Assert
        assertNotNull(statistics);
        assertTrue(statistics.isEmpty(), "Sollte initial leer sein");
    }

    @Test
    void testGetEventStatistics_ReturnsUnmodifiableMap() {
        // Act
        Map<String, Integer> statistics = eventHandler.getEventStatistics();

        // Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            statistics.put("TestEvent", 1);
        }, "Sollte unmodifiable sein");
    }

    @Test
    void testResetStatistics_ClearsStatistics() {
        // Act
        eventHandler.resetStatistics();

        // Assert
        Map<String, Integer> statistics = eventHandler.getEventStatistics();
        assertTrue(statistics.isEmpty(), "Statistiken sollten nach Reset leer sein");
    }

    @Test
    void testGetDebugInfo_ReturnsInformation() {
        // Act
        String debugInfo = eventHandler.getDebugInfo();

        // Assert
        assertNotNull(debugInfo);
        assertTrue(debugInfo.contains("Registrierte Event-Listener"), "Sollte Header enthalten");
        assertTrue(debugInfo.contains("Event-Statistiken"), "Sollte Statistik-Bereich enthalten");
    }

    @Test
    void testIsRegistered_HandlesNull() {
        // Act & Assert
        assertFalse(eventHandler.isRegistered(null), "Sollte false bei null zurückgeben");
    }

    @Test
    void testUnregisterListener_HandlesNull() {
        // Act
        boolean result = eventHandler.unregisterListener(null);

        // Assert
        assertFalse(result, "Sollte false bei null zurückgeben");
    }

    @Test
    void testGetRegisteredListeners_InitiallyEmpty() {
        // Act
        var listeners = eventHandler.getRegisteredListeners();

        // Assert
        assertNotNull(listeners);
        assertTrue(listeners.isEmpty(), "Sollte initial leer sein");
    }

    @Test
    void testGetRegisteredListeners_ReturnsUnmodifiableSet() {
        // Act
        var listeners = eventHandler.getRegisteredListeners();

        // Assert - Verify unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> {
            listeners.add(null);
        }, "Sollte unmodifiable sein");
    }

    /*
     * HINWEIS: Tests die Listener-Registrierung testen würden,
     * benötigen einen gemockten Bukkit-Server (Bukkit.getPluginManager()).
     *
     * Diese Tests sollten als Integration-Tests mit MockBukkit
     * oder einem laufenden Test-Server durchgeführt werden.
     *
     * Beispiele für zu testende Funktionalität:
     * - registerListener(Listener) mit Bukkit-Listener
     * - registerListener(Class, Consumer) mit funktionalem Listener
     * - registerListener(Class, EventPriority, Consumer)
     * - unregisterListener(Listener) mit registrierten Listenern
     * - unregisterAll() mit mehreren Listenern
     * - isRegistered(Listener) mit registrierten Listenern
     */
}
