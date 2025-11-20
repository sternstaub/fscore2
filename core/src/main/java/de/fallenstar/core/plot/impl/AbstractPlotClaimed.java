package de.fallenstar.core.plot.impl;

import org.bukkit.Location;

import java.util.UUID;

/**
 * Abstrakte Basisklasse für Plots mit transferierbarem Ownership.
 *
 * <p><b>Naming Convention:</b> Prefix-Pattern für abstrakte Klassen: {@code Abstract[Name]}</p>
 *
 * <p>AbstractPlotClaimed erweitert {@link AbstractPlotBase} um die Fähigkeit,
 * den Plot-Besitzer zu ändern. Dies ist relevant für Plots, die verkauft,
 * übertragen oder zurückgesetzt werden können.</p>
 *
 * <p><b>Unterschied zu AbstractPlotBase:</b></p>
 * <ul>
 *   <li><b>AbstractPlotBase:</b> Immutable Owner-ID (final)</li>
 *   <li><b>AbstractPlotClaimed:</b> Mutable Owner-ID (via Setter)</li>
 * </ul>
 *
 * <p><b>Verwendung:</b></p>
 * <pre>
 * class TradeguildPlot extends AbstractPlotClaimed
 *     implements PlotWithName, PlotWithStorageContainer {
 *
 *     private String name = "Handelshaus";
 *
 *     public TradeguildPlot(UUID id, UUID ownerId, Location location) {
 *         super(id, ownerId, location);
 *     }
 *
 *     {@literal @}Override
 *     public List&lt;AbstractPlotAction&gt; getAvailablePlotActions() {
 *         return Stream.of(
 *             getNameActions(),
 *             getStorageActions()
 *         ).flatMap(List::stream).toList();
 *     }
 * }
 * </pre>
 *
 * <p><b>Owner-Transfer-Beispiel:</b></p>
 * <pre>
 * // Plot verkaufen
 * AbstractPlotClaimed plot = getTradeguildPlot();
 * UUID newOwnerId = buyer.getUniqueId();
 *
 * // Validierung
 * if (economyProvider.withdraw(buyer, plot.getPrice())) {
 *     plot.setOwnerId(newOwnerId);
 *     economyProvider.deposit(plot.getOwnerId(), plot.getPrice());
 * }
 * </pre>
 *
 * <p><b>Vererbungshierarchie:</b></p>
 * <pre>
 * Plot (Interface)
 *   ↑
 * AbstractPlotBase (abstrakt, immutable owner)
 *   ↑
 * AbstractPlotClaimed (abstrakt, mutable owner)
 *   ↑
 * TradeguildPlot (konkret)
 * </pre>
 *
 * @author FallenStar Development
 * @version 1.0.0
 * @see AbstractPlotBase
 * @see de.fallenstar.core.plot.Plot
 */
public abstract class AbstractPlotClaimed extends AbstractPlotBase {

    /**
     * Die UUID des aktuellen Plot-Besitzers.
     *
     * <p>Diese Referenz überschreibt das finale {@code ownerId}-Feld aus
     * {@link AbstractPlotBase}. In AbstractPlotClaimed ist die Owner-ID
     * veränderbar via {@link #setOwnerId(UUID)}.</p>
     *
     * <p><b>Hinweis:</b> Bei Owner-Änderungen sollten auch zugehörige
     * Daten (Permissions, Economy, etc.) aktualisiert werden.</p>
     */
    private UUID currentOwnerId;

    /**
     * Erstellt einen neuen AbstractPlotClaimed mit den angegebenen Eigenschaften.
     *
     * <p><b>Validierung:</b></p>
     * <ul>
     *   <li>id darf nicht null sein</li>
     *   <li>ownerId darf nicht null sein</li>
     *   <li>location darf nicht null sein</li>
     * </ul>
     *
     * <p><b>Hinweis:</b> Dieser Konstruktor ist protected, da AbstractPlotClaimed
     * nicht direkt instanziiert werden soll. Nutze eine konkrete Subklasse.</p>
     *
     * @param id       Die eindeutige Plot-ID (nicht null)
     * @param ownerId  Die UUID des initialen Besitzers (nicht null)
     * @param location Die Welt-Position des Plots (nicht null)
     * @throws IllegalArgumentException wenn einer der Parameter null ist
     */
    protected AbstractPlotClaimed(UUID id, UUID ownerId, Location location) {
        super(id, ownerId, location);
        this.currentOwnerId = ownerId;
    }

