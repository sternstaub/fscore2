package de.fallenstar.core.plot;

import de.fallenstar.core.plot.action.PlotAction;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

/**
 * Basis-Interface für alle Plot-Typen im FallenStar-System.
 *
 * <p>Dieses Interface definiert die Kern-Anforderungen für einen Plot.
 * Konkrete Plot-Implementierungen können zusätzliche Traits implementieren
 * (z.B. PlotNamed, PlotContainerStorage) für erweiterte Funktionalität.</p>
 *
 * <p><b>Trait-Pattern:</b></p>
 * <p>Das Plot-System basiert auf Trait-Komposition statt Vererbung.
 * Plot-Typen können beliebige Traits kombinieren:</p>
 * <pre>
 * // Beispiel: TradeguildPlot mit mehreren Traits
 * class TradeguildPlot implements Plot, PlotNamed, PlotContainerStorage, PlotContainerNpc {
 *     {@literal @}Override
 *     public List&lt;PlotAction&gt; getAvailablePlotActions() {
 *         return Stream.of(
 *             getNameActions(),      // von PlotNamed
 *             getStorageActions(),   // von PlotContainerStorage
 *             getNpcActions()        // von PlotContainerNpc
 *         ).flatMap(List::stream).toList();
 *     }
 * }
 * </pre>
 *
 * <p><b>Kern-Methoden:</b></p>
 * <ul>
 *   <li>{@link #getId()} - Eindeutige Plot-ID</li>
 *   <li>{@link #getOwnerId()} - Besitzer-UUID (für Permissions)</li>
 *   <li>{@link #getLocation()} - Welt-Position des Plots</li>
 *   <li>{@link #getAvailablePlotActions()} - Alle verfügbaren Aktionen</li>
 * </ul>
 *
 * <p><b>Integration mit GuiBuilder:</b></p>
 * <pre>
 * // Automatisches GUI aus Plot-Actions erstellen
 * Inventory gui = GuiBuilder.buildFromActions(
 *     plot.getAvailablePlotActions(),
 *     player,
 *     "Plot-Verwaltung"
 * );
 * player.openInventory(gui);
 * </pre>
 *
 * @author FallenStar Development
 * @version 1.0.0
 * @see PlotAction
 * @see de.fallenstar.core.ui.GuiBuilder
 */
public interface Plot {

    /**
     * Gibt die eindeutige ID des Plots zurück.
     *
     * <p>Die Plot-ID ist persistent und ändert sich niemals während
     * der Lebenszeit eines Plots. Sie wird für Datenbankoperationen,
     * Caching und Plot-Referenzen verwendet.</p>
     *
     * <p><b>Wichtig:</b> Zwei verschiedene Plots haben niemals die gleiche ID.</p>
     *
     * <p><b>Verwendung:</b></p>
     * <pre>
     * // Plot aus Datenbank laden
     * Plot plot = plotManager.getPlot(plotId);
     *
     * // Plot in Cache speichern
     * plotCache.put(plot.getId(), plot);
     *
     * // Plot-Vergleich
     * if (plot1.getId().equals(plot2.getId())) {
     *     // Gleicher Plot
     * }
     * </pre>
     *
     * @return Die eindeutige Plot-ID (niemals null)
     */
    UUID getId();

    /**
     * Gibt die UUID des Plot-Besitzers zurück.
     *
     * <p>Diese Methode wird von {@link PlotAction#isOwner(org.bukkit.entity.Player)}
     * verwendet, um Ownership-Checks durchzuführen. Nur der Owner kann
     * bestimmte Aktionen ausführen (z.B. Plot umbenennen, löschen).</p>
     *
     * <p><b>Hinweis:</b> Die Owner-ID kann sich ändern (z.B. bei Verkauf/Transfer).
     * Für persistente Plot-Referenzen sollte {@link #getId()} verwendet werden.</p>
     *
     * <p><b>Beispiel:</b></p>
     * <pre>
     * // Permission-Check in PlotAction
     * protected boolean isOwner(Player player) {
     *     return plot.getOwnerId().equals(player.getUniqueId());
     * }
     * </pre>
     *
     * @return Die UUID des Besitzers (niemals null)
     */
    UUID getOwnerId();

    /**
     * Gibt die Welt-Position des Plots zurück.
     *
     * <p>Die Location markiert typischerweise den Spawn-Punkt oder die
     * Mitte des Plots. Sie wird für Teleportation und Welt-bezogene
     * Operationen verwendet.</p>
     *
     * <p><b>Verwendung:</b></p>
     * <pre>
     * // Teleport-Action
     * class PlotActionTeleport extends PlotAction {
     *     public void execute(Player player) {
     *         player.teleport(plot.getLocation());
     *         player.sendMessage("§aDu wurdest zum Plot teleportiert!");
     *     }
     * }
     *
     * // Welten-Check
     * if (plot.getLocation().getWorld().equals(player.getWorld())) {
     *     // Player ist in der gleichen Welt wie der Plot
     * }
     * </pre>
     *
     * <p><b>Wichtig:</b> Die Location sollte eine Kopie oder immutable sein,
     * um unbeabsichtigte Modifikationen zu vermeiden.</p>
     *
     * @return Die Welt-Position des Plots (niemals null)
     */
    Location getLocation();

    /**
     * Gibt alle verfügbaren PlotActions für diesen Plot zurück.
     *
     * <p>Diese Methode sammelt alle Actions aus implementierten Traits
     * und kombiniert sie zu einer einzigen Liste. Die Liste wird vom
     * {@link de.fallenstar.core.ui.GuiBuilder} verwendet, um automatisch
     * ein GUI zu generieren.</p>
     *
     * <p><b>Trait-basierte Implementierung:</b></p>
     * <pre>
     * class TradeguildPlot implements Plot, PlotNamed, PlotContainerStorage {
     *     {@literal @}Override
     *     public List&lt;PlotAction&gt; getAvailablePlotActions() {
     *         List&lt;PlotAction&gt; actions = new ArrayList&lt;&gt;();
     *         actions.addAll(getNameActions());      // von PlotNamed
     *         actions.addAll(getStorageActions());   // von PlotContainerStorage
     *         return actions;
     *     }
     * }
     * </pre>
     *
     * <p><b>Alternative mit Streams:</b></p>
     * <pre>
     * {@literal @}Override
     * public List&lt;PlotAction&gt; getAvailablePlotActions() {
     *     return Stream.of(
     *         getNameActions(),
     *         getStorageActions(),
     *         getNpcActions()
     *     ).flatMap(List::stream).toList();
     * }
     * </pre>
     *
     * <p><b>Verwendung mit GuiBuilder:</b></p>
     * <pre>
     * // Automatisches GUI erstellen
     * Inventory gui = GuiBuilder.buildFromActions(
     *     plot.getAvailablePlotActions(),
     *     player,
     *     "Plot-Verwaltung"
     * );
     * player.openInventory(gui);
     * </pre>
     *
     * <p><b>Wichtig:</b></p>
     * <ul>
     *   <li>Die Liste sollte niemals null sein (nutze {@code List.of()} für leere Liste)</li>
     *   <li>Actions können {@link de.fallenstar.core.ui.MenuAction} implementieren für Submenüs</li>
     *   <li>Visibility wird von GuiBuilder automatisch gefiltert (via {@link de.fallenstar.core.ui.GuiRenderable#isVisible})</li>
     * </ul>
     *
     * @return Liste aller verfügbaren PlotActions (niemals null, kann leer sein)
     */
    List<PlotAction> getAvailablePlotActions();
}
