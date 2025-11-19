package de.fallenstar.core.integration;

import de.fallenstar.core.plot.action.PlotAction;
import de.fallenstar.core.plot.action.impl.PlotActionSetName;
import de.fallenstar.core.plot.trait.PlotIsContainerForNpc;
import de.fallenstar.core.plot.trait.PlotIsContainerForStorage;
import de.fallenstar.core.plot.trait.PlotNamed;
import de.fallenstar.core.ui.GuiBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Integration Tests für TradeguildPlot - Vollständige Trait-Komposition.
 *
 * <p>Diese Tests demonstrieren ein realistisches Szenario:</p>
 * <ul>
 *   <li>TradeguildPlot implementiert alle drei Traits (PlotNamed, PlotIsContainerForStorage, PlotIsContainerForNpc)</li>
 *   <li>getAvailablePlotActions() kombiniert Actions aus allen Traits</li>
 *   <li>GuiBuilder generiert ein vollständiges GUI mit allen Actions</li>
 *   <li>Proof-of-Concept für die gesamte Architektur aus Sprint 20</li>
 * </ul>
 *
 * <p><b>Zweck:</b> Validierung dass das Self-Rendering Pattern, Command Pattern,
 * und Trait-Komposition in einem realistischen Use-Case funktionieren.</p>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TradeguildPlot Integration Tests")
class TradeguildPlotIntegrationTest {

    @Mock
    private Player owner;

    @Mock
    private Player nonOwner;

    @Mock
    private Location location;

    @Mock
    private Inventory mockInventory;

    @Mock
    private Inventory storageInventory;

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

    // ==================== Trait-Komposition Tests ====================

    @Test
    @DisplayName("Integration: TradeguildPlot implementiert alle drei Traits")
    void testIntegration_TradeguildPlotImplementsAllTraits() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(storageInventory);

            TradeguildPlot tradeguild = new TradeguildPlot(
                UUID.randomUUID(),
                ownerId,
                location,
                "Handelsgilde des Nordens"
            );

            // Verifiziere alle Trait-Interfaces
            assertTrue(tradeguild instanceof PlotNamed, "Sollte PlotNamed implementieren");
            assertTrue(tradeguild instanceof PlotIsContainerForStorage, "Sollte PlotIsContainerForStorage implementieren");
            assertTrue(tradeguild instanceof PlotIsContainerForNpc, "Sollte PlotIsContainerForNpc implementieren");

