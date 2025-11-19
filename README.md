# FallenStar Core

Ein modulares Minecraft-Plugin-System für Plot-Management, Wirtschaft, NPCs und Item-Verwaltung.

**Plattform:** Spigot/Paper 1.20+
**Status:** 🚧 In Entwicklung (Sprint 1 abgeschlossen, Sprint 2 läuft)
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
- **PlotNamed** - Plots mit Namen-Verwaltung
- **PlotIsContainerForStorage** - Plots mit Lager-Funktion
- **PlotIsContainerForNpc** - Plots mit NPC-Verwaltung

#### Verfügbare Plot-Aktionen
- **Namen ändern** - Owner können Plot-Namen anpassen

### 🚧 In Entwicklung (Sprint 2)

- **Architektur-Refactoring** - Einheitliche Naming Conventions & Package-Struktur
- **Invokable-Pattern** - Command- und GUI-Invokation-System

### 📅 Geplant (Sprint 3+)

- **Storage-Verwaltung** - Lager öffnen, Preise setzen
- **NPC-Verwaltung** - NPCs spawnen, entfernen, konfigurieren
- **Teleport-System** - Zu Plots teleportieren
- **Persistenz** - Plot-Daten in Datenbank speichern
- **Wirtschafts-Features** - Handels-Steuern, Shop-Verwaltung

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

**Aktueller Sprint:** Sprint 2 - Architektur-Refactoring (in Progress)
**Letzter Sprint:** Sprint 1 - Core-Foundation (abgeschlossen)

**Sprint 1 Achievements:**
- ✅ Maven Multi-Module Struktur
- ✅ Provider-System mit Graceful Degradation
- ✅ Self-Rendering Pattern (GuiRenderable)
- ✅ Command Pattern (PlotAction)
- ✅ Trait-Komposition (PlotNamed, PlotIsContainerForStorage, PlotIsContainerForNpc)
- ✅ Universal GuiBuilder
- ✅ Proof-of-Concept validiert (PlotActionSetName)

**Test-Metriken (Sprint 1):**
- Tests: 146/146 ✅
- Code Coverage: ~95%
- Build: SUCCESS

**Sprint 2 Ziele:**
- Einheitliche Naming Conventions (Prefix/Suffix-Pattern)
- Package-Struktur etablieren (Root für Interfaces, impl/ für Klassen)
- Invokable-Pattern vorbereiten (Command & GUI)
- Alle bestehenden Klassen migrieren

**Sprint 3+ Roadmap:**
- Command-System (InvokableByCommand)
- Konkrete PlotActions (Claim, Storage, NPC, Teleport)
- Persistenz-Layer (PlotManager, Datenbank)
- Provider-Implementierungen (Towny, Vault, Citizens)

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