    /**
     * Gibt die UUID des aktuellen Plot-Besitzers zurück.
     *
     * <p>Diese Methode überschreibt {@link AbstractPlotBase#getOwnerId()}
     * und gibt die veränderbare {@code currentOwnerId} zurück statt der
     * finalen {@code ownerId} aus der Superklasse.</p>
     *
     * @return Die UUID des aktuellen Besitzers (niemals null)
     */
    @Override
    public UUID getOwnerId() {
        return currentOwnerId;
    }

    /**
     * Setzt einen neuen Besitzer für diesen Plot.
     *
     * <p><b>Verwendung:</b></p>
     * <p>Diese Methode sollte verwendet werden, wenn Plots verkauft, übertragen
     * oder zurückgesetzt werden. Sie aktualisiert nur die Owner-ID; zusätzliche
     * Logik (Permissions, Economy, Events) muss separat implementiert werden.</p>
     *
     * <p><b>Beispiel mit Event-Handling:</b></p>
     * <pre>
     * // Plot-Transfer mit Event
     * void transferPlot(AbstractPlotClaimed plot, UUID newOwnerId) {
     *     UUID oldOwnerId = plot.getOwnerId();
     *
     *     // Event auslösen
     *     PlotOwnerChangeEvent event = new PlotOwnerChangeEvent(plot, oldOwnerId, newOwnerId);
     *     Bukkit.getPluginManager().callEvent(event);
     *
     *     if (!event.isCancelled()) {
     *         plot.setOwnerId(newOwnerId);
     *
     *         // Permissions aktualisieren
     *         permissionProvider.revokeAll(oldOwnerId, plot);
     *         permissionProvider.grantOwner(newOwnerId, plot);
     *
     *         // Datenbank aktualisieren
     *         plotManager.savePlot(plot);
     *     }
     * }
     * </pre>
     *
     * <p><b>Validierung:</b></p>
     * <ul>
     *   <li>newOwnerId darf nicht null sein</li>
     *   <li>Caller sollte zusätzliche Business-Logik implementieren</li>
     *   <li>Keine automatische Persistierung (muss manuell erfolgen)</li>
     * </ul>
     *
     * @param newOwnerId Die UUID des neuen Besitzers (nicht null)
     * @throws IllegalArgumentException wenn newOwnerId null ist
     */
    public void setOwnerId(UUID newOwnerId) {
        if (newOwnerId == null) {
            throw new IllegalArgumentException("Neue Owner-ID darf nicht null sein");
        }
        this.currentOwnerId = newOwnerId;
    }

    /**
     * Prüft ob dieser Plot von einem bestimmten Spieler besessen wird.
     *
     * <p>Diese Helper-Methode ist nützlich für Permission-Checks
     * und kann von Subklassen verwendet werden.</p>
     *
     * <p><b>Beispiel:</b></p>
     * <pre>
     * class TradeguildPlot extends AbstractPlotClaimed {
     *     void doSomething(Player player) {
     *         if (isOwnedBy(player.getUniqueId())) {
     *             // Owner-exklusive Aktion
     *         }
     *     }
     * }
     * </pre>
     *
     * @param playerId Die UUID des zu prüfenden Spielers
     * @return true wenn der Spieler Owner ist, false sonst
     */
    public boolean isOwnedBy(UUID playerId) {
        return currentOwnerId.equals(playerId);
    }

    /**
     * Gibt eine String-Repräsentation dieses Plots zurück.
     *
     * <p><b>Format:</b> {@code ClassName{id=..., currentOwnerId=..., location=...}}</p>
     *
     * @return String-Repräsentation für Debugging
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", currentOwnerId=" + currentOwnerId +
                ", location=" + location +
                '}';
    }
}