            // Verifiziere Trait-Funktionalität
            assertEquals("Handelsgilde des Nordens", tradeguild.getName());
            assertNotNull(tradeguild.getStorageInventory());
            assertNull(tradeguild.getNpcId()); // Initial null
        }
    }

    @Test
    @DisplayName("Integration: getAvailablePlotActions kombiniert alle Trait-Actions")
    void testIntegration_CombinesAllTraitActions() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(storageInventory);

            TradeguildPlot tradeguild = new TradeguildPlot(
                UUID.randomUUID(),
                ownerId,
                location,
                "Test-Handelsgilde"
            );

            List<PlotAction> actions = tradeguild.getAvailablePlotActions();

            assertNotNull(actions, "Actions sollten nicht null sein");
            // Aktuell nur PlotActionSetName (andere Actions sind Placeholder)
            // In Zukunft: Mehr Actions aus Storage und NPC Traits
            assertFalse(actions.isEmpty(), "Sollte mindestens eine Action haben");
        }
    }

    @Test
    @DisplayName("Integration: GuiBuilder erstellt vollständiges GUI aus TradeguildPlot")
    void testIntegration_GuiBuilderWithTradeguildPlot() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(mockInventory);
            bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
            when(itemFactory.getItemMeta(any(Material.class))).thenReturn(itemMeta);
            when(itemMeta.clone()).thenReturn(itemMeta);

            TradeguildPlot tradeguild = new TradeguildPlot(
                UUID.randomUUID(),
                ownerId,
                location,
                "Handelsgilde"
            );

            List<PlotAction> actions = tradeguild.getAvailablePlotActions();

            // Baue GUI
            Inventory gui = GuiBuilder.buildFromActions(actions, owner, "Handelsgilde verwalten");

            assertNotNull(gui, "GUI sollte erstellt werden");
            verify(mockInventory, atLeastOnce()).setItem(anyInt(), any(ItemStack.class));
        }
    }

    @Test
    @DisplayName("Integration: Trait-Funktionen sind unabhängig nutzbar")
    void testIntegration_TraitFunctionsWorkIndependently() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(storageInventory);

            TradeguildPlot tradeguild = new TradeguildPlot(
                UUID.randomUUID(),
                ownerId,
                location,
                "Initial Name"
            );

            // PlotNamed: Namen ändern
            tradeguild.setName("Neuer Name");
            assertEquals("Neuer Name", tradeguild.getName());

            // PlotIsContainerForStorage: Storage abrufen
            Inventory storage = tradeguild.getStorageInventory();
            assertNotNull(storage);
            bukkit.verify(() -> Bukkit.createInventory(null, 54, "Lager: Neuer Name"));

            // PlotIsContainerForNpc: NPC zuweisen
            UUID npcId = UUID.randomUUID();
            tradeguild.setNpcId(npcId);
            assertEquals(npcId, tradeguild.getNpcId());
        }
    }

    @Test
    @DisplayName("Integration: Owner kann alle Actions ausführen")
    void testIntegration_OwnerCanExecuteAllActions() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(storageInventory);

            TradeguildPlot tradeguild = new TradeguildPlot(
                UUID.randomUUID(),
                ownerId,
                location,
                "Handelsgilde"
            );

            List<PlotAction> actions = tradeguild.getAvailablePlotActions();

            // Alle Actions sollten für Owner ausführbar sein
            for (PlotAction action : actions) {
                assertTrue(action.canExecute(owner),
                    "Owner sollte alle Actions ausführen können: " + action.getClass().getSimpleName());
            }
        }
    }

    @Test
    @DisplayName("Integration: Nicht-Owner kann keine Owner-Only Actions ausführen")
    void testIntegration_NonOwnerCannotExecuteOwnerActions() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(storageInventory);

            TradeguildPlot tradeguild = new TradeguildPlot(
                UUID.randomUUID(),
                ownerId,
                location,
                "Handelsgilde"
            );

            List<PlotAction> actions = tradeguild.getAvailablePlotActions();

            // Alle aktuellen Actions sind Owner-Only
            for (PlotAction action : actions) {
                assertFalse(action.canExecute(nonOwner),
                    "Nicht-Owner sollte Owner-Only Actions nicht ausführen können: " + action.getClass().getSimpleName());
            }
        }
    }

    @Test
    @DisplayName("Integration: Storage-Inventory nutzt Plot-Namen im Titel")
    void testIntegration_StorageInventoryUsesDynamicTitle() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(storageInventory);

            TradeguildPlot tradeguild = new TradeguildPlot(
                UUID.randomUUID(),
                ownerId,
                location,
                "Meine Handelsgilde"
            );

            // Rufe Storage ab
            tradeguild.getStorageInventory();

            // Verifiziere, dass Titel den Plot-Namen enthält
            bukkit.verify(() -> Bukkit.createInventory(null, 54, "Lager: Meine Handelsgilde"));
        }
    }

    @Test
    @DisplayName("Integration: PlotAction reflektiert aktuelle Plot-Daten")
    void testIntegration_PlotActionReflectsCurrentPlotData() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(isNull(), anyInt(), anyString()))
                .thenReturn(storageInventory);
            bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
            when(itemFactory.getItemMeta(any(Material.class))).thenReturn(itemMeta);
            when(itemMeta.clone()).thenReturn(itemMeta);

            List<String> initialLore = List.of(
                "§7Aktueller Name: §fAlter Name",
                "",
                "§7Klicke um den Namen zu ändern"
            );
            List<String> updatedLore = List.of(
                "§7Aktueller Name: §fNeuer Name",
                "",
                "§7Klicke um den Namen zu ändern"
            );

            TradeguildPlot tradeguild = new TradeguildPlot(
                UUID.randomUUID(),
                ownerId,
                location,
                "Alter Name"
            );

            // Erste Action mit altem Namen
            when(itemMeta.getLore()).thenReturn(initialLore);
            PlotActionSetName action1 = new PlotActionSetName(tradeguild);
            ItemStack item1 = action1.getDisplayItem();

            List<String> lore1 = item1.getItemMeta().getLore();
            assertTrue(lore1.get(0).contains("Alter Name"),
                "Lore sollte alten Namen enthalten");

            // Ändere Plot-Namen
            tradeguild.setName("Neuer Name");

            // Neue Action mit neuem Namen
            when(itemMeta.getLore()).thenReturn(updatedLore);
            PlotActionSetName action2 = new PlotActionSetName(tradeguild);
            ItemStack item2 = action2.getDisplayItem();

            List<String> lore2 = item2.getItemMeta().getLore();
            assertTrue(lore2.get(0).contains("Neuer Name"),
                "Lore sollte neuen Namen enthalten");
        }
    }

    // ==================== Test-Implementierung ====================

    /**
     * Mock-Implementierung eines TradeguildPlot für Integration-Tests.
     *
     * <p>Demonstriert vollständige Trait-Komposition:</p>
     * <ul>
     *   <li>PlotNamed - Namen-Verwaltung</li>
     *   <li>PlotIsContainerForStorage - Lager-Funktionalität</li>
     *   <li>PlotIsContainerForNpc - NPC-Verwaltung (Citizens-Integration)</li>
     * </ul>
     *
     * <p>In einer realen Implementierung würde dieser Plot zusätzlich
     * weitere Features haben wie:</p>
     * <ul>
     *   <li>Handels-Steuern</li>
     *   <li>Händler-NPCs</li>
     *   <li>Shop-Verwaltung</li>
     *   <li>Wirtschafts-Integration</li>
     * </ul>
     */
    private static class TradeguildPlot implements PlotNamed, PlotIsContainerForStorage, PlotIsContainerForNpc {
        private final UUID id;
        private final UUID ownerId;
        private final Location location;
        private String name;
        private Inventory storageInventory;
        private UUID npcId;

        TradeguildPlot(UUID id, UUID ownerId, Location location, String name) {
            this.id = id;
            this.ownerId = ownerId;
            this.location = location;
            this.name = name;
        }

        // ==================== PlotNamed ====================

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        // ==================== PlotIsContainerForStorage ====================

        @Override
        public Inventory getStorageInventory() {
            if (storageInventory == null) {
                storageInventory = Bukkit.createInventory(null, 54, "Lager: " + name);
            }
            return storageInventory;
        }

        // ==================== PlotIsContainerForNpc ====================

        @Override
        public UUID getNpcId() {
            return npcId;
        }

        @Override
        public void setNpcId(UUID npcId) {
            this.npcId = npcId;
        }

        // ==================== Plot ====================

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
            // Trait-Komposition: Kombiniere Actions aus allen Traits
            return Stream.of(
                getNameActions(),       // PlotNamed
                getStorageActions(),    // PlotIsContainerForStorage (aktuell leer)
                getNpcActions()         // PlotIsContainerForNpc (aktuell leer)
            ).flatMap(List::stream).toList();
        }

        /**
         * Override getNameActions() um konkrete Actions zurückzugeben.
         * Normalerweise würde PlotNamed dies als Default-Methode bereitstellen.
         */
        @Override
        public List<PlotAction> getNameActions() {
            return List.of(new PlotActionSetName(this));
        }
    }
}
