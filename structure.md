# MrCrayfish's Gun Mod - Code Structure

**Project:** MrCrayfish's Gun Mod for Minecraft 1.16.5  
**Version:** 1.2.6  
**Mod ID:** `cgm`  
**Build System:** Gradle (ForgeGradle 5.1+)  
**Language:** Java 8  

---

## Directory Tree

```
MrCrayfishGunMod-1.16.X/
├── src/
│   ├── main/
│   │   ├── java/com/mrcrayfish/guns/     (Java source files)
│   │   │   ├── annotation/                (3 files)
│   │   │   ├── block/                     (2 files)
│   │   │   ├── capability/                (4 files) ★ NEU
│   │   │   ├── client/                    (58+ files) ★
│   │   │   ├── common/                    (23 files) ★
│   │   │   ├── crafting/                  (7 files)
│   │   │   ├── datagen/                   (8 files)
│   │   │   ├── effect/                    (1 file)
│   │   │   ├── enchantment/               (11 files)
│   │   │   ├── entity/                    (7 files)
│   │   │   ├── event/                     (4 files)
│   │   │   ├── init/                      (11 files) ★
│   │   │   ├── interfaces/                (6 files)
│   │   │   ├── inventory/                 (2 files) ★ NEU
│   │   │   ├── item/                      (22 files)
│   │   │   ├── jei/                       (2 files)
│   │   │   ├── mixin/                     (10 files)
│   │   │   ├── network/                   (20 files)
│   │   │   ├── particles/                 (2 files)
│   │   │   ├── tileentity/                (3 files)
│   │   │   └── util/                      (8 files)
│   │   └── resources/
│   │       ├── assets/cgm/
│   │       │   ├── lang/en_us.json
│   │       │   ├── models/item/           (vest_cid/pd/swat/zivi + glock19 etc.)
│   │       │   ├── sounds/item/glock19/
│   │       │   └── textures/items/        (vest_cid/pd/swat/zivi PNGs)
│   │       ├── META-INF/mods.toml
│   │       └── cgm.mixins.json
│   └── generated/resources/data/cgm/
│       ├── guns/                          (glock19.json, glock19_switch.json)
│       └── recipes/
├── build.gradle
├── gradle.properties
└── CLAUDE.md
```

---

## Key Java Packages

### Capability (`com.mrcrayfish.guns.capability`) ★ NEU
Forge-Capability-System für die Schutzweste.

- **`IVestHandler.java`** - Interface: `getVest()`, `setVest(ItemStack)`
- **`VestHandler.java`** - Implementierung mit NBT-Serialisierung
- **`VestCapability.java`** - `@CapabilityInject`-Registration + Storage
- **`VestCapabilityProvider.java`** - `ICapabilitySerializable` Provider

### Inventory (`com.mrcrayfish.guns.inventory`) ★ NEU
Inventory-Wrapper für den Westen-Slot.

- **`VestInventory.java`** - `IInventory`-Wrapper um `IVestHandler`; `setChanged()` triggert Server-Sync
- **`VestSlot.java`** - `Slot` der nur `VestItem`-Instanzen akzeptiert

### Client (`com.mrcrayfish.guns.client`)
Client-seitige Renderer, Handler und UI.

- **`ClientHandler.java`** - Haupt-Client-Setup; registriert `VestLayer` + `HelmLayer` via `ObfuscationReflectionHelper` auf beide `PlayerRenderer`-Instanzen
- **`renderer/layer/VestLayer.java`** ★ NEU - `LayerRenderer` für 3D-Westen-Rendering am Spieler; nutzt `body.translateAndRotate`
- **`renderer/layer/HelmLayer.java`** ★ NEU - `LayerRenderer` für SWAT-Helm; wählt eines von 4 Modellen per `SwatHelmItem.getHelmState()`
- **`handler/FireModeHandler.java`** - HUD-Anzeige des Feuermodus für `SwitchGunItem`
- **`handler/HelmNvHandler.java`** ★ NEU - N-Taste (NV toggle), Ö-Taste (Visier toggle), grünes Overlay, Spieler-Outline bei aktiver NV; Visier-HUD (ALT/SPD/HP/DIR/TARGET) oben rechts in Grün
- **`handler/ThermalModeHandler.java`** ★ NEU - Taste T toggelt Thermal; blauer Screen-Overlay + orange Outlines aller LivingEntities durch Wände (GL_ALWAYS)

