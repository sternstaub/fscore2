package de.fallenstar.core.ui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Universeller Builder für Minecraft-GUIs aus GuiRenderable-Listen.
 *
 * <p>Der GuiBuilder ist das Herzstück des FallenStar-UI-Systems. Er erstellt
 * automatisch Inventar-GUIs aus beliebigen {@link GuiRenderable} Objekten,
 * ohne dass manuell UI-Klassen geschrieben werden müssen.</p>
 *
 * <p><b>Self-Rendering Pattern:</b></p>
 * <p>GuiBuilder nutzt das Self-Rendering Pattern: Objekte rendern sich selbst
 * via {@link GuiRenderable#getDisplayItem()}, der Builder fügt sie nur zusammen.</p>
 *
 * <p><b>Automatische Features:</b></p>
 * <ul>
 *   <li>Filtert unsichtbare Actions via {@link GuiRenderable#isVisible(Player)}</li>
 *   <li>Berechnet optimale GUI-Größe (immer Vielfaches von 9)</li>
 *   <li>Füllt Inventory sequenziell mit Display-Items</li>
 *   <li>Funktioniert mit ALLEN GuiRenderable-Typen (AbstractPlotAction, MenuAction, etc.)</li>
 * </ul>
 *
 * <p><b>Beispiel-Verwendung:</b></p>
 * <pre>
 * // Erstelle GUI aus Plot-Actions
 * List&lt;AbstractPlotAction&gt; actions = plot.getAvailablePlotActions();
 * Inventory gui = GuiBuilder.buildFromActions(actions, player, "Plot-Verwaltung");
 * player.openInventory(gui);
 * </pre>
 *
 * <p><b>Trait-Pattern Integration:</b></p>
 * <pre>
 * // Plot mit mehreren Traits
 * class TradeguildPlot implements PlotWithName, PlotContainerStorage, PlotContainerNpc {
 *     public List&lt;AbstractPlotAction&gt; getAvailablePlotActions() {
 *         return Stream.of(
 *             getNameActions(),
 *             getStorageActions(),
 *             getNpcActions()
 *         ).flatMap(List::stream).toList();
 *     }
 * }
 *
 * // GUI automatisch aus allen Traits generieren
 * Inventory gui = GuiBuilder.buildFromActions(
 *     plot.getAvailablePlotActions(),
 *     player,
 *     "Tradeguild-Verwaltung"
 * );
 * </pre>
 *
 * <p><b>MenuAction Support:</b></p>
 * <pre>
 * // MenuAction mit Submenü
 * class AbstractPlotActionManageStorage extends AbstractPlotAction implements MenuAction {
 *     public void execute(Player player) {
 *         // Erstelle Submenü via GuiBuilder
 *         Inventory submenu = GuiBuilder.buildFromActions(
 *             getSubActions(),
 *             player,
 *             "Lager-Verwaltung"
 *         );
 *         player.openInventory(submenu);
 *     }
 * }
 * </pre>
 *
 * <p><b>Visibility-Filtering:</b></p>
 * <pre>
 * // Actions mit Permission-Check
 * class AbstractPlotActionAdmin extends AbstractPlotAction {
 *     public boolean isVisible(Player player) {
 *         return player.hasPermission("plot.admin");
 *     }
 * }
 *
 * // GuiBuilder filtert automatisch unsichtbare Actions
 * Inventory gui = GuiBuilder.buildFromActions(actions, player, "Admin-Menü");
 * // Nur sichtbare Actions werden angezeigt
 * </pre>
 *
 * <p><b>GUI-Größen-Berechnung:</b></p>
 * <pre>
 * 1-9 Actions   → 9 Slots  (1 Reihe)
 * 10-18 Actions → 18 Slots (2 Reihen)
 * 19-27 Actions → 27 Slots (3 Reihen)
 * 28-36 Actions → 36 Slots (4 Reihen)
 * 37-45 Actions → 45 Slots (5 Reihen)
 * 46-54 Actions → 54 Slots (6 Reihen, Maximum)
 * </pre>
 *
 * @author FallenStar Development
 * @version 1.0.0
 * @see GuiRenderable
 * @see de.fallenstar.core.plot.action.PlotAction
 * @see MenuAction
 */
public final class GuiBuilder {

    /**
     * Maximale Anzahl von Slots in einem Minecraft Inventory (6 Reihen).
     */
    private static final int MAX_INVENTORY_SIZE = 54;

    /**
     * Anzahl von Slots pro Inventory-Reihe.
     */
    private static final int SLOTS_PER_ROW = 9;

    /**
     * Private Constructor (Utility-Klasse).
     */
    private GuiBuilder() {
        throw new UnsupportedOperationException("GuiBuilder ist eine Utility-Klasse und kann nicht instanziiert werden");
    }

    /**
     * Erstellt ein Inventory-GUI aus einer Liste von GuiRenderable-Objekten.
     *
     * <p>Diese Methode ist der zentrale Entry-Point für GUI-Generierung.
     * Sie filtert unsichtbare Actions, berechnet die optimale GUI-Größe
     * und füllt das Inventory mit Display-Items.</p>
     *
     * <p><b>Workflow:</b></p>
     * <ol>
     *   <li>Filtere unsichtbare Actions via {@link GuiRenderable#isVisible(Player)}</li>
     *   <li>Berechne GUI-Größe (nächstes Vielfaches von 9, max. 54)</li>
     *   <li>Erstelle Bukkit Inventory mit berechnetem Titel und Größe</li>
     *   <li>Fülle Inventory sequenziell mit Display-Items</li>
     * </ol>
     *
     * <p><b>Beispiele:</b></p>
     * <pre>
     * // Einfaches GUI
     * List&lt;AbstractPlotAction&gt; actions = List.of(
     *     new PlotActionSetName(plot),
     *     new AbstractPlotActionTeleport(plot)
     * );
     * Inventory gui = GuiBuilder.buildFromActions(actions, player, "Plot-Menü");
     * player.openInventory(gui);
     * </pre>
     *
     * <pre>
     * // Mit Visibility-Filtering
     * List&lt;AbstractPlotAction&gt; actions = List.of(
     *     new PlotActionSetName(plot),        // Sichtbar für alle
     *     new AbstractPlotActionDelete(plot),         // Nur für Owner sichtbar
     *     new AbstractPlotActionAdminReset(plot)      // Nur für Admins sichtbar
     * );
     * Inventory gui = GuiBuilder.buildFromActions(actions, player, "Plot-Menü");
     * // Nicht-Owner sehen nur PlotActionSetName
     * </pre>
     *
     * <pre>
     * // Leere Action-Liste
     * List&lt;AbstractPlotAction&gt; actions = List.of();
     * Inventory gui = GuiBuilder.buildFromActions(actions, player, "Leeres Menü");
     * // Erstellt GUI mit minimaler Größe (9 Slots, leer)
     * </pre>
     *
     * <p><b>Wichtige Hinweise:</b></p>
     * <ul>
     *   <li>Unsichtbare Actions werden NICHT im GUI angezeigt</li>
     *   <li>GUI-Größe ist immer ein Vielfaches von 9 (1-6 Reihen)</li>
     *   <li>Bei mehr als 54 Actions werden nur die ersten 54 angezeigt</li>
     *   <li>Actions werden in der Reihenfolge der Liste angezeigt (0-based)</li>
     *   <li>Leere Slots bleiben AIR (nicht gefüllt)</li>
     * </ul>
     *
     * @param actions Liste von GuiRenderable-Objekten (z.B. AbstractPlotActions)
     * @param player Der Player, für den das GUI erstellt wird (für Visibility-Checks)
     * @param title Der Titel des Inventars (wird im GUI-Header angezeigt)
     * @return Ein gefülltes Bukkit Inventory, bereit zum Öffnen
     * @throws IllegalArgumentException wenn actions, player oder title null sind
     */
    public static Inventory buildFromActions(List<? extends GuiRenderable> actions, Player player, String title) {
        // Validierung
        if (actions == null) {
            throw new IllegalArgumentException("Actions-Liste darf nicht null sein");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player darf nicht null sein");
        }
        if (title == null) {
            throw new IllegalArgumentException("Titel darf nicht null sein");
        }

        // Schritt 1: Filtere unsichtbare Actions
        List<? extends GuiRenderable> visibleActions = actions.stream()
            .filter(action -> action.isVisible(player))
            .toList();

        // Schritt 2: Berechne GUI-Größe
        int size = calculateInventorySize(visibleActions.size());

        // Schritt 3: Erstelle Inventory
        Inventory inventory = Bukkit.createInventory(null, size, title);

        // Schritt 4: Fülle Inventory mit Display-Items
        for (int i = 0; i < visibleActions.size() && i < MAX_INVENTORY_SIZE; i++) {
            GuiRenderable action = visibleActions.get(i);
            ItemStack displayItem = action.getDisplayItem();
            inventory.setItem(i, displayItem);
        }

        return inventory;
    }

    /**
     * Berechnet die optimale Inventory-Größe für eine gegebene Anzahl von Items.
     *
     * <p>Minecraft Inventories müssen immer ein Vielfaches von 9 Slots haben
     * (1-6 Reihen à 9 Slots). Diese Methode berechnet die kleinste Inventory-Größe,
     * die alle Items aufnehmen kann.</p>
     *
     * <p><b>Berechnungslogik:</b></p>
     * <pre>
     * Größe = ceil(itemCount / 9.0) * 9
     * Minimum: 9 Slots  (1 Reihe)
     * Maximum: 54 Slots (6 Reihen)
     * </pre>
     *
     * <p><b>Beispiele:</b></p>
     * <pre>
     * calculateInventorySize(0)  = 9   (Minimum)
     * calculateInventorySize(1)  = 9   (1 Reihe)
     * calculateInventorySize(9)  = 9   (1 Reihe, exakt)
     * calculateInventorySize(10) = 18  (2 Reihen)
     * calculateInventorySize(27) = 27  (3 Reihen, exakt)
     * calculateInventorySize(50) = 54  (6 Reihen)
     * calculateInventorySize(100) = 54 (Maximum erreicht)
     * </pre>
     *
     * <p><b>Edge Cases:</b></p>
     * <ul>
     *   <li>0 Items → 9 Slots (Minimum)</li>
     *   <li>Negative Werte → 9 Slots (Minimum)</li>
     *   <li>&gt;54 Items → 54 Slots (Maximum)</li>
     * </ul>
     *
     * @param itemCount Anzahl der Items, die im Inventory Platz finden müssen
     * @return Berechnete Inventory-Größe (immer Vielfaches von 9, zwischen 9 und 54)
     */
    private static int calculateInventorySize(int itemCount) {
        // Edge Case: Negative oder 0 Items
        if (itemCount <= 0) {
            return SLOTS_PER_ROW;  // Minimum: 1 Reihe (9 Slots)
        }

        // Berechne Anzahl benötigter Reihen (aufrunden)
        int rows = (int) Math.ceil(itemCount / (double) SLOTS_PER_ROW);

        // Berechne Gesamtgröße (Reihen * 9)
        int size = rows * SLOTS_PER_ROW;

        // Begrenze auf Maximum (54 Slots = 6 Reihen)
        return Math.min(size, MAX_INVENTORY_SIZE);
    }
}
