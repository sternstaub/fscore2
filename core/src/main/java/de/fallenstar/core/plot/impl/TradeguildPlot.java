package de.fallenstar.core.plot.impl;

import de.fallenstar.core.plot.action.AbstractPlotAction;
import de.fallenstar.core.plot.trait.PlotWithName;
import de.fallenstar.core.plot.trait.PlotWithNpcContainer;
import de.fallenstar.core.plot.trait.PlotWithStorageContainer;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Konkrete Plot-Implementierung für Handelsgilden.
 *
 * <p><b>Naming Convention:</b> Suffix-Pattern für konkrete Klassen: {@code [Name][Type]}</p>
 *
 * <p>TradeguildPlot ist ein Plot-Typ, der speziell für Handelsgilden konzipiert ist.
 * Er kombiniert mehrere Traits:</p>
 * <ul>
 *   <li><b>PlotWithName:</b> Gildenhaus kann benannt werden</li>
 *   <li><b>PlotWithStorageContainer:</b> Lager für Handelswaren</li>
 *   <li><b>PlotWithNpcContainer:</b> Händler-NPCs spawnen</li>
 * </ul>
 *
 * <p><b>Trait-Komposition:</b></p>
 * <p>TradeguildPlot demonstriert das Trait-Pattern perfekt: Statt tiefer Vererbung
 * werden Fähigkeiten durch Interface-Komposition hinzugefügt.</p>
 *
 * <p><b>Verwendung:</b></p>
 * <pre>
 * // Plot erstellen
 * UUID plotId = UUID.randomUUID();
 * UUID ownerId = player.getUniqueId();
 * Location location = player.getLocation();
 *
 * TradeguildPlot plot = new TradeguildPlot(plotId, ownerId, location);
 * plot.setName("Handelshaus des Nordens");
 *
 * // GUI automatisch generieren
 * Inventory gui = GuiBuilder.buildFromActions(
 *     plot.getAvailablePlotActions(),
 *     player,
 *     "Handelsgilde verwalten"
 * );
 * player.openInventory(gui);
 * </pre>
 *
 * <p><b>Verfügbare Actions:</b></p>
 * <ul>
 *   <li>Namen ändern (PlotActionSetName)</li>
 *   <li>Lager öffnen (PlotActionOpenStorage - zukünftig)</li>
 *   <li>Preise setzen (PlotActionSetStoragePrice - zukünftig)</li>
 *   <li>NPC spawnen (PlotActionSpawnNpc - zukünftig)</li>
 * </ul>
 *
 * <p><b>Architektur-Highlights:</b></p>
 * <ul>
 *   <li>✅ Keine plot-spezifischen UI-Klassen (Self-Rendering)</li>
 *   <li>✅ Universelles GUI-System via GuiBuilder</li>
 *   <li>✅ Trait-basierte Funktionalität (keine Vererbungs-Hierarchie)</li>
 *   <li>✅ Actions als First-Class Citizens (Command Pattern)</li>
 * </ul>
 *
 * @author FallenStar Development
 * @version 1.0.0
 * @see AbstractPlotClaimed
 * @see PlotWithName
 * @see PlotWithStorageContainer
 * @see PlotWithNpcContainer
 */
