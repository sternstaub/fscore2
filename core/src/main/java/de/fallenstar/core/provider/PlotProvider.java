package de.fallenstar.core.provider;

import org.bukkit.Location;

import java.util.Optional;
import java.util.UUID;

/**
 * Provider-Interface für Plot-Verwaltung.
 *
 * <p>Dieses Interface abstrahiert den Zugriff auf Plot-Systeme wie Towny,
 * GriefPrevention oder das interne Plot-System. Module können konkrete
 * Implementierungen bereitstellen.</p>
 *
 * <p><b>Graceful Degradation:</b> Wenn kein Modul eine Implementierung
 * registriert, wird die NoOp-Implementierung verwendet, die leere
 * Optionals zurückgibt.</p>
 *
 * @see de.fallenstar.core.provider.impl.NoOpPlotProvider
 */
public interface PlotProvider {

    /**
     * Gibt das Plot an der angegebenen Location zurück.
     *
     * @param location Die zu prüfende Location
     * @return Optional mit Plot, wenn an dieser Location ein Plot existiert
     */
    Optional<Object> getPlotAt(Location location);

    /**
     * Gibt das Plot mit der angegebenen ID zurück.
     *
     * @param plotId Die eindeutige Plot-ID
     * @return Optional mit Plot, wenn ein Plot mit dieser ID existiert
     */
    Optional<Object> getPlotById(UUID plotId);

    /**
     * Prüft, ob ein Spieler Eigentümer eines Plots ist.
     *
     * @param plotId Die Plot-ID
     * @param playerId Die Spieler-UUID
     * @return true wenn der Spieler Eigentümer ist, false sonst
     */
    boolean isOwner(UUID plotId, UUID playerId);

    /**
     * Prüft, ob ein Spieler Mitglied eines Plots ist.
     *
     * @param plotId Die Plot-ID
     * @param playerId Die Spieler-UUID
     * @return true wenn der Spieler Mitglied ist, false sonst
     */
    boolean isMember(UUID plotId, UUID playerId);

    /**
     * Gibt den Namen des Provider-Typs zurück.
     *
     * <p>Beispiele: "Towny", "GriefPrevention", "Internal", "NoOp"</p>
     *
     * @return Der Provider-Name
     */
    String getProviderName();

    /**
     * Prüft, ob dieser Provider aktiv ist.
     *
     * @return true wenn der Provider funktionsfähig ist, false sonst
     */
    boolean isEnabled();
}
