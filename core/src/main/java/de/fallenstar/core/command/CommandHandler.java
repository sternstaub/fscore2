package de.fallenstar.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;
import java.util.logging.Logger;

/**
 * Zentraler Command-Handler für das FallenStar-System.
 *
 * <p>Der CommandHandler verwaltet alle registrierten Commands und delegiert
 * Command-Ausführungen an die entsprechenden Implementierungen.</p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *   <li>Subcommand-Unterstützung (z.B. /plot set, /plot list)</li>
 *   <li>Automatische Permission-Checks</li>
 *   <li>Tab-Completion</li>
 *   <li>Usage-Messages bei fehlerhaften Commands</li>
 *   <li>Hierarchische Command-Struktur</li>
 * </ul>
 *
 * <p><b>Verwendung:</b></p>
 * <pre>
 * CommandHandler handler = new CommandHandler("plot");
 * handler.registerSubcommand("set", new PlotSetCommand());
 * handler.registerSubcommand("list", new PlotListCommand());
 * plugin.getCommand("plot").setExecutor(handler);
 * </pre>
 *
 * @author FallenStar Development
 * @version 1.0.0
 */
public class CommandHandler implements CommandExecutor, TabCompleter {

    private final String baseCommand;
    private final Map<String, Command> subcommands;
    private final Logger logger;
    private Command defaultCommand;

    /**
     * Erstellt einen neuen CommandHandler.
     *
     * @param baseCommand Der Basis-Command-Name (z.B. "plot")
     */
    public CommandHandler(String baseCommand) {
        this.baseCommand = baseCommand;
        this.subcommands = new HashMap<>();
        this.logger = Logger.getLogger("FallenStarCore");
        this.defaultCommand = null;
    }

    /**
     * Registriert einen Subcommand.
     *
     * <p>Wenn bereits ein Subcommand mit diesem Namen existiert,
     * wird es überschrieben und eine Warnung geloggt.</p>
     *
     * @param name Der Subcommand-Name (z.B. "set" für /plot set)
     * @param command Die Command-Implementierung
     */
    public void registerSubcommand(String name, Command command) {
        if (name == null || command == null) {
            throw new IllegalArgumentException("Subcommand name and implementation must not be null");
        }

        String lowerName = name.toLowerCase();
        if (subcommands.containsKey(lowerName)) {
            logger.warning(String.format(
                "Subcommand '%s %s' wird überschrieben",
                baseCommand, lowerName
            ));
        }

        subcommands.put(lowerName, command);
        logger.info(String.format(
            "Subcommand registriert: /%s %s",
            baseCommand, lowerName
        ));
    }

    /**
     * Deregistriert einen Subcommand.
     *
     * @param name Der Subcommand-Name
     * @return true wenn erfolgreich entfernt, false wenn nicht gefunden
     */
    public boolean unregisterSubcommand(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Subcommand name must not be null");
        }