### Event (`com.mrcrayfish.guns.event`)
Forge-Event-Handler.

- **`ShieldHandler.java`** - `LivingHurtEvent`: blockiert frontale Treffer mit `CgmShieldItem`, spielt Shield-Block-Sound, beschädigt Schild-Durability
- **`VestEventHandler.java`** - Capability-Attach, Clone (Respawn), Login/Dimension-Sync, Schadensminderung
- **`KillHandler.java`** - Kill-Detection, Kill-Effekte + `placeEvidence()`: EvidenceBlock + GunKillEvent bei Waffen-Kill ★ NEU
- **`HelmEventHandler.java`** - `PotionApplicableEvent`: blockt Blindness wenn Visier unten
- **`GunIdHandler.java`** ★ NEU - `PlayerTickEvent` (alle 20 Ticks): scannt Inventar, vergibt zufällige 12-stellige ID an Waffen ohne ID, bindet ersten Besitzer in `GunRegistry`
- **`GunTooltipHandler.java`** ★ NEU - `ItemTooltipEvent`: hängt "ID: ############" an Waffen-Tooltip (client-only)
- **`GunKillEvent.java`** ★ NEU - Forge-Event: `{world, evidencePos, victim, shooter, weapon, EvidenceData}` — für Metropolia konsumierbar

### Item (`com.mrcrayfish.guns.item`)
- **`SwatHelmItem.java`** ★ NEU - Helm-Item (HEAD-Slot), statische Helpers `isNvActive/isVisierDown/getHelmState`; NBT-Keys `nv` + `visier`
- **`VestItem.java`** ★ NEU - Basis-Item für alle 4 Schutzwesten (`stacksTo(1)`)
- **`SwitchGunItem.java`** - `GunItem`-Subklasse mit 3-Modus Feuerselektor (SEMI/BURST/AUTO) via NBT
- **`GunSwitchItem.java`** - Crafting-Zutat für Glock-19-Umbau
- **`CgmShieldItem.java`** - Schild (120 Durability), blockiert frontale Treffer; `isFrontalHit()` korrekt implementiert (dot-product check)

### Mixin (`com.mrcrayfish.guns.mixin`)
- **`common/ContainerInvoker.java`** ★ NEU - `@Invoker` auf `Container.addSlot`
- **`common/PlayerContainerMixin.java`** ★ NEU - Injiziert Westen-Slot (77, 44) in `PlayerContainer`
- **`client/ContainerScreenAccessor.java`** ★ NEU - `@Accessor` für `leftPos`/`topPos` von `ContainerScreen`
- **`client/InventoryScreenMixin.java`** ★ NEU - Zeichnet Slot-Hintergrund im Inventar-Screen

### Network (`com.mrcrayfish.guns.network`)
- **`message/MessageSyncVest.java`** ★ NEU - S→C Packet: synct `IVestHandler`-ItemStack zum Client
- **`message/MessageFireMode.java`** - Synct `SwitchGunItem` Feuermodus zum Server
- **`message/MessageToggleHelm.java`** ★ NEU - C→S Packet: togglet NV (type=0) oder Visier (type=1) am Helm
- **`message/MessageToggleThermal.java`** ★ NEU - C→S: Thermal-Toggle-Request (Server prüft Allowed-Liste)
- **`message/MessageSyncThermal.java`** ★ NEU - S→C: synct `thermal` NBT zum Client

### Common (`com.mrcrayfish.guns.common`) — Ergänzungen ★ NEU
- **`GunRegistry.java`** - `WorldSavedData` (`cgm_gun_registry`): Map `gunId → OwnerInfo`, persistent in Overworld-DataStorage; `assignNewId()` zufällig 12-stellig; `bindOwner()` schreibt nur beim ersten Mal (Besitzer fest)

### Command (`com.mrcrayfish.guns.command`)
- **`ThermalModeCommand.java`** - `/thermalmode allow/disallow/list` (OP) + `enable/disable` (player)

