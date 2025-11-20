package de.fallenstar.core.plot;

import de.fallenstar.core.plot.action.AbstractPlotAction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für {@link Plot}.
 *
 * <p>Testet das Basis-Plot-Interface mit Fokus auf:</p>
 * <ul>
 *   <li>Kern-Methoden (getId, getOwnerId, getLocation)</li>
 *   <li>AbstractPlotAction-Integration (getAvailablePlotActions)</li>
 *   <li>Trait-Pattern Komposition</li>
 *   <li>Null-Safety</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Plot Interface Tests")
class PlotTest {

    @Mock
    private World world;

    @Mock
    private Player player;

    private UUID plotId;
    private UUID ownerId;
    private Location location;

    @BeforeEach
    void setUp() {
        plotId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        location = new Location(world, 100, 64, 200);
    }

    // ==================== Basis-Methoden Tests ====================

    @Test
    @DisplayName("getId: Gibt korrekte Plot-ID zurück")
    void testGetId_ReturnsCorrectId() {
        TestPlot plot = new TestPlot(plotId, ownerId, location);

        assertEquals(plotId, plot.getId(), "getId sollte die korrekte Plot-ID zurückgeben");
    }

    @Test
    @DisplayName("getId: Gibt niemals null zurück")
    void testGetId_NeverReturnsNull() {
        TestPlot plot = new TestPlot(plotId, ownerId, location);

        assertNotNull(plot.getId(), "getId sollte niemals null zurückgeben");
    }

    @Test
    @DisplayName("getOwnerId: Gibt korrekte Owner-ID zurück")
    void testGetOwnerId_ReturnsCorrectOwnerId() {
        TestPlot plot = new TestPlot(plotId, ownerId, location);

        assertEquals(ownerId, plot.getOwnerId(), "getOwnerId sollte die korrekte Owner-ID zurückgeben");
    }

    @Test
    @DisplayName("getOwnerId: Gibt niemals null zurück")
    void testGetOwnerId_NeverReturnsNull() {
        TestPlot plot = new TestPlot(plotId, ownerId, location);

        assertNotNull(plot.getOwnerId(), "getOwnerId sollte niemals null zurückgeben");
    }

    @Test
    @DisplayName("getLocation: Gibt korrekte Location zurück")
    void testGetLocation_ReturnsCorrectLocation() {
        TestPlot plot = new TestPlot(plotId, ownerId, location);

        Location result = plot.getLocation();

        assertNotNull(result, "getLocation sollte nicht null sein");
        assertEquals(100, result.getX(), "X-Koordinate sollte korrekt sein");
        assertEquals(64, result.getY(), "Y-Koordinate sollte korrekt sein");
        assertEquals(200, result.getZ(), "Z-Koordinate sollte korrekt sein");
        assertEquals(world, result.getWorld(), "Welt sollte korrekt sein");
    }

    @Test
    @DisplayName("getLocation: Gibt niemals null zurück")
    void testGetLocation_NeverReturnsNull() {
        TestPlot plot = new TestPlot(plotId, ownerId, location);

        assertNotNull(plot.getLocation(), "getLocation sollte niemals null zurückgeben");
    }

    // ==================== getAvailablePlotActions Tests ====================

    @Test
    @DisplayName("getAvailablePlotActions: Gibt Actions zurück")
    void testGetAvailableAbstractPlotActions_ReturnsActions() {
        TestPlotWithActions plot = new TestPlotWithActions(plotId, ownerId, location);

        List<AbstractPlotAction> actions = plot.getAvailablePlotActions();

        assertNotNull(actions, "Actions sollten nicht null sein");
        assertEquals(2, actions.size(), "Sollte 2 Actions haben");
    }

    @Test
    @DisplayName("getAvailablePlotActions: Leere Liste für Plot ohne Actions")
    void testGetAvailableAbstractPlotActions_EmptyListForPlotWithoutActions() {
        TestPlot plot = new TestPlot(plotId, ownerId, location);

        List<AbstractPlotAction> actions = plot.getAvailablePlotActions();

        assertNotNull(actions, "Actions sollten nicht null sein");
        assertTrue(actions.isEmpty(), "Actions sollten leer sein");
    }

    @Test
    @DisplayName("getAvailablePlotActions: Gibt niemals null zurück")
    void testGetAvailableAbstractPlotActions_NeverReturnsNull() {
        TestPlot plot = new TestPlot(plotId, ownerId, location);

        assertNotNull(plot.getAvailablePlotActions(),
            "getAvailablePlotActions sollte niemals null zurückgeben");
    }

    // ==================== Trait-Pattern Tests ====================

