# Code-Konventionen

## SOLID-Prinzipien (Kern der Architektur)

### Single Responsibility Principle (SRP)

**Regel:** Jede Klasse hat genau eine Verantwortung.

```java
// ❌ Falsch: Logik + Rendering + Permissions
class StoragePriceUi {
    void setPrice() { ... }
    ItemStack getIcon() { ... }
    boolean hasPermission() { ... }
}

// ✅ Richtig: Separation of Concerns
class PlotActionSetPrice extends PlotAction {
    boolean canExecute(Player p) { ... }  // Permission
    void execute(Player p) { ... }         // Logik
    ItemStack getDisplayItem() { ... }     // Rendering
}
```

### Open/Closed Principle (OCP)

**Regel:** Erweiterbar ohne bestehenden Code zu ändern.

```java
// ❌ Falsch: Hard-coded Plot-Types
if (plot instanceof TradeguildPlot) {
    showTradeGui();
} else if (plot instanceof StoragePlot) {
    showStorageGui();
}

// ✅ Richtig: Trait-basiert
interface PlotWithActions {
    List<PlotAction> getAvailablePlotActions();
}
```

### Dependency Inversion Principle (DIP)

**Regel:** Abhängig von Abstraktionen, nicht Konkretionen.

```java
// ❌ Falsch: Hard-coded Currency
class EconomyModule {
    private GoldCoin coin = new GoldCoin();
}

// ✅ Richtig: Interface + Registry
interface CurrencyItem { ... }
class CurrencyRegistry {
    void register(CurrencyItem currency);
}
```

---

## Design Patterns

### Command Pattern (PlotAction)

**Zweck:** Aktionen als Objekte mit Logik, Permissions und Rendering.

```java
abstract class PlotAction implements GuiRenderable {
    protected final Plot plot;

    // Permission Check
    boolean requiresOwnership() { return true; }
    boolean canExecute(Player player) {
        return !requiresOwnership() || isOwner(player);
    }

    // Execution
    abstract void execute(Player player);

    // Rendering (Self-Rendering Pattern)
    abstract ItemStack getDisplayItem();
    boolean isVisible(Player player) { return true; }
}
```

### Trait-Pattern (Plot-Interfaces)

**Zweck:** Komposition statt Vererbung.

```java
interface NamedPlot {
    List<PlotAction> getNameActions();
}

interface StorageContainerPlot {
    List<PlotAction> getStorageActions();
}

interface NpcContainerPlot {
    List<PlotAction> getNpcActions();
}

// Konkrete Implementierung kombiniert Traits
class TradeguildPlot implements NamedPlot, StorageContainerPlot, NpcContainerPlot {
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

### Self-Rendering Pattern

**Zweck:** Objekte rendern sich selbst, keine separaten UI-Klassen.

```java
interface GuiRenderable {
    ItemStack getDisplayItem();
    boolean isVisible(Player player);
}

// ✅ Action ist selbst-rendernd
class PlotActionSetName extends PlotAction {
    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Material.NAME_TAG)
            .displayName("Plot umbenennen");
    }
}
```

### Builder Pattern (GuiBuilder)

**Zweck:** Automatische GUI-Generierung aus Action-Listen.

```java
class GuiBuilder {
    static Inventory buildFromActions(List<GuiRenderable> actions, Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "Aktionen");
        int slot = 0;
        for (GuiRenderable action : actions) {
            if (action.isVisible(player)) {
                gui.setItem(slot++, action.getDisplayItem());
            }
        }
        return gui;
    }
}
```

### Menu Pattern (Hierarchische UIs)

**Zweck:** Verschachtelte Menüs durch PlotAction-Komposition.

```java
interface MenuAction {
    List<GuiRenderable> getSubActions();
}

