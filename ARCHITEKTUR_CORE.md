# Core-Framework Architektur

**Basierend auf Analyse von fs-core-sample-dump**

---

## 🎯 Grundprinzip: Core = Framework, Module = Integrationen

### Core-Verantwortlichkeit
Das Core-Plugin ist das **Framework** und enthält:
- ✅ Alle Basis-Interfaces und Abstraktionen
- ✅ Plot-System (komplettes API)
- ✅ Economy-System (komplettes API)
- ✅ UI-Framework (Actions, GuiBuilder, etc.)
- ✅ Event-Definitionen (Custom Events)
- ✅ Event-Handler (Registrierung)
- ✅ Provider-Registry
- ✅ DataStore-Abstraktionen
- ✅ NoOp-Implementierungen (Fallback)

### Module-Verantwortlichkeit
Module sind **optionale Integrationen** für externe Plugins:
- ✅ `module-towny` - Towny-Integration (Städte/Towns)
- ✅ `module-citizens` - Citizens-Integration (NPC-Spawning)
- ✅ `module-vault` - Vault-Integration (Economy)
- ✅ `module-mmoitems` - MMOItems-Integration (Custom Items)
- ✅ Weitere Feature-Module nach Bedarf

---

## 📦 Core Package-Struktur

### Abgeleitet aus fs-core-sample-dump

```
core/src/main/java/de/fallenstar/core/

├── provider/                    # Provider-Interfaces (Abstraktion)
│   ├── PlotProvider.java
│   ├── EconomyProvider.java
│   ├── NPCProvider.java
│   ├── ItemProvider.java
│   ├── UIProvider.java
│   ├── AuthProvider.java
│   ├── ChatProvider.java
│   └── NetworkProvider.java
│
├── provider/impl/               # NoOp-Implementierungen (Fallback)
│   ├── NoOpPlotProvider.java
│   ├── NoOpEconomyProvider.java
│   ├── NoOpNPCProvider.java
│   ├── NoOpItemProvider.java
│   └── NativeTextUIProvider.java  # Native UI-Fallback
│
├── registry/                    # Provider-Verwaltung
│   └── ProviderRegistry.java    # Zentrale Registry für alle Provider
│
├── plot/                        # Plot-System (KOMPLETT IM CORE)
│   ├── Plot.java                # Basis-Interface
│   ├── PlotManager.java         # Plot-Verwaltung
│   ├── PlotType.java            # Enum für Plot-Typen
│   │
│   ├── trait/                   # Trait-Interfaces (Komposition)
│   │   ├── PlotNamed.java
│   │   ├── PlotContainerStorage.java
│   │   ├── PlotContainerNpc.java
│   │   └── Priceable.java
│   │
│   ├── action/                  # PlotAction-System
│   │   ├── PlotAction.java      # Abstrakte Basisklasse
│   │   ├── MenuAction.java      # Interface für Hierarchie
│   │   │
│   │   └── impl/                # Konkrete Implementierungen
│   │       ├── PlotActionSetName.java
│   │       ├── PlotActionOpenStorage.java
│   │       ├── PlotActionSetStoragePrice.java
│   │       ├── PlotActionSpawnNpc.java
│   │       └── PlotActionTeleport.java
│   │
│   ├── model/                   # Datenklassen
│   │   ├── PlotData.java
│   │   ├── PlotStorage.java
│   │   └── PlotLocation.java
│   │
│   └── impl/                    # Konkrete Plot-Implementierungen
│       ├── BasePlot.java        # Basis-Implementierung
│       ├── TradeguildPlot.java  # Handelsgilden-Plot
│       ├── ResidencePlot.java   # Wohnhaus-Plot
│       └── WorkshopPlot.java    # Werkstatt-Plot
│
├── economy/                     # Economy-System (KOMPLETT IM CORE)
│   ├── CurrencyManager.java
│   ├── PricingEngine.java
│   ├── MarketCalculator.java
│   ├── WorldEconomyManager.java
│   │
│   ├── model/                   # Datenklassen
│   │   ├── Currency.java
│   │   ├── Price.java
│   │   ├── Transaction.java
│   │   └── MarketData.java
│   │
│   └── registry/                # Registries
│       ├── CurrencyRegistry.java
│       └── PriceRegistry.java
│
├── ui/                          # UI-Framework (KOMPLETT IM CORE)
│   ├── GuiRenderable.java       # Self-Rendering Interface
│   ├── GuiBuilder.java          # Universal GUI-Generator
│   ├── UiActionInfo.java        # Kontext für UI-Actions
│   │
│   ├── renderer/                # UI-Renderer
│   │   ├── ChatRenderer.java
│   │   ├── BookRenderer.java
│   │   ├── InventoryRenderer.java
│   │   └── SignRenderer.java
│   │
│   └── component/               # UI-Komponenten
│       ├── Menu.java
│       ├── Dialog.java
│       └── Form.java
│
├── npc/                         # NPC-System (Basis im Core)
│   ├── NPC.java                 # Basis-Interface
│   ├── NPCManager.java
│   ├── NPCAction.java           # Command Pattern für NPC-Actions
│   │
│   ├── trait/                   # NPC-Traits
│   │   ├── Tradeable.java
│   │   ├── Questable.java
│   │   └── Distributable.java
│   │
│   ├── distributor/             # Distributor-Pattern
│   │   ├── Distributor.java     # Interface
│   │   ├── NpcDistributor.java
│   │   └── QuestDistributor.java
│   │
│   └── model/                   # Datenklassen
│       ├── NPCData.java
│       ├── TradeOffer.java
│       └── Quest.java
│
├── item/                        # Item-System (Basis im Core)
│   ├── ItemManager.java
│   ├── ItemFactory.java
│   │
│   ├── model/                   # Datenklassen
│   │   ├── CustomItem.java
│   │   └── ItemData.java
│   │
│   └── registry/
│       └── ItemRegistry.java
│
├── event/                       # Custom Events (ALLE IM CORE)
│   ├── plot/
│   │   ├── PlotCreateEvent.java
│   │   ├── PlotDeleteEvent.java
│   │   ├── PlotNameChangeEvent.java
│   │   ├── PlotStorageOpenEvent.java
│   │   └── PlotPriceChangeEvent.java
│   │
│   ├── economy/
│   │   ├── TransactionEvent.java
│   │   ├── PriceUpdateEvent.java
│   │   └── CurrencyExchangeEvent.java
│   │
│   ├── npc/
│   │   ├── NPCSpawnEvent.java
│   │   ├── NPCTradeEvent.java
│   │   └── NPCQuestAcceptEvent.java
│   │
│   └── item/
│       ├── CustomItemCreateEvent.java
│       └── CustomItemUseEvent.java
│
├── listener/                    # Event-Handler (Registrierung im Core)
│   ├── PlotListener.java
│   ├── EconomyListener.java
│   ├── NPCListener.java
│   └── ItemListener.java
│
├── storage/                     # DataStore-Abstraktionen
│   ├── DataStore.java           # Interface
│   ├── DataStoreFactory.java
│   │
│   └── impl/
│       ├── SQLiteDataStore.java
│       ├── MySQLDataStore.java
│       └── MemoryDataStore.java  # Fallback
│
├── command/                     # Core-Commands
│   ├── PlotCommand.java
│   ├── EconomyCommand.java
│   ├── NPCCommand.java
│   └── AdminCommand.java
│
├── util/                        # Utilities
│   ├── LocationUtil.java
│   ├── ItemUtil.java
│   └── MessageUtil.java
│
└── FallenStarCore.java          # Main Plugin-Klasse

```