### API (`com.mrcrayfish.guns.api`) ★ NEU
Hook-Interfaces + Datenklassen + Facade für externe Mods.

- **`IProtectiveVest.java`** - `float getProtection()` — jedes Item das in den Vest-Slot passt und Schutz gewährt
- **`ICgmShield.java`** - `boolean isFrontalHit(LivingEntity, x, z)` — jedes Item das Shield-Blocking nutzen will
- **`IHelm.java`** - Default-Methoden `isNvActive/isVisierDown/isThermalActive/getHelmState/set*` via NBT-Keys; externe Helme implementieren dieses Interface
- **`OwnerInfo.java`** ★ NEU - Immutable `{uuid, name, firstSeenEpoch}` — erster Waffenbesitzer
- **`EvidenceData.java`** ★ NEU - Immutable Spurendaten `{gunId, gunType, ammoType, shooterUuid, shooterName, victimUuid, victimName, epoch}` + `writeNbt/readNbt`
- **`CgmGunApi.java`** ★ NEU - Statische Facade: `isGun/getGunId/getGunType/getAmmoType/getOwner(world,id)/getEvidence(world,pos)` — für Metropolia Mod als `compileOnly` konsumierbar

### Init (`com.mrcrayfish.guns.init`)
- **`ModItems.java`** - Alle Items inkl. `SWAT_HELM`, `VEST_ZIVI/PD/SWAT/CID` (jetzt mit Protection-Wert im Konstruktor), `GLOCK19`, `GLOCK19_SWITCH`, `GUN_SWITCH`, `CGM_SHIELD`

---

## Schutzwesten-System (Übersicht)

| Weste | Item-ID | Textur | Schadensreduktion |
|-------|---------|--------|-------------------|
| Zivil | `cgm:vest_zivi` | `vest_zivi.png` | 20% |
| PD | `cgm:vest_pd` | `vest_pd.png` | 30% |
| SWAT | `cgm:vest_swat` | `vest_swat.png` | 40% |
| CID | `cgm:vest_cid` | `vest_cid.png` | 30% |

**Render-Pipeline:**
1. `VestLayer` (LayerRenderer) → `body.translateAndRotate` → korrekte Pose (Sneak/Swim/Stand)
2. HEAD-Display-Transform aus JSON: `rotation [0,180,0]`, `translation [0,-18.5,-1.25]`
3. Sync: `TRACKING_ENTITY_AND_SELF` + `StartTracking`-Event → alle Clients sehen Westen anderer Spieler

**Vanilla-Inventar-Slot:**
- `PlayerContainerMixin` injiziert Slot bei (77, 44)
- `InventoryScreenMixin` zeichnet Slot-Hintergrund

---

## Glock-19 / Switch-System

| Waffe | Item-ID | Klasse | Magazin | Besonderheit |
|-------|---------|--------|---------|--------------|
| Glock 19 | `cgm:glock19` | `GunItem` | 15 | Base |
| Glock 19 Switch | `cgm:glock19_switch` | `SwitchGunItem` | 15 | SEMI/BURST/AUTO |
| Glock 19 Large Mag | `cgm:glock19_largemag` | `GunItem` | 32 | Crafting: Glock19 + Magazin |
| Glock 19 Large Mag Switch | `cgm:glock19_largemag_switch` | `SwitchGunItem` | 32 | Crafting: Glock19 Switch + Magazin |

- Alle 4 Varianten unterstützen `underBarrel` + `sideLeft` + `sideRight`
- sideLeft/sideRight in Gun-Data-JSON: `xOffset ±3.0, yOffset 3.6, zOffset 4.0`
- Ammo: `additionalguns:bullet_small`, Damage: 8
- `glock19_largemag_switch.json` (Modell) = Kopie von `glock19_switch.json`
- **Dependency:** Additional Guns (`additionalguns`, mandatory in `mods.toml`, `runtimeOnly` in `build.gradle`)

## Side-Mount-System

