package de.fallenstar.core.provider;

import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Provider-Interface für Item-Verwaltung.
 *
 * <p>Dieses Interface abstrahiert den Zugriff auf Custom-Item-Plugins wie
 * MMOItems, ItemsAdder oder das interne Item-System.</p>
 *
 * <p><b>Graceful Degradation:</b> Wenn kein Custom-Item-Plugin verfügbar ist,
 * wird die NoOp-Implementierung verwendet, die nur Vanilla-Items unterstützt.</p>
 *
 * @see de.fallenstar.core.provider.impl.NoOpItemProvider
 */
public interface ItemProvider {

    /**
     * Gibt ein Custom-Item anhand seiner ID zurück.
     *
     * @param itemId Die Item-ID (z.B. "SWORD_LEGENDARY")
     * @return Optional mit dem ItemStack, wenn das Item existiert
     */
    Optional<ItemStack> getCustomItem(String itemId);

    /**
     * Gibt ein Custom-Item mit bestimmter Anzahl zurück.
     *
     * @param itemId Die Item-ID
     * @param amount Die Anzahl
     * @return Optional mit dem ItemStack, wenn das Item existiert
     */
    Optional<ItemStack> getCustomItem(String itemId, int amount);

    /**
     * Prüft, ob ein ItemStack ein Custom-Item ist.
     *
     * @param item Das zu prüfende ItemStack
     * @return true wenn es ein Custom-Item ist, false sonst
     */
    boolean isCustomItem(ItemStack item);

    /**
     * Gibt die ID eines Custom-Items zurück.
     *
     * @param item Das ItemStack
     * @return Optional mit der Item-ID, wenn es ein Custom-Item ist
     */
    Optional<String> getCustomItemId(ItemStack item);

    /**
     * Erstellt ein Vanilla-ItemStack.
     *
     * @param material Der Material-Name (z.B. "DIAMOND_SWORD")
     * @param amount Die Anzahl
     * @return Optional mit dem ItemStack, wenn das Material existiert
     */
    Optional<ItemStack> createVanillaItem(String material, int amount);

    /**
     * Prüft, ob ein Custom-Item mit der angegebenen ID existiert.
     *
     * @param itemId Die Item-ID
     * @return true wenn das Item existiert, false sonst
     */
    boolean customItemExists(String itemId);

    /**
     * Gibt den Anzeigenamen eines Items zurück.
     *
     * @param item Das ItemStack
     * @return Der Anzeigename (oder Material-Name als Fallback)
     */
    String getDisplayName(ItemStack item);

    /**
     * Gibt den Namen des Provider-Typs zurück.
     *
     * @return Der Provider-Name (z.B. "MMOItems", "ItemsAdder", "Vanilla", "NoOp")
     */
    String getProviderName();

    /**
     * Prüft, ob dieser Provider aktiv ist.
     *
     * @return true wenn der Provider funktionsfähig ist, false sonst
     */
    boolean isEnabled();
}
