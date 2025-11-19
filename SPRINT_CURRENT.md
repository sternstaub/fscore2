# Sprint 20: Core-Foundation & Architektur-Setup

**Ziel:** Grundlegende Architektur-Komponenten implementieren und Maven-Projekt initialisieren

**Status:** Phase 1 abgeschlossen ✅
**Start:** 2025-11-19
**Letzte Aktualisierung:** 2025-11-19 05:05
**Verantwortlich:** Claude (AI-Assistant)

---

## 🎯 Sprint-Ziele

1. Maven Multi-Module Projekt-Struktur aufsetzen
2. Core-Interfaces implementieren (GuiRenderable, PlotAction, MenuAction)
3. Basis-Klassen mit vollständiger Test-Abdeckung erstellen
4. Proof-of-Concept: Erste PlotAction mit GuiBuilder

---

## 📋 Phasen (Initial)

### Phase 1: Maven-Projekt-Struktur
**Ziel:** Multi-Module Maven-Projekt mit allen Modulen aufsetzen

**Status:** ✅ ABGESCHLOSSEN

**Aufgaben:**
- [x] Root `pom.xml` erstellen (Parent POM)
- [x] Module definieren:
  - [x] `core` - Core-Plugin mit Interfaces
  - [x] `module-towny` - Towny-Integration
  - [x] `module-citizens` - Citizens-Integration
  - [x] `module-vault` - Vault-Integration
  - [~] `module-mmoitems` - MMOItems-Integration (temporär deaktiviert - API nicht verfügbar)
- [x] Dependencies konfigurieren:
  - [x] Spigot/Paper API
  - [x] JUnit 5 (Jupiter)
  - [x] Mockito
  - [x] Vault API (über JitPack)
  - [x] Citizens API
  - [x] Towny API
- [x] Build-Konfiguration (Maven Compiler, Shade Plugin, Surefire)
- [x] Test: `mvn clean package` läuft durch ✅

**Erwartetes Ergebnis:**
```
fallenstar-core/
├── pom.xml (Parent)
├── core/
│   ├── pom.xml
│   └── src/
│       ├── main/java/
│       └── test/java/
├── module-plots/pom.xml
├── module-economy/pom.xml
├── module-items/pom.xml
└── module-npcs/pom.xml
```

---

### Phase 2: Core-Interfaces implementieren
**Ziel:** Basis-Interfaces für Self-Rendering Pattern

**Aufgaben:**
- [ ] `GuiRenderable` Interface erstellen
  ```java
  interface GuiRenderable {
      ItemStack getDisplayItem();
      boolean isVisible(Player player);
  }
  ```
- [ ] Unit Tests für GuiRenderable (Mock-Implementierungen)
- [ ] JavaDoc auf Deutsch
- [ ] Build-Test: `mvn clean test`

**Package:** `de.fallenstar.core.ui`

---

### Phase 3: PlotAction Basisklasse
**Ziel:** Command Pattern für Plot-Aktionen

**Aufgaben:**
- [ ] `PlotAction` abstrakte Klasse erstellen
  - [ ] `requiresOwnership()` Methode
  - [ ] `canExecute(Player)` Methode
  - [ ] `isOwner(Player)` Helper
  - [ ] `execute(Player)` abstrakt
  - [ ] `GuiRenderable` implementieren
- [ ] `PlotActionTest` mit Mockito
  - [ ] Test: Owner kann ausführen
  - [ ] Test: Nicht-Owner kann nicht ausführen (wenn requiresOwnership)
  - [ ] Test: isVisible gibt true zurück (Standard)
- [ ] JavaDoc auf Deutsch
- [ ] Build-Test: `mvn clean test`

**Package:** `de.fallenstar.core.plot.action`

---

### Phase 4: MenuAction Interface
**Ziel:** Hierarchische Menü-Unterstützung

**Aufgaben:**
- [ ] `MenuAction` Interface erstellen
  ```java
  interface MenuAction {
      List<GuiRenderable> getSubActions();
  }
  ```
- [ ] Test-Implementierung mit Mock-PlotAction
- [ ] Unit Tests
- [ ] JavaDoc auf Deutsch
- [ ] Build-Test: `mvn clean test`

**Package:** `de.fallenstar.core.ui`

---

### Phase 5: GuiBuilder Utility
**Ziel:** Universeller GUI-Generator aus Actions

**Aufgaben:**
- [ ] `GuiBuilder` Klasse erstellen
  - [ ] `buildFromActions(List<GuiRenderable>, Player, String)` Methode
  - [ ] Filtere unsichtbare Actions
  - [ ] Berechne GUI-Größe dynamisch (Vielfaches von 9)
  - [ ] Fülle Inventory mit Display-Items
- [ ] `GuiBuilderTest` mit Mock-Actions
  - [ ] Test: Unsichtbare Actions werden gefiltert
  - [ ] Test: GUI-Größe wird korrekt berechnet
  - [ ] Test: Items werden in richtiger Reihenfolge gesetzt
- [ ] JavaDoc auf Deutsch
- [ ] Build-Test: `mvn clean test`

**Package:** `de.fallenstar.core.ui`

---

