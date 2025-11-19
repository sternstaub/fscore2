# FallenStar Core

**Status:** ✅ Sprint 20 abgeschlossen (Core-Foundation etabliert)
**Sprache:** Deutsch (Dokumentation) / Englisch (Code)
**Plattform:** Minecraft Plugin (Spigot/Paper)
**Build:** ✅ SUCCESS (146 Tests, ~95% Coverage)

---

## Projekt-Status

**Sprint 20 (2025-11-19):** Core-Foundation & Architektur-Setup
- ✅ Maven Multi-Module Struktur
- ✅ Provider-System mit Graceful Degradation
- ✅ Self-Rendering Pattern (GuiRenderable)
- ✅ Command Pattern (PlotAction)
- ✅ Trait-Komposition (PlotNamed, PlotIsContainerForStorage, PlotIsContainerForNpc)
- ✅ Universal GuiBuilder
- ✅ Proof-of-Concept validiert (PlotActionSetName)
- ✅ 146 Unit & Integration Tests
- ✅ ~95% Code Coverage

**Nächste Schritte (Sprint 21+):**
- Konkrete PlotActions (Storage, NPC, Teleport)
- Persistenz-Layer (Plot-Manager, Datenbank)
- Provider-Implementierungen (Towny, Vault, Citizens)

---

## Projektübersicht

FallenStar Core ist ein modulares Minecraft-Plugin-System mit Fokus auf:

- **Plot-Management** (Grundstücke mit verschiedenen Funktionen)
- **Wirtschaftssystem** (Handel, Preise, Währungen)
- **NPC-Integration** (Citizens-Integration für Händler und Quests)
- **Item-Management** (Vanilla + MMOItems-Support)

---

## Architektur-Philosophie

### Modularer Aufbau

```
core/              - Core-Plugin mit Interfaces und UI-Framework
                     (Plot-, Economy-, UI-, NPC-Systeme)
module-towny/      - Towny-Integration (Städte/Towns)
module-citizens/   - Citizens-Integration (NPC-Spawning)
module-vault/      - Vault-Integration (Economy-Provider)
module-mmoitems/   - MMOItems-Integration (Custom Items) [temporär deaktiviert]
```

### Provider-Pattern (Graceful Degradation)

**Core = Framework, Module = Integrationen**

Das Core-Plugin enthält alle Basis-Systeme (Plot, Economy, UI, NPC).
Die Module sind optionale Adapter für externe Plugins:

- **module-towny** - Verbindet Towny-Städte mit FallenStar Plot-System
- **module-vault** - Nutzt Vault für Economy-Transaktionen
- **module-citizens** - Spawnt NPCs über Citizens
- **module-mmoitems** - Integriert MMOItems Custom Items *(aktuell deaktiviert)*

Wenn ein Modul fehlt, läuft das Core-System mit NoOp-Fallback weiter (Graceful Degradation).

---

## Kern-Prinzipien

### 1. Trait-basierte Komposition

```java
interface PlotNamed { ... }
interface PlotIsContainerForStorage { ... }
interface PlotIsContainerForNpc { ... }

class TradeguildPlot implements PlotNamed, PlotIsContainerForStorage, PlotIsContainerForNpc {
    // Kombiniert alle Traits
    List<PlotAction> getAvailablePlotActions() {
        return Stream.of(
            getNameActions(),      // PlotNamed
            getStorageActions(),   // PlotIsContainerForStorage
            getNpcActions()        // PlotIsContainerForNpc
        ).flatMap(List::stream).toList();
    }
}
```

### 2. Self-Rendering Pattern

Objekte rendern sich selbst, keine separaten UI-Klassen:

```java
class PlotAction implements GuiRenderable {
    ItemStack getDisplayItem() { ... }
    void execute(Player player) { ... }
}
```

### 3. Command Pattern

Aktionen als First-Class Objects:

```java
abstract class PlotAction {
    boolean canExecute(Player player);
    void execute(Player player);
    ItemStack getDisplayItem();
}
```

### 4. Universal Builder

Ein GUI-System für alle Plot-Typen:

```java
GuiBuilder.buildFromActions(plot.getAvailablePlotActions(), player);
```

---

## Design-Erkenntnisse

