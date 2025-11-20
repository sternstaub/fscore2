package de.fallenstar.core.plot.impl;

import de.fallenstar.core.plot.Plot;
import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

/**
 * Abstrakte Basisklasse für alle Plot-Implementierungen.
 *
 * <p><b>Naming Convention:</b> Prefix-Pattern für abstrakte Klassen: {@code Abstract[Name]}</p>
 *
 * <p>AbstractPlotBase implementiert die grundlegenden Kern-Eigenschaften, die alle
 * Plots benötigen: ID, Owner-ID und Location. Diese Felder sind immutable (final)
 * und werden im Konstruktor gesetzt.</p>
 *
 * <p><b>Architektur-Pattern:</b></p>
 * <p>Diese Klasse folgt dem Template-Pattern: Sie stellt die gemeinsame Basis-Funktionalität
 * bereit, während Subklassen die plot-spezifischen Actions implementieren.</p>
 *
 * <p><b>Verwendung:</b></p>
 * <pre>
 * // Konkrete Plot-Typen erweitern AbstractPlotBase
 * class TradeguildPlot extends AbstractPlotClaimed implements PlotWithName {
 *     private String name = "Handelshaus";
 *
 *     public TradeguildPlot(UUID id, UUID ownerId, Location location) {
 *         super(id, ownerId, location);
 *     }
 *
 *     {@literal @}Override
 *     public List&lt;AbstractPlotAction&gt; getAvailablePlotActions() {
 *         return getNameActions();
 *     }
 * }
 * </pre>
 *
 * <p><b>Immutability:</b></p>
 * <p>Die Kern-Eigenschaften (id, ownerId, location) sind final und können nach
 * Erstellung nicht mehr geändert werden. Dies gewährleistet Thread-Safety und
 * verhindert inkonsistente Zustände.</p>
 *
 * <p><b>Package-Struktur:</b></p>
 * <pre>
 * plot/
 * ├── Plot.java              (Interface)
 * └── impl/
 *     ├── AbstractPlotBase.java     (abstrakte Basis)
 *     ├── AbstractPlotClaimed.java  (Subklasse mit Owner)
 *     └── TradeguildPlot.java      (konkrete Implementierung)
 * </pre>
 *
 * @author FallenStar Development
 * @version 1.0.0
 * @see Plot
 * @see AbstractPlotClaimed
 */
public abstract class AbstractPlotBase implements Plot {

    /**
     * Die eindeutige ID dieses Plots.
     *
     * <p>Diese ID ist persistent und ändert sich niemals während der Lebenszeit
     * eines Plots. Sie wird für Datenbankoperationen, Caching und Plot-Referenzen
     * verwendet.</p>
     */
    protected final UUID id;

    /**
     * Die UUID des Plot-Besitzers.
     *
     * <p><b>Hinweis:</b> In AbstractPlotBase ist diese ID immutable.
     * Wenn Plot-Ownership transferierbar sein soll, sollte eine Subklasse
     * wie AbstractPlotClaimed verwendet werden, die eine Setter-Methode bietet.</p>
     */
    protected final UUID ownerId;

    /**
     * Die Welt-Position des Plots.
     *
     * <p>Die Location markiert typischerweise den Spawn-Punkt oder die Mitte
     * des Plots. Sie wird für Teleportation und Welt-bezogene Operationen
     * verwendet.</p>
     */
    protected final Location location;

    /**
     * Erstellt einen neuen AbstractPlotBase mit den angegebenen Eigenschaften.
     *
     * <p><b>Validierung:</b></p>
     * <ul>
     *   <li>id darf nicht null sein</li>
     *   <li>ownerId darf nicht null sein</li>
     *   <li>location darf nicht null sein</li>
     * </ul>
     *
     * <p><b>Hinweis:</b> Dieser Konstruktor ist protected, da AbstractPlotBase
     * nicht direkt instanziiert werden soll. Nutze eine konkrete Subklasse.</p>
     *
     * @param id       Die eindeutige Plot-ID (nicht null)
     * @param ownerId  Die UUID des Besitzers (nicht null)
     * @param location Die Welt-Position des Plots (nicht null)
     * @throws IllegalArgumentException wenn einer der Parameter null ist
     */
    protected AbstractPlotBase(UUID id, UUID ownerId, Location location) {
        if (id == null) {
            throw new IllegalArgumentException("Plot-ID darf nicht null sein");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner-ID darf nicht null sein");
        }
        if (location == null) {
            throw new IllegalArgumentException("Location darf nicht null sein");
        }

        this.id = id;
        this.ownerId = ownerId;
        this.location = location.clone(); // Clone für Immutability
    }

    /**
     * Gibt die eindeutige ID des Plots zurück.
     *
     * @return Die eindeutige Plot-ID (niemals null)
     */
    @Override
    public UUID getId() {
        return id;
    }

    /**
     * Gibt die UUID des Plot-Besitzers zurück.
     *
     * @return Die UUID des Besitzers (niemals null)
     */
    @Override
    public UUID getOwnerId() {
        return ownerId;
    }

    /**
     * Gibt die Welt-Position des Plots zurück.
     *
     * <p><b>Hinweis:</b> Die zurückgegebene Location ist ein Clone,
     * um Immutability zu gewährleisten.</p>
     *
     * @return Eine Kopie der Plot-Location (niemals null)
     */
    @Override
    public Location getLocation() {
        return location.clone();
    }

    /**
     * Vergleicht diesen Plot mit einem anderen Objekt auf Gleichheit.
     *
     * <p>Zwei Plots sind gleich, wenn sie die gleiche ID haben.</p>
     *
     * @param obj Das zu vergleichende Objekt
     * @return true wenn die Plot-IDs identisch sind, false sonst
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Plot)) return false;
        Plot other = (Plot) obj;
        return id.equals(other.getId());
    }

    /**
     * Gibt den Hash-Code dieses Plots zurück.
     *
     * <p>Der Hash-Code basiert ausschließlich auf der Plot-ID.</p>
     *
     * @return Hash-Code basierend auf der Plot-ID
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Gibt eine String-Repräsentation dieses Plots zurück.
     *
     * <p><b>Format:</b> {@code ClassName{id=..., ownerId=..., location=...}}</p>
     *
     * @return String-Repräsentation für Debugging
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", location=" + location +
                '}';
    }
}
