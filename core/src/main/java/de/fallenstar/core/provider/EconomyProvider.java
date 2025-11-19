package de.fallenstar.core.provider;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Provider-Interface für Economy-Systeme.
 *
 * <p>Dieses Interface abstrahiert den Zugriff auf Economy-Plugins wie Vault
 * oder das interne Economy-System.</p>
 *
 * <p><b>Graceful Degradation:</b> Wenn kein Economy-Plugin verfügbar ist,
 * wird die NoOp-Implementierung verwendet, die alle Balances als 0.0
 * zurückgibt und Transaktionen ablehnt.</p>
 *
 * @see de.fallenstar.core.provider.impl.NoOpEconomyProvider
 */
public interface EconomyProvider {

    /**
     * Gibt den Kontostand eines Spielers zurück.
     *
     * @param player Der Spieler
     * @return Der Kontostand
     */
    double getBalance(Player player);

    /**
     * Gibt den Kontostand eines Spielers zurück (UUID-basiert).
     *
     * @param playerId Die Spieler-UUID
     * @return Der Kontostand
     */
    double getBalance(UUID playerId);

    /**
     * Prüft, ob ein Spieler genug Geld hat.
     *
     * @param player Der Spieler
     * @param amount Der zu prüfende Betrag
     * @return true wenn der Spieler genug Geld hat, false sonst
     */
    boolean has(Player player, double amount);

    /**
     * Zieht Geld vom Konto eines Spielers ab.
     *
     * @param player Der Spieler
     * @param amount Der abzuziehende Betrag
     * @return true wenn erfolgreich, false sonst
     */
    boolean withdraw(Player player, double amount);

    /**
     * Fügt Geld zum Konto eines Spielers hinzu.
     *
     * @param player Der Spieler
     * @param amount Der hinzuzufügende Betrag
     * @return true wenn erfolgreich, false sonst
     */
    boolean deposit(Player player, double amount);

    /**
     * Gibt den Verkaufspreis eines Items zurück.
     *
     * @param item Das Item
     * @return Der Verkaufspreis (0.0 wenn kein Preis gesetzt)
     */
    double getSellPrice(ItemStack item);

    /**
     * Gibt den Kaufpreis eines Items zurück.
     *
     * @param item Das Item
     * @return Der Kaufpreis (0.0 wenn kein Preis gesetzt)
     */
    double getBuyPrice(ItemStack item);

    /**
     * Setzt den Verkaufspreis eines Items.
     *
     * @param item Das Item
     * @param price Der neue Preis
     */
    void setSellPrice(ItemStack item, double price);

    /**
     * Setzt den Kaufpreis eines Items.
     *
     * @param item Das Item
     * @param price Der neue Preis
     */
    void setBuyPrice(ItemStack item, double price);

    /**
     * Gibt den Namen der Währung zurück.
     *
     * @return Der Währungsname (z.B. "Gold", "Taler")
     */
    String getCurrencyName();

    /**
     * Gibt den Namen des Provider-Typs zurück.
     *
     * @return Der Provider-Name (z.B. "Vault", "Internal", "NoOp")
     */
    String getProviderName();

    /**
     * Prüft, ob dieser Provider aktiv ist.
     *
     * @return true wenn der Provider funktionsfähig ist, false sonst
     */
    boolean isEnabled();
}
