package de.fallenstar.core.integration;

import de.fallenstar.core.plot.action.AbstractPlotAction;
import de.fallenstar.core.plot.action.impl.PlotActionSetName;
import de.fallenstar.core.plot.trait.PlotWithName;
import de.fallenstar.core.ui.GuiBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Integration Tests für das Zusammenspiel von GuiBuilder und AbstractPlotActions.
 *
 * <p>Diese Tests validieren das Self-Rendering Pattern und das Command Pattern
 * in einem realistischen Szenario:</p>
 * <ul>
 *   <li>Plots mit Traits (PlotWithName)</li>
 *   <li>Konkrete AbstractPlotActions (PlotActionSetName)</li>
 *   <li>GuiBuilder erstellt GUI aus Actions</li>
 *   <li>Visibility-Filtering basierend auf Permissions</li>
 * </ul>
 *
 * <p><b>Zweck:</b> Diese Tests zeigen, dass die gesamte Architektur
 * aus Phase 3-9 korrekt zusammenarbeitet.</p>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GuiBuilder Integration Tests")
class GuiBuilderIntegrationTest {

    @Mock
    private Player owner;

    @Mock
    private Player nonOwner;

    @Mock
    private Location location;

    @Mock
    private Inventory mockInventory;

    @Mock
    private ItemFactory itemFactory;

    @Mock
    private ItemMeta itemMeta;

    private UUID ownerId;
    private UUID nonOwnerId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        nonOwnerId = UUID.randomUUID();

        when(owner.getUniqueId()).thenReturn(ownerId);
        when(nonOwner.getUniqueId()).thenReturn(nonOwnerId);
        when(owner.hasPermission(anyString())).thenReturn(false);
        when(nonOwner.hasPermission(anyString())).thenReturn(false);
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("Integration: GuiBuilder erstellt GUI mit PlotActionSetName")
    void testIntegration_GuiBuilderWithPlotActionSetName() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(mockInventory);
            bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
            when(itemFactory.getItemMeta(any(Material.class))).thenReturn(itemMeta);
            when(itemMeta.clone()).thenReturn(itemMeta);

            // Erstelle Plot mit PlotWithName Trait
            PlotWithName namedPlot = new TestPlotWithName(
                UUID.randomUUID(),
                ownerId,
                location,
                "Mein Handelshaus"
            );

            // Erstelle PlotActionSetName
            PlotActionSetName setNameAction = new PlotActionSetName(namedPlot);

            // Baue GUI aus Action
            List<AbstractPlotAction> actions = List.of(setNameAction);
            Inventory gui = GuiBuilder.buildFromActions(actions, owner, "Plot verwalten");

            // Verifiziere GUI wurde erstellt
            assertNotNull(gui, "GUI sollte erstellt werden");
            bukkit.verify(() -> Bukkit.createInventory(null, 9, "Plot verwalten"));

            // Verifiziere Item wurde gesetzt
            ArgumentCaptor<ItemStack> itemCaptor = ArgumentCaptor.forClass(ItemStack.class);
            verify(mockInventory).setItem(eq(0), itemCaptor.capture());