    @Test
    @DisplayName("Trait-Pattern: Plot kann mehrere Traits kombinieren")
    void testTraitPattern_CombinesMultipleTraits() {
        TestPlotWithMultipleTraits plot = new TestPlotWithMultipleTraits(plotId, ownerId, location);

        List<AbstractPlotAction> actions = plot.getAvailablePlotActions();

        // 2 Actions vom ersten Trait + 3 Actions vom zweiten Trait = 5 Actions total
        assertNotNull(actions, "Actions sollten nicht null sein");
        assertEquals(5, actions.size(), "Sollte 5 Actions haben (aus beiden Traits)");
    }

    // ==================== Gleichheits-Tests ====================

    @Test
    @DisplayName("Zwei Plots mit gleicher ID sind gleich (Identität via getId)")
    void testPlotEquality_SameIdMeansSamePlot() {
        TestPlot plot1 = new TestPlot(plotId, ownerId, location);
        TestPlot plot2 = new TestPlot(plotId, UUID.randomUUID(), new Location(world, 0, 0, 0));

        assertEquals(plot1.getId(), plot2.getId(), "Plots mit gleicher ID sollten gleich sein");
    }

    @Test
    @DisplayName("Zwei Plots mit verschiedener ID sind unterschiedlich")
    void testPlotEquality_DifferentIdMeansDifferentPlot() {
        UUID otherId = UUID.randomUUID();
        TestPlot plot1 = new TestPlot(plotId, ownerId, location);
        TestPlot plot2 = new TestPlot(otherId, ownerId, location);

        assertNotEquals(plot1.getId(), plot2.getId(),
            "Plots mit verschiedener ID sollten unterschiedlich sein");
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("Integration: Plot mit AbstractPlotActions funktioniert korrekt")
    void testIntegration_PlotWithActions() {
        TestPlotWithActions plot = new TestPlotWithActions(plotId, ownerId, location);

        // Hole Actions
        List<AbstractPlotAction> actions = plot.getAvailablePlotActions();

        // Validiere Actions
        assertNotNull(actions, "Actions sollten nicht null sein");
        assertEquals(2, actions.size(), "Sollte 2 Actions haben");

        // Teste erste Action
        AbstractPlotAction firstAction = actions.get(0);
        assertNotNull(firstAction, "Erste Action sollte nicht null sein");
        assertNotNull(firstAction.getDisplayItem(), "DisplayItem sollte nicht null sein");
    }

    // ==================== Test-Implementierungen ====================

    /**
     * Einfache Test-Implementierung ohne Actions.
     */
    private static class TestPlot implements Plot {
        private final UUID id;
        private final UUID ownerId;
        private final Location location;

        TestPlot(UUID id, UUID ownerId, Location location) {
            this.id = id;
            this.ownerId = ownerId;
            this.location = location;
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
        public List<AbstractPlotAction> getAvailablePlotActions() {
            return List.of();
        }
    }

    /**
     * Test-Plot mit 2 Actions.
     */
    private static class TestPlotWithActions implements Plot {
        private final UUID id;
        private final UUID ownerId;
        private final Location location;

        TestPlotWithActions(UUID id, UUID ownerId, Location location) {
            this.id = id;
            this.ownerId = ownerId;
            this.location = location;
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
        public List<AbstractPlotAction> getAvailablePlotActions() {
            return List.of(
                new TestAction(this, Material.DIAMOND),
                new TestAction(this, Material.GOLD_INGOT)
            );
        }
    }

    /**
     * Test-Plot mit mehreren Traits (simuliert).
     */
    private static class TestPlotWithMultipleTraits implements Plot {
        private final UUID id;
        private final UUID ownerId;
        private final Location location;

        TestPlotWithMultipleTraits(UUID id, UUID ownerId, Location location) {
            this.id = id;
            this.ownerId = ownerId;
            this.location = location;
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
        public List<AbstractPlotAction> getAvailablePlotActions() {
            // Simuliert Trait-Komposition
            List<AbstractPlotAction> actions = new ArrayList<>();
            actions.addAll(getTraitOneActions());
            actions.addAll(getTraitTwoActions());
            return actions;
        }

        private List<AbstractPlotAction> getTraitOneActions() {
            return List.of(
                new TestAction(this, Material.DIAMOND),
                new TestAction(this, Material.EMERALD)
            );
        }

        private List<AbstractPlotAction> getTraitTwoActions() {
            return List.of(
                new TestAction(this, Material.GOLD_INGOT),
                new TestAction(this, Material.IRON_INGOT),
                new TestAction(this, Material.COAL)
            );
        }
    }

    /**
     * Minimale AbstractPlotAction für Tests.
     */
    private static class TestAction extends AbstractPlotAction {
        private final Material material;

        TestAction(Plot plot, Material material) {
            super(plot);
            this.material = material;
        }

        @Override
        public void execute(Player player) {
            // No-op
        }

        @Override
        public ItemStack getDisplayItem() {
            return new ItemStack(material);
        }
    }
}
