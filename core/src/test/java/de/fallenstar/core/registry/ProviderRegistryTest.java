package de.fallenstar.core.registry;

import de.fallenstar.core.provider.*;
import de.fallenstar.core.provider.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests für die ProviderRegistry.
 *
 * <p>Testet die Registrierung, Deregistrierung und Abfrage von Providern,
 * sowie das Graceful Degradation-Verhalten mit NoOp-Providern.</p>
 */
class ProviderRegistryTest {

    private ProviderRegistry registry;

    @BeforeEach
    void setUp() {
        // Erstelle neue Registry-Instanz für jeden Test
        registry = ProviderRegistry.getInstance();
        registry.clearAll();
    }

    @Test
    void testGetInstance_ReturnsSingleton() {
        // Arrange & Act
        ProviderRegistry instance1 = ProviderRegistry.getInstance();
        ProviderRegistry instance2 = ProviderRegistry.getInstance();

        // Assert
        assertSame(instance1, instance2, "getInstance() sollte immer dieselbe Instanz zurückgeben");
    }

    @Test
    void testRegister_RegistersProvider() {
        // Arrange
        PlotProvider plotProvider = new NoOpPlotProvider();

        // Act
        registry.register(PlotProvider.class, plotProvider);

        // Assert
        assertTrue(registry.isRegistered(PlotProvider.class), "Provider sollte registriert sein");
        assertSame(plotProvider, registry.get(PlotProvider.class), "get() sollte registrierten Provider zurückgeben");
    }

    @Test
    void testRegister_OverwritesExistingProvider() {
        // Arrange
        PlotProvider provider1 = new NoOpPlotProvider();
        PlotProvider provider2 = new NoOpPlotProvider();

        // Act
        registry.register(PlotProvider.class, provider1);
        registry.register(PlotProvider.class, provider2);

        // Assert
        assertSame(provider2, registry.get(PlotProvider.class), "Zweiter Provider sollte ersten überschreiben");
    }

