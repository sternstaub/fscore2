package de.fallenstar.core.invokable;

import org.bukkit.entity.Player;

/**
 * Interface für Objekte, die durch Minecraft-Commands invoked werden können.
 *
 * <p><b>Naming Convention:</b> Pattern {@code InvokableBy[Mechanism]} - Invokable durch Command</p>
 *
 * <p>InvokableByCommand definiert, wie ein Objekt als Minecraft-Command
 * registriert und ausgeführt wird. Es kombiniert:</p>
 * <ul>
 *   <li><b>Metadaten:</b> Via {@link #getCommandInvoker()} - Command-Name, Aliases, Description, etc.</li>
 *   <li><b>Invokation:</b> Via {@link #invokeByCommand(Player, String[])} - Ausführungs-Logik</li>
 * </ul>
 *
 * <p><b>Beispiel-Implementierung:</b></p>
 * <pre>
 * public class PlotActionSetName extends AbstractPlotAction
 *     implements InvokableByCommand {
 *
 *     {@literal @}Override
 *     public CommandInvoker getCommandInvoker() {
 *         return CommandInvoker.builder()
 *             .command("plot")
 *             .subcommand("setname")
 *             .aliases("name", "rename")
 *             .description("Ändert den Namen deines Plots")
 *             .usage("/plot setname &lt;neuer Name&gt;")
 *             .permission("fallenstar.plot.setname")
 *             .build();
 *     }
 *
 *     {@literal @}Override
 *     public void invokeByCommand(Player player, String[] args) {
 *         if (args.length == 0) {
 *             player.sendMessage("§cBitte gib einen Namen an!");
 *             return;
 *         }
 *
 *         String newName = String.join(" ", args);
 *         plot.setName(newName);
 *         player.sendMessage("§aPlot umbenannt zu: §f" + newName);
 *     }
 * }
 * </pre>
 *
 * <p><b>Command-Registrierung:</b></p>
 * <pre>
 * // CommandManager sammelt alle InvokableByCommand-Implementierungen
 * void registerCommands(List&lt;InvokableByCommand&gt; invokables) {
 *     for (InvokableByCommand invokable : invokables) {
 *         CommandInvoker invoker = invokable.getCommandInvoker();
 *
 *         // Registriere Bukkit-Command
 *         PluginCommand cmd = plugin.getCommand(invoker.getCommand());
 *         cmd.setExecutor((sender, command, label, args) -> {
 *             if (sender instanceof Player player) {
 *                 invokable.invokeByCommand(player, args);
 *                 return true;
 *             }
 *             return false;
 *         });
 *
 *         // Setze Metadaten
 *         cmd.setDescription(invoker.getDescription());
 *         cmd.setUsage(invoker.getUsage());
 *         cmd.setAliases(invoker.getAliases());
 *         cmd.setPermission(invoker.getPermission());
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
 *     public CommandInvoker getCommandInvoker() {
 *         return CommandInvoker.builder()
 *             .command("plot")
 *             .subcommand("tp")
 *             .aliases("teleport")
 *             .description("Teleportiert zu deinem Plot")
 *             .build();
 *     }
 *
 *     {@literal @}Override
 *     public void invokeByCommand(Player player, String[] args) {
 *         // /plot tp
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
 * <p><b>Subcommands:</b></p>
 * <pre>
 * // /plot storage open
 * class PlotActionOpenStorage implements InvokableByCommand {
 *     {@literal @}Override
 *     public CommandInvoker getCommandInvoker() {
 *         return CommandInvoker.builder()
 *             .command("plot")
 *             .subcommand("storage open")  // Verschachtelte Subcommands
 *             .build();
 *     }
 * }
 * </pre>
 *
 * <p><b>Argument-Handling:</b></p>
 * <pre>
 * {@literal @}Override
 * public void invokeByCommand(Player player, String[] args) {
 *     // Validiere Argumente
 *     if (args.length == 0) {
 *         player.sendMessage("§cUsage: " + getCommandInvoker().getUsage());
 *         return;
 *     }
 *
 *     // Parse Argumente
 *     String plotIdStr = args[0];
 *     UUID plotId = UUID.fromString(plotIdStr);
 *
 *     // Führe Aktion aus
 *     Plot plot = plotManager.getPlot(plotId);
 *     if (plot != null) {
 *         performAction(plot);
 *     }
 * }
 * </pre>
 *
 * <p><b>Permission-Checks:</b></p>
 * <pre>
 * {@literal @}Override
 * public void invokeByCommand(Player player, String[] args) {
 *     CommandInvoker invoker = getCommandInvoker();
 *
 *     // Permission-Check
 *     if (invoker.getPermission() != null &&
 *         !player.hasPermission(invoker.getPermission())) {
 *         player.sendMessage("§cKeine Berechtigung!");
 *         return;
 *     }
 *
 *     // Führe Aktion aus
 *     performAction(player, args);
 * }
 * </pre>
 *
 * <p><b>Vorteile:</b></p>
 * <ul>
 *   <li>✅ Metadaten und Logik in einer Klasse</li>
 *   <li>✅ Type-safe Command-Handling</li>
 *   <li>✅ Automatische Command-Registrierung möglich</li>
 *   <li>✅ Explizite Methode {@code invokeByCommand()}</li>
 * </ul>
 *
 * @author FallenStar Development
 * @version 1.0.0
 * @see Invokable
 * @see CommandInvoker
 */
