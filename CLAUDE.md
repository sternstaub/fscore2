# Claude Context: FallenStar Core

**Entrypoint für neue Claude-Sitzungen**

---

## 📋 Globale Kontext-Dateien

Lies IMMER diese Dateien zu Beginn einer neuen Sitzung:

1. **[README.md](README.md)** - Benutzer-Dokumentation (Installation, Features, Commands - IMMER aktuell halten!)
2. **[CONVENTIONS_NAMING.md](CONVENTIONS_NAMING.md)** - Namenskonventionen (Prefix/Suffix-Pattern, Package-Struktur)
3. **[CONVENTIONS_CODE.md](CONVENTIONS_CODE.md)** - SOLID-Prinzipien, Design Patterns, Anti-Patterns
4. **[ERKENNTNISSE.md](ERKENNTNISSE.md)** - Sprint-Learnings, Bug-Analysen, Evolution

---

## ⚡ Arbeitsweise & Methodik (IMMER berücksichtigen!)

### Sprint-basierte Entwicklung

**Grundprinzip:** Arbeit erfolgt in Sprints mit dynamischen Phasen.

#### Sprint-Struktur

```
Sprint N
├── Phase 1: [Initial definiert]
├── Phase 2: [Während Sprint erarbeitet]
├── Phase 3: [Kann sich ändern basierend auf Erkenntnissen]
└── Phase X: [Dynamisch hinzugefügt wenn nötig]
```

#### Regeln

1. **Zu Beginn eines Sprints:**
   - Definiere initiale Phasen basierend auf Zielen
   - Erstelle Sprint-Übersicht mit Phasen
   - Nutze TodoWrite für Phase-Tracking

2. **Während des Sprints:**
   - Phasen können sich ändern basierend auf Erkenntnissen
   - Neue Phasen können hinzugefügt werden
   - Bestehende Phasen können angepasst oder entfernt werden
   - Dokumentiere Änderungen und Gründe

3. **Am Ende eines Sprints:**
   - Dokumentiere Erkenntnisse in `ERKENNTNISSE.md`
   - Update `CLAUDE.md` wenn neue Patterns/Anti-Patterns entdeckt wurden
   - Bereite nächsten Sprint vor

#### Beispiel: Sprint-Ablauf

```markdown
## Sprint 1: Core-Foundation Etablierung

### Initiale Phasen (Start)
1. Maven Multi-Module Struktur
2. Provider-System mit Graceful Degradation
3. GuiRenderable Interface erstellen

### Angepasste Phasen (Während Sprint)
1. Maven Multi-Module Struktur ✅
2. Provider-System ✅
3. GuiRenderable Interface ✅
4. **NEU:** MenuAction Interface (Erkenntnis: Hierarchie nötig)
5. **NEU:** PlotAction Basisklasse
6. **NEU:** Proof-of-Concept (PlotActionSetName)

### Grund für Änderungen
- MenuAction wurde während Design als kritisch erkannt
- PlotAction als abstrakte Basis etabliert
- Proof-of-Concept validiert Architektur früher
```

#### Phasen-Management

**Wann Phasen ändern?**

- ✅ Neue Erkenntnisse erfordern andere Reihenfolge
- ✅ Abhängigkeiten werden während Arbeit erkannt
- ✅ Proof-of-Concepts zeigen besseren Weg
- ✅ Bugs/Probleme erfordern Refokussierung
- ❌ Nicht aus Bequemlichkeit überspringen

**Wie Phasen dokumentieren?**

```markdown
### Sprint-Log (in ERKENNTNISSE.md oder separate Datei)

#### Phase 1: GuiRenderable Interface ✅
- Implementiert in: `core/src/main/java/...`
- Dauer: ~30min
- Erkenntnisse: Interface gut, aber isVisible() braucht Kontext

#### Phase 2: PlotAction Basisklasse ✅
- Implementiert in: `core/src/main/java/...`
- Dauer: ~45min
- Erkenntnisse: requiresOwnership() Pattern funktioniert gut

#### Phase 3: MenuAction Interface ⚠️ NEU
- Grund: Hierarchische Menüs während Design erkannt
- Priorität: HOCH (blockiert GuiBuilder)
```

#### Sprint-Übergabe (Context-Wechsel)

