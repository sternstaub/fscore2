# Sprint-Erkenntnisse & Architektur-Evolution

Diese Datei dokumentiert die wichtigsten Erkenntnisse aus dem AI-generierten Prototyp-Projekt, die in die Neuinitialisierung einfließen.

---

## Sprint 18: Der Architektur-Durchbruch

### Problem-Ausgangslage

**Plot-spezifische UI-Explosion:**
```java
class TradeguildUi { ... }
class StoragePlotUi { ... }
class NpcPlotUi { ... }
// Eine UI-Klasse pro Plot-Typ → nicht skalierbar
```

**Manuelle UI-Konstruktion:**
```java
void showGui(Player player) {
    if (plot instanceof TradeguildPlot) {
        new TradeguildUi(plot).show(player);
    } else if (plot instanceof StoragePlot) {
        new StoragePlotUi(plot).show(player);
    }
    // instanceof-Ketten → Anti-Pattern
}
```

### Lösung: Universal GUI System

#### 1. GuiRenderable Interface

**Konzept:** Objekte rendern sich selbst.

```java
interface GuiRenderable {
    ItemStack getDisplayItem();
    boolean isVisible(Player player);
}
```

**Vorteil:** Keine separaten UI-Klassen mehr nötig.

#### 2. PlotAction (Command Pattern)

**Konzept:** Aktionen als First-Class Objects mit Logik + Permissions + Rendering.

```java
abstract class PlotAction implements GuiRenderable {
    protected final Plot plot;

    // Permission System
    boolean requiresOwnership() { return true; }
    boolean canExecute(Player player) {
        return !requiresOwnership() || isOwner(player);
    }

    protected boolean isOwner(Player player) {
        return plot.getOwner().equals(player.getUniqueId());
    }

    // Business Logic
    abstract void execute(Player player);

    // Self-Rendering
    abstract ItemStack getDisplayItem();

    boolean isVisible(Player player) {
        return true;  // Override für bedingte Sichtbarkeit
    }
}
```

**Konkrete Implementierung:**
```java
class PlotActionSetName extends PlotAction {
    @Override
    public void execute(Player player) {
        player.sendMessage("§aGib einen neuen Namen ein:");
        // Chat-Input-Handler registrieren
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Material.NAME_TAG)
            .setDisplayName("§ePlot umbenennen")
            .setLore("§7Klicke, um deinen Plot umzubenennen");
    }
}
```

#### 3. Trait-basierte Plot-Interfaces

**Konzept:** Plots exposieren ihre verfügbaren Aktionen über Traits.

```java
interface NamedPlot {
    default List<PlotAction> getNameActions() {
        return List.of(new PlotActionSetName(this));
    }
}

interface StorageContainerPlot {
    default List<PlotAction> getStorageActions() {
        return List.of(
            new PlotActionOpenStorage(this),
            new PlotActionSetStoragePrice(this)
        );
    }
}

interface NpcContainerPlot {
    default List<PlotAction> getNpcActions() {
        return List.of(
            new PlotActionSpawnNpc(this),
            new PlotActionConfigureNpc(this)
        );
    }
}
```

**Zusammenführung:**
```java
class TradeguildPlot extends Plot
    implements NamedPlot, StorageContainerPlot, NpcContainerPlot {

    @Override
    public List<PlotAction> getAvailablePlotActions() {
        List<PlotAction> actions = new ArrayList<>();
        actions.addAll(getNameActions());
        actions.addAll(getStorageActions());
        actions.addAll(getNpcActions());
        return actions;
    }
}
```

#### 4. GuiBuilder Pattern

**Konzept:** Automatische GUI-Generierung aus Action-Listen.

```java
class GuiBuilder {
    public static Inventory buildFromActions(
        List<GuiRenderable> actions,
        Player player,
        String title
    ) {
        // Filtere sichtbare Actions
        List<GuiRenderable> visible = actions.stream()
            .filter(action -> action.isVisible(player))
            .toList();

        // Berechne GUI-Größe
        int size = (int) Math.ceil(visible.size() / 9.0) * 9;
        Inventory gui = Bukkit.createInventory(null, size, title);

        // Fülle GUI
        int slot = 0;
        for (GuiRenderable action : visible) {
            gui.setItem(slot++, action.getDisplayItem());
        }

        return gui;
    }
}
```

