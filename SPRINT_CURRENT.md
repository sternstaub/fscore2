# Sprint 2: Architektur-Refactoring

**Status:** 🚧 IN PROGRESS
**Gestartet:** 2025-11-19
**Ziel:** Einheitliche Naming Conventions & Package-Struktur etablieren

---

## 🎯 Sprint-Ziel

Refactoring der gesamten Codebasis gemäß den neuen Naming Conventions und Package-Struktur-Regeln, die in Sprint 1 erkannt wurden.

**Warum?**
- Inkonsistente Naming Conventions (PlotNamed vs. PlotIsContainerForStorage)
- Keine klare Package-Struktur (Interfaces und Implementierungen gemischt)
- Vorbereitung für Invokable-Pattern (Sprint 3)

---

## 📋 Phasen

### Phase 1: Conventions aktualisieren ✅

**Status:** ABGESCHLOSSEN

- [x] CONVENTIONS_NAMING.md neu geschrieben
- [x] CLAUDE.md aktualisiert (Sprint-Referenzen korrigiert)
- [x] README.md aktualisiert (Sprint 1 → Sprint 2 Status)
- [x] ERKENNTNISSE.md erweitert (Sprint 1 abgeschlossen, Sprint 2 gestartet)

**Neue Patterns:**
- Interface (Trait): `[Subject]With[Capability]`
- Interface (Invokable): `InvokableBy[Mechanism]`
- Abstrakte Klasse: `Abstract[Name]`
- Konkrete Klasse: `[Name][Type]`
- Enumeration: `Defined[Concept]s`

---

### Phase 2: Trait-Interfaces umbenennen

**Status:** PENDING

**Aufgaben:**
- [ ] PlotNamed → PlotWithName
- [ ] PlotIsContainerForStorage → PlotWithStorageContainer
- [ ] PlotIsContainerForNpc → PlotWithNpcContainer
- [ ] Alle Referenzen in Tests aktualisieren
- [ ] JavaDoc aktualisieren
