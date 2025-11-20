# FallenStar Core

Ein modulares Minecraft-Plugin-System für Plot-Management, Wirtschaft, NPCs und Item-Verwaltung.

**Plattform:** Spigot/Paper 1.20+
**Status:** 🚧 In Entwicklung (Sprint 2 abgeschlossen, Sprint 3 in Vorbereitung)
**Build:** ✅ SUCCESS (146 Tests, ~95% Coverage)

---

## 📋 Inhaltsverzeichnis

- [Über das Projekt](#über-das-projekt)
- [Installation](#installation)
- [Features](#features)
- [Commands](#commands)
- [Konfiguration](#konfiguration)
- [Module](#module)
- [Entwicklung](#entwicklung)
- [Dokumentation](#dokumentation)
- [Lizenz](#lizenz)

---

## Über das Projekt

FallenStar Core ist ein flexibles Plugin-System für Minecraft-Server, das folgende Bereiche abdeckt:

- **Plot-Management** - Verwaltung von Grundstücken mit verschiedenen Funktionen (Handelsgilden, Lager, etc.)
- **Wirtschaftssystem** - Handel, Preise und Währungsverwaltung
- **NPC-Integration** - Citizens-basierte NPCs für Händler und Quests
- **Item-Management** - Unterstützung für Vanilla-Items und MMOItems

Das System ist modular aufgebaut: Das Core-Plugin stellt die Basis-Funktionalität bereit, während optionale Module Integrationen mit anderen Plugins (Towny, Vault, Citizens) ermöglichen.

---

## Installation

### Voraussetzungen

- **Minecraft Server:** Spigot/Paper 1.20 oder höher
- **Java:** 17 oder höher
- **Maven:** 3.8+ (nur für Build aus Quellcode)

### Server-Installation

1. **Plugin herunterladen**
   ```bash
   # Download der neuesten Release (noch nicht verfügbar)
   # Oder Build aus Quellcode (siehe unten)
   ```

2. **In plugins-Ordner kopieren**
   ```bash
   cp fallenstar-core-1.0.0.jar /path/to/server/plugins/
   ```

3. **Server (neu)starten**
   ```bash
   java -jar spigot.jar
   ```

4. **Optional: Module installieren**
   ```bash
   # Für Towny-Integration
   cp module-towny-1.0.0.jar /path/to/server/plugins/

   # Für Vault-Integration
   cp module-vault-1.0.0.jar /path/to/server/plugins/

   # Für Citizens-Integration
   cp module-citizens-1.0.0.jar /path/to/server/plugins/
   ```

### Build aus Quellcode

```bash
# Repository klonen
git clone https://github.com/[USERNAME]/fallenstar-core.git
cd fallenstar-core

# Build mit Maven
mvn clean package

# JAR-Dateien befinden sich in:
# - core/target/fallenstar-core-1.0.0.jar
# - module-*/target/module-*.jar
```

---

## Features

### ✅ Implementiert (Sprint 1)

#### Core-Framework
- **Provider-System** - Graceful Degradation für optionale Dependencies
- **Universal GUI-System** - Dynamische Inventar-GUIs für alle Plot-Typen
- **Plot-Aktionen** - Erweiterbare Command-Pattern-Implementierung
- **Trait-Komposition** - Flexible Plot-Funktionalität durch Trait-Interfaces

#### Plot-Typen (Basis)
- **PlotWithName** - Plots mit Namen-Verwaltung
- **PlotWithStorageContainer** - Plots mit Lager-Funktion
- **PlotWithNpcContainer** - Plots mit NPC-Verwaltung

#### Verfügbare Plot-Aktionen
- **Namen ändern** - Owner können Plot-Namen anpassen

### ✅ Implementiert (Sprint 2)

#### Architektur-Refactoring
- **Naming Conventions** - Einheitliche Prefix/Suffix-Pattern für alle Klassen
  - Abstract-Klassen: `Abstract[Name]` (z.B. `AbstractPlotAction`)
  - Concrete-Klassen: `[Name][Type]` (z.B. `PlotActionSetName`)
  - Trait-Interfaces: `[Subject]With[Capability]` (z.B. `PlotWithName`)
  - Invokable-Interfaces: `InvokableBy[Mechanism]` (z.B. `InvokableByCommand`)
- **Package-Struktur** - Root für Interfaces, `impl/` für Implementierungen
  - `de.fallenstar.core.plot` - Plot-Interfaces
  - `de.fallenstar.core.plot.impl` - Konkrete Plot-Klassen
  - `de.fallenstar.core.plot.action` - Action-Interfaces
  - `de.fallenstar.core.plot.action.impl` - Konkrete Actions

#### Invokable-Pattern
- **Multi-Invokation** - Objekte können auf verschiedene Arten aufgerufen werden
  - `InvokableByCommand` - Aufruf via Minecraft-Command
  - `InvokableByGuiButton` - Aufruf via GUI-Click
  - Type-safe Invokation (keine generischen execute()-Methoden)
- **CommandInvoker** - Metadaten für Command-Registrierung
- **AbstractPlotAction** - Implementiert `InvokableByGuiButton` als Basis

#### Plot-Hierarchie
- **AbstractPlotBase** - Immutable Basis mit ID, Owner, Location
- **AbstractPlotClaimed** - Mutable Owner (Plot-Transfers)
- **TradeguildPlot** - Reference-Implementierung mit Trait-Komposition

### 📅 Geplant (Sprint 3)

#### Command-System
- **CommandManager** - Automatische Command-Registrierung aus InvokableByCommand
- **Tab-Completion** - Intelligente Command-Vervollständigung
- **Permission-System** - Integration mit Bukkit-Permissions

#### Konkrete PlotActions
- **PlotActionClaim** - Plots claimen/freigeben
- **PlotActionTeleport** - Zu Plots teleportieren
- **PlotActionOpenStorage** - Lager-Inventar öffnen (via Command + GUI)
- **PlotActionSetPrice** - Preise für Storage/Zugriff setzen

#### Persistenz-Layer
- **PlotManager** - Plot-Verwaltung (CRUD-Operationen)
- **Datenbank-Integration** - H2/SQLite für Plot-Daten
- **Auto-Save** - Periodisches Speichern aller Plots

### 📅 Geplant (Sprint 4+)

- **NPC-Verwaltung** - NPCs spawnen, entfernen, konfigurieren
- **Event-System** - PlotClaimEvent, PlotTransferEvent, etc.
- **Provider-Implementierungen** - Konkrete Towny/Vault/Citizens-Integrationen
- **Wirtschafts-Features** - Handels-Steuern, Shop-Verwaltung
- **Web-API** - REST-API für externe Tools

---

## Commands

> **Hinweis:** Commands werden in Sprint 21+ implementiert.

### Geplante Commands

```
/plot create <typ>        - Erstellt einen neuen Plot
/plot delete <id>         - Löscht einen Plot
/plot info <id>           - Zeigt Plot-Informationen
/plot list                - Listet eigene Plots
/plot manage <id>         - Öffnet Plot-Verwaltungs-GUI
/plot tp <id>             - Teleportiert zu einem Plot
```

---

## Konfiguration

> **Hinweis:** Konfiguration wird in Sprint 21+ implementiert.

### Geplante Konfiguration

```yaml
# config.yml (Beispiel)
plots:
  max-per-player: 5
  default-storage-size: 54

economy:
  enabled: true
  currency: "Taler"

npcs:
  enabled: true
  auto-despawn: true
```

---

## Module

FallenStar Core nutzt ein modulares System: Das Core-Plugin funktioniert eigenständig, Module erweitern die Funktionalität durch Integration mit anderen Plugins.

### Verfügbare Module

| Modul | Beschreibung | Abhängigkeit | Status |
|-------|--------------|--------------|--------|
| **core** | Basis-Framework mit Plot-, UI- und Event-Systemen | - | ✅ Aktiv |
| **module-towny** | Integration mit Towny für Stadt-Plot-Verknüpfung | [Towny](https://github.com/TownyAdvanced/Towny) | ✅ Aktiv |
| **module-vault** | Wirtschafts-Integration über Vault API | [Vault](https://github.com/MilkBowl/Vault) | ✅ Aktiv |
| **module-citizens** | NPC-Spawning über Citizens | [Citizens](https://github.com/CitizensDev/Citizens2) | ✅ Aktiv |
| **module-mmoitems** | Custom-Item-Integration | [MMOItems](https://github.com/Ssomar-Developement/MMOItems) | ⏸️ Deaktiviert |

### Module installieren

Module sind **optional**. Wenn ein Modul fehlt, läuft das Core-Plugin mit reduzierter Funktionalität weiter (Graceful Degradation).

**Beispiel:**
- Ohne `module-vault`: Economy-Features nutzen Fallback-Implementierung
- Ohne `module-citizens`: NPCs können nicht gespawnt werden (Funktion deaktiviert)
- Ohne `module-towny`: Plot-System funktioniert unabhängig von Towny

---

## Entwicklung

### Projekt-Status

**Aktueller Sprint:** Sprint 3 - Command-System & Core-Actions (in Vorbereitung)
**Letzter Sprint:** Sprint 2 - Architektur-Refactoring (abgeschlossen)

**Sprint 1 Achievements:**
- ✅ Maven Multi-Module Struktur
- ✅ Provider-System mit Graceful Degradation
- ✅ Self-Rendering Pattern (GuiRenderable)
- ✅ Command Pattern (PlotAction)
- ✅ Trait-Komposition (PlotWithName, PlotWithStorageContainer, PlotWithNpcContainer)
- ✅ Universal GuiBuilder
- ✅ Proof-of-Concept validiert (PlotActionSetName)

**Test-Metriken (Sprint 1):**
- Tests: 146/146 ✅
- Code Coverage: ~95%
- Build: SUCCESS

**Sprint 2 Achievements:**
- ✅ Naming Conventions etabliert (Prefix/Suffix-Pattern)
  - Abstract-Klassen: `Abstract[Name]`
  - Trait-Interfaces: `[Subject]With[Capability]`
  - Invokable-Interfaces: `InvokableBy[Mechanism]`
- ✅ Package-Struktur etabliert (Root für Interfaces, impl/ für Klassen)
- ✅ Invokable-Pattern implementiert
  - `Invokable` Marker-Interface
  - `InvokableByCommand` mit `CommandInvoker`
  - `InvokableByGuiButton` implementiert in `AbstractPlotAction`
- ✅ Plot-Hierarchie erstellt
  - `AbstractPlotBase` (immutable)
  - `AbstractPlotClaimed` (mutable owner)
  - `TradeguildPlot` als Reference-Implementierung
- ✅ Alle bestehenden Klassen migriert

**Sprint 3 Roadmap (geplant):**
- **Phase 1:** Command-System (CommandManager mit auto-registration)
- **Phase 2:** Konkrete PlotActions (Claim, Teleport, Storage, SetPrice)
- **Phase 3:** Persistenz-Layer (PlotManager, H2/SQLite)
- **Phase 4:** Provider-Implementierungen (Towny, Vault, Citizens)
- **Phase 5:** Event-System (PlotClaimEvent, PlotTransferEvent)

### Build & Tests

```bash
# Tests ausführen
mvn clean test

# Package mit allen Tests
mvn clean package

# Coverage-Report generieren
mvn clean verify
# Report: target/site/jacoco/index.html
```

### Architektur

Das Projekt folgt modernen Design-Prinzipien:

- **Trait-Komposition** statt Vererbung
- **Self-Rendering Pattern** für UI-Komponenten
- **Command Pattern** für Plot-Aktionen
- **Provider Pattern** für optionale Dependencies
- **SOLID-Prinzipien** konsequent angewendet

Details zur Architektur: [CONVENTIONS_CODE.md](CONVENTIONS_CODE.md)

---

## Dokumentation

### Für Benutzer
- **[README.md](README.md)** (diese Datei) - Installation, Features, Commands

### Für Entwickler
- **[CLAUDE.md](CLAUDE.md)** - KI-Kontext & Arbeitsweise
- **[CONVENTIONS_NAMING.md](CONVENTIONS_NAMING.md)** - Namenskonventionen
- **[CONVENTIONS_CODE.md](CONVENTIONS_CODE.md)** - Code-Prinzipien & Design Patterns
- **[ERKENNTNISSE.md](ERKENNTNISSE.md)** - Sprint-Learnings & Architektur-Evolution

### API-Dokumentation

```bash
# JavaDoc generieren
mvn javadoc:javadoc
# Output: target/site/apidocs/index.html
```

---

## Lizenz

*TBD*

---

## Kontakt & Support

*TBD*

---

## Credits

Entwickelt mit Unterstützung von [Claude Code](https://claude.com/claude-code).
