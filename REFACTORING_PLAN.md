# Refactoring-Plan: Helm / Westen / Shield → Eigene Mod

**Ziel:** Items (SwatHelm, Westen ×4, CombatShield) aus CGM-Fork in eine eigene Mod auslagern.
CGM-Fork stellt API-Interfaces bereit; neue Mod implementiert diese und hängt von CGM-Fork ab.

---

## 1. Was bleibt in CGM-Fork

| Was | Warum |
|-----|-------|
| Guns, Attachments, Glock, Side-Mounts | Zu tief integriert |
| `VestCapability` + `VestHandler` + Provider | Wird vom `PlayerContainerMixin` direkt genutzt |
| `PlayerContainerMixin` (Slot-Injection) | Mixin muss beim Start geladen sein → bleibt in CGM |
| `InventoryScreenMixin` (Slot-Background) | Gleicher Grund |
| `VestInventory` / `VestSlot` | Abhängig vom Mixin |
| `ContainerInvoker` Mixin | Hilfsmixin für Slot-Injection |
| **NEU** `api/IProtectiveVest.java` | Hook für neue Mod |
| **NEU** `api/ICgmShield.java` | Hook für neue Mod |

---

## 2. Was wandert in neue Mod (`cgm_gear`, Arbeitstitel)

| Was | Quelle |
|-----|--------|
| `SwatHelmItem` | `item/SwatHelmItem.java` |
| `VestItem` (alle 4 Varianten) | `item/VestItem.java` + ModItems-Registrierung |
| `CgmShieldItem` | `item/CgmShieldItem.java` |
| `VestEventHandler` (Schadensreduktion) | `event/VestEventHandler.java` |
| `ShieldHandler` | `event/ShieldHandler.java` |
| `HelmEventHandler` | `event/HelmEventHandler.java` |
| `VestLayer` (Renderer) | `client/renderer/layer/VestLayer.java` |
| `HelmNvHandler` | `client/handler/HelmNvHandler.java` |
| `ThermalModeHandler` | `client/handler/ThermalModeHandler.java` |
| `ThermalModeCommand` + `ThermalModeManager` | `command/` + `common/` |
| `MessageSyncVest` | `network/message/` |
| `MessageToggleHelm` | `network/message/` |
| `MessageSyncThermal` | `network/message/` |
| `MessageToggleThermal` | `network/message/` |
| Alle Assets (Texturen, Modelle, Sounds) | `assets/cgm/` → `assets/cgm_gear/` |

---

## 3. Hooks / API in CGM-Fork

### `com.mrcrayfish.guns.api.IProtectiveVest`
```java
public interface IProtectiveVest {
    /** 0.0 = kein Schutz, 1.0 = voller Schutz */
    float getProtection();
}
```
**Wo genutzt:**
- `VestSlot.mayPlace()` → prüft `stack.getItem() instanceof IProtectiveVest` (statt `VestItem`)
- `VestEventHandler.getProtection()` → castet auf `IProtectiveVest` statt hardgecodeter Item-Checks

### `com.mrcrayfish.guns.api.ICgmShield`
```java
public interface ICgmShield {
    boolean isFrontalHit(LivingEntity blocker, double atkX, double atkZ);
}
```
**Wo genutzt:**
- `ShieldHandler.onLivingHurt()` → prüft `instanceof ICgmShield` statt `instanceof CgmShieldItem`

---

## 4. Neue Mod `cgm_gear`

### Projekt-Setup
- **Mod-ID:** `cgm_gear`
- **Forge:** 1.16.5 (gleiche Version wie CGM-Fork)
- **Dependency in `mods.toml`:** `cgm` (mandatory = true)
- **Dependency in `build.gradle`:** CGM-Fork als `compileOnly` / `runtimeOnly`

