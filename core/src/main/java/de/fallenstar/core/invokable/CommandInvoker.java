package de.fallenstar.core.invokable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Metadaten-Klasse für Command-Invokation.
 *
 * <p>CommandInvoker speichert alle Informationen, die benötigt werden, um
 * eine {@link InvokableByCommand}-Action als Minecraft-Command zu registrieren.</p>
 *
 * <p><b>Beispiel:</b></p>
 * <pre>
 * CommandInvoker invoker = CommandInvoker.builder()
 *     .command("plot")
 *     .subcommand("setname")
 *     .aliases("name", "rename")
 *     .description("Ändert den Namen deines Plots")
 *     .usage("/plot setname &lt;neuer Name&gt;")
 *     .permission("fallenstar.plot.setname")
 *     .build();
 * </pre>
 *
 * <p><b>Verwendung mit InvokableByCommand:</b></p>
 * <pre>
 * class PlotActionSetName implements InvokableByCommand {
 *
 *     {@literal @}Override
 *     public CommandInvoker getCommandInvoker() {
 *         return CommandInvoker.builder()
 *             .command("plot")
 *             .subcommand("setname")
 *             .description("Ändert den Plot-Namen")
 *             .build();
 *     }
 *
 *     {@literal @}Override
 *     public void invokeByCommand(Player player, String[] args) {
 *         String newName = String.join(" ", args);
 *         plot.setName(newName);
 *     }
 * }
 * </pre>
 *
 * <p><b>Command-Hierarchie:</b></p>
 * <pre>
 * /plot              - command
 * /plot setname      - command + subcommand
 * /plot storage open - command + subcommand + subcommand
 * </pre>
 *
 * @author FallenStar Development
 * @version 1.0.0
 * @see InvokableByCommand
 */
public final class CommandInvoker {

    private final String command;
    private final String subcommand;
    private final List<String> aliases;
    private final String description;
    private final String usage;
    private final String permission;

    /**
     * Privater Konstruktor - nutze {@link Builder}.
     */
    private CommandInvoker(String command, String subcommand, List<String> aliases,
                           String description, String usage, String permission) {
        this.command = command;
        this.subcommand = subcommand;
        this.aliases = aliases != null ? new ArrayList<>(aliases) : new ArrayList<>();
        this.description = description;
        this.usage = usage;
        this.permission = permission;
    }

    /**
     * Gibt den Haupt-Command zurück.
     *
     * @return Der Hauptbefehl (z.B. "plot")
     */
    public String getCommand() {
        return command;
    }

    /**
     * Gibt den Subcommand zurück (optional).
     *
     * @return Der Subcommand oder null
     */
    public String getSubcommand() {
        return subcommand;
    }

    /**
     * Gibt die Aliases zurück.
     *
     * @return Unveränderbare Liste von Aliases (niemals null)
     */
    public List<String> getAliases() {
        return List.copyOf(aliases);
    }

    /**
     * Gibt die Beschreibung zurück.
     *
     * @return Die Beschreibung oder null
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gibt die Usage-Anleitung zurück.
     *
     * @return Die Usage-Anleitung oder null
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Gibt die benötigte Permission zurück.
     *
     * @return Die Permission oder null (keine Permission erforderlich)
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Gibt den vollständigen Command-String zurück.
     *
     * <p><b>Beispiele:</b></p>
     * <ul>
     *   <li>command="plot", subcommand=null → "plot"</li>
     *   <li>command="plot", subcommand="setname" → "plot setname"</li>
     * </ul>
     *
     * @return Der vollständige Command (niemals null)
     */
    public String getFullCommand() {
        if (subcommand == null || subcommand.isBlank()) {
            return command;
        }
        return command + " " + subcommand;
    }

    /**
     * Erstellt einen neuen Builder.
     *
     * @return Ein neuer Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder für CommandInvoker.
     */
    public static final class Builder {
        private String command;
        private String subcommand;
        private List<String> aliases = new ArrayList<>();
        private String description;
        private String usage;
        private String permission;

        private Builder() {}

        /**
         * Setzt den Haupt-Command.
         *
         * @param command Der Hauptbefehl (erforderlich)
         * @return Dieser Builder
         */
        public Builder command(String command) {
            this.command = command;
            return this;
        }

        /**
         * Setzt den Subcommand.
         *
         * @param subcommand Der Subcommand (optional)
         * @return Dieser Builder
         */
        public Builder subcommand(String subcommand) {
            this.subcommand = subcommand;
            return this;
        }

        /**
         * Fügt Aliases hinzu.
         *
         * @param aliases Die Aliases (optional)
         * @return Dieser Builder
         */
        public Builder aliases(String... aliases) {
            if (aliases != null) {
                this.aliases.addAll(Arrays.asList(aliases));
            }
            return this;
        }

        /**
         * Setzt die Beschreibung.
         *
         * @param description Die Beschreibung (optional)
         * @return Dieser Builder
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Setzt die Usage-Anleitung.
         *
         * @param usage Die Usage-Anleitung (optional)
         * @return Dieser Builder
         */
        public Builder usage(String usage) {
            this.usage = usage;
            return this;
        }

        /**
         * Setzt die benötigte Permission.
         *
         * @param permission Die Permission (optional)
         * @return Dieser Builder
         */
        public Builder permission(String permission) {
            this.permission = permission;
            return this;
        }

        /**
         * Erstellt den CommandInvoker.
         *
         * @return Der fertige CommandInvoker
         * @throws IllegalStateException wenn command null oder leer ist
         */
        public CommandInvoker build() {
            if (command == null || command.isBlank()) {
                throw new IllegalStateException("Command darf nicht null oder leer sein");
            }
            return new CommandInvoker(command, subcommand, aliases, description, usage, permission);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CommandInvoker)) return false;
        CommandInvoker other = (CommandInvoker) obj;
        return Objects.equals(command, other.command) &&
                Objects.equals(subcommand, other.subcommand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, subcommand);
    }

    @Override
    public String toString() {
        return "CommandInvoker{" +
                "command='" + command + '\'' +
                ", subcommand='" + subcommand + '\'' +
                ", aliases=" + aliases +
                ", description='" + description + '\'' +
                ", permission='" + permission + '\'' +
                '}';
    }
}
