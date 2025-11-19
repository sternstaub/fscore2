# Namenskonventionen

## Grundprinzip: Hierarchie-Erkennbarkeit & Explizitheit

**Kern-Regeln:**
1. Die Vererbungshierarchie muss aus dem Klassennamen erkennbar sein
2. Explizite Namen > kurze Namen (Selbstdokumentation)
3. Prefix-Pattern für Abstrakte Typen (Interfaces, Abstrakte Klassen)
4. Suffix-Pattern für Konkrete Typen (Implementierungen)

---

## Pattern-Übersicht

| Typ | Pattern | Beispiel |
|-----|---------|----------|
| **Interface (Trait)** | Prefix: `[Subject]With[Capability]` | `PlotWithName`, `PlotWithStorageContainer` |
| **Interface (Invokable)** | Prefix: `InvokableBy[Mechanism]` | `InvokableByCommand`, `InvokableByGuiButton` |
| **Abstrakte Klasse** | Prefix: `Abstract[Name]` | `AbstractPlotBase`, `AbstractPlotClaimed` |
| **Konkrete Klasse** | Suffix: `[Name][Type]` | `TradeguildPlot`, `PlotActionSetName` |
| **Enumeration** | Prefix + Plural: `Defined[Concept]s` | `DefinedPlotTypes`, `DefinedCurrencies` |

---

## Interfaces

### Trait-Interfaces (Optionale Fähigkeiten)

**Pattern:** `[Subject]With[Capability]`

**Philosophie:** Ein Trait beschreibt eine OPTIONALE Fähigkeit, die ein Objekt haben KANN.

```java
// ✅ Explizit und klar
interface PlotWithName {
    String getName();
    void setName(String name);
}

interface PlotWithStorageContainer {
    Inventory getStorageInventory();
}

interface PlotWithNpcContainer {
    UUID getNpcId();
    void setNpcId(UUID npcId);
}

interface PlotWithOwner {
    UUID getOwnerId();
    void setOwnerId(UUID ownerId);
}
```

**Warum explizit?**
- `PlotWithStorageContainer` > `PlotWithStorage` (klar: es ist ein Container)
- `PlotWithNpcContainer` > `PlotWithNpc` (klar: enthält NPCs)
- Selbstdokumentierend, kein Raten nötig

**Alphabetische Sortierung zeigt verwandte Traits:**
```
PlotWithName
PlotWithNpcContainer
PlotWithOwner
PlotWithStorageContainer
```

### Invokable-Interfaces (Aufruf-Mechanismen)

**Pattern:** `InvokableBy[Mechanism]`

```java
// ✅ Klar: Über welchen Mechanismus wird invoked?
interface InvokableByCommand {
    CommandInvoker getCommandInvoker();
    void invokeByCommand(Player player, String[] args);
}

interface InvokableByGuiButton {
    GuiButton getGuiButtonInvoker();
    void invokeByGuiButton(Player player);
}

interface InvokableByNpc {
    NpcInvoker getNpcInvoker();
    void invokeByNpc(Player player);
}
```

**Anti-Pattern:**
```java
// ❌ Invertierte Hierarchie
interface CommandInvokable { ... }  // Liest sich falsch
interface Commandable { ... }       // Zu generisch
```

---

## Abstrakte Klassen

**Pattern:** `Abstract[Name]`

**Philosophie:** Abstrakte Klassen sind Templates mit gemeinsamem State.

```java
// ✅ Präfix zeigt sofort: Dies ist abstrakt
abstract class AbstractPlotBase implements Plot {
    protected final UUID id;
    protected final Location location;
    protected final PlotType type;
}

abstract class AbstractPlotClaimed extends AbstractPlotBase implements PlotWithOwner {
    protected UUID ownerId;
}

abstract class AbstractPlotAction implements Invokable {
    protected final Plot plot;
}
```

**Hierarchie erkennbar:**
```
AbstractPlotBase
AbstractPlotClaimed (extends AbstractPlotBase)
AbstractPlotAction
```

**Anti-Pattern:**
```java
// ❌ Unklar ob abstract oder concrete
class PlotBase { ... }      // Ist das abstrakt?
class PlotAction { ... }    // Oder konkret?
```

---

## Konkrete Klassen

**Pattern:** `[Name][Type]` (Suffix-Pattern)

**Philosophie:** Konkrete Klassen beschreiben WAS sie sind (Suffix).

### Plot-Implementierungen

```java
// ✅ Suffix zeigt Plot-Typ
class TradeguildPlot extends AbstractPlotClaimed { ... }
class StoragePlot extends AbstractPlotClaimed { ... }
class FarmPlot extends AbstractPlotClaimed { ... }
class PlotUnclaimed extends AbstractPlotBase { ... }
```

