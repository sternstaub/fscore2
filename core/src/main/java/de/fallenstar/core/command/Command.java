package de.fallenstar.core.command;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Interface für alle Commands im FallenStar-System.
 *
 * <p>Commands können hierarchisch strukturiert werden (Subcommands) und
 * unterstützen Tab-Completion sowie Permission-Checks.</p>
 *
 * <p><b>Beispiel-Implementierung:</b></p>
 * <pre>
 * public class PlotSetNameCommand implements Command {
 *     public boolean execute(CommandSender sender, String[] args) {
 *         // Command-Logik
 *         return true;
 *     }
 *
 *     public List&lt;String&gt; tabComplete(CommandSender sender, String[] args) {
 *         return List.of();
 *     }
 * }
 * </pre>
 *
 * @author FallenStar Development
 * @version 1.0.0
 */
public interface Command {

    /**
     * Führt den Command aus.
     *
     * @param sender Der Command-Sender (Player, Console, etc.)
     * @param args Command-Argumente (ohne Command-Name)
     * @return true wenn erfolgreich, false bei Fehler (zeigt Usage)
     */
    boolean execute(CommandSender sender, String[] args);

    /**
     * Gibt Tab-Completion-Vorschläge zurück.
     *
     * @param sender Der Command-Sender
     * @param args Bisherige Argumente
     * @return Liste von Vorschlägen (leer wenn keine)
     */
    default List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    /**
     * Gibt die Permission zurück, die für diesen Command benötigt wird.
     *
     * <p>Wenn null oder leer zurückgegeben wird, ist keine Permission erforderlich.</p>
     *
     * @return Permission-String oder null
     */
    default String getPermission() {
        return null;
    }

    /**
     * Gibt die Command-Usage-Nachricht zurück.
     *
     * <p>Wird angezeigt, wenn execute() false zurückgibt.</p>
     *
     * @return Usage-String (z.B. "/plot set <name>")
     */
    default String getUsage() {
        return "";
    }

    /**
     * Gibt die Command-Beschreibung zurück.
     *
     * @return Beschreibung für Hilfe-Texte
     */
    default String getDescription() {
        return "Keine Beschreibung verfügbar";
    }

    /**
     * Prüft ob der Sender diesen Command ausführen darf.
     *
     * <p>Standard-Implementierung prüft nur die Permission.</p>
     *
     * @param sender Der Command-Sender
     * @return true wenn erlaubt, false sonst
     */
    default boolean canExecute(CommandSender sender) {
        String permission = getPermission();
        if (permission == null || permission.isEmpty()) {
            return true;
        }
        return sender.hasPermission(permission);
    }
}