Zwei neue Attachment-Slots (SIDE_LEFT, SIDE_RIGHT) in der CGM-GUI.
Items implementieren `ISideMount` und passen in **beide** Side-Slots.
Rotation beim Rendern: SIDE_LEFT → +90° Z-Achse, SIDE_RIGHT → -90° Z-Achse.
API-kompatibel: weitere Mods können `ISideMount`/`SideMountItem` nutzen.

**Kern-Klassen:**
- `ISideMount.java` — Interface (extends `IAttachment<SideMount>`)
- `impl/SideMount.java` — Attachment-Properties
- `SideMountItem.java` — Item-Klasse (passt in beide Side-Slots)

**Geänderte Klassen:**
- `IAttachment.Type` — SIDE_LEFT, SIDE_RIGHT hinzugefügt
- `Gun.Attachments` — sideLeft/sideRight Felder + JSON/NBT/copy
- `AttachmentSlot.mayPlace()` — ISideMount in beiden Side-Slots erlaubt
- `AttachmentContainer` — slotsChanged nutzt Slot-Index für Tag-Key; Side-Slots bei x=SIDE_X=-22 (links neben GUI), Inventar bei Originalpositionen (102/160)
- `AttachmentScreen` — imageHeight=184 (fix), Side-Slot Hintergründe manuell gerendert via fill(), Tooltips für Side-Slots separat
- `GunRenderingHandler` — SIDE_LEFT +90° Z, SIDE_RIGHT -90° Z

**Modell-Rotations-Fix:**
- `flashlight.json` + `red_dot.json`: `"none": {"rotation": [0, 180, 0]}` → korrigiert Ausrichtung beim Rendern als Attachment (NONE-Transform)

## Attachments (Flashlight & Laser)

| Attachment  | Item-ID        | Slot                   | Modifier        | Toggle-Key |
|-------------|----------------|------------------------|-----------------|------------|
| Flashlight  | `cgm:flashlight` | SIDE_LEFT / SIDE_RIGHT / UNDER_BARREL | LIGHT_RECOIL   | L          |
| Laser       | `cgm:red_dot`    | SIDE_LEFT / SIDE_RIGHT / UNDER_BARREL | REDUCED_RECOIL | K          |

**Flashlight-Verhalten:**
- Key L toggelt an/aus (nur wenn Flashlight-Attachment dran)
- Zustand in ItemStack-NBT `FlashlightOn` gespeichert, per `MessageToggleLight` zum Server gesynct
- Lokaler Spieler bekommt Night-Vision-Effekt (Level 0) solange Flashlight an
- Andere Spieler sehen den NBT-State (Grundlage für spätere RyoamicLights-Integration)

**Laser-Verhalten:**
- Key K toggelt an/aus (nur wenn Laser-Attachment dran)
- Zustand in ItemStack-NBT `LaserOn` gespeichert, per `MessageToggleLight` gesynct
- Roter, semi-transparenter Strahl (alpha ~0.15) via `RenderWorldLastEvent`
- Stoppt an Blöcken (ClipContext) und Entities (AABB-Check)
- Rotes Kreuz-Dot am Entity-Trefferpunkt
- Sichtbar für alle Spieler in der Nähe (Client liest NBT aus gehaltenen Items anderer Spieler)

**Netzwerk:**
- `MessageToggleLight` (C→S, ID 18): type=0 Flashlight, type=1 Laser
- Server: `ServerPlayHandler.handleToggleLight()` prüft Attachment, toggelt NBT, broadcastChanges()

**Neue Klassen:**
- `network/message/MessageToggleLight.java` — C→S Packet
- `client/handler/FlashlightHandler.java` — Keybind L (Toggle + NBT)
- `client/handler/LaserRenderer.java` — Keybind K + Hit-Tracking + Dot-Rendering
- `client/FlashlightLightIntegration.java` — RyoamicLights soft-dep Integration (Spieler = Lichtquelle)

**RyoamicLights (Flashlight):**
- Soft-Dependency: `ryoamiclights` (mandatory=false, CLIENT)
- Bei aktivem Flashlight emittiert der Spieler Licht-Level 15 via `DynamicLightHandlers`
- Nur aktiv wenn RyoamicLights im Modpack vorhanden

