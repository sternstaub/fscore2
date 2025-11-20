package de.fallenstar.core.plot.action;

import de.fallenstar.core.plot.Plot;
import org.bukkit.Material;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für {@link AbstractPlotAction}.
 *
 * <p>Testet das Command Pattern für Plot-Aktionen mit Fokus auf:</p>
 * <ul>
 *   <li>Permission-System (Owner vs. Nicht-Owner)</li>
 *   <li>requiresOwnership() Override-Verhalten</li>
 *   <li>GuiRenderable-Integration</li>
 *   <li>Null-Safety</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AbstractPlotAction Tests")
class AbstractPlotActionTest {

    @Mock
    private Plot plot;

    @Mock
    private Player owner;

    @Mock
    private Player nonOwner;

    private UUID ownerId;
    private UUID nonOwnerId;

    @BeforeEach
    void setUp() {
        // Setup UUIDs
        ownerId = UUID.randomUUID();
        nonOwnerId = UUID.randomUUID();

        // Mock Plot
        when(plot.getOwnerId()).thenReturn(ownerId);

        // Mock Players
        when(owner.getUniqueId()).thenReturn(ownerId);
        when(nonOwner.getUniqueId()).thenReturn(nonOwnerId);
    }

    // ==================== Konstruktor Tests ====================