**Wenn Sprint in neuer Claude-Sitzung fortgesetzt wird:**

1. Lese `CLAUDE.md` (dieser File)
2. Lese Sprint-Status aus `ERKENNTNISSE.md` oder separater `SPRINT_CURRENT.md`
3. Verstehe aktuelle Phase
4. Setze fort oder passe Phasen an

**Format für Sprint-Übergabe:**

```markdown
## Aktueller Sprint: Sprint 2

**Ziel:** Architektur-Refactoring (Naming & Package-Struktur)
**Status:** In Progress (Phase 2 von 8)
**Nächster Schritt:** Trait-Interfaces umbenennen

### Abgeschlossene Phasen
- [x] Phase 1: Conventions aktualisieren

### Aktuelle Phase
- [ ] Phase 2: Trait-Interfaces umbenennen ← HIER

### Geplante Phasen
- [ ] Phase 3: Abstrakte Klassen erstellen
- [ ] Phase 4: Package-Struktur etablieren
- [ ] ...

### Erkenntnisse bisher
- Prefix/Suffix-Pattern etabliert
- Package-Struktur definiert (Root für Interfaces, impl/ für Klassen)
```

#### Test-Driven Development (PFLICHT)

**Nach JEDER Implementierung:**

1. **Unit Tests schreiben**
   - Nutze Maven/JUnit für alle Tests
   - Teste normale Fälle und Edge Cases
   - Mindestens 80% Code Coverage anstreben

2. **Build testen**
   ```bash
   mvn clean test
   mvn clean package
   ```
   - Bei Fehlern: Sofort fixen
   - Nie mit fehlendem Build committen

3. **Änderungen dokumentieren**
   - Dokumentation auf **Deutsch**
   - JavaDoc für Public APIs
   - Inline-Kommentare für komplexe Logik
   - Update relevanter Markdown-Dateien

4. **Compiler-Fehler selbstständig fixen**
   - Kleinere Fehler (Imports, Typos, etc.) sofort beheben
   - Bei größeren Problemen: User informieren
   - Immer Build validieren nach Fix

#### Implementierungs-Workflow

```
Phase: Komponente X implementieren
│
├─> 1. Code schreiben
├─> 2. Unit Tests schreiben (PFLICHT)
├─> 3. mvn clean test ausführen
├─> 4. Fehler fixen (falls vorhanden)
├─> 5. Dokumentation auf Deutsch aktualisieren
├─> 6. README.md mit neuem Feature aktualisieren (PFLICHT - siehe unten)
├─> 7. mvn clean package ausführen (Final-Check)
└─> 8. Phase als abgeschlossen markieren
```

#### Benutzer-Dokumentation (PFLICHT)

**Hohe Priorität:** Vollständige, prägnante Dokumentation ist ein Kern-Qualitätsmerkmal!

**README.md ist für Menschen, nicht für KI:**
- README.md richtet sich an **Endbenutzer und Server-Admins**
- Fokus auf Installation, Features, Commands, Konfiguration
- Entwicklungs-Details gehören in dedizierte Dateien (siehe Dokumentations-Struktur)

**Regel: Jedes neue Feature MUSS in README.md dokumentiert werden!**

**Was dokumentieren?**

1. **Neue Features → Sektion "Features"**
   ```markdown
   ### ✅ Implementiert (Sprint 21)
   - **Storage-Verwaltung** - Lager öffnen, Preise setzen
   ```

2. **Neue Commands → Sektion "Commands"**
   ```markdown
   /plot storage open <id>     - Öffnet Plot-Lager
   /plot storage price <preis> - Setzt Zugriffspreis
   ```

3. **Neue Konfigurationsoptionen → Sektion "Konfiguration"**
   ```yaml
   storage:
     default-price: 100
     max-items: 54
   ```

4. **Neue Module → Sektion "Module"**
   ```markdown
   | **module-xyz** | XYZ-Integration | [XYZ](link) | ✅ Aktiv |
   ```

**Wie dokumentieren?**

- **Vollständig:** Alle Parameter, alle Optionen, alle Beispiele
- **Prägnant:** Kurz und klar, keine Romane
- **Benutzerfreundlich:** Aus Sicht des Anwenders, nicht des Entwicklers
- **Aktuell:** Bei jedem Feature-Update sofort aktualisieren