---

## 🔌 Module-Struktur (Externe Integrationen)

### module-towny/ (Städte-Integration)

```
module-towny/src/main/java/de/fallenstar/module/towny/

├── provider/
│   └── TownyPlotProvider.java   # Implementiert PlotProvider
│
├── listener/
│   └── TownyEventListener.java  # Hört auf Towny-Events
│
├── adapter/
│   ├── TownAdapter.java         # Konvertiert Town → Plot
│   └── ResidentAdapter.java
│
└── TownyModule.java             # Modul-Hauptklasse
```

**Verantwortung:**
- Towny → FallenStar Plot-System Brücke
- Town als Plot registrieren
- Towny-Permissions in Plot-System abbilden

---

### module-citizens/ (NPC-Integration)

```
module-citizens/src/main/java/de/fallenstar/module/citizens/

├── provider/
│   └── CitizensNPCProvider.java # Implementiert NPCProvider
│
├── listener/
│   └── CitizensEventListener.java
│
├── adapter/
│   └── CitizensNPCAdapter.java  # NPC → Citizens Entity
│
└── CitizensModule.java
```

**Verantwortung:**
- Citizens → FallenStar NPC-System Brücke
- NPC-Spawning via Citizens
- Citizens-Traits in NPC-System abbilden

---

### module-vault/ (Economy-Integration)

```
module-vault/src/main/java/de/fallenstar/module/vault/

├── provider/
│   └── VaultEconomyProvider.java # Implementiert EconomyProvider
│
├── adapter/
│   └── VaultCurrencyAdapter.java
│
└── VaultModule.java
```

**Verantwortung:**
- Vault → FallenStar Economy-System Brücke
- Transaktionen via Vault abwickeln
- Balance-Abfragen

---

### module-mmoitems/ (Item-Integration)

```
module-mmoitems/src/main/java/de/fallenstar/module/mmoitems/

├── provider/
│   └── MMOItemsItemProvider.java # Implementiert ItemProvider
│
├── adapter/
│   └── MMOItemAdapter.java
│
└── MMOItemsModule.java
```

**Verantwortung:**
- MMOItems → FallenStar Item-System Brücke
- Custom Items erstellen
- Item-Stats abfragen

---

## 🔄 Provider-Pattern Workflow

### Beispiel: Plot-System mit Towny-Integration

