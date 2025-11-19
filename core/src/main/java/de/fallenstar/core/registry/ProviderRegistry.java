package de.fallenstar.core.registry;

import de.fallenstar.core.provider.*;
import de.fallenstar.core.provider.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Zentrale Registry für alle Provider-Implementierungen.
 *
 * <p>Die ProviderRegistry verwaltet alle Provider-Implementierungen und
 * ermöglicht es Modulen, ihre Provider zu registrieren. Das Core-System
 * kann Provider über diese Registry abrufen.</p>
 *
 * <p><b>Singleton-Pattern:</b> Die Registry ist ein Singleton und wird
 * über {@link #getInstance()} abgerufen.</p>
 *
 * <p><b>Graceful Degradation:</b> Wenn kein Provider für einen Typ
 * registriert ist, wird automatisch die NoOp-Implementierung verwendet.</p>
 *
 * @author FallenStar Development
 * @version 1.0.0
 */
public class ProviderRegistry {

    private static ProviderRegistry instance;
    private final Map<Class<?>, Object> providers;
    private final Logger logger;

    /**
     * Privater Konstruktor für Singleton.
     */
    private ProviderRegistry() {
        this.providers = new HashMap<>();
        this.logger = Logger.getLogger("FallenStarCore");
    }

    /**
     * Gibt die Singleton-Instanz der ProviderRegistry zurück.
     *
     * @return Die ProviderRegistry-Instanz
     */
    public static ProviderRegistry getInstance() {
        if (instance == null) {
            instance = new ProviderRegistry();
        }
        return instance;
    }

    /**
     * Registriert einen Provider.
     *
     * <p>Wenn bereits ein Provider für diesen Typ registriert ist,
     * wird er überschrieben und eine Warnung geloggt.</p>
     *
     * @param providerClass Die Provider-Interface-Klasse
     * @param provider Die Provider-Implementierung
     * @param <T> Der Provider-Typ
     */
    public <T> void register(Class<T> providerClass, T provider) {
        if (providerClass == null || provider == null) {
            throw new IllegalArgumentException("Provider class and implementation must not be null");
        }

        if (providers.containsKey(providerClass)) {
            Object existing = providers.get(providerClass);
            logger.warning(String.format(
                "Provider %s wird überschrieben: %s -> %s",
                providerClass.getSimpleName(),
                existing.getClass().getSimpleName(),
                provider.getClass().getSimpleName()
            ));
        }

        providers.put(providerClass, provider);
        logger.info(String.format(
            "Provider registriert: %s -> %s",
            providerClass.getSimpleName(),
            provider.getClass().getSimpleName()
        ));
    }

    /**
     * Deregistriert einen Provider.
     *
     * @param providerClass Die Provider-Interface-Klasse
     * @param <T> Der Provider-Typ
     */
    public <T> void unregister(Class<T> providerClass) {
        if (providerClass == null) {
            throw new IllegalArgumentException("Provider class must not be null");
        }

        Object removed = providers.remove(providerClass);
        if (removed != null) {
            logger.info(String.format(
                "Provider deregistriert: %s",
                providerClass.getSimpleName()
            ));
        }
    }

    /**
     * Gibt einen Provider zurück.
     *
     * <p><b>Wichtig:</b> Diese Methode gibt niemals null zurück.
     * Wenn kein Provider registriert ist, wird eine NoOp-Implementierung
     * zurückgegeben (Graceful Degradation).</p>
     *
     * @param providerClass Die Provider-Interface-Klasse
     * @param <T> Der Provider-Typ
     * @return Der Provider (niemals null)
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> providerClass) {
        if (providerClass == null) {
            throw new IllegalArgumentException("Provider class must not be null");
        }

        Object provider = providers.get(providerClass);
        if (provider != null) {
            return (T) provider;
        }

        // Graceful Degradation: NoOp-Provider zurückgeben
        return (T) getNoOpProvider(providerClass);
    }

    /**
     * Prüft, ob ein Provider für den angegebenen Typ registriert ist.
     *
     * @param providerClass Die Provider-Interface-Klasse
     * @return true wenn ein Provider registriert ist, false sonst
     */
    public boolean isRegistered(Class<?> providerClass) {
        return providers.containsKey(providerClass);
    }

    /**
     * Gibt einen optionalen Provider zurück.
     *
     * <p>Im Gegensatz zu {@link #get(Class)} gibt diese Methode ein
     * leeres Optional zurück, wenn kein Provider registriert ist.</p>
     *
     * @param providerClass Die Provider-Interface-Klasse
     * @param <T> Der Provider-Typ
     * @return Optional mit Provider, oder leer wenn nicht registriert
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOptional(Class<T> providerClass) {
        if (providerClass == null) {
            return Optional.empty();
        }
        return Optional.ofNullable((T) providers.get(providerClass));
    }

    /**
     * Entfernt alle registrierten Provider.
     *
     * <p><b>Achtung:</b> Diese Methode sollte nur beim Plugin-Shutdown
     * aufgerufen werden!</p>
     */
    public void clearAll() {
        int count = providers.size();
        providers.clear();
        logger.info(String.format("Alle Provider deregistriert (%d)", count));
    }

    /**
     * Gibt die Anzahl der registrierten Provider zurück.
     *
     * @return Anzahl der Provider
     */
    public int getProviderCount() {
        return providers.size();
    }

    /**
     * Gibt Debug-Informationen über registrierte Provider zurück.
     *
     * @return Debug-String mit allen Providern
     */
    public String getDebugInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Registrierte Provider (").append(providers.size()).append("):\n");
        providers.forEach((key, value) -> {
            sb.append("  - ").append(key.getSimpleName())
              .append(" -> ").append(value.getClass().getSimpleName())
              .append("\n");
        });
        return sb.toString();
    }

    /**
     * Gibt eine NoOp-Implementierung für den angegebenen Provider-Typ zurück.
     *
     * <p>Diese Methode wird intern verwendet, wenn kein Provider registriert ist.</p>
     *
     * @param providerClass Die Provider-Interface-Klasse
     * @return Die NoOp-Implementierung
     * @throws IllegalArgumentException wenn der Provider-Typ unbekannt ist
     */
    private Object getNoOpProvider(Class<?> providerClass) {
        if (providerClass == PlotProvider.class) {
            return new NoOpPlotProvider();
        } else if (providerClass == EconomyProvider.class) {
            return new NoOpEconomyProvider();
        } else if (providerClass == NPCProvider.class) {
            return new NoOpNPCProvider();
        } else if (providerClass == ItemProvider.class) {
            return new NoOpItemProvider();
        } else {
            throw new IllegalArgumentException(
                "Unbekannter Provider-Typ: " + providerClass.getSimpleName()
            );
        }
    }
}