**Beispiel für gute Feature-Dokumentation:**

```markdown
### ✅ Storage-Verwaltung

Jeder Plot kann ein Lager mit bis zu 54 Items haben.

**Features:**
- Lager öffnen per Command oder GUI
- Zugriffspreis festlegen (Economy-Integration)
- Automatisches Inventar-Management

**Commands:**
- `/plot storage open <id>` - Öffnet dein Plot-Lager
- `/plot storage price <preis>` - Setzt Zugriffspreis für andere Spieler
- `/plot storage share <player>` - Gibt Spieler Zugriff

**Konfiguration:**
```yaml
storage:
  enabled: true
  default-size: 54
  max-price: 10000
```

**Workflow-Ergänzung:**

Nach Implementierung eines Features:
1. Code + Tests schreiben ✅
2. **README.md aktualisieren** ← PFLICHT!
3. JavaDoc für APIs schreiben
4. ERKENNTNISSE.md für Entwicklungs-Details
5. Build + Commit

**Checkliste vor Commit:**
```
[ ] Feature funktioniert und ist getestet
[ ] README.md enthält Feature-Beschreibung
[ ] README.md enthält Commands (falls vorhanden)
[ ] README.md enthält Konfiguration (falls vorhanden)
[ ] Alle Beispiele funktionieren
```

#### Test-Konventionen

**Namensschema:**
```java
// Klasse: PlotAction.java
// Test:   PlotActionTest.java

// Methode: canExecute()
// Test:   testCanExecute_OwnerReturnsTrue()
//         testCanExecute_NonOwnerReturnsFalse()
```

**Test-Struktur:**
```java
@Test
void testMethodName_Condition_ExpectedResult() {
    // Arrange (Vorbereitung)
    PlotAction action = new PlotActionSetName(plot);
    Player owner = createMockPlayer(plot.getOwnerId());

    // Act (Ausführung)
    boolean result = action.canExecute(owner);

    // Assert (Validierung)
    assertTrue(result, "Owner sollte Aktion ausführen können");
}
```

**Mocking:**
- Nutze Mockito für externe Dependencies
- Mock Bukkit-APIs (Player, Inventory, etc.)
- Keine echten Server-Instanzen in Unit Tests

**Dokumentations-Template:**

```java
/**
 * Führt die Plot-Aktion aus.
 *
 * <p>Diese Methode wird aufgerufen, wenn ein Spieler die Aktion
 * über das GUI auswählt. Die Berechtigung wurde bereits durch
 * {@link #canExecute(Player)} geprüft.</p>
 *
 * @param player Der Spieler, der die Aktion ausführt
 * @throws IllegalStateException wenn die Aktion nicht ausführbar ist
 * @see #canExecute(Player)
 */
public abstract void execute(Player player);
```

---

## 🎯 Projekt-Essenz (Quick Reference)

### Was ist FallenStar Core?

Modulares Minecraft-Plugin-System mit Fokus auf:
- Plot-Management (Grundstücke mit verschiedenen Funktionen)
- Wirtschaftssystem (Handel, Preise, Währungen)
- NPC-Integration (Citizens-basierte Händler)
- Item-Management (Vanilla + MMOItems)

### Architektur-Prinzipien

```
✅ Trait-Komposition statt Vererbung
✅ Self-Rendering Pattern (keine UI-Klassen)
✅ Command Pattern (PlotAction als First-Class Objects)
✅ Universal Builder (ein GUI-System für alle Typen)
✅ Provider Pattern (Graceful Degradation für optionale Dependencies)
```

### Kritische Design-Regel

**VOR jeder Implementierung prüfen:**
```
[ ] Funktioniert universal (nicht typ-spezifisch)?
[ ] Erweiterbar ohne Code-Änderungen (Open/Closed)?
[ ] Nutzt Self-Rendering Pattern?
[ ] Keine instanceof-Checks?
[ ] Keine hart-kodierten Dependencies?
[ ] Keine Reflection (außer absolut unvermeidbar)?
[ ] README.md wird mit Feature aktualisiert? (PFLICHT!)
```

---

## ⚠️ Anti-Patterns (NIEMALS verwenden)

```
❌ Plot-spezifische UI-Klassen (TradeguildUi, StoragePlotUi)
❌ instanceof-Ketten statt Polymorphismus
❌ Reflection statt direkte Dependencies (siehe Beispiel unten)
❌ Datenspeicher-Mismatch (Single Source of Truth!)
❌ Generische Namen (Helper, Util, Manager ohne Kontext)
```

### Warum keine Reflection?

**❌ Falsch (Reflection):**
```java
// Fehleranfällig, keine Compile-Time-Sicherheit
Method method = plot.getClass().getMethod("getPrice");
double price = (double) method.invoke(plot);
```

**✅ Richtig (Interface):**
```java
// Type-safe, Compile-Time-geprüft
if (plot instanceof Priceable priceable) {
    double price = priceable.getPrice();
}
```

**Ausnahmen:** Nur wenn absolut unvermeidbar (z.B. Plugin-Loading, Serialization)

---

## 🏗️ Kern-Pattern (Immer verwenden)

### 1. GuiRenderable (Self-Rendering)

```java
interface GuiRenderable {
    ItemStack getDisplayItem();
    boolean isVisible(Player player);
}
```

### 2. PlotAction (Command Pattern)

```java
abstract class PlotAction implements GuiRenderable {
    boolean canExecute(Player player);
    void execute(Player player);
}
```

### 3. Trait-Komposition

```java
interface PlotNamed { List<PlotAction> getNameActions(); }
interface PlotContainerStorage { List<PlotAction> getStorageActions(); }