```java
// 1. Core definiert Interface
public interface PlotProvider {
    Optional<Plot> getPlotAt(Location location);
    void createPlot(PlotData data);
    void deletePlot(UUID plotId);
}

// 2. Core hat NoOp-Fallback
public class NoOpPlotProvider implements PlotProvider {
    @Override
    public Optional<Plot> getPlotAt(Location location) {
        return Optional.empty();  // Kein Plot-System verfügbar
    }
}

// 3. Modul implementiert konkret
public class TownyPlotProvider implements PlotProvider {
    @Override
    public Optional<Plot> getPlotAt(Location location) {
        TownBlock townBlock = TownyAPI.getTownBlock(location);
        if (townBlock != null) {
            return Optional.of(new TownAdapter(townBlock));
        }
        return Optional.empty();
    }
}

// 4. Core nutzt via Registry
PlotProvider plotProvider = ProviderRegistry.get(PlotProvider.class);
Optional<Plot> plot = plotProvider.getPlotAt(playerLocation);
```

**Vorteil:** Core funktioniert IMMER, auch ohne Towny installiert.

---

## 📋 Event-Definitionen im Core

### Beispiel: PlotCreateEvent

```java
package de.fallenstar.core.event.plot;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event wird gefeuert, wenn ein neues Plot erstellt wird.
 *
 * <p>Dieses Event wird sowohl für programmatisch erstellte Plots
 * als auch für über Towny/andere Systeme erstellte Plots gefeuert.</p>
 */
public class PlotCreateEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Plot plot;
    private final PlotType type;
    private boolean cancelled;

    public PlotCreateEvent(Plot plot, PlotType type) {
        this.plot = plot;
        this.type = type;
        this.cancelled = false;
    }

    public Plot getPlot() {
        return plot;
    }

    public PlotType getType() {
        return type;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
```

### Event-Handler im Core

```java
package de.fallenstar.core.listener;

import de.fallenstar.core.event.plot.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Zentraler Event-Listener für Plot-Events.
 *
 * <p>Dieser Listener wird im Core registriert und reagiert auf
 * alle Plot-bezogenen Events.</p>
 */
public class PlotListener implements Listener {

    @EventHandler
    public void onPlotCreate(PlotCreateEvent event) {
        // Logging, Validierung, etc.
        Plot plot = event.getPlot();
        // ... Core-Logik
    }

    @EventHandler
    public void onPlotNameChange(PlotNameChangeEvent event) {
        // Validierung des neuen Namens
        String newName = event.getNewName();
        if (newName.length() > 32) {
            event.setCancelled(true);
            event.getReason("Name zu lang!");
        }
    }

    // Weitere Event-Handler...
}
```

---

## 🎯 Zusammenfassung: Was gehört wohin?

### ✅ IM CORE (Framework)

| Komponente | Grund |
|------------|-------|
| **Plot-System** | Kern-Feature, alle Plugins brauchen es |
| **Economy-System** | Zentrale Währungs-/Preis-Verwaltung |
| **UI-Framework** | GuiBuilder, PlotAction - universell |
| **Event-Definitionen** | Alle Custom Events zentral definiert |
| **Event-Handler** | Registrierung und Basis-Logik |
| **Provider-Interfaces** | Abstraktion für externe Systeme |
| **ProviderRegistry** | Zentrale Provider-Verwaltung |
| **NPC-Basis** | Interface, Manager, Actions (Basis-API) |
| **Item-Basis** | Interface, Manager, Registry (Basis-API) |
| **DataStore** | Datenbank-Abstraktionen |

### ✅ IN MODULEN (Integrationen)

| Modul | Verantwortung |
|-------|---------------|
| **module-towny** | Towny → Plot-System (Städte-Integration) |
| **module-citizens** | Citizens → NPC-System (Spawning) |
| **module-vault** | Vault → Economy-System (Transaktionen) |
| **module-mmoitems** | MMOItems → Item-System (Custom Items) |

---

## 🚀 Vorteile dieser Architektur

1. **Graceful Degradation**
   - Core läuft IMMER, auch ohne externe Plugins
   - NoOp-Provider als Fallback

2. **Single Source of Truth**
   - Alle Events zentral definiert
   - Ein Plot-System für alle Integrationen

3. **Erweiterbarkeit**
   - Neue Module einfach hinzufügen
   - Provider-Pattern ermöglicht Austauschbarkeit

4. **Testbarkeit**
   - Core vollständig testbar ohne externe Plugins
   - Mocking via NoOp-Provider

5. **Klare Verantwortlichkeiten**
   - Core = Framework & API
   - Module = Konkrete Integrationen

---

## 📝 Nächste Schritte

1. Maven Multi-Module Struktur aufsetzen
2. Provider-Interfaces definieren
3. Plot-System im Core implementieren
4. Economy-System im Core implementieren
5. Event-Definitionen erstellen
6. Erste Module (Towny, Vault) als PoC

---

**Hinweis:** Diese Architektur basiert auf der Analyse von fs-core-sample-dump und wurde für eine saubere Neuimplementierung optimiert.
