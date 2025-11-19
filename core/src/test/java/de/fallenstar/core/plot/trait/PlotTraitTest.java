package de.fallenstar.core.plot.trait;

import de.fallenstar.core.plot.Plot;
import de.fallenstar.core.plot.action.PlotAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für Plot-Trait-Interfaces.
 *
 * <p>Testet die Trait-Interfaces mit Fokus auf:</p>
 * <ul>
 *   <li>PlotNamed - Namen-Verwaltung</li>
 *   <li>PlotIsContainerForStorage - Storage-Verwaltung</li>
 *   <li>PlotIsContainerForNpc - NPC-Verwaltung</li>
 *   <li>Trait-Komposition (mehrere Traits kombiniert)</li>
 *   <li>Default-Methoden (getXActions)</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Plot Trait Tests")
class PlotTraitTest {

    @Mock
    private Location location;

    @Mock
    private Inventory inventory;

    @Mock
    private Server server;

    private UUID plotId;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        plotId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
    }

    // ==================== PlotNamed Tests ====================

    @Test
    @DisplayName("PlotNamed: getName und setName funktionieren")
    void testPlotNamed_GetAndSetName() {
        TestPlotNamed plot = new TestPlotNamed(plotId, ownerId, location);

        // Initial Name
        assertEquals("Testplot", plot.getName(), "Initial Name sollte 'Testplot' sein");

        // Setze neuen Namen
        plot.setName("Neuer Name");
        assertEquals("Neuer Name", plot.getName(), "Name sollte geändert worden sein");
    }

    @Test
    @DisplayName("PlotNamed: getNameActions gibt leere Liste zurück (Placeholder)")
    void testPlotNamed_GetNameActionsReturnsEmptyList() {
        TestPlotNamed plot = new TestPlotNamed(plotId, ownerId, location);

        List<PlotAction> actions = plot.getNameActions();

        assertNotNull(actions, "Actions sollten nicht null sein");
        assertTrue(actions.isEmpty(), "Actions sollten leer sein (Placeholder für Phase 9)");
    }

    // ==================== PlotIsContainerForStorage Tests ====================

    @Test
    @DisplayName("PlotIsContainerForStorage: getStorageInventory gibt Inventory zurück")
    void testPlotIsContainerForStorage_GetStorageInventory() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(inventory);

            TestPlotStorage plot = new TestPlotStorage(plotId, ownerId, location);

            Inventory result = plot.getStorageInventory();

            assertNotNull(result, "Storage Inventory sollte nicht null sein");
            bukkit.verify(() -> Bukkit.createInventory(null, 54, "Lager"));
        }
    }

    @Test
    @DisplayName("PlotIsContainerForStorage: getStorageActions gibt leere Liste zurück (Placeholder)")
    void testPlotIsContainerForStorage_GetStorageActionsReturnsEmptyList() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(inventory);

            TestPlotStorage plot = new TestPlotStorage(plotId, ownerId, location);

            List<PlotAction> actions = plot.getStorageActions();

            assertNotNull(actions, "Actions sollten nicht null sein");
            assertTrue(actions.isEmpty(), "Actions sollten leer sein (Placeholder)");
        }
    }

    // ==================== PlotIsContainerForNpc Tests ====================

    @Test
    @DisplayName("PlotIsContainerForNpc: getNpcId und setNpcId funktionieren")
    void testPlotIsContainerForNpc_GetAndSetNpcId() {
        TestPlotNpc plot = new TestPlotNpc(plotId, ownerId, location);

        // Initial null
        assertNull(plot.getNpcId(), "Initial NPC-ID sollte null sein");

        // Setze NPC-ID
        UUID npcId = UUID.randomUUID();
        plot.setNpcId(npcId);
        assertEquals(npcId, plot.getNpcId(), "NPC-ID sollte gesetzt worden sein");

        // Entferne NPC-ID
        plot.setNpcId(null);
        assertNull(plot.getNpcId(), "NPC-ID sollte wieder null sein");
    }

    @Test
    @DisplayName("PlotIsContainerForNpc: getNpcActions gibt leere Liste zurück (Placeholder)")
    void testPlotIsContainerForNpc_GetNpcActionsReturnsEmptyList() {
        TestPlotNpc plot = new TestPlotNpc(plotId, ownerId, location);

        List<PlotAction> actions = plot.getNpcActions();

        assertNotNull(actions, "Actions sollten nicht null sein");
        assertTrue(actions.isEmpty(), "Actions sollten leer sein (Placeholder)");
    }

    // ==================== Trait-Komposition Tests ====================

    @Test
    @DisplayName("Trait-Komposition: Plot kann alle drei Traits implementieren")
    void testTraitComposition_AllThreeTraits() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(inventory);

            TestPlotAllTraits plot = new TestPlotAllTraits(plotId, ownerId, location);

            // PlotNamed
            assertTrue(plot instanceof PlotNamed, "Sollte PlotNamed implementieren");
            assertEquals("Vollständiger Plot", plot.getName());

            // PlotIsContainerForStorage
            assertTrue(plot instanceof PlotIsContainerForStorage, "Sollte PlotIsContainerForStorage implementieren");
            assertNotNull(plot.getStorageInventory());

            // PlotIsContainerForNpc
            assertTrue(plot instanceof PlotIsContainerForNpc, "Sollte PlotIsContainerForNpc implementieren");
            assertNull(plot.getNpcId());
        }
    }

    @Test
    @DisplayName("Trait-Komposition: getAvailablePlotActions kombiniert alle Trait-Actions")
    void testTraitComposition_CombinesAllActions() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(inventory);

            TestPlotAllTraits plot = new TestPlotAllTraits(plotId, ownerId, location);

            List<PlotAction> actions = plot.getAvailablePlotActions();

            assertNotNull(actions, "Actions sollten nicht null sein");
            // Aktuell alle leer (Placeholder), aber Methode funktioniert
            assertEquals(0, actions.size(), "Alle Trait-Actions sind aktuell Placeholder (leer)");
        }
    }

    @Test
    @DisplayName("Trait-Komposition: Jeder Trait kann unabhängig verwendet werden")
    void testTraitComposition_IndependentTraits() {
        TestPlotNamed namedPlot = new TestPlotNamed(plotId, ownerId, location);
        TestPlotNpc npcPlot = new TestPlotNpc(plotId, ownerId, location);

        // PlotNamed funktioniert unabhängig
        namedPlot.setName("Unabhängig");
        assertEquals("Unabhängig", namedPlot.getName());

        // PlotContainerNpc funktioniert unabhängig
        UUID npcId = UUID.randomUUID();
        npcPlot.setNpcId(npcId);
        assertEquals(npcId, npcPlot.getNpcId());
    }

    // ==================== Test-Implementierungen ====================

    /**
     * Test-Plot mit PlotNamed Trait.
     */
    private static class TestPlotNamed implements PlotNamed {
        private final UUID id;
        private final UUID ownerId;
        private final Location location;
        private String name = "Testplot";

        TestPlotNamed(UUID id, UUID ownerId, Location location) {
            this.id = id;
            this.ownerId = ownerId;
            this.location = location;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public UUID getOwnerId() {
            return ownerId;
        }

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public List<PlotAction> getAvailablePlotActions() {
            return new ArrayList<>(getNameActions());
        }
    }

    /**
     * Test-Plot mit PlotIsContainerForStorage Trait.
     */
    private static class TestPlotStorage implements PlotIsContainerForStorage {
        private final UUID id;
        private final UUID ownerId;
        private final Location location;
        private Inventory storageInventory;

        TestPlotStorage(UUID id, UUID ownerId, Location location) {
            this.id = id;
            this.ownerId = ownerId;
            this.location = location;
        }

        @Override
        public Inventory getStorageInventory() {
            if (storageInventory == null) {
                storageInventory = Bukkit.createInventory(null, 54, "Lager");
            }
            return storageInventory;
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public UUID getOwnerId() {
            return ownerId;
        }

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public List<PlotAction> getAvailablePlotActions() {
            return new ArrayList<>(getStorageActions());
        }
    }

    /**
     * Test-Plot mit PlotIsContainerForNpc Trait.
     */
    private static class TestPlotNpc implements PlotIsContainerForNpc {
        private final UUID id;
        private final UUID ownerId;
        private final Location location;
        private UUID npcId;

        TestPlotNpc(UUID id, UUID ownerId, Location location) {
            this.id = id;
            this.ownerId = ownerId;
            this.location = location;
        }

        @Override
        public UUID getNpcId() {
            return npcId;
        }

        @Override
        public void setNpcId(UUID npcId) {
            this.npcId = npcId;
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public UUID getOwnerId() {
            return ownerId;
        }

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public List<PlotAction> getAvailablePlotActions() {
            return new ArrayList<>(getNpcActions());
        }
    }

    /**
     * Test-Plot mit allen drei Traits (Komposition).
     */
    private static class TestPlotAllTraits implements PlotNamed, PlotIsContainerForStorage, PlotIsContainerForNpc {
        private final UUID id;
        private final UUID ownerId;
        private final Location location;
        private String name = "Vollständiger Plot";
        private Inventory storageInventory;
        private UUID npcId;

        TestPlotAllTraits(UUID id, UUID ownerId, Location location) {
            this.id = id;
            this.ownerId = ownerId;
            this.location = location;
        }

        // PlotNamed
        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        // PlotIsContainerForStorage
        @Override
        public Inventory getStorageInventory() {
            if (storageInventory == null) {
                storageInventory = Bukkit.createInventory(null, 54, "Lager: " + name);
            }
            return storageInventory;
        }

        // PlotIsContainerForNpc
        @Override
        public UUID getNpcId() {
            return npcId;
        }

        @Override
        public void setNpcId(UUID npcId) {
            this.npcId = npcId;
        }

        // Plot
        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public UUID getOwnerId() {
            return ownerId;
        }

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public List<PlotAction> getAvailablePlotActions() {
            // Trait-Komposition: Sammle alle Actions aus allen Traits
            return Stream.of(
                getNameActions(),
                getStorageActions(),
                getNpcActions()
            ).flatMap(List::stream).toList();
        }
    }
}