public interface InvokableByCommand extends Invokable {

    /**
     * Gibt die Command-Metadaten zurück.
     *
     * <p>Diese Metadaten werden vom CommandManager verwendet, um den
     * Command bei Bukkit zu registrieren.</p>
     *
     * <p><b>Beispiel:</b></p>
     * <pre>
     * {@literal @}Override
     * public CommandInvoker getCommandInvoker() {
     *     return CommandInvoker.builder()
     *         .command("plot")
     *         .subcommand("setname")
     *         .aliases("name", "rename")
     *         .description("Ändert den Plot-Namen")
     *         .usage("/plot setname &lt;neuer Name&gt;")
     *         .permission("fallenstar.plot.setname")
     *         .build();
     * }
     * </pre>
     *
     * @return Die Command-Metadaten (niemals null)
     */
    CommandInvoker getCommandInvoker();

    /**
     * Wird aufgerufen, wenn der Command vom Spieler ausgeführt wird.
     *
     * <p>Diese Methode implementiert die Command-Logik. Sie wird vom
     * CommandManager aufgerufen, nachdem der Command gematcht wurde.</p>
     *
     * <p><b>Parameter:</b></p>
     * <ul>
     *   <li>{@code player} - Der Spieler, der den Command ausführt</li>
     *   <li>{@code args} - Die Command-Argumente (ohne Command-Name)</li>
     * </ul>
     *
     * <p><b>Beispiel - Einfacher Command:</b></p>
     * <pre>
     * {@literal @}Override
     * public void invokeByCommand(Player player, String[] args) {
     *     player.teleport(plot.getLocation());
     *     player.sendMessage("§aTeleportiert!");
     * }
     * </pre>
     *
     * <p><b>Beispiel - Mit Argumenten:</b></p>
     * <pre>
     * {@literal @}Override
     * public void invokeByCommand(Player player, String[] args) {
     *     if (args.length == 0) {
     *         player.sendMessage("§cBitte gib einen Namen an!");
     *         player.sendMessage("§7Usage: " + getCommandInvoker().getUsage());
     *         return;
     *     }
     *
     *     String newName = String.join(" ", args);
     *     plot.setName(newName);
     *     player.sendMessage("§aPlot umbenannt zu: §f" + newName);
     * }
     * </pre>
     *
     * <p><b>Beispiel - Mit Validation:</b></p>
     * <pre>
     * {@literal @}Override
     * public void invokeByCommand(Player player, String[] args) {
     *     // Permission-Check
     *     if (!canExecute(player)) {
     *         player.sendMessage("§cKeine Berechtigung!");
     *         return;
     *     }
     *
     *     // Argument-Validation
     *     if (args.length == 0) {
     *         player.sendMessage("§cBitte gib einen Preis an!");
     *         return;
     *     }
     *
     *     try {
     *         double price = Double.parseDouble(args[0]);
     *         plot.setPrice(price);
     *         player.sendMessage("§aPreis gesetzt: §6" + price);
     *     } catch (NumberFormatException e) {
     *         player.sendMessage("§cUngültiger Preis!");
     *     }
     * }
     * </pre>
     *
     * <p><b>Best Practices:</b></p>
     * <ul>
     *   <li>Validiere Argumente IMMER</li>
     *   <li>Sende hilfreiche Fehlermeldungen</li>
     *   <li>Nutze {@code getCommandInvoker().getUsage()} für Usage-Info</li>
     *   <li>Prüfe Permissions wenn nötig</li>
     *   <li>Gib immer Feedback (Success oder Error)</li>
     * </ul>
     *
     * @param player Der Spieler, der den Command ausführt (niemals null)
     * @param args   Die Command-Argumente (niemals null, kann leer sein)
     */
    void invokeByCommand(Player player, String[] args);
}