    @Test
    void testRegister_ThrowsExceptionForNullProviderClass() {
        // Arrange
        PlotProvider provider = new NoOpPlotProvider();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(null, provider);
        }, "register() sollte IllegalArgumentException bei null Provider-Class werfen");
    }

    @Test
    void testRegister_ThrowsExceptionForNullProvider() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(PlotProvider.class, null);
        }, "register() sollte IllegalArgumentException bei null Provider werfen");
    }

    @Test
    void testUnregister_RemovesProvider() {
        // Arrange
        PlotProvider plotProvider = new NoOpPlotProvider();
        registry.register(PlotProvider.class, plotProvider);

        // Act
        registry.unregister(PlotProvider.class);

        // Assert
        assertFalse(registry.isRegistered(PlotProvider.class), "Provider sollte nicht mehr registriert sein");
    }

    @Test
    void testUnregister_ThrowsExceptionForNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            registry.unregister(null);
        }, "unregister() sollte IllegalArgumentException bei null werfen");
    }

    @Test
    void testGet_ReturnsNoOpProvider_WhenNotRegistered() {
        // Act
        PlotProvider plotProvider = registry.get(PlotProvider.class);

        // Assert
        assertNotNull(plotProvider, "get() sollte niemals null zurückgeben");
        assertInstanceOf(NoOpPlotProvider.class, plotProvider, "get() sollte NoOp-Provider zurückgeben wenn nicht registriert");
        assertFalse(plotProvider.isEnabled(), "NoOp-Provider sollte disabled sein");
    }

    @Test
    void testGet_ReturnsNoOpEconomyProvider_WhenNotRegistered() {
        // Act
        EconomyProvider economyProvider = registry.get(EconomyProvider.class);

        // Assert
        assertNotNull(economyProvider, "get() sollte niemals null zurückgeben");
        assertInstanceOf(NoOpEconomyProvider.class, economyProvider);
        assertEquals("NoOp", economyProvider.getProviderName());
    }

    @Test
    void testGet_ReturnsNoOpNPCProvider_WhenNotRegistered() {
        // Act
        NPCProvider npcProvider = registry.get(NPCProvider.class);

        // Assert
        assertNotNull(npcProvider, "get() sollte niemals null zurückgeben");
        assertInstanceOf(NoOpNPCProvider.class, npcProvider);
        assertEquals("NoOp", npcProvider.getProviderName());
    }

    @Test
    void testGet_ReturnsNoOpItemProvider_WhenNotRegistered() {
        // Act
        ItemProvider itemProvider = registry.get(ItemProvider.class);

        // Assert
        assertNotNull(itemProvider, "get() sollte niemals null zurückgeben");
        assertInstanceOf(NoOpItemProvider.class, itemProvider);
        assertEquals("NoOp", itemProvider.getProviderName());
    }

    @Test
    void testGet_ThrowsExceptionForNullProviderClass() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            registry.get(null);
        }, "get() sollte IllegalArgumentException bei null werfen");
    }

    @Test
    void testGetOptional_ReturnsEmpty_WhenNotRegistered() {
        // Act
        var optional = registry.getOptional(PlotProvider.class);

        // Assert
        assertTrue(optional.isEmpty(), "getOptional() sollte leeres Optional zurückgeben wenn nicht registriert");
    }

    @Test
    void testGetOptional_ReturnsProvider_WhenRegistered() {
        // Arrange
        PlotProvider plotProvider = new NoOpPlotProvider();
        registry.register(PlotProvider.class, plotProvider);

        // Act
        var optional = registry.getOptional(PlotProvider.class);

        // Assert
        assertTrue(optional.isPresent(), "getOptional() sollte Provider zurückgeben wenn registriert");
        assertSame(plotProvider, optional.get());
    }

    @Test
    void testGetOptional_ReturnsEmpty_ForNull() {
        // Act
        var optional = registry.getOptional(null);

        // Assert
        assertTrue(optional.isEmpty(), "getOptional() sollte leeres Optional bei null zurückgeben");
    }

    @Test
    void testIsRegistered_ReturnsFalse_WhenNotRegistered() {
        // Act & Assert
        assertFalse(registry.isRegistered(PlotProvider.class), "isRegistered() sollte false zurückgeben wenn nicht registriert");
    }

    @Test
    void testIsRegistered_ReturnsTrue_WhenRegistered() {
        // Arrange
        PlotProvider plotProvider = new NoOpPlotProvider();
        registry.register(PlotProvider.class, plotProvider);

        // Act & Assert
        assertTrue(registry.isRegistered(PlotProvider.class), "isRegistered() sollte true zurückgeben wenn registriert");
    }

    @Test
    void testClearAll_RemovesAllProviders() {
        // Arrange
        registry.register(PlotProvider.class, new NoOpPlotProvider());
        registry.register(EconomyProvider.class, new NoOpEconomyProvider());
        registry.register(NPCProvider.class, new NoOpNPCProvider());

        // Act
        registry.clearAll();

        // Assert
        assertEquals(0, registry.getProviderCount(), "Nach clearAll() sollten keine Provider registriert sein");
        assertFalse(registry.isRegistered(PlotProvider.class));
        assertFalse(registry.isRegistered(EconomyProvider.class));
        assertFalse(registry.isRegistered(NPCProvider.class));
    }

    @Test
    void testGetProviderCount_ReturnsCorrectCount() {
        // Arrange
        assertEquals(0, registry.getProviderCount(), "Initial sollten 0 Provider registriert sein");

        registry.register(PlotProvider.class, new NoOpPlotProvider());
        assertEquals(1, registry.getProviderCount());

        registry.register(EconomyProvider.class, new NoOpEconomyProvider());
        assertEquals(2, registry.getProviderCount());

        registry.unregister(PlotProvider.class);
        assertEquals(1, registry.getProviderCount());
    }

    @Test
    void testGetDebugInfo_ReturnsProviderInformation() {
        // Arrange
        registry.register(PlotProvider.class, new NoOpPlotProvider());
        registry.register(EconomyProvider.class, new NoOpEconomyProvider());

        // Act
        String debugInfo = registry.getDebugInfo();

        // Assert
        assertNotNull(debugInfo);
        assertTrue(debugInfo.contains("PlotProvider"), "Debug-Info sollte PlotProvider enthalten");
        assertTrue(debugInfo.contains("EconomyProvider"), "Debug-Info sollte EconomyProvider enthalten");
        assertTrue(debugInfo.contains("NoOpPlotProvider"), "Debug-Info sollte Implementierungs-Namen enthalten");
    }
}