Dieses Projekt wurde ursprünglich in sehr kurzer Zeit AI-generiert und war inkonsistent. Die folgenden Erkenntnisse wurden während des Refactorings gewonnen:

### ✅ Was funktioniert

- **Trait-Komposition** statt tiefer Vererbungshierarchien
- **Self-Rendering Pattern** eliminiert UI-Klassen-Explosion
- **GuiBuilder** ermöglicht universelle, erweiterbare UIs
- **MenuAction-Interface** für hierarchische Menüs

### ❌ Vermiedene Anti-Patterns

- Plot-spezifische UI-Klassen (`TradeguildUi`, `StoragePlotUi`)
- `instanceof`-Ketten statt Polymorphismus
- Reflection statt direkte Dependencies
- Datenspeicher-Mismatch (verschiedene Quellen für gleiche Daten)

### 🔄 Refactoring-Schwerpunkte

| Bereich | Problem | Lösung |
|---------|---------|--------|
| **Items** | CoinProvider hart-kodiert | CurrencyItem Interface + Registry |
| **Plots** | Preis-Logik nur in PlotContainerStorage | Priceable Interface + Manager |
| **NPCs** | Manuelle UI-Konstruktion | NpcAction + GuiBuilder |
| **Economy** | TradeUI nicht GuiRenderable-konform | TradeAction mit Self-Rendering |

---

## Entwicklungs-Konventionen

### Vor JEDER Implementierung prüfen

- [ ] Funktioniert universal (nicht typ-spezifisch)?
- [ ] Erweiterbar ohne Code-Änderungen (Open/Closed Principle)?
- [ ] Nutzt Self-Rendering Pattern?
- [ ] Keine `instanceof`-Checks?
- [ ] Keine hart-kodierten Dependencies?

### Namenskonventionen

**Hierarchie muss erkennbar sein:**

```
✅ PlotAction → PlotActionSetName
❌ SetNameAction → PlotAction
```

Siehe: [CONVENTIONS_NAMING.md](CONVENTIONS_NAMING.md)

### Code-Prinzipien

- **SOLID-Prinzipien** konsequent anwenden
- **Design Patterns** für wiederkehrende Probleme
- **Keine Reflection** außer absolut notwendig
- **Single Source of Truth** für Daten

Siehe: [CONVENTIONS_CODE.md](CONVENTIONS_CODE.md)

---

## Historie & Kontext

### Ursprung

Das Projekt wurde aus einem AI-generierten Prototyp ("fs-core-sample-dump") extrahiert, der folgende Probleme hatte:

- **Inkonsistente Architektur** durch iteratives Design
- **Zersplitterte Konzepte** (Preis-Logik an mehreren Stellen)
- **UI-Klassen-Explosion** (eine Klasse pro Plot-Typ)
- **Datenspeicher-Mismatch** (Storage-Price-Loop-Bug)

### Sprint 18 Durchbruch

Die Einführung des **GuiBuilder + PlotAction + Trait-Pattern** löste die Architektur-Probleme:

- Ein System für alle Plot-Typs
- Erweiterbar ohne Core-Änderungen
- Self-Documenting durch naming conventions

### Neuinitialisierung (Aktuell)

Dieses Repository ist eine **saubere Neuinitialisierung** mit:

- Nur den bewährten Design-Erkenntnissen
- Ohne Legacy-Code-Ballast
- Fokus auf klare Architektur von Anfang an

---

## Nächste Schritte

1. **Core-Module strukturieren** (Interfaces, Base-Klassen)
2. **Plot-System implementieren** (Trait-basiert)
3. **Economy-Module** (Provider-Pattern)
4. **NPC-Integration** (GuiRenderable-konform)
5. **Item-System** (CurrencyRegistry)

---

## Dokumentation

- **[CONVENTIONS_NAMING.md](CONVENTIONS_NAMING.md)** - Namenskonventionen
- **[CONVENTIONS_CODE.md](CONVENTIONS_CODE.md)** - Code-Prinzipien & Patterns
- **[ERKENNTNISSE.md](ERKENNTNISSE.md)** - Sprint-Learnings & Evolution

---

## Lizenz

*TBD*

---

## Kontakt

*TBD*
