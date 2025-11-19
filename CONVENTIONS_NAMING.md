# Namenskonventionen

## Grundprinzip: Hierarchie-Erkennbarkeit

**Kern-Regel:** Die Vererbungshierarchie muss aus dem Klassennamen erkennbar sein.

### ✅ Korrekt

```
PlotAction
├── PlotActionSetName
├── PlotActionSetStorage
└── PlotActionTeleport
```

**Alphabetische Sortierung zeigt Hierarchie:**
- `PlotAction`
- `PlotActionSetName`
- `PlotActionSetStorage`

### ❌ Inkorrekt

```
SetNameAction
PlotAction
```

**Problem:** Die Hierarchie ist nicht erkennbar, wenn alphabetisch sortiert.

---

## Naming Patterns

### Interfaces & Traits

**Pattern:** Adjektiv oder Capability-Name

```java
interface Nameable { ... }
interface Priceable { ... }
interface StorageContainer { ... }
```

### Abstrakte Klassen

**Pattern:** Basis-Konzept ohne Suffix

```java
abstract class PlotAction { ... }
abstract class NpcBehavior { ... }
```

### Konkrete Implementierungen

**Pattern:** Basisklasse + spezifische Funktion

```java
class PlotActionSetName extends PlotAction { ... }
class NpcBehaviorPatrol extends NpcBehavior { ... }
```

### Manager & Provider

**Pattern:** Konzept + Rolle

```java
class PlotPriceManager { ... }
class EconomyProvider { ... }
class CurrencyRegistry { ... }
```

---

## Anti-Patterns

❌ **Generische Namen ohne Kontext**
```java
class Helper { ... }
class Util { ... }
```

❌ **Suffixe vor Basisklasse**
```java
class SetNameAction extends Action { ... }  // Falsch
class ActionSetName extends Action { ... }  // Richtig
```

❌ **Inkonsistente Präfixe**
```java
class PlotStorage { ... }
class StorageForPlots { ... }  // Inkonsistent
```

---

## Spezielle Regeln

### UI-Komponenten

**Self-Rendering Pattern:**
```java
interface GuiRenderable {
    ItemStack getDisplayItem();
    boolean isVisible(Player player);
}
```

Implementierungen sollten ihre Funktion beschreiben:
- `PlotActionSetName` (Action + Rendering)
- Nicht: `SetNameUi` (nur UI, keine Logik)

### Command Pattern

**Pattern:** Verb im Namen für ausführbare Aktionen
```java
class PlotActionTeleport { ... }
class PlotActionSetStorage { ... }
```

---

## Deutsche vs. Englische Namen

**Regel:** Code in Englisch, Dokumentation in Deutsch.

```java
// ✅ Richtig
class PlotAction {
    /** Führt die Aktion aus */
    void execute();
}

// ❌ Falsch
class PlotAktion {
    void ausfuehren();
}
```