### Phase 6: Plot Interface (Basis)
**Ziel:** Basis-Interface für alle Plot-Typen

**Aufgaben:**
- [ ] `Plot` Interface erstellen
  - [ ] `UUID getId()`
  - [ ] `UUID getOwnerId()`
  - [ ] `Location getLocation()`
  - [ ] `List<PlotAction> getAvailablePlotActions()`
- [ ] Mock-Implementierung für Tests
- [ ] Unit Tests
- [ ] JavaDoc auf Deutsch

**Package:** `de.fallenstar.core.plot`

---

### Phase 7: Trait-Interfaces definieren
**Ziel:** Kompositionsfähige Plot-Traits

**Aufgaben:**
- [ ] `NamedPlot` Interface
  ```java
  interface NamedPlot extends Plot {
      String getName();
      void setName(String name);
      default List<PlotAction> getNameActions() {
          return List.of(new PlotActionSetName(this));
      }
  }
  ```
- [ ] `StorageContainerPlot` Interface (Placeholder)
- [ ] `NpcContainerPlot` Interface (Placeholder)
- [ ] Unit Tests für Default-Implementierungen
- [ ] JavaDoc auf Deutsch

**Package:** `de.fallenstar.core.plot.trait`

---

### Phase 8: Proof-of-Concept - PlotActionSetName
**Ziel:** Erste konkrete PlotAction als Validierung

**Aufgaben:**
- [ ] `PlotActionSetName` Klasse erstellen
  - [ ] Extends `PlotAction`
  - [ ] Implementiere `execute(Player)` (Placeholder: Message senden)
  - [ ] Implementiere `getDisplayItem()` (NAME_TAG mit Lore)
  - [ ] `requiresOwnership()` returns true
- [ ] `PlotActionSetNameTest`
  - [ ] Test: Owner kann ausführen
  - [ ] Test: Nicht-Owner kann nicht ausführen
  - [ ] Test: DisplayItem hat korrekten Material-Typ
  - [ ] Test: DisplayItem hat DisplayName
- [ ] Integration-Test: GuiBuilder mit PlotActionSetName
- [ ] JavaDoc auf Deutsch
- [ ] Build-Test: `mvn clean test`

**Package:** `de.fallenstar.core.plot.action.impl`

---

### Phase 9: Integration-Test & Dokumentation
**Ziel:** Alles zusammenführen und dokumentieren

**Aufgaben:**
- [ ] Integration-Test: TradeguildPlot (Mock) mit mehreren Actions
- [ ] Integration-Test: GuiBuilder erstellt GUI aus Trait-Actions
- [ ] Coverage-Report generieren (`mvn clean verify`)
- [ ] Mindestens 80% Coverage sicherstellen
- [ ] `ERKENNTNISSE.md` updaten mit Sprint-Learnings
- [ ] `README.md` updaten mit Projekt-Status
- [ ] Final Build: `mvn clean package`

---

## 🔄 Anpassungen während des Sprints

_(Hier werden Änderungen an Phasen dokumentiert)_

### Änderung 1 (Datum: TBD)
- **Was:** TBD
- **Warum:** TBD
- **Auswirkung:** TBD

---

## 🧠 Erkenntnisse während des Sprints

_(Sammlung von Learnings während der Arbeit)_

### Erkenntnis 1
- **Bereich:** TBD
- **Problem/Entdeckung:** TBD
- **Lösung/Konsequenz:** TBD

---

## ✅ Definition of Done

Ein Sprint gilt als abgeschlossen, wenn:

- [ ] Alle Phasen sind abgeschlossen
- [ ] Alle Tests sind grün (`mvn clean test`)
- [ ] Build läuft durch (`mvn clean package`)
- [ ] Code Coverage mindestens 80%
- [ ] Alle Public APIs haben JavaDoc (Deutsch)
- [ ] Compiler-Fehler wurden behoben
- [ ] `ERKENNTNISSE.md` wurde aktualisiert
- [ ] `README.md` spiegelt aktuellen Stand wider
- [ ] Keine Anti-Patterns verwendet (Checklist erfüllt)

---

## 📊 Sprint-Metriken

| Metrik | Ziel | Aktuell | Status |
|--------|------|---------|--------|
| Phasen abgeschlossen | 9 | 0 | 🔴 |
| Unit Tests geschrieben | ~20 | 0 | 🔴 |
| Code Coverage | ≥80% | 0% | 🔴 |
| Build-Status | ✅ Grün | ⚪ Nicht gestartet | ⚪ |
| JavaDoc-Abdeckung | 100% (Public APIs) | 0% | 🔴 |

---

## 🚀 Nächster Sprint (Vorschau)

**Sprint 21: Plot-System Implementierung**
- Konkrete Plot-Implementierungen
- PlotManager & Persistenz
- Weitere PlotActions (Storage, Teleport)
- Provider-Pattern für Towny-Integration

---

## 📝 Notizen

- Alle Code-Änderungen in `core/` Module während dieses Sprints
- `module-plots` wird vorbereitet, aber noch nicht implementiert
- Dependencies nur als `provided` Scope für optionale Plugins
- Mockito für alle Bukkit-API-Dependencies in Tests
