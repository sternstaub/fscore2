package de.fallenstar.core.provider.impl;

import de.fallenstar.core.provider.ItemProvider;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

/**
 * NoOp-Implementierung des ItemProvider.
 *
 * <p>Diese Implementierung wird verwendet, wenn kein externes Custom-Item-Plugin
 * (wie MMOItems) verfügbar ist. Sie unterstützt nur Vanilla-Items und gibt für
 * Custom-Item-Abfragen leere Optionals zurück.</p>
 *
 * <p><b>Graceful Degradation:</b> Das System läuft weiter mit Vanilla-Items.</p>
 *
 * @see ItemProvider
 */
public class NoOpItemProvider implements ItemProvider {

    @Override
    public Optional<ItemStack> getCustomItem(String itemId) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> getCustomItem(String itemId, int amount) {
        return Optional.empty();
    }

    @Override
    public boolean isCustomItem(ItemStack item) {
        return false;
    }

    @Override
    public Optional<String> getCustomItemId(ItemStack item) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> createVanillaItem(String material, int amount) {
        try {
            Material mat = Material.valueOf(material.toUpperCase());
            return Optional.of(new ItemStack(mat, amount));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean customItemExists(String itemId) {
        return false;
    }

    @Override
    public String getDisplayName(ItemStack item) {
        if (item == null) {
            return "Unknown";
        }

        // Versuche DisplayName aus ItemMeta zu holen
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }

        // Fallback: Material-Name
        return item.getType().name().replace("_", " ");
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
