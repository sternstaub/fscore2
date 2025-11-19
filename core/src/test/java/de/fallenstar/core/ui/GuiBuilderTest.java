package de.fallenstar.core.ui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeAll;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für {@link GuiBuilder}.
 *
 * <p>Testet den universellen GUI-Generator mit Fokus auf:</p>
 * <ul>
 *   <li>Visibility-Filtering (isVisible)</li>
 *   <li>GUI-Größen-Berechnung (Vielfaches von 9)</li>
 *   <li>Item-Platzierung im Inventory</li>
 *   <li>Edge Cases (null, leer, zu viele Items)</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GuiBuilder Tests")
class GuiBuilderTest {

    @Mock
    private Player player;

    @Mock
    private Server server;

    @Mock
    private ItemFactory itemFactory;

    @Mock
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        // Mock ItemMeta für alle ItemStacks
        ItemMeta mockMeta = mock(ItemMeta.class);
        when(itemFactory.getItemMeta(any(Material.class))).thenReturn(mockMeta);
    }

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("GuiBuilder kann nicht instanziiert werden (Utility-Klasse)")
    void testConstructor_ThrowsException() {
        try {
            // Nutze Reflection um private Constructor zu testen
            var constructor = GuiBuilder.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("GuiBuilder sollte nicht instanziierbar sein");
        } catch (Exception e) {
            // Reflection wrapped die Exception in InvocationTargetException
            assertTrue(e.getCause() instanceof UnsupportedOperationException,
                "Cause sollte UnsupportedOperationException sein, war aber: " + e.getCause().getClass());
        }
    }

    // ==================== buildFromActions - Basis Tests ====================

    @Test
    @DisplayName("buildFromActions: Erstellt GUI mit korrekter Größe (3 Items → 9 Slots)")
    void testBuildFromActions_CorrectSize_ThreeItems() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            // Mock Bukkit
            bukkit.when(() -> Bukkit.createInventory(isNull(), eq(9), anyString()))
                .thenReturn(inventory);

            List<GuiRenderable> actions = List.of(
                new TestGuiRenderable(Material.DIAMOND),
                new TestGuiRenderable(Material.GOLD_INGOT),
                new TestGuiRenderable(Material.IRON_INGOT)
            );

            Inventory result = GuiBuilder.buildFromActions(actions, player, "Test GUI");

            assertNotNull(result, "GUI sollte nicht null sein");
            bukkit.verify(() -> Bukkit.createInventory(null, 9, "Test GUI"));
            verify(inventory, times(3)).setItem(anyInt(), any(ItemStack.class));
        }
    }

    @Test
    @DisplayName("buildFromActions: Erstellt GUI mit korrekter Größe (10 Items → 18 Slots)")
    void testBuildFromActions_CorrectSize_TenItems() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), eq(18), anyString()))
                .thenReturn(inventory);

            List<GuiRenderable> actions = createTestActions(10);

            Inventory result = GuiBuilder.buildFromActions(actions, player, "Test GUI");

            assertNotNull(result, "GUI sollte nicht null sein");
            bukkit.verify(() -> Bukkit.createInventory(null, 18, "Test GUI"));
            verify(inventory, times(10)).setItem(anyInt(), any(ItemStack.class));
        }
    }

    @Test
    @DisplayName("buildFromActions: Items werden sequenziell platziert (Slot 0, 1, 2...)")
    void testBuildFromActions_ItemsPlacedSequentially() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(inventory);

            List<GuiRenderable> actions = List.of(
                new TestGuiRenderable(Material.DIAMOND),
                new TestGuiRenderable(Material.GOLD_INGOT),
                new TestGuiRenderable(Material.IRON_INGOT)
            );

            GuiBuilder.buildFromActions(actions, player, "Test GUI");

            verify(inventory).setItem(eq(0), any(ItemStack.class));
            verify(inventory).setItem(eq(1), any(ItemStack.class));
            verify(inventory).setItem(eq(2), any(ItemStack.class));
        }
    }

    // ==================== Visibility-Filtering Tests ====================

    @Test
    @DisplayName("buildFromActions: Filtert unsichtbare Actions")
    void testBuildFromActions_FiltersInvisibleActions() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), eq(9), anyString()))
                .thenReturn(inventory);

            List<GuiRenderable> actions = List.of(
                new TestGuiRenderable(Material.DIAMOND, true),   // Sichtbar
                new TestGuiRenderable(Material.GOLD_INGOT, false), // Unsichtbar
                new TestGuiRenderable(Material.IRON_INGOT, true)   // Sichtbar
            );

            GuiBuilder.buildFromActions(actions, player, "Test GUI");

            // Nur 2 sichtbare Actions sollten platziert werden
            verify(inventory, times(2)).setItem(anyInt(), any(ItemStack.class));
            verify(inventory).setItem(eq(0), any(ItemStack.class));
            verify(inventory).setItem(eq(1), any(ItemStack.class));
            verify(inventory, never()).setItem(eq(2), any(ItemStack.class));
        }
    }

    @Test
    @DisplayName("buildFromActions: Alle Actions unsichtbar → Leeres GUI (9 Slots)")
    void testBuildFromActions_AllInvisible_EmptyGui() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), eq(9), anyString()))
                .thenReturn(inventory);

            List<GuiRenderable> actions = List.of(
                new TestGuiRenderable(Material.DIAMOND, false),
                new TestGuiRenderable(Material.GOLD_INGOT, false)
            );

            GuiBuilder.buildFromActions(actions, player, "Test GUI");

            // Keine Items sollten platziert werden
            verify(inventory, never()).setItem(anyInt(), any(ItemStack.class));
            bukkit.verify(() -> Bukkit.createInventory(null, 9, "Test GUI"));
        }
    }

    // ==================== GUI-Größen-Berechnung Tests ====================

    @Test
    @DisplayName("buildFromActions: Leere Liste → Minimum 9 Slots")
    void testBuildFromActions_EmptyList_MinimumSize() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), eq(9), anyString()))
                .thenReturn(inventory);

            List<GuiRenderable> actions = List.of();

            GuiBuilder.buildFromActions(actions, player, "Empty GUI");

            bukkit.verify(() -> Bukkit.createInventory(null, 9, "Empty GUI"));
        }
    }

    @Test
    @DisplayName("buildFromActions: Verschiedene Größen werden korrekt berechnet")
    void testBuildFromActions_VariousSizes() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(inventory);

            // 1 Item → 9 Slots
            GuiBuilder.buildFromActions(createTestActions(1), player, "Test");
            bukkit.verify(() -> Bukkit.createInventory(null, 9, "Test"));

            // 9 Items → 9 Slots (exakt)
            GuiBuilder.buildFromActions(createTestActions(9), player, "Test");
            bukkit.verify(() -> Bukkit.createInventory(null, 9, "Test"), times(2));

            // 10 Items → 18 Slots
            GuiBuilder.buildFromActions(createTestActions(10), player, "Test");
            bukkit.verify(() -> Bukkit.createInventory(null, 18, "Test"));

            // 27 Items → 27 Slots (exakt)
            GuiBuilder.buildFromActions(createTestActions(27), player, "Test");
            bukkit.verify(() -> Bukkit.createInventory(null, 27, "Test"));

            // 28 Items → 36 Slots
            GuiBuilder.buildFromActions(createTestActions(28), player, "Test");
            bukkit.verify(() -> Bukkit.createInventory(null, 36, "Test"));

            // 54 Items → 54 Slots (Maximum)
            GuiBuilder.buildFromActions(createTestActions(54), player, "Test");
            bukkit.verify(() -> Bukkit.createInventory(null, 54, "Test"));
        }
    }

    @Test
    @DisplayName("buildFromActions: Mehr als 54 Items → Maximum 54 Slots")
    void testBuildFromActions_MoreThan54Items_MaximumSize() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), eq(54), anyString()))
                .thenReturn(inventory);

            List<GuiRenderable> actions = createTestActions(100);

            GuiBuilder.buildFromActions(actions, player, "Large GUI");

            bukkit.verify(() -> Bukkit.createInventory(null, 54, "Large GUI"));
            // Nur 54 Items sollten platziert werden (Maximum)
            verify(inventory, times(54)).setItem(anyInt(), any(ItemStack.class));
        }
    }

    // ==================== Edge Cases & Validation ====================

    @Test
    @DisplayName("buildFromActions: Null actions → IllegalArgumentException")
    void testBuildFromActions_NullActions_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            GuiBuilder.buildFromActions(null, player, "Test");
        }, "Null actions sollten Exception werfen");
    }

    @Test
    @DisplayName("buildFromActions: Null player → IllegalArgumentException")
    void testBuildFromActions_NullPlayer_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            GuiBuilder.buildFromActions(List.of(), null, "Test");
        }, "Null player sollte Exception werfen");
    }

    @Test
    @DisplayName("buildFromActions: Null title → IllegalArgumentException")
    void testBuildFromActions_NullTitle_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            GuiBuilder.buildFromActions(List.of(), player, null);
        }, "Null title sollte Exception werfen");
    }

    @Test
    @DisplayName("buildFromActions: Titel wird korrekt übergeben")
    void testBuildFromActions_TitlePassedCorrectly() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), eq("Custom Title")))
                .thenReturn(inventory);

            GuiBuilder.buildFromActions(List.of(), player, "Custom Title");

            bukkit.verify(() -> Bukkit.createInventory(null, 9, "Custom Title"));
        }
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("buildFromActions: Gemischte Sichtbarkeit und korrekte Größenberechnung")
    void testBuildFromActions_MixedVisibility_CorrectSizeCalculation() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), eq(9), anyString()))
                .thenReturn(inventory);

            // 5 Actions total, 3 sichtbar → 9 Slots (basiert auf sichtbaren)
            List<GuiRenderable> actions = List.of(
                new TestGuiRenderable(Material.DIAMOND, true),
                new TestGuiRenderable(Material.GOLD_INGOT, false),
                new TestGuiRenderable(Material.IRON_INGOT, true),
                new TestGuiRenderable(Material.EMERALD, false),
                new TestGuiRenderable(Material.COAL, true)
            );

            GuiBuilder.buildFromActions(actions, player, "Test");

            // Nur 3 sichtbare → 9 Slots
            bukkit.verify(() -> Bukkit.createInventory(null, 9, "Test"));
            verify(inventory, times(3)).setItem(anyInt(), any(ItemStack.class));
        }
    }

    // ==================== Helper Methods ====================

    /**
     * Erstellt eine Liste von Test-GuiRenderables.
     */
    private List<GuiRenderable> createTestActions(int count) {
        List<GuiRenderable> actions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            actions.add(new TestGuiRenderable(Material.STONE));
        }
        return actions;
    }

    // ==================== Test-Implementierungen ====================

    /**
     * Einfache Test-Implementierung von GuiRenderable.
     */
    private static class TestGuiRenderable implements GuiRenderable {
        private final Material material;
        private final boolean visible;

        TestGuiRenderable(Material material) {
            this(material, true);
        }

        TestGuiRenderable(Material material, boolean visible) {
            this.material = material;
            this.visible = visible;
        }

        @Override
        public ItemStack getDisplayItem() {
            return new ItemStack(material);
        }

        @Override
        public boolean isVisible(Player player) {
            return visible;
        }
    }
}