public class TradeguildPlot extends AbstractPlotClaimed
        implements PlotWithName, PlotWithStorageContainer, PlotWithNpcContainer {

    /**
     * Der Name der Handelsgilde.
     *
     * <p>Standard-Name: "Handelshaus" (kann via {@link #setName(String)} geändert werden)</p>
     */
    private String name;

    /**
     * Das Lager-Inventar für Handelswaren.
     *
     * <p><b>Hinweis:</b> In der aktuellen Phase ist dies ein Placeholder (null).
     * In zukünftigen Phasen wird ein echtes Bukkit-Inventory implementiert.</p>
     */
    private Inventory storageInventory;

    /**
     * Die UUID des Händler-NPCs.
     *
     * <p><b>Hinweis:</b> In der aktuellen Phase ist dies ein Placeholder (null).
     * In zukünftigen Phasen wird hier die Citizens-NPC-ID gespeichert.</p>
     */
    private UUID npcId;

    /**
     * Erstellt einen neuen TradeguildPlot mit den angegebenen Eigenschaften.
     *
     * <p><b>Initialisierung:</b></p>
     * <ul>
     *   <li>Name: "Handelshaus"</li>
     *   <li>Storage: null (wird bei Bedarf initialisiert)</li>
     *   <li>NPC: null (wird bei Bedarf gespawnt)</li>
     * </ul>
     *
     * @param id       Die eindeutige Plot-ID (nicht null)
     * @param ownerId  Die UUID des Besitzers (nicht null)
     * @param location Die Welt-Position des Plots (nicht null)
     * @throws IllegalArgumentException wenn einer der Parameter null ist
     */
    public TradeguildPlot(UUID id, UUID ownerId, Location location) {
        super(id, ownerId, location);
        this.name = "Handelshaus";  // Default-Name
        this.storageInventory = null;  // Placeholder
        this.npcId = null;  // Placeholder
    }

    // ==================== PlotWithName Implementation ====================

    /**
     * Gibt den aktuellen Namen der Handelsgilde zurück.
     *
     * @return Der Name der Handelsgilde (niemals null)
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Setzt einen neuen Namen für die Handelsgilde.
     *
     * <p><b>Validierung:</b></p>
     * <ul>
     *   <li>Name darf nicht null sein</li>
     *   <li>Name darf nicht leer sein (nach trim)</li>
     *   <li>Maximale Länge: 32 Zeichen</li>
     * </ul>
     *
     * @param name Der neue Gildenname (nicht null, nicht leer, max. 32 Zeichen)
     * @throws IllegalArgumentException wenn die Validierung fehlschlägt
     */
    @Override
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name darf nicht null sein");
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Name darf nicht leer sein");
        }
        if (trimmed.length() > 32) {
            throw new IllegalArgumentException("Name zu lang (max. 32 Zeichen)");
        }
        this.name = trimmed;
    }

    // ==================== PlotWithStorageContainer Implementation ====================

    /**
     * Gibt das Lager-Inventar zurück.
     *
     * <p><b>Aktuelle Implementierung (Placeholder):</b></p>
     * <p>Gibt immer null zurück. In zukünftigen Phasen wird hier ein
     * echtes Bukkit-Inventory mit 54 Slots zurückgegeben.</p>
     *
     * @return null (Placeholder)
     */
    @Override
    public Inventory getStorageInventory() {
        // Placeholder - wird in zukünftigen Phasen implementiert
        return storageInventory;
    }

    /**
     * Setzt das Lager-Inventar.
     *
     * <p><b>Aktuelle Implementierung (Placeholder):</b></p>
     * <p>Speichert die Referenz, aber erstellt kein echtes Inventory.
     * In zukünftigen Phasen wird dies mit Bukkit.createInventory() implementiert.</p>
     *
     * @param inventory Das neue Lager-Inventar (kann null sein)
     */
    public void setStorageInventory(Inventory inventory) {
        // Placeholder - wird in zukünftigen Phasen implementiert
        this.storageInventory = inventory;
    }

    // ==================== PlotWithNpcContainer Implementation ====================

    /**
     * Gibt die UUID des Händler-NPCs zurück.
     *
     * <p><b>Aktuelle Implementierung (Placeholder):</b></p>
     * <p>Gibt immer null zurück. In zukünftigen Phasen wird hier die
     * Citizens-NPC-UUID gespeichert.</p>
     *
     * @return null (Placeholder)
     */
    @Override
    public UUID getNpcId() {
        // Placeholder - wird in zukünftigen Phasen implementiert
        return npcId;
    }

    /**
     * Setzt die UUID des Händler-NPCs.
     *
     * <p><b>Aktuelle Implementierung (Placeholder):</b></p>
     * <p>Speichert die UUID, aber spawnt keinen echten NPC.
     * In zukünftigen Phasen wird dies mit dem Citizens-Provider implementiert.</p>
     *
     * @param npcId Die UUID des NPCs (kann null sein)
     */
    @Override
    public void setNpcId(UUID npcId) {
        // Placeholder - wird in zukünftigen Phasen implementiert
        this.npcId = npcId;
    }

    // ==================== Plot Implementation ====================

    /**
     * Gibt alle verfügbaren PlotActions für diesen Tradeguild-Plot zurück.
     *
     * <p><b>Trait-Komposition:</b></p>
     * <p>Diese Methode sammelt Actions aus allen implementierten Traits:</p>
     * <ul>
     *   <li>{@link #getNameActions()} - Von PlotWithName</li>
     *   <li>{@link #getStorageActions()} - Von PlotWithStorageContainer</li>
     *   <li>{@link #getNpcActions()} - Von PlotWithNpcContainer</li>
     * </ul>
     *
     * <p><b>Verwendung mit GuiBuilder:</b></p>
     * <pre>
     * Inventory gui = GuiBuilder.buildFromActions(
     *     plot.getAvailablePlotActions(),
     *     player,
     *     "Handelsgilde verwalten"
     * );
     * </pre>
     *
     * <p><b>Aktuelle Phase:</b></p>
     * <p>In der aktuellen Phase sind die meisten Trait-Actions noch Placeholders
     * (leere Listen). Die Methode funktioniert trotzdem korrekt und wird in
     * zukünftigen Phasen automatisch mehr Actions enthalten.</p>
     *
     * @return Liste aller verfügbaren Actions (niemals null, kann leer sein)
     */
    @Override
    public List<AbstractPlotAction> getAvailablePlotActions() {
        // Trait-Komposition: Sammle Actions von allen Traits
        List<AbstractPlotAction> actions = new ArrayList<>();
        actions.addAll(getNameActions());
        actions.addAll(getStorageActions());
        actions.addAll(getNpcActions());
        return actions;

        // Alternative mit Streams (funktional):
        // return Stream.of(
        //     getNameActions(),
        //     getStorageActions(),
        //     getNpcActions()
        // ).flatMap(List::stream).toList();
    }

    /**
     * Gibt eine String-Repräsentation dieses Tradeguild-Plots zurück.
     *
     * <p><b>Format:</b> {@code TradeguildPlot{id=..., name=..., ownerId=..., location=...}}</p>
     *
     * @return String-Repräsentation für Debugging
     */
    @Override
    public String toString() {
        return "TradeguildPlot{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ownerId=" + getOwnerId() +
                ", location=" + location +
                ", hasStorage=" + (storageInventory != null) +
                ", hasNpc=" + (npcId != null) +
                '}';
    }
}
