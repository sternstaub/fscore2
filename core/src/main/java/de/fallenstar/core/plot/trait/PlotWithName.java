package de.fallenstar.core.plot.trait;

import de.fallenstar.core.plot.Plot;
import de.fallenstar.core.plot.action.PlotAction;

import java.util.List;

/**
 * Trait für Plots mit Namen-Verwaltung.
 *
 * <p>PlotWithName ist ein Trait-Interface, das Plot-Typen die Fähigkeit gibt,
 * einen Namen zu speichern und zu ändern. Das Interface folgt dem Trait-Pattern:
 * Es erweitert Plot und kann mit anderen Traits kombiniert werden.</p>
 *
 * <p><b>Naming Convention:</b></p>
 * <p>Pattern: {@code [Subject]With[Capability]} - Ein Plot MIT der Fähigkeit, einen Namen zu haben.</p>
 *
 * <p><b>Trait-Komposition:</b></p>
 * <pre>
 * class TradeguildPlot implements Plot, PlotWithName, PlotWithStorageContainer {
 *     private String name = "Handelshaus";
 *
 *     {@literal @}Override
 *     public String getName() {
 *         return name;
 *     }
 *
 *     {@literal @}Override
 *     public void setName(String name) {
 *         this.name = name;
 *     }
 *
 *     {@literal @}Override
 *     public List&lt;PlotAction&gt; getAvailablePlotActions() {
 *         return Stream.of(
 *             getNameActions(),      // von PlotWithName
 *             getStorageActions()    // von PlotWithStorageContainer
 *         ).flatMap(List::stream).toList();
 *     }
 * }
 * </pre>
 *
 * <p><b>Default-Actions:</b></p>
 * <p>Das Interface stellt eine Default-Implementierung für {@link #getNameActions()}
 * bereit, die automatisch passende PlotActions zurückgibt. Diese können im GUI
 * angezeigt werden.</p>
 *
 * <p><b>Verwendung:</b></p>
 * <pre>
 * // Plot mit Namen
 * PlotWithName namedPlot = getTradeguildPlot();
 * String currentName = namedPlot.getName();
 * namedPlot.setName("Neuer Name");
 *
 * // GUI aus Trait-Actions generieren
 * List&lt;PlotAction&gt; nameActions = namedPlot.getNameActions();
 * Inventory gui = GuiBuilder.buildFromActions(nameActions, player, "Namen verwalten");
 * </pre>
 *
 * @author FallenStar Development
 * @version 1.0.0
 * @see Plot
 * @see PlotAction
 */
public interface PlotWithName extends Plot {

    /**
     * Gibt den aktuellen Namen des Plots zurück.
     *
     * <p>Der Name ist eine frei wählbare Bezeichnung für den Plot,
     * die vom Besitzer gesetzt werden kann. Der Name kann für
     * Anzeigen, Listen und GUI-Titel verwendet werden.</p>
     *
     * <p><b>Beispiele:</b></p>
     * <pre>
     * "Handelshaus des Nordens"
     * "Mein Lagerplatz"
     * "Schmiedekunst GmbH"
     * </pre>
     *
     * <p><b>Hinweis:</b> Der Name sollte niemals null sein.
     * Für neu erstellte Plots sollte ein Default-Name verwendet werden.</p>
     *
     * @return Der aktuelle Plot-Name (niemals null)
     */
    String getName();

    /**
     * Setzt einen neuen Namen für den Plot.
     *
     * <p>Diese Methode wird typischerweise von PlotActions aufgerufen,
     * wenn der Spieler den Plot umbenennen möchte.</p>
     *
     * <p><b>Validierung:</b></p>
     * <p>Implementierungen können den Namen validieren und ggf. ablehnen:</p>
     * <ul>
     *   <li>Längen-Begrenzung (z.B. max. 32 Zeichen)</li>
     *   <li>Erlaubte Zeichen (z.B. keine Farb-Codes)</li>
     *   <li>Keine leeren Namen</li>
     * </ul>
     *
     * <p><b>Beispiel-Implementierung:</b></p>
     * <pre>
     * {@literal @}Override
     * public void setName(String name) {
     *     if (name == null || name.isBlank()) {
     *         throw new IllegalArgumentException("Name darf nicht leer sein");
     *     }
     *     if (name.length() > 32) {
     *         throw new IllegalArgumentException("Name zu lang (max. 32 Zeichen)");
     *     }
     *     this.name = name;
     * }
     * </pre>
     *
     * @param name Der neue Name für den Plot (nicht null, nicht leer)
     * @throws IllegalArgumentException wenn der Name ungültig ist
     */
    void setName(String name);

    /**
     * Gibt die Liste von PlotActions für Namen-Verwaltung zurück.
     *
     * <p>Diese Default-Implementierung gibt eine Liste mit Name-bezogenen
     * Actions zurück. In der aktuellen Phase ist dies ein Placeholder,
     * der in zukünftigen Phasen um PlotActionSetName erweitert wird.</p>
     *
     * <p><b>Default-Implementierung:</b></p>
     * <pre>
     * default List&lt;PlotAction&gt; getNameActions() {
     *     return List.of();  // Placeholder
     * }
     * </pre>
     *
     * <p><b>Zukünftige Implementierung:</b></p>
     * <pre>
     * default List&lt;PlotAction&gt; getNameActions() {
     *     return List.of(
     *         new PlotActionSetName(this),
     *         new PlotActionResetName(this)
     *     );
     * }
     * </pre>
     *
     * <p><b>Override-Möglichkeit:</b></p>
     * <p>Implementierungen können diese Methode überschreiben, um
     * zusätzliche oder alternative Actions anzubieten.</p>
     *
     * @return Liste von PlotActions für Namen-Verwaltung (niemals null)
     */
    default List<PlotAction> getNameActions() {
        // Placeholder - wird in zukünftigen Phasen erweitert
        return List.of();
    }
}
