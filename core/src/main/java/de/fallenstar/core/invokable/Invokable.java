package de.fallenstar.core.invokable;

/**
 * Marker-Interface für invokable Objekte.
 *
 * <p>Invokable ist ein Basis-Interface für alle Objekte, die auf verschiedene
 * Arten aufgerufen (invoked) werden können. Dies schließt ein:</p>
 * <ul>
 *   <li>Über Commands (InvokableByCommand)</li>
 *   <li>Über GUI-Buttons (InvokableByGuiButton)</li>
 *   <li>Über NPCs (InvokableByNpc - zukünftig)</li>
 *   <li>Über Events (InvokableByEvent - zukünftig)</li>
 * </ul>
 *
 * <p><b>Design-Philosophie:</b></p>
 * <p>Statt generischer execute()-Methoden nutzen wir spezifische Interfaces
 * für jeden Invokation-Mechanismus. Das macht klar, WIE ein Objekt aufgerufen
 * werden kann und WAS es dafür benötigt.</p>
 *
 * <p><b>Beispiel:</b></p>
 * <pre>
 * // PlotAction kann per Command UND GUI invoked werden
 * class PlotActionSetName extends AbstractPlotAction
 *     implements InvokableByCommand, InvokableByGuiButton {
 *
 *     {@literal @}Override
 *     public void invokeByCommand(Player player, String[] args) {
 *         // Command-spezifische Logik
 *         String newName = String.join(" ", args);
 *         setPlotName(newName);
 *     }
 *
 *     {@literal @}Override
 *     public void invokeByGuiButton(Player player) {
 *         // GUI-spezifische Logik (Chat-Input)
 *         openChatInputDialog(player);
 *     }
 * }
 * </pre>
 *
 * <p><b>Invokation-Pattern:</b></p>
 * <p>Jedes Invokable-Interface definiert:</p>
 * <ol>
 *   <li>Eine {@code invokeBy[Mechanism]()} Methode</li>
 *   <li>Eine {@code get[Mechanism]Invoker()} Methode für Metadaten</li>
 * </ol>
 *
 * <p><b>Vorteile gegenüber generischem execute():</b></p>
 * <ul>
 *   <li>✅ Explizit welcher Mechanismus unterstützt wird</li>
 *   <li>✅ Verschiedene Signaturen für verschiedene Mechanismen</li>
 *   <li>✅ Type-safe zur Compile-Time</li>
 *   <li>✅ Selbstdokumentierend</li>
 * </ul>
 *
 * <p><b>Verwendung mit GuiBuilder:</b></p>
 * <pre>
 * // GuiBuilder filtert automatisch InvokableByGuiButton
 * List&lt;InvokableByGuiButton&gt; guiActions = actions.stream()
 *     .filter(a -> a instanceof InvokableByGuiButton)
 *     .map(a -> (InvokableByGuiButton) a)
 *     .toList();
 *
 * Inventory gui = GuiBuilder.buildFromActions(guiActions, player, "Aktionen");
 * </pre>
 *
 * <p><b>Naming Convention:</b></p>
 * <p>Sub-Interfaces folgen dem Pattern: {@code InvokableBy[Mechanism]}</p>
 *
 * @author FallenStar Development
 * @version 1.0.0
 * @see InvokableByCommand
 * @see InvokableByGuiButton
 */
public interface Invokable {
    // Marker-Interface: Keine Methoden
    // Sub-Interfaces definieren spezifische Invokation-Methoden
}
