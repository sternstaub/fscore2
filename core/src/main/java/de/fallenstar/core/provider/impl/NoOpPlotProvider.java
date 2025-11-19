package de.fallenstar.core.provider.impl;

import de.fallenstar.core.provider.PlotProvider;
import org.bukkit.Location;

import java.util.Optional;
import java.util.UUID;

/**
 * NoOp-Implementierung des PlotProvider.
 *
 * <p>Diese Implementierung wird verwendet, wenn kein externes Plot-Plugin
 * (wie Towny) verfügbar ist. Sie gibt für alle Methoden "sichere"
 * Standardwerte zurück, die das System lauffähig halten.</p>
 *
 * <p><b>Graceful Degradation:</b> Das System läuft weiter, aber ohne
 * Plot-Funktionalität.</p>
 *
 * @see PlotProvider
 */
public class NoOpPlotProvider implements PlotProvider {

    @Override
    public Optional<Object> getPlotAt(Location location) {
        return Optional.empty();
    }

    @Override
    public Optional<Object> getPlotById(UUID plotId) {
        return Optional.empty();
    }

    @Override
    public boolean isOwner(UUID plotId, UUID playerId) {
        return false;
    }

    @Override
    public boolean isMember(UUID plotId, UUID playerId) {
        return false;
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