class TradeguildPlot implements PlotNamed, PlotContainerStorage {
    List<PlotAction> getAvailablePlotActions() {
        return Stream.of(getNameActions(), getStorageActions())
            .flatMap(List::stream).toList();
    }
}
```

### 4. GuiBuilder (Universal)

```java
GuiBuilder.buildFromActions(plot.getAvailablePlotActions(), player);
```

---

## 📚 Dokumentations-Struktur

| Datei | Zweck | Zielgruppe | Wann aktualisieren? |
|-------|-------|------------|---------------------|
| **CLAUDE.md** | Entrypoint, Quick Reference, Arbeitsweise | KI / Entwickler | Bei neuen Patterns/Regeln |
| **README.md** | Installation, Features, Commands, Konfiguration | Endbenutzer / Admins | **Bei JEDEM Feature** (PFLICHT!) |
| **CONVENTIONS_NAMING.md** | Naming-Regeln | KI / Entwickler | Bei neuen Konventionen |
| **CONVENTIONS_CODE.md** | Design Patterns, SOLID | KI / Entwickler | Bei neuen Patterns |
| **ERKENNTNISSE.md** | Sprint-Historie, Learnings, Bugs | KI / Entwickler | Am Ende jedes Sprints |
| **SPRINT_CURRENT.md** | Aktueller Sprint-Status (optional) | KI / Entwickler | Während Sprint |

---

## 🚀 Typische Aufgaben & Vorgehen

### Sprint starten

1. Definiere Sprint-Ziel (z.B. "Core-Interfaces implementieren")
2. Erstelle initiale Phasen-Liste
3. Nutze TodoWrite für Phase-Tracking
4. Optional: Erstelle `SPRINT_CURRENT.md` für Sprint-Status
5. Beginne mit Phase 1

### Sprint fortsetzen (nach Context-Wechsel)

1. Lese `CLAUDE.md` (Arbeitsweise-Sektion)
2. Lese `SPRINT_CURRENT.md` oder entsprechende Sektion in `ERKENNTNISSE.md`
3. Verstehe aktuelle Phase und Erkenntnisse
4. Passe Phasen an wenn nötig
5. Setze Arbeit fort

### Neue PlotAction implementieren

1. Lese: `CONVENTIONS_NAMING.md` (Naming-Pattern)
2. Lese: `CONVENTIONS_CODE.md` (Command Pattern)
3. Erstelle: `PlotAction[FunctionName]` extends `PlotAction`
4. Implementiere: `canExecute()`, `execute()`, `getDisplayItem()`
5. Prüfe: Design-Checkliste erfüllt?

### Neues Trait-Interface hinzufügen

1. Lese: `ERKENNTNISSE.md` (Trait-Pattern Sektion)
2. Erstelle Interface mit `get[X]Actions()` Methode
3. Implementiere Default-Implementierung
4. Füge zu Plot-Klasse hinzu via `implements`

### Bug-Fix durchführen

1. Lese: `ERKENNTNISSE.md` (bekannte Bugs & Lösungen)
2. Prüfe: Single Source of Truth Regel
3. Prüfe: Keine Datenspeicher-Mismatch
4. Dokumentiere Lösung in `ERKENNTNISSE.md`

---

## 🧠 Wichtige Erkenntnisse (Highlights)

### Storage-Price-Loop Bug (Sprint 26)

**Problem:** UI speichert in `EconomyProvider`, NPC liest aus `PlotPriceManager`
**Lösung:** Single Source of Truth → beide nutzen `EconomyProvider`
**Regel:** Wenn zwei Komponenten gleiche Daten nutzen → EINE Quelle!

### UI-Explosion (Sprint 18)

**Problem:** Eine UI-Klasse pro Plot-Typ (`TradeguildUi`, `StoragePlotUi`)
**Lösung:** `GuiBuilder` + `PlotAction` → universelles System
**Regel:** Self-Rendering > separate UI-Klassen

### MenuAction Missverständnis

**WICHTIG:** `MenuAction` ist ein **Interface**, keine Klasse!

```java
// ✅ Richtig
class PlotActionManageStorage extends PlotAction implements MenuAction

