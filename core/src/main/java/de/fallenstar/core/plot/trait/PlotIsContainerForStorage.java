package de.fallenstar.core.plot.trait;

import de.fallenstar.core.plot.Plot;
import de.fallenstar.core.plot.action.PlotAction;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Trait für Plots mit Lager-Funktionalität (Storage Container).
 *
 * <p>PlotIsContainerForStorage ist ein Trait-Interface für Plots, die einen
 * Lager-Bereich (Chest-Inventory) bereitstellen. Spieler können Items
 * in diesem Lager ablegen und wieder entnehmen.</p>
 *
 * <p><b>Trait-Komposition:</b></p>
 * <pre>
 * class StoragePlot implements Plot, PlotNamed, PlotIsContainerForStorage {
 *     private Inventory storageInventory;
 *
 *     {@literal @}Override
 *     public Inventory getStorageInventory() {
 *         return storageInventory;
 *     }
 *
 *     {@literal @}Override
 *     public List&lt;PlotAction&gt; getAvailablePlotActions() {
 *         return Stream.of(
 *             getNameActions(),
 *             getStorageActions()
 *         ).flatMap(List::stream).toList();
 *     }
 * }
 * </pre>
 *
 * <p><b>Verwendungs-Szenarien:</b></p>
 * <ul>
 *   <li>Lagerplätze für Items</li>
 *   <li>Handelsplätze mit Warenlager</li>
 *   <li>Produktions-Plots mit Material-Storage</li>
 * </ul>
 *
 * <p><b>Default-Actions:</b></p>
 * <p>Das Interface stellt eine Default-Implementierung für {@link #getStorageActions()}
 * bereit. Diese ist aktuell ein Placeholder und wird in zukünftigen Phasen
 * mit konkreten Actions erweitert.</p>
 *
 * @author FallenStar Development
 * @version 1.0.0-SNAPSHOT
 * @see Plot
 * @see PlotAction
 */
public interface PlotIsContainerForStorage extends Plot {

    /**
     * Gibt das Storage-Inventory des Plots zurück.
     *
     * <p>Das Storage-Inventory ist ein Bukkit-Inventory (typischerweise ein
     * Chest-Inventory), in dem Items gespeichert werden können.</p>
     *
     * <p><b>Implementierungs-Hinweise:</b></p>
     * <ul>
     *   <li>Das Inventory sollte persistent sein (z.B. in Datenbank gespeichert)</li>
     *   <li>Größe kann variieren (9-54 Slots)</li>
     *   <li>Inventory sollte bei Bedarf geladen werden (Lazy Loading)</li>
     * </ul>
     *
     * <p><b>Beispiel:</b></p>
     * <pre>
     * // Implementierung
     * private Inventory storageInventory;
     *
     * {@literal @}Override
     * public Inventory getStorageInventory() {
     *     if (storageInventory == null) {
     *         storageInventory = Bukkit.createInventory(null, 54, "Lager: " + getName());
     *         loadStorageFromDatabase();
     *     }
     *     return storageInventory;
     * }
     * </pre>
     *
     * <p><b>Verwendung:</b></p>
     * <pre>
     * // PlotAction öffnet Storage
     * class PlotActionOpenStorage extends PlotAction {
     *     public void execute(Player player) {
     *         PlotIsContainerForStorage storagePlot = (PlotIsContainerForStorage) plot;
     *         player.openInventory(storagePlot.getStorageInventory());
     *     }
     * }
     * </pre>
     *
     * @return Das Storage-Inventory des Plots (niemals null)
     */
    Inventory getStorageInventory();

    /**
     * Gibt die Liste von PlotActions für Lager-Verwaltung zurück.
     *
     * <p>Diese Default-Implementierung ist ein Placeholder für zukünftige
     * Storage-bezogene Actions wie:</p>
     * <ul>
     *   <li>PlotActionOpenStorage - Öffnet das Lager</li>
     *   <li>PlotActionClearStorage - Leert das Lager</li>
     *   <li>PlotActionUpgradeStorage - Vergrößert das Lager</li>
     *   <li>PlotActionSetStoragePrice - Setzt Mietpreis für Lager</li>
     * </ul>
     *
     * <p><b>Aktuelle Implementierung (Placeholder):</b></p>
     * <pre>
     * default List&lt;PlotAction&gt; getStorageActions() {
     *     return List.of();  // Wird in zukünftigen Phasen erweitert
     * }
     * </pre>
     *
     * <p><b>Override-Möglichkeit:</b></p>
     * <p>Konkrete Plot-Typen können diese Methode überschreiben, um
     * spezifische Storage-Actions anzubieten.</p>
     *
     * @return Liste von PlotActions für Lager-Verwaltung (niemals null)
     */
    default List<PlotAction> getStorageActions() {
        // Placeholder - wird in zukünftigen Phasen erweitert
        return List.of();
    }
}