class PlotActionManageStorage extends PlotAction implements MenuAction {
    @Override
    public List<GuiRenderable> getSubActions() {
        return List.of(
            new PlotActionSetPrice(plot),
            new PlotActionOpenStorage(plot)
        );
    }
}
```

---

## Kritische Regeln (VOR jeder Implementierung)

### ✅ Checkliste

- [ ] Funktioniert universal (nicht plot-typ-spezifisch)?
- [ ] Erweiterbar ohne Code-Änderungen (OCP)?
- [ ] Nutzt Self-Rendering Pattern?
- [ ] Keine manuellen Type-Checks (`instanceof`)?
- [ ] Keine hart-kodierten Dependencies?
- [ ] Trait-basiert statt Vererbung?

---

## Anti-Patterns (Vermeiden!)

### ❌ Plot-spezifische UI-Klassen

```java
// Falsch
class TradeguildUi { ... }
class StoragePlotUi { ... }

// Richtig: Universelles System
GuiBuilder.buildFromActions(plot.getAvailablePlotActions(), player);
```

### ❌ instanceof-Ketten

```java
// Falsch
if (plot instanceof TradeguildPlot) { ... }
else if (plot instanceof StoragePlot) { ... }

// Richtig: Polymorphismus
plot.getAvailablePlotActions().forEach(action -> action.execute(player));
```

### ❌ Reflection statt Dependencies

```java
// Falsch
Method method = plot.getClass().getMethod("getPrice");
double price = (double) method.invoke(plot);

// Richtig: Interface
if (plot instanceof Priceable priceable) {
    double price = priceable.getPrice();
}
```

### ❌ Datenspeicher-Mismatch

```java
// Falsch: Verschiedene Datenquellen für gleiche Information
ui.savePrice(economyProvider);
npc.loadPrice(plotPriceManager);  // Mismatch!

// Richtig: Single Source of Truth
double price = economyProvider.getSellPrice(item);
```

---

## Modulare Architektur

### Provider-Pattern (Graceful Degradation)

**Zweck:** Optionale Dependencies ohne Hard-Coupling.

```java
// Core definiert Interface
interface EconomyProvider {
    double getBalance(Player player);
    void withdraw(Player player, double amount);
}

// Module implementiert mit optionaler Vault-Integration
class VaultEconomyProvider implements EconomyProvider {
    private final Economy vault;

    VaultEconomyProvider() {
        this.vault = Bukkit.getServer().getServicesManager()
            .getRegistration(Economy.class)?.getProvider();
    }

    @Override
    public double getBalance(Player player) {
        return vault != null ? vault.getBalance(player) : 0.0;
    }
}
```

### Dependency Injection

**Regel:** Konstruktor-Injection für erforderliche Dependencies.

```java
class PlotModule {
    private final EconomyProvider economy;
    private final PermissionProvider permissions;

    // ✅ Dependencies explizit deklariert
    public PlotModule(EconomyProvider economy, PermissionProvider permissions) {
        this.economy = economy;
        this.permissions = permissions;
    }
}
```

---

## Dokumentation

### JavaDoc für Public APIs

```java
/**
 * Führt eine Plot-Aktion aus.
 *
 * @param player Der Spieler, der die Aktion ausführt
 * @throws IllegalStateException wenn die Aktion nicht ausführbar ist
 * @see #canExecute(Player)
 */
public abstract void execute(Player player);
```

### Inline-Kommentare für komplexe Logik

```java
// Berechne dynamischen Preis basierend auf Angebot/Nachfrage
double dynamicPrice = basePrice * (1.0 + marketDemand - marketSupply);
```

---

## Testing-Strategie

### Unit Tests für Logik

```java
@Test
void testPlotActionRequiresOwnership() {
    PlotAction action = new PlotActionSetName(plot);
    assertFalse(action.canExecute(nonOwner));
    assertTrue(action.canExecute(owner));
}
```

### Integration Tests für Provider

```java
@Test
void testEconomyProviderWithoutVault() {
    EconomyProvider economy = new VaultEconomyProvider();
    assertEquals(0.0, economy.getBalance(player));  // Graceful degradation
}
```