// ❌ Falsch
class MenuAction extends PlotAction
```

---

## 📝 Sprach-Konvention

- **Code:** Englisch (Klassen, Methoden, Variablen)
- **Dokumentation:** Deutsch (JavaDoc, README, Markdown)
- **Kommentare:** Deutsch (inline comments)

---

## 🔄 Projekt-Status

**Aktueller Sprint:** Sprint 3 - Command-System & Core-Actions (in Vorbereitung)
**Letzter abgeschlossener Sprint:** Sprint 2 - Architektur-Refactoring

**Sprint 1 Achievements:**
- ✅ Maven Multi-Module Struktur
- ✅ Provider-System mit Graceful Degradation
- ✅ Self-Rendering Pattern (GuiRenderable)
- ✅ Command Pattern (PlotAction)
- ✅ Trait-Komposition (PlotWithName, PlotWithStorageContainer, PlotWithNpcContainer)
- ✅ Universal GuiBuilder
- ✅ Proof-of-Concept (PlotActionSetName)

**Sprint 2 Achievements:**
- ✅ Naming Conventions etabliert
  - Prefix-Pattern: `Abstract[Name]` für abstrakte Klassen
  - Suffix-Pattern: `[Name][Type]` für konkrete Klassen
  - Trait-Pattern: `[Subject]With[Capability]` für Trait-Interfaces
  - Invokable-Pattern: `InvokableBy[Mechanism]` für Invokable-Interfaces
- ✅ Package-Struktur etabliert (Root für Interfaces, `impl/` für Implementierungen)
- ✅ Invokable-Pattern implementiert
  - `Invokable` Marker-Interface als Basis
  - `InvokableByCommand` mit `CommandInvoker` für Command-Metadaten
  - `InvokableByGuiButton` implementiert in `AbstractPlotAction`
  - Type-safe Invokation (keine generischen execute()-Methoden)
- ✅ Plot-Hierarchie etabliert
  - `AbstractPlotBase` (immutable) als Basis
  - `AbstractPlotClaimed` (mutable owner) für transferierbare Plots
  - `TradeguildPlot` als vollständige Reference-Implementierung
- ✅ Alle bestehenden Klassen migriert (Traits umbenannt, Package-Struktur angepasst)

**Sprint 3 Roadmap:**
- **Phase 1:** Command-System (CommandManager mit Auto-Registration)
- **Phase 2:** Konkrete PlotActions (Claim, Teleport, OpenStorage, SetPrice)
- **Phase 3:** Persistenz-Layer (PlotManager, H2/SQLite)
- **Phase 4:** Provider-Implementierungen (TownyProvider, VaultProvider, CitizensProvider)
- **Phase 5:** Event-System (PlotClaimEvent, PlotTransferEvent)

**Sprint 4+ Geplant:** NPC-Management, Web-API, Erweiterte Wirtschafts-Features

---

**Bei Unklarheiten:** Lese die referenzierten Dateien oder frage nach spezifischen Aspekten.