**Alphabetische Sortierung gruppiert Plots:**
```
FarmPlot
PlotUnclaimed
StoragePlot
TradeguildPlot
```

### Action-Implementierungen

```java
// ✅ Hierarchie erkennbar
class PlotActionSetName extends AbstractPlotAction { ... }
class PlotActionClaim extends AbstractPlotAction { ... }
class PlotActionSell extends AbstractPlotAction { ... }
class PlotActionOpenStorage extends AbstractPlotAction { ... }
```

**Alphabetische Sortierung zeigt Hierarchie:**
```
PlotActionClaim
PlotActionOpenStorage
PlotActionSell
PlotActionSetName
```

**Anti-Pattern:**
```java
// ❌ Hierarchie nicht erkennbar
class SetNameAction extends PlotAction { ... }
class ClaimAction extends PlotAction { ... }

// Alphabetisch sortiert:
ClaimAction    // ← Wo ist die Basisklasse?
PlotAction
SetNameAction
```

---

## Enumerations

**Pattern:** `Defined[Concept]s` (Prefix + Plural)

**Philosophie:** Enums sind vordefinierte Wertelisten.

```java
// ✅ Klar: Dies ist eine Liste vordefinierter Plot-Typen
enum DefinedPlotTypes {
    UNCLAIMED("Unclaimed", Material.GRASS_BLOCK),
    TRADEGUILD("Handelsgilde", Material.GOLD_BLOCK),
    STORAGE("Lager", Material.CHEST),
    FARM("Farm", Material.WHEAT);

    private final String displayName;
    private final Material icon;
}

// ✅ Weitere Beispiele
enum DefinedCurrencies {
    TALER, GOLD_NUGGET, DIAMOND;
}

enum DefinedPermissions {
    PLOT_CLAIM("fallenstar.plot.claim"),
    PLOT_DELETE("fallenstar.plot.delete");
}
```

**Warum Prefix + Plural?**
1. **"Defined"** - zeigt: Dies sind vordefinierte Werte
2. **Plural** - zeigt: Dies ist eine Liste/Sammlung
3. **Vermeidet Verwechslung** mit Klassen:
   - `DefinedPlotTypes` (Enum) ≠ `TradeguildPlot` (Klasse)

**Anti-Pattern:**
```java
// ❌ Verwechslungsgefahr mit Klassen
enum PlotType { ... }
class PlotTypeTradeguild { ... }  // ← Verwechslung!

// ❌ Singular bei Listen
enum PlotType { ... }  // Eine Type-Liste, nicht ein Type
```

---

## Package-Struktur

### Universelles Pattern

```
[package]/
├── [Interfaces].java           # Public contracts (Prefix-Pattern)
└── impl/
    ├── Abstract[Base].java     # Abstrakte Klassen (Prefix-Pattern)
    └── [Concrete].java         # Konkrete Klassen (Suffix-Pattern)
```

### Beispiel: plot/

```
de.fallenstar.core.plot/
├── Plot.java                           # Interface
├── PlotWithName.java                   # Interface
├── PlotWithOwner.java                  # Interface
├── PlotWithStorageContainer.java       # Interface
├── PlotWithNpcContainer.java           # Interface
├── DefinedPlotTypes.java               # Enum
│
└── impl/
    ├── AbstractPlotBase.java           # Abstrakt
    ├── AbstractPlotClaimed.java        # Abstrakt
    ├── PlotUnclaimed.java              # Konkret
    ├── TradeguildPlot.java             # Konkret
    └── StoragePlot.java                # Konkret
```

### Beispiel: action/

```
de.fallenstar.core.action/
├── AbstractPlotAction.java             # Abstrakt (im Root!)
│
└── impl/
    ├── PlotActionSetName.java          # Konkret
    ├── PlotActionClaim.java            # Konkret
    └── PlotActionSell.java             # Konkret
```

**Regel:**
- **Interfaces** → Root-Package
- **Abstrakte Klassen** → Root-Package (wenn Basis) ODER impl/ (wenn spezialisiert)
- **Konkrete Klassen** → impl/
- **Enums** → Root-Package (sind Contracts)

---

## Methodennamen bei mehreren Invokation-Wegen

**Problem:** Wenn ein Objekt mehrere Invokation-Interfaces implementiert, muss klar sein, welcher Weg gemeint ist.

### ✅ Explizite Methodennamen

```java
class PlotActionSetName implements InvokableByCommand, InvokableByGuiButton {

    // ✅ Klar: Command-Invokation
    @Override
    public void invokeByCommand(Player player, String[] args) {
        // Command-Logik
    }

    // ✅ Klar: GUI-Invokation
    @Override
    public void invokeByGuiButton(Player player) {
        // GUI-Logik
    }
}
```

### ❌ Generische Methodennamen