**Verwendung:**
```java
// Universell für ALLE Plot-Typen
Inventory gui = GuiBuilder.buildFromActions(
    plot.getAvailablePlotActions(),
    player,
    "Plot-Verwaltung"
);
player.openInventory(gui);
```

#### 5. MenuAction (Hierarchische Menüs)

**Konzept:** PlotActions können Submenüs haben.

```java
interface MenuAction {
    List<GuiRenderable> getSubActions();
}

class PlotActionManageStorage extends PlotAction implements MenuAction {
    @Override
    public void execute(Player player) {
        // Öffne Submenü
        Inventory submenu = GuiBuilder.buildFromActions(
            getSubActions(),
            player,
            "Lager-Verwaltung"
        );
        player.openInventory(submenu);
    }

    @Override
    public List<GuiRenderable> getSubActions() {
        return List.of(
            new PlotActionOpenStorage(plot),
            new PlotActionSetStoragePrice(plot),
            new PlotActionUpgradeStorage(plot)
        );
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Material.CHEST)
            .setDisplayName("§eLager verwalten");
    }
}
```

### Kern-Erkenntnis

**Wichtig:** MenuAction ist ein **Interface**, keine Klasse!

Jede PlotAction kann **optional** Submenüs haben durch Implementierung von MenuAction.

```java
// ✅ Richtig
class PlotActionManageStorage extends PlotAction implements MenuAction

// ❌ Falsch
class MenuAction extends PlotAction  // MenuAction ist kein Basistyp!
```

---

## Sprint 26: Der Storage-Price-Loop Bug

### Problem

**Datenspeicher-Mismatch:**

```java
// StoragePriceUi speichert hier:
economyProvider.setSellPrice(item, price);  // Global

// GuildTraderNPC liest hier:
double price = plotPriceManager.getPrice(plot, item);  // Plot-spezifisch

// Resultat: price == 0.0 → Keine TradeSets!
```

**Symptom:** Spieler setzen Preise im UI, aber NPCs verkaufen nichts.

### Lösung

**Single Source of Truth:**

```java
// NPCs lesen aus der gleichen Quelle wie UI schreibt
class GuildTraderNPC {
    private final EconomyProvider economy;

    List<TradeSet> generateTrades() {
        for (ItemStack item : availableItems) {
            double price = economy.getSellPrice(item);  // ✅ Gleiche Quelle
            if (price > 0) {
                tradeSets.add(new TradeSet(item, price));
            }
        }
        return tradeSets;
    }
}
```

**Entfernt:**
- Reflection-Code für `PlotPriceManager`
- Plot-spezifische Preis-Logik (vorerst)

### Erkenntnis

**Daten-Konsistenz-Regel:**

Wenn zwei Komponenten die gleiche Information nutzen:
1. **Single Source of Truth** definieren
2. Beide lesen/schreiben aus/in **derselben Quelle**
3. Keine Daten-Duplikation über verschiedene Manager

---

## Sprint 19: Identifizierte Schwachstellen

### 1. Items-Modul: Hart-kodierte Währung

**Problem:**
```java
class EconomyModule {
    private final CoinProvider coinProvider = new GoldCoinProvider();  // Hard-coded!
}
```

**Lösung:**
```java
interface CurrencyItem {
    ItemStack createCurrency(int amount);
    int getCurrencyValue(ItemStack item);
    boolean isCurrency(ItemStack item);
}

class CurrencyRegistry {
    private final Map<String, CurrencyItem> currencies = new HashMap<>();

    void register(String id, CurrencyItem currency) {
        currencies.put(id, currency);
    }

    CurrencyItem get(String id) {
        return currencies.get(id);
    }
}
```

### 2. Plots-Modul: Preis-Logik nur in StorageContainerPlot

