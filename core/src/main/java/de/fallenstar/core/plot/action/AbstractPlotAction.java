package de.fallenstar.core.plot.action;

import de.fallenstar.core.plot.Plot;
import de.fallenstar.core.ui.GuiRenderable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Abstrakte Basisklasse für alle Plot-Aktionen (Command Pattern).
 *
 * <p><b>Naming Convention:</b> Prefix-Pattern für abstrakte Klassen: {@code Abstract[Name]}</p>
 *
 * <p>AbstractPlotAction kombiniert drei Verantwortlichkeiten in einer Klasse:</p>
 * <ul>
 *   <li><b>Business Logic:</b> {@link #execute(Player)} führt die Aktion aus</li>
 *   <li><b>Permissions:</b> {@link #canExecute(Player)} prüft Berechtigungen</li>
 *   <li><b>GUI-Rendering:</b> {@link #getDisplayItem()} zeigt die Aktion im GUI</li>
 * </ul>
 *
 * <p><b>Self-Rendering Pattern:</b></p>
 * <p>AbstractPlotAction-Subklassen rendern sich selbst im GUI durch Implementierung von
 * {@link GuiRenderable}. Es sind keine separaten UI-Klassen notwendig.</p>
 *
 * <p><b>Permission-System:</b></p>
 * <p>Das Permission-System arbeitet zweistufig:</p>
 * <ol>
 *   <li>{@link #requiresOwnership()} definiert ob Owner-Rechte nötig sind (Standard: true)</li>
 *   <li>{@link #canExecute(Player)} prüft ob Player die Aktion ausführen darf</li>
 * </ol>
 *
 * <p><b>Beispiel-Implementierung:</b></p>
 * <pre>
 * public class PlotActionSetName extends AbstractPlotAction {
 *
 *     public PlotActionSetName(Plot plot) {
 *         super(plot);
 *     }
 *
 *     {@literal @}Override
 *     public void execute(Player player) {
 *         player.sendMessage("§aGib einen neuen Namen ein:");
 *         // Chat-Input-Handler registrieren
 *     }
 *
 *     {@literal @}Override
 *     public ItemStack getDisplayItem() {
 *         ItemStack item = new ItemStack(Material.NAME_TAG);
 *         ItemMeta meta = item.getItemMeta();
 *         meta.setDisplayName("§6Plot umbenennen");
 *         meta.setLore(List.of("§7Klicke um deinen Plot umzubenennen"));
 *         item.setItemMeta(meta);
 *         return item;
 *     }
 * }
 * </pre>
 *
 * <p><b>Verwendung:</b></p>
 * <pre>
 * AbstractPlotAction action = new PlotActionSetName(plot);
 *
 * if (action.canExecute(player)) {
 *     action.execute(player);
 * } else {
 *     player.sendMessage("§cDu hast keine Berechtigung!");
 * }
 * </pre>
 *
 * <p><b>Trait-Pattern Integration:</b></p>
 * <pre>
 * interface PlotWithName extends Plot {
 *     default List&lt;AbstractPlotAction&gt; getNameActions() {
 *         return List.of(new PlotActionSetName(this));
 *     }
 * }
 * </pre>
 *
 * @author FallenStar Development
 * @version 1.0.0
 * @see GuiRenderable
 * @see Plot
 */
public abstract class AbstractPlotAction implements GuiRenderable {

    /**
     * Der Plot, auf dem diese Aktion ausgeführt wird.
     *
     * <p>Dieser Plot wird im Konstruktor gesetzt und ist immutable (final).
     * Alle Subklassen haben Zugriff auf den Plot über dieses Feld.</p>
     */
    protected final Plot plot;

    /**
     * Erstellt eine neue AbstractPlotAction für den angegebenen Plot.
     *
     * @param plot Der Plot, auf dem die Aktion ausgeführt werden soll (nicht null)
     * @throws IllegalArgumentException wenn plot null ist
     */
    protected AbstractPlotAction(Plot plot) {
        if (plot == null) {
            throw new IllegalArgumentException("Plot darf nicht null sein");
        }
        this.plot = plot;
    }

    /**
     * Definiert ob diese Aktion Owner-Rechte benötigt.
     *
     * <p><b>Standard-Verhalten:</b> Gibt true zurück (Owner-Rechte erforderlich).</p>
     *
     * <p><b>Überschreiben für Public-Actions:</b></p>
     * <pre>
     * {@literal @}Override
     * protected boolean requiresOwnership() {
     *     return false;  // Jeder darf diese Aktion ausführen
     * }
     * </pre>
     *
     * @return true wenn Owner-Rechte benötigt werden, false sonst
     */
    protected boolean requiresOwnership() {
        return true;
    }

    /**
     * Prüft ob der angegebene Player diese Aktion ausführen darf.
     *
     * <p>Die Standard-Implementierung prüft:</p>
     * <ol>
     *   <li>Wenn {@link #requiresOwnership()} true: Prüfe mit {@link #isOwner(Player)}</li>
     *   <li>Wenn {@link #requiresOwnership()} false: Erlaube immer</li>
     * </ol>
     *
     * <p><b>Überschreiben für Custom-Permissions:</b></p>
     * <pre>
     * {@literal @}Override
     * public boolean canExecute(Player player) {
     *     // Custom Permission-Check
     *     return player.hasPermission("plot.admin") || super.canExecute(player);
     * }
     * </pre>
     *
     * @param player Der Player, für den die Berechtigung geprüft werden soll
     * @return true wenn der Player die Aktion ausführen darf, false sonst
     */
    public boolean canExecute(Player player) {
        if (!requiresOwnership()) {
            return true;
        }
        return isOwner(player);
    }

    /**
     * Prüft ob der angegebene Player der Owner des Plots ist.
     *
     * <p>Diese Helper-Methode wird von {@link #canExecute(Player)} verwendet
     * und kann auch in Subklassen für Custom-Checks genutzt werden.</p>
     *
     * @param player Der Player, der geprüft werden soll
     * @return true wenn der Player Owner ist, false sonst
     */
    protected boolean isOwner(Player player) {
        return plot.getOwnerId().equals(player.getUniqueId());
    }

    /**
     * Führt die Plot-Aktion aus.
     *
     * <p><b>Wichtig:</b> Diese Methode wird nur aufgerufen, wenn
     * {@link #canExecute(Player)} true zurückgegeben hat. Die Berechtigung
     * muss NICHT erneut geprüft werden.</p>
     *
     * <p><b>Implementierungs-Richtlinien:</b></p>
     * <ul>
     *   <li>Sende dem Player Feedback (Success/Error Messages)</li>
     *   <li>Werfe keine Exceptions für normale Fehler (nutze Messages)</li>
     *   <li>Nutze die Bukkit API für asynchrone Operationen</li>
     * </ul>
     *
     * <p><b>Beispiel:</b></p>
     * <pre>
     * {@literal @}Override
     * public void execute(Player player) {
     *     player.sendMessage("§aPlot wird teleportiert...");
     *     player.teleport(plot.getLocation());
     *     player.sendMessage("§aDu wurdest zu deinem Plot teleportiert!");
     * }
     * </pre>
     *
     * @param player Der Player, der die Aktion ausführt (niemals null)
     */
    public abstract void execute(Player player);

    /**
     * Gibt das ItemStack zurück, das in der GUI angezeigt werden soll.
     *
     * <p>Diese Methode ist von {@link GuiRenderable} geerbt und MUSS
     * von allen Subklassen implementiert werden.</p>
     *
     * <p><b>Beispiel-Implementierung:</b></p>
     * <pre>
     * {@literal @}Override
     * public ItemStack getDisplayItem() {
     *     ItemStack item = new ItemStack(Material.NAME_TAG);
     *     ItemMeta meta = item.getItemMeta();
     *     meta.setDisplayName("§6Plot umbenennen");
     *     meta.setLore(List.of(
     *         "§7Aktueller Name: §f" + plot.getName(),
     *         "",
     *         "§eKlicke zum Umbenennen"
     *     ));
     *     item.setItemMeta(meta);
     *     return item;
     * }
     * </pre>
     *
     * @return Das ItemStack für die GUI-Darstellung (niemals null)
     */
    @Override
    public abstract ItemStack getDisplayItem();

    /**
     * Prüft ob diese Aktion für den angegebenen Player sichtbar sein soll.
     *
     * <p>Die Standard-Implementierung von {@link GuiRenderable} gibt immer
     * true zurück. Subklassen können dies überschreiben für bedingte Sichtbarkeit.</p>
     *
     * <p><b>Beispiel:</b></p>
     * <pre>
     * {@literal @}Override
     * public boolean isVisible(Player player) {
     *     // Nur für Owner sichtbar
     *     return isOwner(player);
     * }
     * </pre>
     *
     * @param player Der Player, für den die Sichtbarkeit geprüft werden soll
     * @return true wenn sichtbar (Standard aus {@link GuiRenderable#isVisible(Player)})
     */
    @Override
    public boolean isVisible(Player player) {
        return GuiRenderable.super.isVisible(player);
    }
}