**Laser-Rendering:**
- `ItemModelsProperties` Predicate `cgm:laser_on` → liest `LaserOn` vom Gun-Stack via `entity.getMainHandItem()` (nicht Attachment-Stack!)
- Im Inventar/GUI zeigt `red_dot.json` (predicate=0 da nicht in Mainhand)
- `GunRenderingHandler.renderAttachments()` rendert roten Beam (dünne Box, 0.016m Breite) entlang Gun-`-Z`-Achse skaliert auf `hitDist` Blöcke
- Nur bei FIRST_PERSON / THIRD_PERSON TransformType — kein Beam im Inventar
- Hit-Distance (max 300 Blöcke) per UUID in `LaserRenderer.hitDistances` gecacht via `RenderWorldLastEvent`

### Schnell-Navigation

| Was | Pfad |
|-----|------|
| Item-Registrierung | `src/main/java/com/mrcrayfish/guns/init/ModItems.java` → `FLASHLIGHT`, `RED_DOT` |
| Item-Klasse | `src/main/java/com/mrcrayfish/guns/item/SideMountItem.java` |
| 3D-Modell Laser | `src/main/resources/assets/cgm/models/item/flashlight.json` |
| 3D-Modell Flashlight | `src/main/resources/assets/cgm/models/item/red_dot.json` |
| Textur Laser | `src/main/resources/assets/cgm/textures/items/flashlight.png` |
| Textur Flashlight | `src/main/resources/assets/cgm/textures/items/red_dot.png` |
| GUI-Icon Laser (SIDE_LEFT) | `src/main/resources/assets/cgm/textures/gui/laser.png` ← hier ablegen |
| GUI-Icon Flashlight (SIDE_RIGHT) | `src/main/resources/assets/cgm/textures/gui/flashlight_right.png` ← hier ablegen |
| Workbench-Rezept Laser | `src/generated/resources/data/cgm/recipes/flashlight.json` |
| Workbench-Rezept Flashlight | `src/generated/resources/data/cgm/recipes/red_dot.json` |
| Rendering-Rotation | `src/main/java/com/mrcrayfish/guns/client/handler/GunRenderingHandler.java` → `renderAttachments()` |
| Side-Slot-Position (xOffset) | `src/generated/resources/data/cgm/guns/glock19*.json` → `sideLeft` / `sideRight` |

## Magazin-Item

- **`cgm:magazin`** - Crafting-Zutat (stacksTo 16), aus 4x Eisen am Workbench

---

## Mixins (`cgm.mixins.json`)

| Mixin | Ziel | Zweck |
|-------|------|-------|
| `common.ContainerInvoker` | `Container` | `@Invoker` für `addSlot` |
| `common.PlayerContainerMixin` | `PlayerContainer` | Westen-Slot einfügen |
| `common.LivingEntityMixin` | `LivingEntity` | Knockback-Modifikation |
| `common.PlayerListMixin` | `PlayerList` | - |
| `common.SittingPhaseMixin` | - | - |
| `common.EndPortalBlockMixin` | `EndPortalBlock` | - |
| `client.ContainerScreenAccessor` | `ContainerScreen` | `leftPos`/`topPos` Accessor |
| `client.InventoryScreenMixin` | `InventoryScreen` | Westen-Slot Hintergrund |
| `client.GameRendererMixin` | `GameRenderer` | - |
| `client.MouseHelperMixin` | `MouseHelper` | - |
| `client.PlayerRendererMixin` | `PlayerRenderer` | Supprimiert `hat.visible` wenn SWAT-Helm getragen wird |
| `client.RenderTypeLookupMixin` | `RenderTypeLookup` | - |
| `client.WorldRendererMixin` | `WorldRenderer` | - |

---

## Build Configuration

- **Forge:** 1.16.5-36.2.20
- **Mappings:** Official (1.16.5)
- **Dependencies:** Obfuscate, Configured, Catalogue, Backpacked, Additional Guns (mandatory)
- **Mixin Config:** `cgm.mixins.json`

---

---

## SWAT-Helm-System