**Problem:**
```java
class StorageContainerPlot {
    private Map<ItemStack, Double> prices = new HashMap<>();  // Nur hier!
}
```

**Lösung:**
```java
interface Priceable {
    void setPrice(ItemStack item, double price);
    double getPrice(ItemStack item);
    Map<ItemStack, Double> getAllPrices();
}

class PlotPriceManager {
    private final Map<UUID, Map<ItemStack, Double>> plotPrices = new HashMap<>();

    void setPrice(Plot plot, ItemStack item, double price) {
        plotPrices.computeIfAbsent(plot.getId(), k -> new HashMap())
            .put(item, price);
    }

    double getPrice(Plot plot, ItemStack item) {
        return plotPrices.getOrDefault(plot.getId(), Map.of())
            .getOrDefault(item, 0.0);
    }
}
```

### 3. NPCs-Modul: Manuelle UI-Konstruktion

**Problem:**
```java
class NpcConfigUi {
    void show(Player player) {
        Inventory gui = Bukkit.createInventory(...);
        gui.setItem(0, new ItemStack(Material.NAME_TAG));  // Manuell!
        gui.setItem(1, new ItemStack(Material.COMPASS));
        // ...
    }
}
```

**Lösung:**
```java
class NpcAction implements GuiRenderable {
    abstract void execute(Player player);
    abstract ItemStack getDisplayItem();
}

class NpcActionSetName extends NpcAction {
    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Material.NAME_TAG)
            .setDisplayName("§eNPC umbenennen");
    }
}

// Verwendung
GuiBuilder.buildFromActions(npc.getAvailableActions(), player);
```

### 4. Economy-Modul: TradeUI nicht GuiRenderable-konform

**Problem:**
```java
class TradeUi {
    void show(Player player) { ... }  // Keine GuiRenderable-Implementierung
}
```

**Lösung:**
```java
class TradeAction implements GuiRenderable {
    private final TradeSet trade;

    @Override
    public ItemStack getDisplayItem() {
        return trade.getItem().clone()
            .setLore("§7Preis: §6" + trade.getPrice() + " Gold");
    }

    @Override
    public void execute(Player player) {
        // Führe Trade aus
    }
}
```

---

## Etablierte Prinzipien (FINAL)

### Design-Checkliste (VOR jeder Implementierung)

```
✅ Funktioniert universal (nicht typ-spezifisch)?
✅ Erweiterbar ohne Code-Änderungen (OCP)?
✅ Nutzt Self-Rendering Pattern?
✅ Keine instanceof-Checks?
✅ Keine hart-kodierten Dependencies?
✅ Trait-basiert statt Vererbung?
✅ Single Source of Truth für Daten?
```

### Anti-Pattern-Liste

```
❌ Plot-spezifische UI-Klassen (TradeguildUi, StoragePlotUi)
❌ instanceof-Ketten statt Polymorphismus
❌ Reflection statt direkte Dependencies
❌ Datenspeicher-Mismatch (verschiedene Quellen für gleiche Daten)
❌ Generische Namen ohne Kontext (Helper, Util)
❌ Suffixe vor Basisklasse (SetNameAction statt PlotActionSetName)
```

### Pattern-Katalog

```
✅ Command Pattern (PlotAction)
✅ Trait-Pattern (Plot-Interfaces)
✅ Self-Rendering Pattern (GuiRenderable)
✅ Builder Pattern (GuiBuilder)
✅ Menu Pattern (MenuAction-Interface)
✅ Provider Pattern (Graceful Degradation)
✅ Registry Pattern (CurrencyRegistry)
```

---

## Lessons Learned

### 1. Self-Rendering > Separation

**Vorher gedacht:**
> "UI-Logik sollte von Business-Logik getrennt sein"

**Gelernt:**
> "Für einfache UIs ist Self-Rendering effizienter als UI-Klassen-Explosion"