        Command removed = subcommands.remove(name.toLowerCase());
        if (removed != null) {
            logger.info(String.format(
                "Subcommand deregistriert: /%s %s",
                baseCommand, name.toLowerCase()
            ));
            return true;
        }
        return false;
    }

    /**
     * Setzt den Default-Command, der ausgeführt wird wenn kein Subcommand angegeben wird.
     *
     * @param command Die Default-Command-Implementierung
     */
    public void setDefaultCommand(Command command) {
        this.defaultCommand = command;
        logger.info(String.format(
            "Default-Command gesetzt für: /%s",
            baseCommand
        ));
    }

    /**
     * Gibt einen Subcommand zurück.
     *
     * @param name Der Subcommand-Name
     * @return Optional mit Command, oder leer wenn nicht registriert
     */
    public Optional<Command> getSubcommand(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(subcommands.get(name.toLowerCase()));
    }

    /**
     * Gibt alle registrierten Subcommand-Namen zurück.
     *
     * @return Unmodifiable Set mit Subcommand-Namen
     */
    public Set<String> getSubcommandNames() {
        return Collections.unmodifiableSet(subcommands.keySet());
    }

    /**
     * Prüft ob ein Subcommand registriert ist.
     *
     * @param name Der Subcommand-Name
     * @return true wenn registriert, false sonst
     */
    public boolean hasSubcommand(String name) {
        return name != null && subcommands.containsKey(name.toLowerCase());
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command bukkitCommand,
                             String label, String[] args) {

        // Keine Argumente -> Default-Command oder Hilfe
        if (args.length == 0) {
            if (defaultCommand != null) {
                return executeCommand(sender, defaultCommand, new String[0]);
            } else {
                sendHelp(sender);
                return true;
            }
        }

        // Subcommand suchen
        String subcommandName = args[0].toLowerCase();
        Command command = subcommands.get(subcommandName);

        if (command == null) {
            sender.sendMessage(ChatColor.RED + "Unbekannter Subcommand: " + args[0]);
            sendHelp(sender);
            return true;
        }

        // Argumente ohne Subcommand-Namen
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        return executeCommand(sender, command, subArgs);
    }

    /**
     * Führt einen Command aus mit Permission-Check.
     *
     * @param sender Der Command-Sender
     * @param command Der auszuführende Command
     * @param args Die Argumente
     * @return true wenn erfolgreich
     */
    private boolean executeCommand(CommandSender sender, Command command, String[] args) {
        // Permission-Check
        if (!command.canExecute(sender)) {
            sender.sendMessage(ChatColor.RED + "Du hast keine Berechtigung für diesen Command.");
            return true;
        }

        // Command ausführen
        boolean success = command.execute(sender, args);

        // Bei Fehler: Usage anzeigen
        if (!success) {
            String usage = command.getUsage();
            if (!usage.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Usage: " + usage);
            }
        }

        return true;
    }

    /**
     * Sendet Hilfe-Text mit allen verfügbaren Subcommands.
     *
     * @param sender Der Command-Sender
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== " + baseCommand.toUpperCase() + " Commands ===");

        if (defaultCommand != null) {
            sender.sendMessage(ChatColor.YELLOW + "/" + baseCommand +
                ChatColor.GRAY + " - " + defaultCommand.getDescription());
        }

        subcommands.forEach((name, command) -> {
            // Nur Commands anzeigen für die der Sender Permission hat
            if (command.canExecute(sender)) {
                String description = command.getDescription();
                sender.sendMessage(ChatColor.YELLOW + "/" + baseCommand + " " + name +
                    ChatColor.GRAY + " - " + description);
            }
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command bukkitCommand,
                                      String alias, String[] args) {

        // Erstes Argument -> Subcommand-Namen vorschlagen
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            List<String> suggestions = new ArrayList<>();

            for (String subcommandName : subcommands.keySet()) {
                Command command = subcommands.get(subcommandName);
                // Nur Commands vorschlagen für die der Sender Permission hat
                if (command.canExecute(sender) && subcommandName.startsWith(partial)) {
                    suggestions.add(subcommandName);
                }
            }

            return suggestions;
        }

        // Weitere Argumente -> an Subcommand delegieren
        if (args.length > 1) {
            String subcommandName = args[0].toLowerCase();
            Command command = subcommands.get(subcommandName);

            if (command != null && command.canExecute(sender)) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                return command.tabComplete(sender, subArgs);
            }
        }

        return List.of();
    }

    /**
     * Gibt die Anzahl der registrierten Subcommands zurück.
     *
     * @return Anzahl der Subcommands
     */
    public int getSubcommandCount() {
        return subcommands.size();
    }

    /**
     * Entfernt alle registrierten Subcommands.
     */
    public void clearSubcommands() {
        int count = subcommands.size();
        subcommands.clear();
        logger.info(String.format(
            "Alle Subcommands für /%s entfernt (%d)",
            baseCommand, count
        ));
    }
}
