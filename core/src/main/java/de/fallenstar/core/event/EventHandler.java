package de.fallenstar.core.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Zentraler Event-Handler für das FallenStar-System.
 *
 * <p>Der EventHandler vereinfacht das Registrieren und Verwalten von Event-Listenern
 * und bietet eine funktionale API für Event-Handling.</p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *   <li>Funktionale Event-Listener-Registrierung</li>
 *   <li>Priority-basiertes Event-Dispatching</li>
 *   <li>Einfaches Deregistrieren von Listenern</li>
 *   <li>Event-Tracking und Debugging</li>
 *   <li>Thread-safe Listener-Verwaltung</li>
 * </ul>
 *
 * <p><b>Verwendung:</b></p>
 * <pre>
 * EventHandler handler = new EventHandler(plugin);
 *
 * // Funktionale API
 * handler.registerListener(PlayerJoinEvent.class, event -> {
 *     event.getPlayer().sendMessage("Willkommen!");
 * });
 *
 * // Mit Priority
 * handler.registerListener(PlayerQuitEvent.class, EventPriority.HIGH, event -> {
 *     // Handle quit
 * });
 *
 * // Traditionelle Listener
 * handler.registerListener(new MyBukkitListener());
 * </pre>
 *
 * @author FallenStar Development
 * @version 1.0.0
 */
public class EventHandler {

    private final Plugin plugin;
    private final Logger logger;
    private final Set<Listener> registeredListeners;
    private final Map<String, Integer> eventCounts;

    /**
     * Erstellt einen neuen EventHandler.
     *
     * @param plugin Das Plugin-Instanz für Event-Registrierung
     */
    public EventHandler(Plugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin must not be null");
        }

        this.plugin = plugin;
        this.logger = Logger.getLogger("FallenStarCore");
        this.registeredListeners = ConcurrentHashMap.newKeySet();
        this.eventCounts = new ConcurrentHashMap<>();
    }

    /**
     * Registriert einen funktionalen Event-Listener mit normaler Priority.
     *
     * @param eventClass Die Event-Klasse
     * @param handler Der Event-Handler
     * @param <T> Der Event-Typ
     */
    public <T extends Event> void registerListener(Class<T> eventClass, Consumer<T> handler) {
        registerListener(eventClass, EventPriority.NORMAL, handler);
    }

    /**
     * Registriert einen funktionalen Event-Listener mit spezifischer Priority.
     *
     * @param eventClass Die Event-Klasse
     * @param priority Die Event-Priority
     * @param handler Der Event-Handler
     * @param <T> Der Event-Typ
     */
    public <T extends Event> void registerListener(Class<T> eventClass, EventPriority priority,
                                                    Consumer<T> handler) {
        if (eventClass == null || handler == null) {
            throw new IllegalArgumentException("Event class and handler must not be null");
        }

        // Erstelle einen Dummy-Listener für Bukkit
        Listener listener = new Listener() {};

        // Erstelle EventExecutor der den Consumer aufruft
        EventExecutor executor = (l, event) -> {
            if (eventClass.isInstance(event)) {
                @SuppressWarnings("unchecked")
                T typedEvent = (T) event;
                handler.accept(typedEvent);

                // Event-Statistik aktualisieren
                trackEvent(eventClass.getSimpleName());
            }
        };

        // Registriere bei Bukkit
        Bukkit.getPluginManager().registerEvent(
            eventClass,
            listener,
            priority,
            executor,
            plugin,
            false
        );

        registeredListeners.add(listener);

        logger.info(String.format(
            "Funktionaler Event-Listener registriert: %s (Priority: %s)",
            eventClass.getSimpleName(),
            priority.name()
        ));
    }

    /**
     * Registriert einen traditionellen Bukkit-Listener.
     *
     * <p>Der Listener muss Bukkit's @EventHandler Annotationen verwenden.</p>
     *
     * @param listener Der Bukkit-Listener
     */
    public void registerListener(Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener must not be null");
        }

        Bukkit.getPluginManager().registerEvents(listener, plugin);
        registeredListeners.add(listener);

        logger.info(String.format(
            "Bukkit-Listener registriert: %s",
            listener.getClass().getSimpleName()
        ));
    }

    /**
     * Deregistriert einen Listener.
     *
     * @param listener Der zu entfernende Listener
     * @return true wenn erfolgreich entfernt, false wenn nicht gefunden
     */
    public boolean unregisterListener(Listener listener) {
        if (listener == null) {
            return false;
        }

        HandlerList.unregisterAll(listener);
        boolean removed = registeredListeners.remove(listener);

        if (removed) {
            logger.info(String.format(
                "Listener deregistriert: %s",
                listener.getClass().getSimpleName()
            ));
        }

        return removed;
    }

    /**
     * Deregistriert alle registrierten Listener.
     */
    public void unregisterAll() {
        for (Listener listener : registeredListeners) {
            HandlerList.unregisterAll(listener);
        }

        int count = registeredListeners.size();
        registeredListeners.clear();

        logger.info(String.format(
            "Alle Event-Listener deregistriert (%d)",
            count
        ));
    }

    /**
     * Gibt die Anzahl der registrierten Listener zurück.
     *
     * @return Anzahl der Listener
     */
    public int getListenerCount() {
        return registeredListeners.size();
    }

    /**
     * Prüft ob ein Listener registriert ist.
     *
     * @param listener Der zu prüfende Listener
     * @return true wenn registriert, false sonst
     */
    public boolean isRegistered(Listener listener) {
        return listener != null && registeredListeners.contains(listener);
    }

    /**
     * Gibt alle registrierten Listener zurück.
     *
     * @return Unmodifiable Set mit Listenern
     */
    public Set<Listener> getRegisteredListeners() {
        return Collections.unmodifiableSet(registeredListeners);
    }

    /**
     * Tracked Event-Aufrufe für Debugging und Statistik.
     *
     * @param eventName Der Event-Name
     */
    private void trackEvent(String eventName) {
        eventCounts.merge(eventName, 1, Integer::sum);
    }

    /**
     * Gibt Event-Statistiken zurück.
     *
     * @return Map mit Event-Namen und Aufruf-Anzahl
     */
    public Map<String, Integer> getEventStatistics() {
        return Collections.unmodifiableMap(eventCounts);
    }

    /**
     * Setzt Event-Statistiken zurück.
     */
    public void resetStatistics() {
        eventCounts.clear();
        logger.info("Event-Statistiken zurückgesetzt");
    }

    /**
     * Gibt Debug-Informationen über registrierte Listener zurück.
     *
     * @return Debug-String
     */
    public String getDebugInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Registrierte Event-Listener (").append(registeredListeners.size()).append("):\n");

        for (Listener listener : registeredListeners) {
            sb.append("  - ").append(listener.getClass().getSimpleName()).append("\n");
        }

        sb.append("\nEvent-Statistiken:\n");
        eventCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> {
                sb.append("  - ").append(entry.getKey())
                  .append(": ").append(entry.getValue())
                  .append(" Aufrufe\n");
            });

        return sb.toString();
    }
}