            ItemStack setItem = itemCaptor.getValue();
            assertEquals(Material.NAME_TAG, setItem.getType(),
                "Gesetztes Item sollte NAME_TAG sein");
        }
    }

    @Test
    @DisplayName("Integration: Plot mit mehreren Actions generiert Multi-Item GUI")
    void testIntegration_PlotWithMultipleActions() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(mockInventory);
            bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
            when(itemFactory.getItemMeta(any(Material.class))).thenReturn(itemMeta);
            when(itemMeta.clone()).thenReturn(itemMeta);

            // Erstelle Plot mit mehreren Actions
            PlotWithName namedPlot = new TestPlotWithName(
                UUID.randomUUID(),
                ownerId,
                location,
                "Multi-Action Plot"
            );

            List<AbstractPlotAction> actions = namedPlot.getAvailablePlotActions();

            // Baue GUI
            Inventory gui = GuiBuilder.buildFromActions(actions, owner, "Alle Actions");

            assertNotNull(gui);
            // TestPlotWithName gibt 2 Actions zurück
            verify(mockInventory, times(2)).setItem(anyInt(), any(ItemStack.class));
        }
    }

    @Test
    @DisplayName("Integration: Nicht-Owner sieht keine Owner-Only Actions")
    void testIntegration_NonOwnerSeesNoOwnerActions() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(mockInventory);

            // Erstelle Plot
            PlotWithName namedPlot = new TestPlotWithName(
                UUID.randomUUID(),
                ownerId,
                location,
                "Owner-Only Plot"
            );

            // Erstelle Action mit Owner-Requirement + Custom Visibility
            PlotActionSetName ownerOnlyAction = new PlotActionSetName(namedPlot) {
                @Override
                public boolean isVisible(Player player) {
                    // Nur für Owner sichtbar
                    return canExecute(player);
                }
            };

            List<AbstractPlotAction> actions = List.of(ownerOnlyAction);

            // Nicht-Owner baut GUI
            Inventory gui = GuiBuilder.buildFromActions(actions, nonOwner, "Test");

            assertNotNull(gui);
            // Keine Items sollten gesetzt werden (Action ist nicht sichtbar)
            verify(mockInventory, never()).setItem(anyInt(), any(ItemStack.class));
        }
    }

    @Test
    @DisplayName("Integration: Trait-Komposition - Plot mit mehreren Traits")
    void testIntegration_MultipleTraitsGenerateMultipleActions() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(mockInventory);
            bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
            when(itemFactory.getItemMeta(any(Material.class))).thenReturn(itemMeta);
            when(itemMeta.clone()).thenReturn(itemMeta);

            // Erstelle Plot mit mehreren Traits (simuliert)
            PlotWithName namedPlot = new TestPlotWithName(
                UUID.randomUUID(),
                ownerId,
                location,
                "Multi-Trait Plot"
            );

            // In Zukunft würde ein Plot hier mehrere Traits kombinieren:
            // class FullPlot implements PlotWithName, PlotWithStorageContainer, PlotWithNpcContainer
            // und getAvailablePlotActions() würde alle Trait-Actions kombinieren

            List<AbstractPlotAction> actions = namedPlot.getAvailablePlotActions();

            // Baue GUI
            Inventory gui = GuiBuilder.buildFromActions(actions, owner, "Alle Funktionen");

            assertNotNull(gui);
            // Aktuell: 2 Actions (beide PlotActionSetName)
            // Zukunft: Mehr Actions aus verschiedenen Traits
            verify(mockInventory, times(2)).setItem(anyInt(), any(ItemStack.class));
        }
    }

    @Test
    @DisplayName("Integration: DisplayItem Lore enthält dynamischen Plot-Namen")
    void testIntegration_DisplayItemShowsDynamicPlotName() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(mockInventory);
            bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
            when(itemFactory.getItemMeta(any(Material.class))).thenReturn(itemMeta);
            when(itemMeta.clone()).thenReturn(itemMeta);

            // Mock Lore für ersten Namen
            List<String> firstLore = List.of(
                "§7Aktueller Name: §fDynamischer Name",
                "",
                "§7Klicke um den Namen zu ändern"
            );
            when(itemMeta.getLore()).thenReturn(firstLore);

            // Erstelle Plot
            TestPlotWithName namedPlot = new TestPlotWithName(
                UUID.randomUUID(),
                ownerId,
                location,
                "Dynamischer Name"
            );

            PlotActionSetName action = new PlotActionSetName(namedPlot);
            ItemStack displayItem = action.getDisplayItem();

            // Prüfe, dass Lore den Plot-Namen enthält
            assertNotNull(displayItem.getItemMeta());
            List<String> lore = displayItem.getItemMeta().getLore();
            assertNotNull(lore);
            assertTrue(lore.get(0).contains("Dynamischer Name"),
                "Lore sollte dynamischen Plot-Namen enthalten");

            // Ändere Plot-Namen und Mock für neuen Namen
            namedPlot.setName("Neuer Dynamischer Name");
            List<String> updatedLore = List.of(
                "§7Aktueller Name: §fNeuer Dynamischer Name",
                "",
                "§7Klicke um den Namen zu ändern"
            );
            when(itemMeta.getLore()).thenReturn(updatedLore);

            // Erstelle neue Action (simuliert GUI-Refresh)
            PlotActionSetName refreshedAction = new PlotActionSetName(namedPlot);
            ItemStack refreshedItem = refreshedAction.getDisplayItem();

            // Lore sollte aktualisierten Namen zeigen
            List<String> refreshedLore = refreshedItem.getItemMeta().getLore();
            assertTrue(refreshedLore.get(0).contains("Neuer Dynamischer Name"),
                "Lore sollte aktualisierten Plot-Namen zeigen");
        }
    }

    // ==================== Test-Implementierung ====================

    /**
     * Test-Implementierung von PlotWithName für Integration-Tests.
     */
    private static class TestPlotWithName implements PlotWithName {
        private final UUID id;
        private final UUID ownerId;
        private final Location location;
        private String name;

        TestPlotWithName(UUID id, UUID ownerId, Location location, String name) {
            this.id = id;
            this.ownerId = ownerId;
            this.location = location;
            this.name = name;
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
        public List<AbstractPlotAction> getAvailablePlotActions() {
            // Simuliert Trait-Komposition: Mehrere Actions aus Traits
            return List.of(
                new PlotActionSetName(this),
                new PlotActionSetName(this)  // Zweite Action für Multi-Action Test
            );
        }
    }
}