**Konsequenz:**
```java
// ✅ Eine Klasse mit allem
class PlotAction implements GuiRenderable {
    void execute();          // Business Logic
    ItemStack getDisplayItem();  // UI Rendering
    boolean canExecute();    // Permissions
}

// ❌ Drei separate Klassen
class PlotActionLogic { ... }
class PlotActionUi { ... }
class PlotActionPermissions { ... }
```

### 2. Traits > Vererbung

**Vorher gedacht:**
> "Tiefe Vererbungshierarchien für Code-Reuse"

**Gelernt:**
> "Flache Trait-Komposition für Flexibilität"

**Konsequenz:**
```java
// ✅ Flexibel kombinierbar
interface NamedPlot { ... }
interface StorageContainerPlot { ... }
class TradeguildPlot implements NamedPlot, StorageContainerPlot { ... }

// ❌ Starr und schwer erweiterbar
class Plot { ... }
class NamedPlot extends Plot { ... }
class StoragePlot extends NamedPlot { ... }
```

### 3. GuiBuilder > instanceof

**Vorher gedacht:**
> "instanceof ist okay für UI-Logik"

**Gelernt:**
> "Polymorphismus + Builder eliminiert alle instanceof-Checks"

**Konsequenz:**
```java
// ✅ Ein System für alle Typen
GuiBuilder.buildFromActions(plot.getAvailablePlotActions(), player);

// ❌ Type-Checks für jeden Plot-Typ
if (plot instanceof TradeguildPlot) showTradeGui();
else if (plot instanceof StoragePlot) showStorageGui();
```

### 4. Single Source of Truth

**Vorher gedacht:**
> "Jedes Modul kann seine eigene Datenverwaltung haben"

**Gelernt:**
> "Daten-Duplikation führt zu Synchronisations-Bugs"

**Konsequenz:**
- **Ein** EconomyProvider für Preise
- **Ein** PlotManager für Plot-Daten
- **Ein** CurrencyRegistry für Währungen

---

## Evolution Timeline

### Phase 1: Prototyp (AI-generiert)
- ❌ Plot-spezifische UI-Klassen
- ❌ instanceof-Ketten
- ❌ Datenspeicher-Mismatch
- ✅ Grundlegende Modul-Struktur

### Phase 2: Sprint 18 (Architektur-Durchbruch)
- ✅ GuiRenderable + PlotAction
- ✅ GuiBuilder Pattern
- ✅ Trait-basierte Plot-Interfaces
- ✅ MenuAction für Hierarchien

### Phase 3: Sprint 26 (Bug-Fix)
- ✅ Single Source of Truth
- ✅ Daten-Konsistenz-Regel
- ❌ Reflection entfernt

### Phase 4: Sprint 19 (Refactoring-Planung)
- 📋 CurrencyRegistry
- 📋 Priceable Interface
- 📋 NpcAction-System
- 📋 TradeAction-Migration

### Phase 5: Neuinitialisierung (Aktuell)
- ✅ Clean Slate
- ✅ Erkenntnisse dokumentiert
- ✅ Konventionen etabliert
- 📋 Implementierung steht bevor

---

## Nächste Schritte

### Priorität 1: Core-Interfaces
- [ ] `GuiRenderable` Interface
- [ ] `PlotAction` abstrakte Klasse
- [ ] `GuiBuilder` Utility

### Priorität 2: Trait-System
- [ ] `NamedPlot` Interface
- [ ] `StorageContainerPlot` Interface
- [ ] `NpcContainerPlot` Interface
- [ ] `Priceable` Interface

### Priorität 3: Provider-System
- [ ] `EconomyProvider` Interface
- [ ] `CurrencyRegistry` Implementierung
- [ ] `PlotPriceManager` Implementierung

### Priorität 4: Konkrete Actions
- [ ] `PlotActionSetName`
- [ ] `PlotActionOpenStorage`
- [ ] `PlotActionSetStoragePrice`
- [ ] `PlotActionSpawnNpc`

---

**Stand:** Initialisiert aus Erkenntnissen des fs-core-sample-dump Projekts
**Ziel:** Saubere Implementierung ohne Legacy-Ballast
**Philosophie:** Design Patterns > Quick Hacks
