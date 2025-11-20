package de.fallenstar.core.ui;

import java.util.List;

/**
 * Interface für Aktionen, die hierarchische Submenüs bereitstellen.
 *
 * <p>MenuAction ist ein Marker-Interface für {@link GuiRenderable} Objekte,
 * die nicht nur sich selbst darstellen, sondern auch ein Untermenü mit
 * weiteren Actions anbieten können.</p>
 *
 * <p><b>Hierarchisches Menü-Pattern:</b></p>
 * <pre>
 * Main Menu
 * ├── Action A (einfache Aktion)
 * ├── Action B (MenuAction)
 * │   ├── Sub-Action B1
 * │   ├── Sub-Action B2
 * │   └── Sub-Action B3
 * └── Action C (einfache Aktion)
 * </pre>
 *
 * <p><b>Verwendung mit AbstractPlotAction:</b></p>
 * <pre>
 * public class AbstractPlotActionManageStorage extends AbstractPlotAction implements MenuAction {
 *
 *     {@literal @}Override
 *     public void execute(Player player) {
 *         // Öffne Submenü statt direkte Aktion
 *         Inventory submenu = GuiBuilder.buildFromActions(
 *             getSubActions(),
 *             player,
 *             "Lager-Verwaltung"
 *         );
 *         player.openInventory(submenu);
 *     }
 *
 *     {@literal @}Override
 *     public List&lt;GuiRenderable&gt; getSubActions() {
 *         return List.of(
 *             new AbstractPlotActionOpenStorage(plot),
 *             new PlotActionSetStoragePrice(plot),
 *             new AbstractPlotActionUpgradeStorage(plot)
 *         );
 *     }
 *
 *     {@literal @}Override
 *     public ItemStack getDisplayItem() {
 *         ItemStack item = new ItemStack(Material.CHEST);
 *         ItemMeta meta = item.getItemMeta();
 *         meta.setDisplayName("§eLager verwalten");
 *         meta.setLore(List.of("§7Klicken für Optionen"));
 *         item.setItemMeta(meta);
 *         return item;
 *     }
 * }
 * </pre>
 *
 * <p><b>Wichtige Hinweise:</b></p>
 * <ul>
 *   <li>MenuAction ist ein <b>Interface</b>, keine Klasse!</li>
 *   <li>AbstractPlotActions können optional MenuAction implementieren</li>
 *   <li>Submenüs können selbst wieder MenuActions enthalten (beliebig tief)</li>
 *   <li>GuiBuilder kann rekursiv Menüs aus MenuActions erstellen</li>
 * </ul>
 *
 * <p><b>Beispiel: Trait-basierte Menüs:</b></p>
 * <pre>
 * interface PlotContainerStorage extends Plot {
 *     default List&lt;AbstractPlotAction&gt; getStorageActions() {
 *         return List.of(
 *             new AbstractPlotActionManageStorage(this)  // MenuAction mit Submenü
 *         );
 *     }
 * }
 * </pre>
 *
 * @author FallenStar Development
 * @version 1.0.0
 * @see GuiRenderable
 * @see de.fallenstar.core.plot.action.PlotAction
 */
public interface MenuAction {

    /**
     * Gibt die Liste von Sub-Actions zurück, die im Untermenü angezeigt werden sollen.
     *
     * <p>Die Sub-Actions werden vom GuiBuilder verwendet, um ein Untermenü zu erstellen,
     * wenn der Player auf diese MenuAction klickt.</p>
     *
     * <p><b>Implementierungs-Richtlinien:</b></p>
     * <ul>
     *   <li>Liste sollte niemals null sein (nutze {@code List.of()} für leere Liste)</li>
     *   <li>Sub-Actions können selbst wieder MenuActions sein (Rekursion)</li>
     *   <li>Sub-Actions sollten sinnvoll gruppiert sein (thematisch zusammengehörig)</li>
     *   <li>Filtere unsichtbare Actions NICHT hier (GuiBuilder macht das)</li>
     * </ul>
     *
     * <p><b>Beispiel:</b></p>
     * <pre>
     * {@literal @}Override
     * public List&lt;GuiRenderable&gt; getSubActions() {
     *     return List.of(
     *         new AbstractPlotActionOpenStorage(plot),
     *         new PlotActionSetStoragePrice(plot),
     *         new AbstractPlotActionUpgradeStorage(plot),
     *         new AbstractPlotActionClearStorage(plot)
     *     );
     * }
     * </pre>
     *
     * <p><b>Leeres Submenü:</b></p>
     * <pre>
     * {@literal @}Override
     * public List&lt;GuiRenderable&gt; getSubActions() {
     *     // Keine Sub-Actions verfügbar
     *     return List.of();
     * }
     * </pre>
     *
     * <p><b>Dynamisches Submenü (abhängig von Zustand):</b></p>
     * <pre>
     * {@literal @}Override
     * public List&lt;GuiRenderable&gt; getSubActions() {
     *     List&lt;GuiRenderable&gt; actions = new ArrayList&lt;&gt;();
     *     actions.add(new AbstractPlotActionOpenStorage(plot));
     *
     *     // Nur wenn Storage leer ist
     *     if (plot.isStorageEmpty()) {
     *         actions.add(new AbstractPlotActionFillStorage(plot));
     *     }
     *
     *     // Nur für Premium-Plots
     *     if (plot.isPremium()) {
     *         actions.add(new AbstractPlotActionUpgradeStorage(plot));
     *     }
     *
     *     return actions;
     * }
     * </pre>
     *
     * @return Liste von GuiRenderable Sub-Actions (niemals null, kann leer sein)
     */
    List<GuiRenderable> getSubActions();
}
