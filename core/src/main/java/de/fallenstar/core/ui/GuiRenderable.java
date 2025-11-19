package de.fallenstar.core.ui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Interface für Objekte, die sich selbst in einer GUI darstellen können.
 *
 * <p>Das GuiRenderable-Pattern ist zentral für das FallenStar-UI-System.
 * Objekte, die dieses Interface implementieren, können automatisch in
 * GUIs angezeigt werden, ohne dass der GUI-Builder die interne Struktur
 * kennen muss.</p>
 *
 * <p><b>Self-Rendering Pattern:</b></p>
 * <pre>
 * - Objekte rendern sich selbst
 * - Keine zentrale Rendering-Logik notwendig
 * - Einfaches Hinzufügen neuer GUI-Elemente
 * - Vollständige Kontrolle über Display-Items
 * </pre>
 *
 * <p><b>Beispiel-Implementierung:</b></p>
 * <pre>
 * public class PlotActionSetName implements GuiRenderable {
 *     {@literal @}Override
 *     public ItemStack getDisplayItem() {
 *         ItemStack item = new ItemStack(Material.NAME_TAG);
 *         ItemMeta meta = item.getItemMeta();
 *         meta.setDisplayName("§6Plot umbenennen");
 *         meta.setLore(List.of("§7Klicken zum Umbenennen"));
 *         item.setItemMeta(meta);
 *         return item;
 *     }
 *
 *     {@literal @}Override
 *     public boolean isVisible(Player player) {
 *         return player.hasPermission("plot.rename");
 *     }
 * }
 * </pre>
 *
 * <p><b>Verwendung mit GuiBuilder:</b></p>
 * <pre>
 * List&lt;GuiRenderable&gt; actions = plot.getAvailableActions();
 * Inventory gui = GuiBuilder.buildFromActions(actions, player, "Plot-Menü");
 * </pre>
 *
 * @author FallenStar Development
 * @version 1.0.0
 * @see de.fallenstar.core.ui.GuiBuilder
 */
public interface GuiRenderable {

    /**
     * Gibt das ItemStack zurück, das in der GUI angezeigt werden soll.
     *
     * <p>Das ItemStack sollte vollständig konfiguriert sein mit:</p>
     * <ul>
     *   <li>Material (Icon)</li>
     *   <li>DisplayName (Titel)</li>
     *   <li>Lore (Beschreibung)</li>
     *   <li>Optional: Enchantments, Flags, etc.</li>
     * </ul>
     *
     * <p><b>Wichtig:</b> Diese Methode wird bei jedem GUI-Öffnen aufgerufen.
     * Für Performance sollte das ItemStack bei jedem Aufruf neu erstellt werden,
     * damit Änderungen (z.B. durch Permissions) sofort sichtbar sind.</p>
     *
     * @return Das ItemStack für die GUI-Darstellung (niemals null)
     */
    ItemStack getDisplayItem();

    /**
     * Prüft ob dieses Element für den angegebenen Player sichtbar sein soll.
     *
     * <p>Diese Methode wird verwendet, um:</p>
     * <ul>
     *   <li>Permission-basierte Sichtbarkeit zu implementieren</li>
     *   <li>Dynamische GUI-Anpassung pro Player zu ermöglichen</li>
     *   <li>Optionale Features ein-/auszublenden</li>
     * </ul>
     *
     * <p><b>Standard-Implementierung:</b> Gibt immer true zurück (immer sichtbar).</p>
     *
     * <p><b>Beispiele:</b></p>
     * <pre>
     * // Permission-Check
     * return player.hasPermission("plot.admin");
     *
     * // Owner-Check
     * return plot.isOwner(player.getUniqueId());
     *
     * // Feature-Toggle
     * return config.isFeatureEnabled("advanced-mode");
     * </pre>
     *
     * @param player Der Player, für den die Sichtbarkeit geprüft werden soll
     * @return true wenn sichtbar, false wenn ausgeblendet werden soll
     */
    default boolean isVisible(Player player) {
        return true;
    }
}
