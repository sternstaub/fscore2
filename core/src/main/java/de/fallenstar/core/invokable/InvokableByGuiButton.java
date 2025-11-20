package de.fallenstar.core.invokable;

import de.fallenstar.core.ui.GuiRenderable;
import org.bukkit.entity.Player;

/**
 * Interface für Objekte, die durch GUI-Button-Clicks invoked werden können.
 *
 * <p><b>Naming Convention:</b> Pattern {@code InvokableBy[Mechanism]} - Invokable durch GUI-Button</p>
 *
 * <p>InvokableByGuiButton kombiniert zwei Aspekte:</p>
 * <ul>
 *   <li><b>Rendering:</b> Via {@link GuiRenderable} - Wie wird es im GUI dargestellt?</li>
 *   <li><b>Invokation:</b> Via {@link #invokeByGuiButton(Player)} - Was passiert bei Click?</li>
 * </ul>
 *
 * <p><b>Integration mit GuiBuilder:</b></p>
 * <p>Der {@link de.fallenstar.core.ui.GuiBuilder} nutzt dieses Interface, um
 * automatisch GUI-Inventories aus Action-Listen zu generieren. Jedes InvokableByGuiButton
 * wird als klickbares Item im GUI dargestellt.</p>
 *
 * <p><b>Beispiel-Implementierung:</b></p>
 * <pre>
 * public class PlotActionSetName extends AbstractPlotAction
 *     implements InvokableByGuiButton {
 *
 *     {@literal @}Override
 *     public void invokeByGuiButton(Player player) {
 *         // GUI-Click-Handler
 *         player.closeInventory();
 *         player.sendMessage("§aGib einen neuen Plot-Namen ein:");
 *         // Registriere Chat-Input-Listener
 *         chatInputManager.registerInput(player, this::handleNameInput);
 *     }
 *
 *     {@literal @}Override
 *     public ItemStack getDisplayItem() {
 *         return new ItemStack(Material.NAME_TAG)
 *             .setDisplayName("§6Plot umbenennen")
 *             .setLore("§7Klicke zum Umbenennen");
 *     }
 *
 *     {@literal @}Override
 *     public boolean isVisible(Player player) {
 *         return canExecute(player);  // Nur für berechtigte Spieler
 *     }
 * }
 * </pre>
 *
 * <p><b>Verwendung mit GuiBuilder:</b></p>
 * <pre>
 * // Automatisches GUI aus InvokableByGuiButton-Liste
 * List&lt;InvokableByGuiButton&gt; actions = plot.getAvailablePlotActions()
 *     .stream()
 *     .filter(a -> a instanceof InvokableByGuiButton)
 *     .map(a -> (InvokableByGuiButton) a)
 *     .toList();
 *
 * Inventory gui = GuiBuilder.buildFromActions(actions, player, "Plot verwalten");
 * player.openInventory(gui);
 * </pre>
 *
 * <p><b>Event-Handling:</b></p>
 * <pre>
 * {@literal @}EventHandler
 * public void onInventoryClick(InventoryClickEvent event) {
 *     Player player = (Player) event.getWhoClicked();
 *     ItemStack clicked = event.getCurrentItem();
 *
 *     // Finde passende InvokableByGuiButton-Action
 *     InvokableByGuiButton action = findActionForItem(clicked);
 *     if (action != null) {
 *         action.invokeByGuiButton(player);
 *         event.setCancelled(true);
 *     }
 * }
 * </pre>
 *
 * <p><b>Multi-Invokation:</b></p>
 * <p>Eine Klasse kann mehrere Invokable-Interfaces implementieren:</p>
 * <pre>
 * class PlotActionTeleport extends AbstractPlotAction
 *     implements InvokableByCommand, InvokableByGuiButton {
 *
 *     {@literal @}Override
 *     public void invokeByCommand(Player player, String[] args) {
 *         // /plot tp &lt;id&gt;
 *         performTeleport(player);
 *     }
 *
 *     {@literal @}Override
 *     public void invokeByGuiButton(Player player) {
 *         // GUI-Click
 *         performTeleport(player);
 *         player.closeInventory();
 *     }
 *
 *     private void performTeleport(Player player) {
 *         player.teleport(plot.getLocation());
 *         player.sendMessage("§aTeleportiert!");
 *     }
 * }
 * </pre>
 *
 * <p><b>Vorteile:</b></p>
 * <ul>
 *   <li>✅ Self-Rendering (keine separaten UI-Klassen)</li>
 *   <li>✅ Type-safe Invokation</li>
 *   <li>✅ Automatische GUI-Generierung via GuiBuilder</li>
 *   <li>✅ Explizite Methode {@code invokeByGuiButton()}</li>
 * </ul>
 *
 * @author FallenStar Development
 * @version 1.0.0
 * @see Invokable
 * @see GuiRenderable
 * @see de.fallenstar.core.ui.GuiBuilder
 */
