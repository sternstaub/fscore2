package de.fallenstar.core.provider.impl;

import de.fallenstar.core.provider.EconomyProvider;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * NoOp-Implementierung des EconomyProvider.
 *
 * <p>Diese Implementierung wird verwendet, wenn kein externes Economy-Plugin
 * (wie Vault) verfügbar ist. Sie gibt für alle Balances 0.0 zurück und
 * lehnt alle Transaktionen ab.</p>
 *
 * <p><b>Graceful Degradation:</b> Das System läuft weiter, aber ohne
 * Economy-Funktionalität.</p>
 *
 * @see EconomyProvider
 */
public class NoOpEconomyProvider implements EconomyProvider {

    @Override
    public double getBalance(Player player) {
        return 0.0;
    }

    @Override
    public double getBalance(UUID playerId) {
        return 0.0;
    }

    @Override
    public boolean has(Player player, double amount) {
        return false;
    }

    @Override
    public boolean withdraw(Player player, double amount) {
        return false;
    }

    @Override
    public boolean deposit(Player player, double amount) {
        return false;
    }

    @Override
    public double getSellPrice(ItemStack item) {
        return 0.0;
    }

    @Override
    public double getBuyPrice(ItemStack item) {
        return 0.0;
    }

    @Override
    public void setSellPrice(ItemStack item, double price) {
        // NoOp - keine Aktion
    }

    @Override
    public void setBuyPrice(ItemStack item, double price) {
        // NoOp - keine Aktion
    }

    @Override
    public String getCurrencyName() {
        return "N/A";
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