### Paketstruktur
```
com.example.cgmgear/
├── CgmGearMod.java               (Mod-Einstieg, Event-Bus, PacketHandler)
├── init/
│   └── GearItems.java            (SWAT_HELM, VEST_*, CGM_SHIELD)
├── item/
│   ├── GearHelmItem.java         (= SwatHelmItem, kopiert)
│   ├── GearVestItem.java         (implements IProtectiveVest, getProtection() pro Subtyp)
│   └── GearShieldItem.java       (implements ICgmShield, isFrontalHit() übernommen)
├── event/
│   ├── GearVestEventHandler.java (LivingHurtEvent → IProtectiveVest via VestCapability)
│   ├── GearShieldHandler.java    (LivingHurtEvent → ICgmShield)
│   └── GearHelmEventHandler.java (PotionApplicable → Blindness blocken)
├── client/
│   ├── GearClientHandler.java    (setup(), Layer + Handler registrieren)
│   ├── renderer/layer/
│   │   └── GearVestLayer.java    (= VestLayer, kopiert, nutzt VestCapability von CGM)
│   └── handler/
│       ├── GearHelmNvHandler.java
│       └── GearThermalHandler.java
├── command/
│   ├── ThermalModeCommand.java   (kopiert)
│   └── ThermalModeManager.java   (kopiert)
└── network/
    ├── GearPacketHandler.java
    └── message/
        ├── MessageSyncVest.java  (kopiert, referenziert VestCapability von CGM)
        ├── MessageToggleHelm.java
        ├── MessageSyncThermal.java
        └── MessageToggleThermal.java
```

### Asset-Migration
```
assets/cgm/models/item/swat_helm*.json       → assets/cgm_gear/models/item/
assets/cgm/models/item/vest_*.json           → assets/cgm_gear/models/item/
assets/cgm/textures/items/vest_*.png         → assets/cgm_gear/textures/items/
assets/cgm/sounds/item/glock19/ (helm-bezogen bleibt in cgm)
assets/cgm/lang/en_us.json (helm/vest-Einträge) → assets/cgm_gear/lang/en_us.json
```

---

## 5. Schritt-für-Schritt

### Phase 1 — CGM-Fork: API-Interfaces (minimal)
1. `com.mrcrayfish.guns.api.IProtectiveVest` anlegen
2. `com.mrcrayfish.guns.api.ICgmShield` anlegen
3. `VestItem` implementiert `IProtectiveVest` (Backward-Compat für bestehendes)
4. `CgmShieldItem` implementiert `ICgmShield`
5. `VestSlot.mayPlace()` auf `IProtectiveVest` umstellen
6. `VestEventHandler.getProtection()` auf Interface-Cast umstellen
7. `ShieldHandler` auf `ICgmShield` umstellen

### Phase 2 — Neue Mod anlegen
1. Gradle-Projekt erstellen (ForgeGradle 5.1, 1.16.5)
2. Items registrieren: `GearVestItem` mit `getProtection()`-Werten, `GearHelmItem`, `GearShieldItem`
3. VestCapability aus CGM nutzen (Capability bleibt in CGM, neue Mod hängt davon ab)
4. Event-Handler registrieren (VestDamage, Shield, HelmFlashbang)
5. Client-Handler + VestLayer registrieren
6. Network-Pakete kopieren + anpassen (neue Mod-ID als Channel-Name)
7. Assets in neue Mod verschieben, Item-Models + Lang anpassen

### Phase 3 — CGM-Fork aufräumen
1. `VestItem`, `CgmShieldItem`, `SwatHelmItem` Item-Registrierungen aus `ModItems.java` entfernen
2. `VestEventHandler`, `ShieldHandler`, `HelmEventHandler` aus GunMod deregistrieren
3. Client-Handler (HelmNvHandler, ThermalModeHandler) aus `ClientHandler.java` entfernen
4. `MessageSyncVest`, `MessageToggleHelm`, `MessageSyncThermal`, `MessageToggleThermal` aus PacketHandler entfernen
5. Assets (vest_*.png, swat_helm*.json, etc.) aus CGM-Fork entfernen
6. Lang-Einträge in `en_us.json` bereinigen
7. `ThermalModeCommand` + `ThermalModeManager` aus CGM entfernen

---

## 6. Wichtige Abhängigkeiten / Risiken

| Risiko | Lösung |
|--------|--------|
| `PlayerContainerMixin` nutzt `VestCapability` aus CGM | Bleibt in CGM; neue Mod nutzt dieselbe Capability |
| Doppelte Slot-Injection wenn beide Mods `PlayerContainerMixin` haben | Mixin bleibt **nur** in CGM, neue Mod hat keinen eigenen |
| `ItemModelsProperties`-Predicate `cgm:helm_state` ist in `GunMod.java` registriert | In neuer Mod mit `cgm_gear:helm_state` neu registrieren, `swat_helm*.json` anpassen |
| `VestLayer` referenziert `VestCapability` aus CGM | OK — neue Mod darf CGM-Klassen nutzen (Dependency) |
| Render-Layer-Registrierung via `ObfuscationReflectionHelper` in `ClientHandler` | In `GearClientHandler` identisch nachmachen |

---

*Erstellt: 2026-06-07*
