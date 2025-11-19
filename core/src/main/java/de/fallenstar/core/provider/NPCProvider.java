package de.fallenstar.core.provider;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.Optional;
import java.util.UUID;

/**
 * Provider-Interface für NPC-Verwaltung.
 *
 * <p>Dieses Interface abstrahiert den Zugriff auf NPC-Plugins wie Citizens
 * oder das interne NPC-System.</p>
 *
 * <p><b>Graceful Degradation:</b> Wenn kein NPC-Plugin verfügbar ist,
 * wird die NoOp-Implementierung verwendet, die keine NPCs spawnen kann.</p>
 *
 * @see de.fallenstar.core.provider.impl.NoOpNPCProvider
 */
public interface NPCProvider {

    /**
     * Spawnt einen NPC an der angegebenen Location.
     *
     * @param location Die Spawn-Location
     * @param name Der Name des NPCs
     * @param entityType Der Entity-Typ (z.B. VILLAGER, PLAYER)
     * @return Optional mit der NPC-ID, wenn erfolgreich gespawnt
     */
    Optional<UUID> spawnNPC(Location location, String name, EntityType entityType);

    /**
     * Entfernt einen NPC.
     *
     * @param npcId Die NPC-ID
     * @return true wenn erfolgreich entfernt, false sonst
     */
    boolean removeNPC(UUID npcId);

    /**
     * Teleportiert einen NPC zu einer Location.
     *
     * @param npcId Die NPC-ID
     * @param location Die Ziel-Location
     * @return true wenn erfolgreich teleportiert, false sonst
     */
    boolean teleportNPC(UUID npcId, Location location);

    /**
     * Setzt den Namen eines NPCs.
     *
     * @param npcId Die NPC-ID
     * @param name Der neue Name
     * @return true wenn erfolgreich gesetzt, false sonst
     */
    boolean setNPCName(UUID npcId, String name);

    /**
     * Gibt den Namen eines NPCs zurück.
     *
     * @param npcId Die NPC-ID
     * @return Optional mit dem NPC-Namen, wenn NPC existiert
     */
    Optional<String> getNPCName(UUID npcId);

    /**
     * Prüft, ob ein NPC mit der angegebenen ID existiert.
     *
     * @param npcId Die NPC-ID
     * @return true wenn der NPC existiert, false sonst
     */
    boolean npcExists(UUID npcId);

    /**
     * Gibt die Location eines NPCs zurück.
     *
     * @param npcId Die NPC-ID
     * @return Optional mit der NPC-Location, wenn NPC existiert
     */
    Optional<Location> getNPCLocation(UUID npcId);

    /**
     * Gibt den Namen des Provider-Typs zurück.
     *
     * @return Der Provider-Name (z.B. "Citizens", "Internal", "NoOp")
     */
    String getProviderName();

    /**
     * Prüft, ob dieser Provider aktiv ist.
     *
     * @return true wenn der Provider funktionsfähig ist, false sonst
     */
    boolean isEnabled();
}
