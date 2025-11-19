package de.fallenstar.core.ui;

import de.fallenstar.core.plot.Plot;
import de.fallenstar.core.plot.action.PlotAction;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für {@link MenuAction}.
 *
 * <p>Testet das hierarchische Menü-Pattern mit Fokus auf:</p>
 * <ul>
 *   <li>PlotAction + MenuAction Integration</li>
 *   <li>Leere und gefüllte Submenüs</li>
 *   <li>Rekursive MenuActions (Submenüs in Submenüs)</li>
 *   <li>Dynamische Sub-Actions</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MenuAction Tests")
class MenuActionTest {

    @Mock
    private Plot plot;

    @Mock
    private Player player;

    private UUID ownerId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        when(plot.getOwnerId()).thenReturn(ownerId);
        when(player.getUniqueId()).thenReturn(ownerId);
    }

    // ==================== Basis-Tests ====================

    @Test
    @DisplayName("PlotAction kann MenuAction implementieren")
    void testPlotAction_ImplementsMenuAction() {
        TestMenuAction action = new TestMenuAction(plot);

        assertNotNull(action, "MenuAction sollte erstellt werden können");
        assertTrue(action instanceof MenuAction, "TestMenuAction sollte MenuAction sein");
        assertTrue(action instanceof PlotAction, "TestMenuAction sollte auch PlotAction sein");
        assertTrue(action instanceof GuiRenderable, "PlotAction implementiert GuiRenderable");
    }

    @Test
    @DisplayName("getSubActions: Gibt korrekte Liste zurück")
    void testGetSubActions_ReturnsCorrectList() {
        TestMenuAction action = new TestMenuAction(plot);
        List<GuiRenderable> subActions = action.getSubActions();

        assertNotNull(subActions, "Sub-Actions sollten nicht null sein");
        assertEquals(3, subActions.size(), "Sollte 3 Sub-Actions haben");
    }

    @Test
    @DisplayName("getSubActions: Sub-Actions sind GuiRenderable")
    void testGetSubActions_AllItemsAreGuiRenderable() {
        TestMenuAction action = new TestMenuAction(plot);
        List<GuiRenderable> subActions = action.getSubActions();

        for (GuiRenderable subAction : subActions) {
            assertNotNull(subAction, "Sub-Action sollte nicht null sein");
            assertNotNull(subAction.getDisplayItem(), "Sub-Action sollte DisplayItem haben");
        }
    }

    // ==================== Leere Submenüs ====================

    @Test
    @DisplayName("getSubActions: Leere Liste ist gültig")
    void testGetSubActions_EmptyListIsValid() {
        EmptyMenuAction action = new EmptyMenuAction(plot);
        List<GuiRenderable> subActions = action.getSubActions();

        assertNotNull(subActions, "Sub-Actions sollten nicht null sein");
        assertTrue(subActions.isEmpty(), "Sub-Actions sollten leer sein");
    }

    // ==================== Rekursive MenuActions ====================

    @Test
    @DisplayName("MenuAction kann andere MenuActions als Sub-Actions haben")
    void testMenuAction_CanContainOtherMenuActions() {
        NestedMenuAction action = new NestedMenuAction(plot);
        List<GuiRenderable> subActions = action.getSubActions();

        assertNotNull(subActions, "Sub-Actions sollten nicht null sein");
        assertEquals(2, subActions.size(), "Sollte 2 Sub-Actions haben");

        // Erste Sub-Action ist auch eine MenuAction
        GuiRenderable firstSub = subActions.get(0);
        assertTrue(firstSub instanceof MenuAction, "Erste Sub-Action sollte MenuAction sein");

        MenuAction nestedMenu = (MenuAction) firstSub;
        List<GuiRenderable> nestedSubActions = nestedMenu.getSubActions();
        assertNotNull(nestedSubActions, "Nested Sub-Actions sollten nicht null sein");
        assertEquals(3, nestedSubActions.size(), "Nested Menu sollte 3 Sub-Actions haben");
    }

    // ==================== Dynamische Sub-Actions ====================

    @Test
    @DisplayName("getSubActions: Dynamische Sub-Actions basierend auf Zustand")
    void testGetSubActions_DynamicBasedOnState() {
        DynamicMenuAction action = new DynamicMenuAction(plot);

        // Zustand 1: isEmpty = true
        action.setEmpty(true);
        List<GuiRenderable> subActions1 = action.getSubActions();
        assertEquals(2, subActions1.size(), "Bei isEmpty sollten 2 Actions verfügbar sein");

        // Zustand 2: isEmpty = false
        action.setEmpty(false);
        List<GuiRenderable> subActions2 = action.getSubActions();
        assertEquals(1, subActions2.size(), "Bei nicht-empty sollte 1 Action verfügbar sein");
    }

    // ==================== GuiRenderable Integration ====================

    @Test
    @DisplayName("MenuAction: getDisplayItem funktioniert korrekt")
    void testMenuAction_GetDisplayItemWorks() {
        TestMenuAction action = new TestMenuAction(plot);
        ItemStack displayItem = action.getDisplayItem();

        assertNotNull(displayItem, "DisplayItem sollte nicht null sein");
        assertEquals(Material.CHEST, displayItem.getType(), "DisplayItem sollte CHEST sein");
    }

    @Test
    @DisplayName("MenuAction: isVisible funktioniert korrekt")
    void testMenuAction_IsVisibleWorks() {
        TestMenuAction action = new TestMenuAction(plot);

        assertTrue(action.isVisible(player), "MenuAction sollte standardmäßig sichtbar sein");
    }

    @Test
    @DisplayName("MenuAction: canExecute funktioniert korrekt")
    void testMenuAction_CanExecuteWorks() {
        TestMenuAction action = new TestMenuAction(plot);

        assertTrue(action.canExecute(player), "Owner sollte MenuAction ausführen können");
    }

    // ==================== Execute-Verhalten ====================

    @Test
    @DisplayName("MenuAction: execute öffnet typischerweise Submenü")
    void testMenuAction_ExecuteOpensSubmenu() {
        TestMenuAction action = new TestMenuAction(plot);

        // Execute sollte ohne Exceptions laufen
        assertDoesNotThrow(() -> action.execute(player),
            "Execute sollte ohne Exceptions laufen");

        assertTrue(action.wasExecuted, "Execute sollte aufgerufen worden sein");
    }

    // ==================== Null-Safety ====================

    @Test
    @DisplayName("getSubActions: Sollte niemals null zurückgeben")
    void testGetSubActions_NeverReturnsNull() {
        List<MenuAction> actions = List.of(
            new TestMenuAction(plot),
            new EmptyMenuAction(plot),
            new NestedMenuAction(plot),
            new DynamicMenuAction(plot)
        );

        for (MenuAction action : actions) {
            assertNotNull(action.getSubActions(),
                action.getClass().getSimpleName() + " sollte nicht null zurückgeben");
        }
    }

    // ==================== Test-Implementierungen ====================

    /**
     * Standard MenuAction mit 3 Sub-Actions.
     */
    private static class TestMenuAction extends PlotAction implements MenuAction {
        boolean wasExecuted = false;

        TestMenuAction(Plot plot) {
            super(plot);
        }

        @Override
        public List<GuiRenderable> getSubActions() {
            return List.of(
                new SimpleGuiRenderable(Material.DIAMOND, "Sub 1"),
                new SimpleGuiRenderable(Material.GOLD_INGOT, "Sub 2"),
                new SimpleGuiRenderable(Material.IRON_INGOT, "Sub 3")
            );
        }

        @Override
        public void execute(Player player) {
            wasExecuted = true;
            // In echter Implementierung: Öffne Submenü mit GuiBuilder
        }

        @Override
        public ItemStack getDisplayItem() {
            return new ItemStack(Material.CHEST);
        }
    }

    /**
     * MenuAction mit leerem Submenü.
     */
    private static class EmptyMenuAction extends PlotAction implements MenuAction {
        EmptyMenuAction(Plot plot) {
            super(plot);
        }

        @Override
        public List<GuiRenderable> getSubActions() {
            return List.of();
        }

        @Override
        public void execute(Player player) {
            // No-op
        }

        @Override
        public ItemStack getDisplayItem() {
            return new ItemStack(Material.BARRIER);
        }
    }

    /**
     * MenuAction mit nested MenuActions (Rekursion).
     */
    private static class NestedMenuAction extends PlotAction implements MenuAction {
        NestedMenuAction(Plot plot) {
            super(plot);
        }

        @Override
        public List<GuiRenderable> getSubActions() {
            return List.of(
                new TestMenuAction(plot),  // Nested MenuAction
                new SimpleGuiRenderable(Material.STONE, "Simple Action")
            );
        }

        @Override
        public void execute(Player player) {
            // No-op
        }

        @Override
        public ItemStack getDisplayItem() {
            return new ItemStack(Material.ENDER_CHEST);
        }
    }

    /**
     * MenuAction mit dynamischen Sub-Actions.
     */
    private static class DynamicMenuAction extends PlotAction implements MenuAction {
        private boolean isEmpty = true;

        DynamicMenuAction(Plot plot) {
            super(plot);
        }

        void setEmpty(boolean empty) {
            this.isEmpty = empty;
        }

        @Override
        public List<GuiRenderable> getSubActions() {
            List<GuiRenderable> actions = new ArrayList<>();
            actions.add(new SimpleGuiRenderable(Material.BUCKET, "Always visible"));

            if (isEmpty) {
                actions.add(new SimpleGuiRenderable(Material.WATER_BUCKET, "Fill"));
            }

            return actions;
        }

        @Override
        public void execute(Player player) {
            // No-op
        }

        @Override
        public ItemStack getDisplayItem() {
            return new ItemStack(Material.HOPPER);
        }
    }

    /**
     * Einfache GuiRenderable Implementierung für Tests.
     */
    private record SimpleGuiRenderable(Material material, String name) implements GuiRenderable {

        @Override
        public ItemStack getDisplayItem() {
            return new ItemStack(material);
        }

        @Override
        public boolean isVisible(Player player) {
            return true;
        }
    }
}
