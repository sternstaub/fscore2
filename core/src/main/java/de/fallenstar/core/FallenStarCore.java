package de.fallenstar.core;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hauptklasse des FallenStar Core Frameworks.
 *
 * <p>Diese Klasse initialisiert das komplette Framework mit allen
 * Systemen (Plot, Economy, UI, NPC, Events) und registriert die
 * Provider-Registry für optionale Modul-Integrationen.</p>
 *
 * <p><b>Architektur:</b></p>
 * <ul>
 *   <li>Core = Framework (alle APIs, Interfaces, Events)</li>
 *   <li>Module = Integrationen (Towny, Vault, Citizens, MMOItems)</li>
 * </ul>
 *
 * @author FallenStar Development
 * @version 1.0.0-SNAPSHOT
 */
public class FallenStarCore extends JavaPlugin {

    private static FallenStarCore instance;

    /**
     * Gibt die Plugin-Instanz zurück.
     *
     * @return Die FallenStarCore Plugin-Instanz
     */
    public static FallenStarCore getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        getLogger().info("FallenStar Core Framework wird geladen...");
    }

    @Override
    public void onEnable() {
        getLogger().info("=================================");
        getLogger().info("  FallenStar Core Framework");
        getLogger().info("  Version: " + getDescription().getVersion());
        getLogger().info("=================================");

        // TODO: Provider-Registry initialisieren
        // TODO: Plot-System initialisieren
        // TODO: Economy-System initialisieren
        // TODO: UI-Framework initialisieren
        // TODO: NPC-System initialisieren
        // TODO: Event-Listener registrieren
        // TODO: Commands registrieren

        getLogger().info("FallenStar Core erfolgreich aktiviert!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FallenStar Core wird deaktiviert...");

        // TODO: Alle Systeme sauber herunterfahren
        // TODO: Daten speichern
        // TODO: Provider deregistrieren

        instance = null;
        getLogger().info("FallenStar Core erfolgreich deaktiviert!");
    }
}
