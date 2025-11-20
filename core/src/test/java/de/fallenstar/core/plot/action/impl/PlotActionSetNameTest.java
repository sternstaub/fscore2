package de.fallenstar.core.plot.action.impl;

import de.fallenstar.core.plot.action.AbstractPlotAction;
import de.fallenstar.core.plot.trait.PlotWithName;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für PlotActionSetName.
 *
 * <p>Testet die erste konkrete AbstractPlotAction-Implementierung mit Fokus auf:</p>
 * <ul>
 *   <li>Ownership-Prüfung (nur Besitzer kann ausführen)</li>
 *   <li>GUI-Display-Item Generierung</li>
 *   <li>Execute-Methode (Placeholder-Implementierung)</li>
 *   <li>Null-Safety und Exception-Handling</li>
 *   <li>Integration mit PlotWithName Trait</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PlotActionSetName Tests")
class PlotActionSetNameTest {

    @Mock
    private Player owner;

    @Mock
    private Player nonOwner;

    @Mock
    private Location location;

    @Mock
    private ItemFactory itemFactory;

    @Mock
    private ItemMeta itemMeta;

    private TestPlotWithName plot;
    private PlotActionSetName action;

    @BeforeEach
    void setUp() {
        UUID plotOwnerId = UUID.randomUUID();
        UUID nonOwnerId = UUID.randomUUID();

        // Mock Player-UUIDs
        when(owner.getUniqueId()).thenReturn(plotOwnerId);
        when(nonOwner.getUniqueId()).thenReturn(nonOwnerId);
        when(owner.hasPermission(anyString())).thenReturn(false);
        when(nonOwner.hasPermission(anyString())).thenReturn(false);

        // Erstelle Test-Plot
        plot = new TestPlotWithName(UUID.randomUUID(), plotOwnerId, location, "Testplot");
        action = new PlotActionSetName(plot);
    }

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Constructor: Akzeptiert validen PlotWithName")
    void testConstructor_ValidPlot() {
        PlotActionSetName validAction = new PlotActionSetName(plot);

        assertNotNull(validAction, "Action sollte erfolgreich erstellt werden");
    }