public interface InvokableByGuiButton extends Invokable, GuiRenderable {

    /**
     * Wird aufgerufen, wenn der Spieler auf das GUI-Item klickt.
     *
     * <p>Diese Methode implementiert die Logik, die ausgeführt wird, wenn
     * ein Spieler im GUI auf das Item dieser Action klickt.</p>
     *
     * <p><b>Typische Operationen:</b></p>
     * <ul>
     *   <li>Inventory schließen (falls nötig)</li>
     *   <li>Aktion ausführen</li>
     *   <li>Feedback an Player senden</li>
     *   <li>Neues GUI öffnen (für Submenüs)</li>
     *   <li>Chat-Input-Dialog starten</li>
     * </ul>
     *
     * <p><b>Beispiel - Direkte Aktion:</b></p>
     * <pre>
     * {@literal @}Override
     * public void invokeByGuiButton(Player player) {
     *     player.closeInventory();
     *     player.teleport(plot.getLocation());
     *     player.sendMessage("§aTeleportiert zu deinem Plot!");
     * }
     * </pre>
     *
     * <p><b>Beispiel - Submenü öffnen:</b></p>
     * <pre>
     * {@literal @}Override
     * public void invokeByGuiButton(Player player) {
     *     // MenuAction: Öffne Untermenü
     *     if (this instanceof MenuAction menuAction) {
     *         Inventory submenu = GuiBuilder.buildFromActions(
     *             menuAction.getSubActions(),
     *             player,
     *             "Lager verwalten"
     *         );
     *         player.openInventory(submenu);
     *     }
     * }
     * </pre>
     *
     * <p><b>Beispiel - Chat-Input starten:</b></p>
     * <pre>
     * {@literal @}Override
     * public void invokeByGuiButton(Player player) {
     *     player.closeInventory();
     *     player.sendMessage("§aGib einen neuen Namen ein:");
     *     chatInputManager.registerInput(player, input -> {
     *         plot.setName(input);
     *         player.sendMessage("§aName geändert zu: §f" + input);
     *     });
     * }
     * </pre>
     *
     * <p><b>Best Practices:</b></p>
     * <ul>
     *   <li>Validiere Berechtigungen VOR Aufruf (via {@link GuiRenderable#isVisible(Player)})</li>
     *   <li>Schließe Inventory nur wenn nötig (nicht bei Submenüs)</li>
     *   <li>Sende immer Feedback an den Player</li>
     *   <li>Nutze Permissions für sensitive Operationen</li>
     * </ul>
     *
     * <p><b>Error-Handling:</b></p>
     * <pre>
     * {@literal @}Override
     * public void invokeByGuiButton(Player player) {
     *     try {
     *         performAction();
     *         player.sendMessage("§aErfolgreich!");
     *     } catch (IllegalStateException e) {
     *         player.sendMessage("§cFehler: " + e.getMessage());
     *     }
     * }
     * </pre>
     *
     * @param player Der Spieler, der auf das GUI-Item geklickt hat (niemals null)
     */
    void invokeByGuiButton(Player player);
}