```java
class PlotActionSetName implements InvokableByCommand, InvokableByGuiButton {

    // ❌ Unklar: Welcher Kontext?
    @Override
    public void execute(Player player) {
        // Wird das vom Command oder GUI aufgerufen?
    }

    // ❌ Unklar: Argumente fehlen für Command
    @Override
    public void execute(Player player, String[] args) {
        // Verwechslungsgefahr!
    }
}
```

**Regel:** Wenn mehrere Invokation-Mechanismen → explizite Methodennamen!

---

## Spezielle Regeln

### Manager & Provider

**Pattern:** `[Concept][Role]`

```java
// ✅ Klar: Rolle am Ende
class PlotManager { ... }
class EconomyProvider { ... }
class CurrencyRegistry { ... }

// ❌ Generisch
class PlotHelper { ... }
class PlotUtil { ... }
```

### Factory-Klassen

**Pattern:** `[Product]Factory`

```java
// ✅ Klar: Erstellt Plots
class PlotFactory {
    static AbstractPlotClaimed createClaimed(PlotType type, UUID ownerId) { ... }
}

// ✅ Klar: Erstellt Actions
class PlotActionFactory {
    static List<AbstractPlotAction> createActionsFor(Plot plot) { ... }
}
```

### Builder-Klassen

**Pattern:** `[Product]Builder`

```java
// ✅ Klar: Baut GUIs
class GuiBuilder {
    static Inventory buildFromActions(List<InvokableByGuiButton> actions) { ... }
}

// ✅ Klar: Baut CommandInvoker
class CommandInvoker {
    static class Builder {
        Builder commandName(String name) { ... }
        Builder description(String desc) { ... }
        CommandInvoker build() { ... }
    }
}
```

---

## Anti-Patterns

### ❌ Generische Namen ohne Kontext

```java
class Helper { ... }        // Helper wofür?
class Util { ... }          // Util wofür?
class Manager { ... }       // Manager wofür?
```

### ❌ Hierarchie nicht erkennbar

```java
// ❌ Basis-Klasse wird beim Sortieren getrennt
class SetNameAction extends Action { ... }
class ClaimAction extends Action { ... }

// Alphabetisch:
Action
ClaimAction
SetNameAction  // ← Weit weg von Action!
```

### ❌ Inkonsistente Präfixe/Suffixe

```java
// ❌ Mal Prefix, mal Suffix
class PlotStorage { ... }
class StorageForPlots { ... }
class NpcPlot { ... }
class PlotWithNpc { ... }
```

### ❌ Abstract-Suffix statt Prefix

```java
// ❌ Suffix macht Hierarchie unsichtbar
abstract class PlotAbstract { ... }
class PlotConcrete extends PlotAbstract { ... }

// Alphabetisch:
PlotAbstract
PlotConcrete  // ← Sieht aus wie gleiche Ebene!

// ✅ Prefix macht Hierarchie sichtbar
abstract class AbstractPlot { ... }
class TradeguildPlot extends AbstractPlot { ... }

// Alphabetisch:
AbstractPlot      // ← Klar: Basis
TradeguildPlot    // ← Klar: Subklasse
```

---

## Deutsche vs. Englische Namen

**Regel:** Code in Englisch, Dokumentation in Deutsch.

```java
// ✅ Richtig
class PlotActionSetName {
    /**
     * Führt die Aktion aus.
     *
     * @param player Der Spieler, der die Aktion ausführt
     */
    void invokeByCommand(Player player, String[] args) {
        // Inline-Kommentar auf Deutsch
    }
}

// ❌ Falsch
class PlotAktionNameSetzen {
    void ausfuehrenDurchBefehl(Spieler spieler, String[] argumente) {
        // ...
    }
}
```

---

## Zusammenfassung: Quick Reference

```java
// INTERFACES (Prefix-Pattern)
interface PlotWithName { ... }
interface InvokableByCommand { ... }

// ABSTRAKTE KLASSEN (Prefix-Pattern)
abstract class AbstractPlotBase { ... }
abstract class AbstractPlotClaimed { ... }

// KONKRETE KLASSEN (Suffix-Pattern)
class TradeguildPlot extends AbstractPlotClaimed { ... }
class PlotActionSetName extends AbstractPlotAction { ... }

// ENUMERATIONS (Prefix + Plural)
enum DefinedPlotTypes { UNCLAIMED, TRADEGUILD, STORAGE }
enum DefinedCurrencies { TALER, GOLD, DIAMOND }

// PACKAGE-STRUKTUR
plot/
├── PlotWithName.java              # Interface
├── DefinedPlotTypes.java          # Enum
└── impl/
    ├── AbstractPlotBase.java      # Abstrakt
    └── TradeguildPlot.java        # Konkret
```
