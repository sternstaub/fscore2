package de.fallenstar.core.provider.impl;

import de.fallenstar.core.provider.NPCProvider;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.Optional;
import java.util.UUID;

/**
 * NoOp-Implementierung des NPCProvider.
 *
 * <p>Diese Implementierung wird verwendet, wenn kein externes NPC-Plugin
 * (wie Citizens) verfügbar ist. Sie kann keine NPCs spawnen und gibt für
 * alle Abfragen leere Optionals zurück.</p>
 *
 * <p><b>Graceful Degradation:</b> Das System läuft weiter, aber ohne
 * NPC-Funktionalität.</p>
 *
 * @see NPCProvider
 */
public class NoOpNPCProvider implements NPCProvider {

    @Override
    public Optional<UUID> spawnNPC(Location location, String name, EntityType entityType) {
        return Optional.empty();
    }

    @Override
    public boolean removeNPC(UUID npcId) {
        return false;
    }

    @Override
    public boolean teleportNPC(UUID npcId, Location location) {
        return false;
    }

    @Override
    public boolean setNPCName(UUID npcId, String name) {
        return false;
    }

    @Override
    public Optional<String> getNPCName(UUID npcId) {
        return Optional.empty();
    }

    @Override
    public boolean npcExists(UUID npcId) {
        return false;
    }

    @Override
    public Optional<Location> getNPCLocation(UUID npcId) {
        return Optional.empty();
    }

    @Override
    public String getProviderName() {
        return "NoOp";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
