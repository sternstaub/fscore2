package de.fallenstar.core.plot.action.impl;

import de.fallenstar.core.plot.action.PlotAction;
import de.fallenstar.core.plot.trait.PlotWithName;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * PlotAction zum Setzen/Ändern des Plot-Namens.
 *
 * <p>Diese Action ermöglicht es dem Plot-Besitzer, den Namen seines Plots
 * zu ändern. Die Action funktioniert nur auf Plots, die das {@link PlotWithName}
 * Trait implementieren.</p>
 *
 * <p><b>Berechtigungen:</b></p>
 * <ul>
 *   <li>Benötigt Plot-Ownership (nur Besitzer kann Namen ändern)</li>
 *   <li>Keine zusätzlichen Permissions erforderlich</li>
 * </ul>
 *
 * <p><b>Verwendung:</b></p>
 * <pre>
 * PlotWithName namedPlot = getTradeguildPlot();
 * PlotActionSetName action = new PlotActionSetName(namedPlot);
 *
 * // Im GUI anzeigen
 * Inventory gui = GuiBuilder.buildFromActions(
 *     namedPlot.getAvailablePlotActions(),
 *     player,
 *     "Plot verwalten"
 * );
 * </pre>
 *
 * <p><b>GUI-Darstellung:</b></p>
 * <ul>
 *   <li>Material: {@link Material#NAME_TAG}</li>
 *   <li>Display-Name: "Namen ändern"</li>
 *   <li>Lore: Aktueller Plot-Name + Beschreibung</li>
 * </ul>
 *
 * <p><b>Aktuelle Implementierung (Phase 9):</b></p>
 * <p>Dies ist ein Proof-of-Concept. Die execute()-Methode sendet aktuell
 * nur eine Placeholder-Message. In zukünftigen Phasen wird ein Chat-Input-System
 * für die tatsächliche Namensänderung implementiert.</p>
 *
 * @author FallenStar Development
 * @version 1.0.0-SNAPSHOT
 * @see PlotAction
 * @see PlotWithName
 * @since Phase 9
 */
public class PlotActionSetName extends PlotAction {

    private final PlotWithName namedPlot;

    /**
     * Erstellt eine neue PlotActionSetName für den angegebenen Plot.
     *
     * @param plot Der Plot, dessen Name geändert werden soll (muss PlotWithName implementieren)
     * @throws IllegalArgumentException wenn plot null ist oder PlotWithName nicht implementiert
     */
    public PlotActionSetName(PlotWithName plot) {
        super(plot);
        if (plot == null) {
            throw new IllegalArgumentException("Plot darf nicht null sein");
        }
        this.namedPlot = plot;
    }

    /**
     * Führt die Namensänderungs-Action aus.
     *
     * <p><b>Aktuelle Implementierung (Placeholder):</b></p>
     * <p>Sendet eine Chat-Message an den Spieler, die anzeigt, dass die Funktion
     * in einer zukünftigen Phase implementiert wird.</p>
     *
     * <p><b>Zukünftige Implementierung:</b></p>
     * <ul>
     *   <li>Öffnet Chat-Input für neuen Namen</li>
     *   <li>Validiert den eingegebenen Namen</li>
     *   <li>Setzt den Namen via {@link PlotWithName#setName(String)}</li>
     *   <li>Zeigt Erfolgs-/Fehlermeldung</li>
     *   <li>Persistiert Änderung in Datenbank</li>
     * </ul>
     *
     * @param player Der Spieler, der die Action ausführt
     */
    @Override
    public void execute(Player player) {
        // Placeholder-Implementierung für Phase 9
        player.sendMessage("§e[PlotAction] §7Namen ändern für Plot: §f" + namedPlot.getName());
        player.sendMessage("§7Diese Funktion wird in einer zukünftigen Phase implementiert.");
        player.sendMessage("§7Du könntest hier einen neuen Namen eingeben.");
    }

    /**
     * Gibt das Display-Item für die GUI-Darstellung zurück.
     *
     * <p>Das Item wird im GUI angezeigt und repräsentiert diese Action visuell.</p>
     *
     * <p><b>Item-Details:</b></p>
     * <ul>
     *   <li>Material: {@link Material#NAME_TAG}</li>
     *   <li>Display-Name: "§6Namen ändern"</li>
     *   <li>Lore Zeile 1: "§7Aktueller Name: §f[Plot-Name]"</li>
     *   <li>Lore Zeile 2: "§7Klicke um den Namen zu ändern"</li>
     * </ul>
     *
     * @return ItemStack mit NAME_TAG und entsprechender Beschriftung (niemals null)
     */
    @Override
    public ItemStack getDisplayItem() {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6Namen ändern");
            meta.setLore(List.of(
                "§7Aktueller Name: §f" + namedPlot.getName(),
                "",
                "§7Klicke um den Namen zu ändern"
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Gibt an, ob diese Action Owner-Berechtigung benötigt.
     *
     * <p>Das Ändern des Plot-Namens ist eine Owner-exklusive Funktion,
     * daher gibt diese Methode immer {@code true} zurück.</p>
     *
     * @return {@code true} - nur Plot-Besitzer können den Namen ändern
     */
    @Override
    protected boolean requiresOwnership() {
        return true;
    }
}