    @Test
    @DisplayName("Konstruktor: Plot darf nicht null sein")
    void testConstructor_NullPlot_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TestAbstractPlotAction(null);
        }, "Plot darf nicht null sein");
    }

    @Test
    @DisplayName("Konstruktor: Gültiger Plot wird gespeichert")
    void testConstructor_ValidPlot_StoresPlot() {
        TestAbstractPlotAction action = new TestAbstractPlotAction(plot);
        assertNotNull(action);
        // Plot wird korrekt gespeichert (indirekt via isOwner)
        assertTrue(action.isOwner(owner));
    }

    // ==================== Permission Tests ====================

    @Test
    @DisplayName("canExecute: Owner kann Aktion ausführen (requiresOwnership=true)")
    void testCanExecute_OwnerWithRequiredOwnership_ReturnsTrue() {
        TestAbstractPlotAction action = new TestAbstractPlotAction(plot);
        assertTrue(action.canExecute(owner), "Owner sollte Aktion ausführen können");
    }

    @Test
    @DisplayName("canExecute: Nicht-Owner kann Aktion NICHT ausführen (requiresOwnership=true)")
    void testCanExecute_NonOwnerWithRequiredOwnership_ReturnsFalse() {
        TestAbstractPlotAction action = new TestAbstractPlotAction(plot);
        assertFalse(action.canExecute(nonOwner), "Nicht-Owner sollte Aktion NICHT ausführen können");
    }

    @Test
    @DisplayName("canExecute: Jeder kann Aktion ausführen (requiresOwnership=false)")
    void testCanExecute_PublicAction_AlwaysReturnsTrue() {
        PublicAbstractPlotAction action = new PublicAbstractPlotAction(plot);
        assertTrue(action.canExecute(owner), "Owner sollte Public-Action ausführen können");
        assertTrue(action.canExecute(nonOwner), "Nicht-Owner sollte Public-Action ausführen können");
    }

    @Test
    @DisplayName("canExecute: Custom Override kann Owner-Check erweitern")
    void testCanExecute_CustomOverride_WorksCorrectly() {
        CustomPermissionAction action = new CustomPermissionAction(plot);

        // Custom Permission (simuliert via hasPermission)
        when(nonOwner.hasPermission("plot.admin")).thenReturn(true);

        assertTrue(action.canExecute(nonOwner),
            "Nicht-Owner mit plot.admin Permission sollte Aktion ausführen können");

        assertTrue(action.canExecute(owner), "Owner sollte weiterhin ausführen können");

        // Ohne Permission sollte Nicht-Owner NICHT ausführen können
        when(nonOwner.hasPermission("plot.admin")).thenReturn(false);
        assertFalse(action.canExecute(nonOwner),
            "Nicht-Owner ohne plot.admin Permission sollte Aktion NICHT ausführen können");
    }

    @Test
    @DisplayName("isOwner: Erkennt Owner korrekt")
    void testIsOwner_OwnerPlayer_ReturnsTrue() {
        TestAbstractPlotAction action = new TestAbstractPlotAction(plot);
        assertTrue(action.isOwner(owner), "isOwner sollte true für Owner-Player zurückgeben");
    }

    @Test
    @DisplayName("isOwner: Erkennt Nicht-Owner korrekt")
    void testIsOwner_NonOwnerPlayer_ReturnsFalse() {
        TestAbstractPlotAction action = new TestAbstractPlotAction(plot);
        assertFalse(action.isOwner(nonOwner), "isOwner sollte false für Nicht-Owner zurückgeben");
    }

    // ==================== GuiRenderable Tests ====================

    @Test
    @DisplayName("getDisplayItem: Wird korrekt implementiert")
    void testGetDisplayItem_ReturnsConfiguredItemStack() {
        TestAbstractPlotAction action = new TestAbstractPlotAction(plot);
        ItemStack item = action.getDisplayItem();

        assertNotNull(item, "DisplayItem sollte nicht null sein");
        assertEquals(Material.PAPER, item.getType(), "DisplayItem sollte Material PAPER haben");
    }

    @Test
    @DisplayName("isVisible: Standard-Implementierung gibt true zurück")
    void testIsVisible_DefaultImplementation_ReturnsTrue() {
        TestAbstractPlotAction action = new TestAbstractPlotAction(plot);
        assertTrue(action.isVisible(owner), "isVisible sollte standardmäßig true zurückgeben");
        assertTrue(action.isVisible(nonOwner), "isVisible sollte standardmäßig true zurückgeben");
    }

    @Test
    @DisplayName("isVisible: Kann überschrieben werden für Owner-Only")
    void testIsVisible_OverriddenForOwnerOnly_FiltersNonOwner() {
        OwnerOnlyVisibleAction action = new OwnerOnlyVisibleAction(plot);
        assertTrue(action.isVisible(owner), "Aktion sollte für Owner sichtbar sein");
        assertFalse(action.isVisible(nonOwner), "Aktion sollte für Nicht-Owner unsichtbar sein");
    }

    // ==================== Execute Tests ====================

    @Test
    @DisplayName("execute: Wird korrekt aufgerufen")
    void testExecute_CallsImplementation() {
        TestAbstractPlotAction action = new TestAbstractPlotAction(plot);
        action.execute(owner);

        // Test, dass execute ohne Exceptions läuft
        assertTrue(action.wasExecuted, "execute() sollte aufgerufen worden sein");
    }

    // ==================== Test-Implementierungen ====================

    /**
     * Standard Test-Implementierung mit requiresOwnership=true.
     */
    private static class TestAbstractPlotAction extends AbstractPlotAction {
        boolean wasExecuted = false;

        TestAbstractPlotAction(Plot plot) {
            super(plot);
        }

        @Override
        public void invokeByGuiButton(Player player) {
            wasExecuted = true;
        }

        @Override
        public ItemStack getDisplayItem() {
            return new ItemStack(Material.PAPER);
        }
    }

    /**
     * Public Action (requiresOwnership=false).
     */
    private static class PublicAbstractPlotAction extends AbstractPlotAction {
        PublicAbstractPlotAction(Plot plot) {
            super(plot);
        }

        @Override
        protected boolean requiresOwnership() {
            return false;
        }

        @Override
        public void invokeByGuiButton(Player player) {
            // No-op
        }

        @Override
        public ItemStack getDisplayItem() {
            return new ItemStack(Material.COMPASS);
        }
    }

    /**
     * Custom Permission Action (canExecute überschrieben).
     */
    private static class CustomPermissionAction extends AbstractPlotAction {
        CustomPermissionAction(Plot plot) {
            super(plot);
        }

        @Override
        public boolean canExecute(Player player) {
            // Entweder Admin-Permission ODER Owner
            return player.hasPermission("plot.admin") || super.canExecute(player);
        }

        @Override
        public void invokeByGuiButton(Player player) {
            // No-op
        }

        @Override
        public ItemStack getDisplayItem() {
            return new ItemStack(Material.REDSTONE);
        }
    }

    /**
     * Owner-Only Visible Action (isVisible überschrieben).
     */
    private static class OwnerOnlyVisibleAction extends AbstractPlotAction {
        OwnerOnlyVisibleAction(Plot plot) {
            super(plot);
        }

        @Override
        public void invokeByGuiButton(Player player) {
            // No-op
        }

        @Override
        public ItemStack getDisplayItem() {
            return new ItemStack(Material.DIAMOND);
        }

        @Override
        public boolean isVisible(Player player) {
            return isOwner(player);
        }
    }
}