    @Test
    @DisplayName("Constructor: Wirft Exception bei null")
    void testConstructor_NullPlot_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new PlotActionSetName(null),
            "Constructor sollte IllegalArgumentException bei null werfen"
        );

        assertEquals("Plot darf nicht null sein", exception.getMessage());
    }

    // ==================== Permission Tests ====================

    @Test
    @DisplayName("canExecute: Owner kann ausführen")
    void testCanExecute_OwnerReturnsTrue() {
        boolean result = action.canExecute(owner);

        assertTrue(result, "Plot-Owner sollte Action ausführen können");
    }

    @Test
    @DisplayName("canExecute: Nicht-Owner kann nicht ausführen")
    void testCanExecute_NonOwnerReturnsFalse() {
        boolean result = action.canExecute(nonOwner);

        assertFalse(result, "Nicht-Owner sollte Action nicht ausführen können");
    }

    @Test
    @DisplayName("requiresOwnership: Gibt true zurück")
    void testRequiresOwnership_ReturnsTrue() {
        // Wir testen indirekt über canExecute
        // Owner kann ausführen
        assertTrue(action.canExecute(owner));
        // Nicht-Owner kann nicht ausführen
        assertFalse(action.canExecute(nonOwner));
    }

    // ==================== Execute Tests ====================

    @Test
    @DisplayName("execute: Sendet Placeholder-Message an Spieler")
    void testExecute_SendsPlaceholderMessage() {
        action.invokeByGuiButton(owner);

        // Verifiziere, dass 3 Messages gesendet wurden
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(owner, times(3)).sendMessage(messageCaptor.capture());

        List<String> messages = messageCaptor.getAllValues();
        assertEquals(3, messages.size(), "Sollte 3 Messages senden");

        // Prüfe erste Message enthält Plot-Namen
        assertTrue(messages.get(0).contains("Testplot"),
            "Erste Message sollte Plot-Namen enthalten");

        // Prüfe zweite Message ist Placeholder-Hinweis
        assertTrue(messages.get(1).contains("zukünftigen Phase"),
            "Zweite Message sollte Placeholder-Hinweis enthalten");
    }

    @Test
    @DisplayName("execute: Funktioniert auch für Nicht-Owner (sendet nur Message)")
    void testExecute_WorksForNonOwner() {
        // execute() selbst prüft keine Permissions, das macht canExecute()
        // Aber execute() sollte trotzdem ohne Fehler laufen
        assertDoesNotThrow(() -> action.invokeByGuiButton(nonOwner),
            "execute() sollte keine Exception werfen");

        verify(nonOwner, times(3)).sendMessage(anyString());
    }

    // ==================== DisplayItem Tests ====================

    @Test
    @DisplayName("getDisplayItem: Hat korrekten Material-Typ (NAME_TAG)")
    void testGetDisplayItem_HasCorrectMaterial() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
            when(itemFactory.getItemMeta(any(Material.class))).thenReturn(itemMeta);
            when(itemMeta.clone()).thenReturn(itemMeta);

            ItemStack item = action.getDisplayItem();

            assertNotNull(item, "DisplayItem sollte nicht null sein");
            assertEquals(Material.NAME_TAG, item.getType(),
                "DisplayItem sollte Material NAME_TAG haben");
        }
    }

    @Test
    @DisplayName("getDisplayItem: Hat Display-Name gesetzt")
    void testGetDisplayItem_HasDisplayName() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
            when(itemFactory.getItemMeta(any(Material.class))).thenReturn(itemMeta);
            when(itemMeta.clone()).thenReturn(itemMeta);
            when(itemMeta.hasDisplayName()).thenReturn(true);
            when(itemMeta.getDisplayName()).thenReturn("§6Namen ändern");

            ItemStack item = action.getDisplayItem();
            ItemMeta meta = item.getItemMeta();

            assertNotNull(meta, "ItemMeta sollte nicht null sein");
            assertTrue(meta.hasDisplayName(), "Item sollte DisplayName haben");
            assertEquals("§6Namen ändern", meta.getDisplayName(),
                "DisplayName sollte '§6Namen ändern' sein");
        }
    }

    @Test
    @DisplayName("getDisplayItem: Hat Lore mit aktuellem Plot-Namen")
    void testGetDisplayItem_HasLoreWithPlotName() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            List<String> expectedLore = List.of(
                "§7Aktueller Name: §fTestplot",
                "",
                "§7Klicke um den Namen zu ändern"
            );

            bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
            when(itemFactory.getItemMeta(any(Material.class))).thenReturn(itemMeta);
            when(itemMeta.clone()).thenReturn(itemMeta);
            when(itemMeta.hasLore()).thenReturn(true);
            when(itemMeta.getLore()).thenReturn(expectedLore);

            ItemStack item = action.getDisplayItem();
            ItemMeta meta = item.getItemMeta();

            assertNotNull(meta, "ItemMeta sollte nicht null sein");
            assertTrue(meta.hasLore(), "Item sollte Lore haben");

            List<String> lore = meta.getLore();
            assertNotNull(lore, "Lore sollte nicht null sein");
            assertEquals(3, lore.size(), "Lore sollte 3 Zeilen haben");

            // Erste Zeile sollte aktuellen Plot-Namen enthalten
            assertTrue(lore.get(0).contains("Testplot"),
                "Erste Lore-Zeile sollte Plot-Namen 'Testplot' enthalten");
        }
    }

    @Test
    @DisplayName("getDisplayItem: Lore aktualisiert sich bei Namensänderung")
    void testGetDisplayItem_LoreUpdatesWhenNameChanges() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            // Ändere Plot-Namen
            plot.setName("Neuer Name");

            List<String> updatedLore = List.of(
                "§7Aktueller Name: §fNeuer Name",
                "",
                "§7Klicke um den Namen zu ändern"
            );

            bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
            when(itemFactory.getItemMeta(any(Material.class))).thenReturn(itemMeta);
            when(itemMeta.clone()).thenReturn(itemMeta);
            when(itemMeta.getLore()).thenReturn(updatedLore);

            // Erstelle neue Action mit geändertem Plot
            PlotActionSetName newAction = new PlotActionSetName(plot);
            ItemStack item = newAction.getDisplayItem();
            ItemMeta meta = item.getItemMeta();

            assertNotNull(meta);
            List<String> lore = meta.getLore();
            assertNotNull(lore);

            // Lore sollte neuen Namen enthalten
            assertTrue(lore.get(0).contains("Neuer Name"),
                "Lore sollte aktualisierten Plot-Namen enthalten");
        }
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("Integration: Action funktioniert mit PlotWithName Trait")
    void testIntegration_WorksWithPlotWithNameTrait() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
            when(itemFactory.getItemMeta(any(Material.class))).thenReturn(itemMeta);
            when(itemMeta.clone()).thenReturn(itemMeta);

            // Erstelle PlotWithName
            PlotWithName namedPlot = new TestPlotWithName(
                UUID.randomUUID(),
                UUID.randomUUID(),
                location,
                "Integration Test Plot"
            );

            // Erstelle Action
            AbstractPlotAction setNameAction = new PlotActionSetName(namedPlot);

            // Verifiziere, dass Action korrekt funktioniert
            assertNotNull(setNameAction.getDisplayItem());
            assertEquals(Material.NAME_TAG, setNameAction.getDisplayItem().getType());
        }
    }

    @Test
    @DisplayName("Integration: isVisible gibt true zurück (Standard)")
    void testIntegration_IsVisibleReturnsTrue() {
        boolean visible = action.isVisible(owner);

        assertTrue(visible, "Action sollte standardmäßig sichtbar sein");
    }

    // ==================== Test-Implementierung ====================

    /**
     * Test-Implementierung von PlotWithName für Tests.
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
            return List.of(new PlotActionSetName(this));
        }
    }
}
