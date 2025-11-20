package de.fallenstar.core.plot.trait;

import de.fallenstar.core.plot.Plot;
import de.fallenstar.core.plot.action.AbstractPlotAction;

import java.util.List;
import java.util.UUID;

/**
 * Trait für Plots mit NPC-Verwaltung (Citizens-Integration).
 *
 * <p>PlotWithNpcContainer ist ein Trait-Interface für Plots, die einen oder
 * mehrere NPCs (Non-Player Characters) hosten können. Diese NPCs werden
 * über das Citizens-Plugin verwaltet und können als Händler, Questgeber
 * oder Dekorations-Elemente dienen.</p>
 *
 * <p><b>Naming Convention:</b></p>
 * <p>Pattern: {@code [Subject]With[Capability]} - Ein Plot MIT einem NPC-Container.</p>
 *
 * <p><b>Trait-Komposition:</b></p>
 * <pre>
 * class TradeguildPlot implements Plot, PlotWithName, PlotWithStorageContainer, PlotWithNpcContainer {
 *     private UUID npcId;
 *
 *     {@literal @}Override
 *     public UUID getNpcId() {
 *         return npcId;
 *     }
 *
 *     {@literal @}Override
 *     public void setNpcId(UUID npcId) {
 *         this.npcId = npcId;
 *     }
 *
 *     {@literal @}Override
 *     public List&lt;AbstractPlotAction&gt; getAvailablePlotActions() {
 *         return Stream.of(
 *             getNameActions(),       // PlotWithName
 *             getStorageActions(),    // PlotWithStorageContainer
 *             getNpcActions()         // PlotWithNpcContainer
 *         ).flatMap(List::stream).toList();
 *     }
 * }
 * </pre>
 *
 * <p><b>Citizens-Integration:</b></p>
 * <p>Dieses Interface arbeitet mit dem Citizens-Plugin zusammen.
 * Die tatsächliche NPC-Verwaltung erfolgt über den NPCProvider,
 * der Citizens-API abstrahiert und Graceful Degradation ermöglicht.</p>
 *
 * <p><b>Verwendungs-Szenarien:</b></p>
 * <ul>
 *   <li>Handelsgilden mit Händler-NPCs</li>
 *   <li>Questgeber-Plots</li>
 *   <li>Dekorations-NPCs für Roleplay</li>
 *   <li>Shopkeeper-Plots</li>
 * </ul>
 *
 * <p><b>Default-Actions:</b></p>
 * <p>Das Interface stellt eine Default-Implementierung für {@link #getNpcActions()}
 * bereit. Diese ist aktuell ein Placeholder und wird in zukünftigen Phasen
 * mit konkreten Actions erweitert.</p>
 *
 * @author FallenStar Development
 * @version 1.0.0-SNAPSHOT
 * @see Plot
 * @see AbstractPlotAction
 * @see de.fallenstar.core.provider.NPCProvider
 */
public interface PlotWithNpcContainer extends Plot {

    /**
     * Gibt die UUID des assoziierten NPCs zurück.
     *
     * <p>Die NPC-ID referenziert einen Citizens-NPC, der zu diesem Plot gehört.
     * Über diese ID kann der NPC via NPCProvider abgerufen und verwaltet werden.</p>
     *
     * <p><b>Hinweise:</b></p>
     * <ul>
     *   <li>Kann null sein, wenn noch kein NPC zugewiesen wurde</li>
     *   <li>Die UUID entspricht der Citizens NPC-ID</li>
     *   <li>NPC sollte automatisch beim Plot-Delete entfernt werden</li>
     * </ul>
     *
     * <p><b>Verwendung mit NPCProvider:</b></p>
     * <pre>
     * PlotWithNpcContainer npcPlot = getTradeguildPlot();
     * UUID npcId = npcPlot.getNpcId();
     *
     * if (npcId != null) {
     *     NPCProvider npcProvider = ProviderRegistry.getProvider(NPCProvider.class);
     *     NPC npc = npcProvider.getNPC(npcId);
     *     // ... NPC-Operationen
     * }
     * </pre>
     *
     * @return Die UUID des NPCs, oder null wenn kein NPC zugewiesen ist
     */
    UUID getNpcId();

    /**
     * Setzt die UUID des assoziierten NPCs.
     *
     * <p>Diese Methode wird typischerweise aufgerufen, wenn ein NPC dem Plot
     * zugewiesen oder von ihm entfernt wird.</p>
     *
     * <p><b>Implementierungs-Hinweise:</b></p>
     * <ul>
     *   <li>Alte NPC-Zuweisungen sollten aufgeräumt werden</li>
     *   <li>NPC-Position sollte zur Plot-Location synchronisiert werden</li>
     *   <li>Persistierung in Datenbank nicht vergessen</li>
     * </ul>
     *
     * <p><b>Beispiel-Implementierung:</b></p>
     * <pre>
     * {@literal @}Override
     * public void setNpcId(UUID npcId) {
     *     // Alten NPC aufräumen
     *     if (this.npcId != null && !this.npcId.equals(npcId)) {
     *         NPCProvider npcProvider = ProviderRegistry.getProvider(NPCProvider.class);
     *         npcProvider.removeNPC(this.npcId);
     *     }
     *
     *     this.npcId = npcId;
     *
     *     // Neuen NPC zur Plot-Location teleportieren
     *     if (npcId != null) {
     *         NPC npc = npcProvider.getNPC(npcId);
     *         npc.teleport(getLocation());
     *     }
     * }
     * </pre>
     *
     * @param npcId Die UUID des NPCs, oder null um NPC-Zuordnung zu entfernen
     */
    void setNpcId(UUID npcId);

    /**
     * Gibt die Liste von AbstractPlotActions für NPC-Verwaltung zurück.
     *
     * <p>Diese Default-Implementierung ist ein Placeholder für zukünftige
     * NPC-bezogene Actions wie:</p>
     * <ul>
     *   <li>AbstractPlotActionCreateNpc - Erstellt einen neuen NPC</li>
     *   <li>AbstractPlotActionRemoveNpc - Entfernt den NPC</li>
     *   <li>AbstractPlotActionConfigureNpc - Öffnet NPC-Konfiguration</li>
     *   <li>AbstractPlotActionTeleportNpc - Teleportiert NPC zur Plot-Location</li>
     * </ul>
     *
     * <p><b>Aktuelle Implementierung (Placeholder):</b></p>
     * <pre>
     * default List&lt;AbstractPlotAction&gt; getNpcActions() {
     *     return List.of();  // Wird in zukünftigen Phasen erweitert
     * }
     * </pre>
     *
     * <p><b>Override-Möglichkeit:</b></p>
     * <p>Konkrete Plot-Typen können diese Methode überschreiben, um
     * spezifische NPC-Actions anzubieten.</p>
     *
     * @return Liste von AbstractPlotActions für NPC-Verwaltung (niemals null)
     */
    default List<AbstractPlotAction> getNpcActions() {
        // Placeholder - wird in zukünftigen Phasen erweitert
        return List.of();
    }
}
