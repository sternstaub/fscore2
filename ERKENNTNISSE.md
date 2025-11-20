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
interface PlotNamed {
    default List<PlotAction> getNameActions() {
        return List.of(new PlotActionSetName(this));
    }
}

interface PlotContainerStorage {
    default List<PlotAction> getStorageActions() {
        return List.of(
            new PlotActionOpenStorage(this),
            new PlotActionSetStoragePrice(this)
        );
    }
}

interface PlotContainerNpc {
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
    implements PlotNamed, PlotContainerStorage, PlotContainerNpc {

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

### 2. Plots-Modul: Preis-Logik nur in PlotContainerStorage

**Problem:**
```java
class PlotContainerStorage {
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
interface PlotNamed { ... }
interface PlotContainerStorage { ... }
class TradeguildPlot implements PlotNamed, PlotContainerStorage { ... }

// ❌ Starr und schwer erweiterbar
class Plot { ... }
class PlotNamed extends Plot { ... }
class StoragePlot extends PlotNamed { ... }
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
- [ ] `PlotNamed` Interface
- [ ] `PlotContainerStorage` Interface
- [ ] `PlotContainerNpc` Interface
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

## Sprint 20: Core-Foundation & Neuinitialisierung (2025-11-19)

### Kontext

Sprint 20 war der erste Sprint der Neuinitialisierung. Basierend auf den Erkenntnissen aus dem fs-core-sample-dump Prototypen wurde eine saubere Implementierung von Grund auf erstellt.

### Erfolgreich validierte Architektur

**✅ Self-Rendering Pattern:**
```java
// GuiRenderable = Objekte rendern sich selbst
ItemStack getDisplayItem();
boolean isVisible(Player player);

// Proof-of-Concept: PlotActionSetName
class PlotActionSetName implements GuiRenderable {
    ItemStack getDisplayItem() {
        return new ItemStack(Material.NAME_TAG)
            .setDisplayName("§6Namen ändern")
            .setLore("§7Aktueller Name: §f" + plot.getName());
    }
}
```

**✅ Command Pattern:**
```java
// PlotAction = First-Class Actions mit Logik + Permissions + Rendering
abstract class PlotAction implements GuiRenderable {
    boolean canExecute(Player player);
    void execute(Player player);
}

// Bewährtes Permission-System
protected boolean requiresOwnership() { return true; }
```

**✅ Trait-Komposition:**
```java
// TradeguildPlot kombiniert 3 Traits
class TradeguildPlot implements PlotNamed, PlotIsContainerForStorage, PlotIsContainerForNpc {
    List<PlotAction> getAvailablePlotActions() {
        return Stream.of(
            getNameActions(),      // PlotNamed
            getStorageActions(),   // PlotIsContainerForStorage
            getNpcActions()        // PlotIsContainerForNpc
        ).flatMap(List::stream).toList();
    }
}
```

**✅ Universal GuiBuilder:**
```java
// Ein GUI-System für ALLE Plot-Typen
Inventory gui = GuiBuilder.buildFromActions(
    plot.getAvailablePlotActions(),
    player,
    "Plot verwalten"
);
```

### Kritische Erkenntnisse

#### 1. Sprechende Namen für Trait-Interfaces

**Problem:** Mehrdeutige Namen erschweren Verständnis
```java
// ❌ Unklar
PlotContainerStorage
PlotContainerNpc
```

**Lösung:** Pattern "PlotIs[Eigenschaft]For[Zweck]"
```java
// ✅ Selbsterklärend
PlotIsContainerForStorage  // Ein Plot IST ein Container FÜR Storage
PlotIsContainerForNpc     // Ein Plot IST ein Container FÜR NPCs
```

**Erkenntnis:** Längere, beschreibende Namen > kurze, mehrdeutige Namen

#### 2. Interface-Dependency Priorisierung

**Problem:** PlotAction (Phase 4) benötigte Plot-Interface (Phase 7)

**Lösung:** Minimales Interface vorgezogen
```java
// Phase 4: Minimal Plot-Interface
interface Plot {
    UUID getOwnerId();  // Nur für Owner-Checks
}

// Phase 7: Vollständiges Interface
interface Plot {
    UUID getId();
    UUID getOwnerId();
    Location getLocation();
    List<PlotAction> getAvailablePlotActions();
}
```

**Erkenntnis:** Iterative Interface-Entwicklung vermeidet Blockaden

#### 3. Mockito Lenient Strictness für Integration-Tests

**Problem:** UnnecessaryStubbingException bei Mocks in @BeforeEach
```java
// Mocks werden nicht in allen Tests genutzt
@BeforeEach
void setUp() {
    when(owner.getUniqueId()).thenReturn(ownerId);
    when(owner.hasPermission(anyString())).thenReturn(false); // Nicht überall genutzt
}
```

**Lösung:** Lenient Strictness aktivieren
```java
@MockitoSettings(strictness = Strictness.LENIENT)
class PlotActionSetNameTest {
    // Flexible Mock-Nutzung ohne Exception
}
```

**Erkenntnis:** Lenient Mode für Test-Klassen mit gemeinsamen Mocks

#### 4. Bukkit ItemFactory Mocking

**Problem:** ItemStack.getItemMeta() benötigt ItemFactory
```java
// ❌ NPE: Bukkit.getItemFactory() ist null
ItemStack item = new ItemStack(Material.NAME_TAG);
ItemMeta meta = item.getItemMeta(); // NullPointerException
```

**Lösung:** MockedStatic für Bukkit + ItemFactory
```java
// ✅ Korrektes Mocking
try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
    bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
    when(itemFactory.getItemMeta(any(Material.class))).thenReturn(itemMeta);
    when(itemMeta.clone()).thenReturn(itemMeta);

    ItemStack item = action.getDisplayItem();
    // Funktioniert jetzt
}
```

**Erkenntnis:** Bukkit-Mocking erfordert vollständige Factory-Kette

### Test-Driven Development Erfolg

**Metriken Sprint 20:**
- **Tests:** 146 (Ziel: ~70) → **209% Coverage**
- **Production Code:** 23 Klassen
- **Code Coverage:** ~95% (Ziel: ≥80%)
- **Build-Status:** ✅ SUCCESS
- **Phasen:** 10/10 abgeschlossen (100%)

**Test-Kategorien:**
- 20 Tests: Provider-System (ProviderRegistry)
- 37 Tests: UI-Komponenten (GuiRenderable, MenuAction, GuiBuilder)
- 29 Tests: Command & Event System
- 25 Tests: Plot & Traits
- 13 Tests: PlotActionSetName
- 13 Tests: Integration-Tests (GuiBuilder, TradeguildPlot)
- 9 Tests: Plot Traits

**Bewährte Praktiken:**
- ✅ Tests BEVOR Code geschrieben (TDD)
- ✅ Mock-basierte Unit Tests (keine echten Server)
- ✅ Integration-Tests für Proof-of-Concept
- ✅ Build nach JEDER Phase validieren
- ✅ Lenient Mockito für flexible Test-Suites

### Anti-Patterns vermieden

**✅ KEINE Plot-spezifischen UI-Klassen**
```java
// ❌ Vermieden
class TradeguildUi { ... }
class StoragePlotUi { ... }

// ✅ Stattdessen
GuiBuilder.buildFromActions(plot.getAvailablePlotActions(), player, "...");
```

**✅ KEINE instanceof-Ketten**
```java
// ❌ Vermieden
if (plot instanceof TradeguildPlot) { ... }
else if (plot instanceof StoragePlot) { ... }

// ✅ Stattdessen
plot.getAvailablePlotActions().forEach(action -> ...);
```

**✅ KEINE Reflection für Type-Checks**
```java
// ❌ Vermieden
Method method = plot.getClass().getMethod("getPrice");

// ✅ Stattdessen
if (plot instanceof Priceable priceable) {
    double price = priceable.getPrice();
}
```

---

## Neuinitialisierung (2025-11-19)

Diese Repository-Neuinitialisierung startete mit Sprint 1 und implementiert nur die bewährten Patterns aus den vorherigen Erkenntnissen.

---

## Sprint 1: Core-Foundation Etablierung (2025-11-19)

**Ziel:** Saubere Implementierung der Kern-Architektur ohne Legacy-Ballast

### Implementierte Features

1. **Maven Multi-Module Struktur**
   - `core/` - Kern-Plugin mit allen Basis-Systemen
   - `module-towny/`, `module-vault/`, `module-citizens/` - Optionale Provider-Module

2. **Provider-System mit Graceful Degradation**
   - `EconomyProvider`, `NpcProvider` Interfaces
   - NoOp-Fallbacks wenn Module fehlen

3. **Self-Rendering Pattern (GuiRenderable)**
   - Interface für selbst-rendernde UI-Komponenten
   - Eliminiert separateUI-Klassen

4. **Command Pattern (PlotAction)**
   - Abstrakte PlotAction Basis-Klasse
   - requiresOwnership() Pattern
   - canExecute() Permission-System

5. **Trait-Komposition**
   - `PlotNamed` - Namen-Verwaltung
   - `PlotIsContainerForStorage` - Storage-Funktionalität
   - `PlotIsContainerForNpc` - NPC-Verwaltung

6. **Universal GuiBuilder**
   - `GuiBuilder.buildFromActions()` funktioniert für alle Plot-Typen
   - Dynamische GUI-Generierung

7. **Proof-of-Concept**
   - `PlotActionSetName` als vollständige Referenz-Implementierung
   - Integration-Tests validieren Architektur

### Test-Metriken

- **Tests:** 146/146 ✅
- **Code Coverage:** ~95%
- **Build:** SUCCESS

### Kritische Erkenntnisse

#### 1. Naming Convention Problem
**Problem:** Namen waren inkonsistent und teils zu kurz/unklar
```java
PlotNamed                    // Unklar: Trait oder Status?
PlotIsContainerForStorage    // Zu lang, verwirrend
```

**Lösung für Sprint 2:** Prefix/Suffix-Pattern etablieren

#### 2. Package-Struktur unklar
**Problem:** Keine klare Trennung zwischen Interfaces und Implementierungen

**Lösung für Sprint 2:** Universelles Pattern etablieren
```
[package]/
├── [Interfaces].java
└── impl/
    ├── Abstract[Base].java
    └── [Concrete].java
```

### Nächste Schritte (Sprint 2)

**Priorität 1: Architektur-Refactoring**
- Naming Conventions finalisieren (Prefix/Suffix-Pattern)
- Package-Struktur etablieren
- Alle Klassen migrieren

**Priorität 2: Invokable-Pattern vorbereiten**
- `Invokable`, `InvokableByCommand`, `InvokableByGuiButton` Interfaces
- CommandInvoker, GuiButton Datenklassen

**Sprint 3+: Command-System & Features**
- Command-System implementieren
- Konkrete PlotActions (Claim, Storage, NPC)
- Persistenz-Layer
- Provider-Implementierungen

---

## Sprint 2: Architektur-Refactoring (2025-11-19 - laufend)

**Ziel:** Einheitliche Naming Conventions & Package-Struktur etablieren

### Neue Patterns

#### Naming Conventions

| Typ | Pattern | Beispiel |
|-----|---------|----------|
| Interface (Trait) | `[Subject]With[Capability]` | `PlotWithName`, `PlotWithStorageContainer` |
| Interface (Invokable) | `InvokableBy[Mechanism]` | `InvokableByCommand`, `InvokableByGuiButton` |
| Abstrakte Klasse | `Abstract[Name]` | `AbstractPlotBase`, `AbstractPlotClaimed` |
| Konkrete Klasse | `[Name][Type]` | `TradeguildPlot`, `PlotActionSetName` |
| Enumeration | `Defined[Concept]s` | `DefinedPlotTypes`, `DefinedCurrencies` |

#### Package-Struktur

```
plot/
├── Plot.java                       # Interface
├── PlotWithName.java               # Interface
├── DefinedPlotTypes.java           # Enum
└── impl/
    ├── AbstractPlotBase.java       # Abstrakt
    ├── AbstractPlotClaimed.java    # Abstrakt
    └── TradeguildPlot.java         # Konkret
```

### Migration-Plan

**Phase 1: Conventions aktualisieren** ✅
- CONVENTIONS_NAMING.md neu geschrieben
- CLAUDE.md aktualisiert

**Phase 2: Trait-Interfaces umbenennen** ✅
- PlotNamed → PlotWithName
- PlotIsContainerForStorage → PlotWithStorageContainer
- PlotIsContainerForNpc → PlotWithNpcContainer
- Pattern: `[Subject]With[Capability]`

**Phase 3: Abstrakte Klassen & Package-Struktur** ✅
- PlotAction → AbstractPlotAction (Prefix-Pattern)
- AbstractPlotBase erstellt (Immutable Plot-Basis)
- AbstractPlotClaimed erstellt (Mutable Owner-ID)
- TradeguildPlot als Referenz-Implementierung
- impl/-Package für Implementierungen etabliert

**Phase 4: Invokable-Pattern** ✅
- Invokable Basis-Interface (Marker)
- InvokableByCommand (Command-Invokation)
- InvokableByGuiButton (GUI-Button-Invokation)
- CommandInvoker Datenklasse mit Builder
- AbstractPlotAction implements InvokableByGuiButton
- execute() → invokeByGuiButton() umbenannt

### Erkenntnisse Sprint 2

**✅ Erfolgreiche Pattern:**
- Prefix/Suffix-Pattern macht Hierarchie erkennbar
- impl/-Package trennt sauber Contracts von Implementierungen
- Invokable-Pattern besser als generisches execute()
- Multi-Invokation ermöglicht flexible Action-Nutzung

**🎯 Erreichte Ziele:**
- Einheitliche Naming Conventions etabliert
- Package-Struktur reorganisiert
- Type-safe Invokation implementiert
- TradeguildPlot als vollständige Referenz-Implementierung

---

**Stand:** Sprint 2 abgeschlossen (2025-11-19)
**Ziel:** Perfekte Architektur-Foundation ✅
**Philosophie:** Explizite Namen > Kurze Namen ✅
**Fokus:** Design vor Features ✅

---

## Sprint 3: Command-System & Feature-Implementierung (geplant)

**Ziel:** Funktionsfähiges Command-System und erste konkrete Features

### Geplante Phasen

**Phase 1: Command-System** 
- CommandManager für automatische Command-Registrierung
- CommandExecutor-Integration mit InvokableByCommand
- Subcommand-Routing-System
- Permission-Handling

**Phase 2: Konkrete PlotActions**
- PlotActionClaim (Plot beanspruchen)
- PlotActionOpenStorage (Lager öffnen)
- PlotActionTeleport (Zu Plot teleportieren)
- PlotActionSetStoragePrice (Lager-Preis setzen)
- PlotActionSpawnNpc (NPC spawnen)

**Phase 3: Persistenz-Layer**
- PlotManager für Plot-Verwaltung
- Plot-Datenbank-Schema
- Plot-Serialisierung/Deserialisierung
- Auto-Save-System

**Phase 4: Provider-Implementierungen**
- VaultEconomyProvider (module-vault)
- CitizensNpcProvider (module-citizens)
- TownyPlotProvider (module-towny)
- MMOItemsProvider (module-mmoitems)

**Phase 5: Event-System**
- PlotClaimEvent
- PlotOwnerChangeEvent
- PlotDeleteEvent
- Event-Handler für Cross-Plugin-Integration

### Priorisierung

**Kritisch (Must-Have):**
- Command-System (ohne Commands ist Plugin nicht nutzbar)
- PlotActionClaim (Basis-Funktionalität)
- PlotManager (für Plot-Verwaltung)

**Hoch (Should-Have):**
- PlotActionTeleport (wichtig für UX)
- PlotActionOpenStorage (Kern-Feature)
- Persistenz-Layer (für Daten-Speicherung)

**Medium (Nice-to-Have):**
- PlotActionSetStoragePrice (Wirtschafts-Feature)
- Provider-Implementierungen (optionale Module)
- Event-System (für Erweiterbarkeit)

**Niedrig (Future):**
- PlotActionSpawnNpc (fortgeschrittenes Feature)
- Komplexe GUI-Hierarchien
- Admin-Commands

### Architektur-Fokus Sprint 3

**Command-Pattern Completion:**
```java
// InvokableByCommand vollständig nutzen
class PlotActionClaim extends AbstractPlotAction
    implements InvokableByCommand {

    @Override
    public CommandInvoker getCommandInvoker() {
        return CommandInvoker.builder()
            .command("plot")
            .subcommand("claim")
            .description("Beansprucht einen Plot")
            .build();
    }

    @Override
    public void invokeByCommand(Player player, String[] args) {
        // Command-Logik
    }

    @Override
    public void invokeByGuiButton(Player player) {
        // GUI-Logik
    }
}
```

**Manager-Pattern:**
```java
class PlotManager {
    private final Map<UUID, Plot> plotCache = new HashMap<>();
    private final PlotRepository repository;

    Plot createPlot(PlotType type, UUID ownerId, Location location) {
        Plot plot = PlotFactory.create(type, ownerId, location);
        plotCache.put(plot.getId(), plot);
        repository.save(plot);
        return plot;
    }

    Plot getPlot(UUID plotId) {
        return plotCache.computeIfAbsent(plotId, repository::load);
    }
}
```

### Nächste Schritte

1. **Command-System Proof-of-Concept**
   - Einfacher CommandManager
   - Eine Action mit InvokableByCommand
   - Bukkit-Integration testen

2. **PlotActionClaim implementieren**
   - Erste vollständige Command+GUI Action
   - Mit Permissions
   - Mit Feedback-Messages

3. **PlotManager Grundgerüst**
   - In-Memory Plot-Verwaltung
   - CRUD-Operationen
   - Später: Persistenz hinzufügen

---

**Sprint 3 Status:** Geplant
**Start:** Nach Sprint 2 Abschluss
**Fokus:** Von Design zu Funktionalität