| Feature | Taste | NBT-Key | Modell |
|---------|-------|---------|--------|
| Night Vision | N | `nv` | `swat_helm_nv` / `swat_helm_nv_visier` |
| Visier | Ö (Semicolon) | `visier` | `swat_helm_visier` / `swat_helm_nv_visier` |

**4 Modell-Varianten** (`assets/cgm/models/item/`):
- `swat_helm.json` – Standard (NV oben, Visier oben)
- `swat_helm_nv.json` – NV aktiv (Linsen unten)
- `swat_helm_visier.json` – Visier unten
- `swat_helm_nv_visier.json` – beides aktiv

**Render-Pipeline:** Forge-Head-Slot-Rendering → `ItemModelsProperties` Predicate `cgm:helm_state` → Modell-Override → korrektes Varianten-Modell

**Hat-Unterdrückung:** `PlayerRendererMixin` injiziert am Ende von `PlayerRenderer.setModelProperties()` → setzt `hat.visible = false` wenn SwatHelmItem im HEAD-Slot

**NV-Effekt:** Grünes Overlay (`RenderGameOverlayEvent.Post`) + Night-Vision-Trank (Server) + Spieler-Outline (`RenderLivingEvent.Post`)

**Visier-HUD** (Session 4):
- Nur aktiv wenn `isVisierDown() == true`
- Zeigt oben rechts in Grün: `ALT`, `SPD`, `HP`, `DIR`, `TARGET/DIST`
- Speed: horizontale XZ-Bewegung × 20 → b/s; tracked auch Fahrzeuge
- DIR: 8-Richtungen auf Deutsch (N/NO/O/SO/S/SW/W/NW)
- TARGET: Entity in Sichtlinie (100m Raycast) → Name + Entfernung
- DIST: Block in Sichtlinie → Entfernung

**Thermal-Mode** (Session 4):
- Server-Befehl `/thermalmode allow/disallow/list` (OP Level 2) + `enable/disable` (jeder, wenn allowed)
- Keybind T (nur wenn im Allowed-List + Helm getragen)
- Rendering: blauer Semi-Overlay + orange Outlines aller LivingEntities durch Wände
- Neues NBT-Key: `thermal` in SwatHelmItem
- Pakete: `MessageToggleThermal` (C→S), `MessageSyncThermal` (S→C)
- Manager: `ThermalModeManager` (in-memory Set<UUID>, resettet bei Neustart)

**Flashbang-Schutz** (Session 4):
- `HelmEventHandler.onPotionApplicable` blockt Blindness (vanilla + cgm:blinded) wenn Visier unten

## Waffen-ID-System ★ NEU (Session 5)

| Feature | Klasse | Beschreibung |
|---------|--------|--------------|
| ID-Vergabe | `GunIdHandler` | PlayerTick alle 20T, Inventar-Scan, zufällige 12-stellige ID per Waffe |
| Persistenz | `GunRegistry` | WorldSavedData, Overworld-DataStorage, Besitzer fix beim ersten Eintrag |
| Tooltip | `GunTooltipHandler` | "ID: ############" unter Munition-Info (alle GunItem-Subklassen) |
| Beweis-Block | `EvidenceBlock` + `EvidenceTileEntity` | Kollisionslos, kein Item, platziert bei Kill mit Spurendaten |
| API/Events | `CgmGunApi` + `GunKillEvent` | Metropolia bindet cgm als compileOnly, liest `getEvidence/getOwner` |

**Datenfluss:**
1. Waffe → Inventar → `GunIdHandler` → NBT-ID + `GunRegistry.bindOwner` (erster Besitzer fix)
2. Tooltip: `ItemTooltipEvent` → ID sichtbar unter Munition
3. Kill → `KillHandler.placeEvidence()` → `EvidenceBlock` am Todesort → `GunKillEvent` gefeuert
4. Metropolia: `CgmGunApi.getEvidence(world, pos)` + `getOwner(world, gunId)` → Bericht

**EvidenceBlock-Assets:**
- `blockstates/evidence.json` → Modell `cgm:block/evidence`
- `models/block/evidence.json` — **PLATZHALTER** (Sand-Textur, 4×2×4 Box) → User ersetzt mit eigenem Modell

*Zuletzt aktualisiert: 2026-06-18 (Session 5)*
