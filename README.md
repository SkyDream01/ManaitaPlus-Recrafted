# ManaitaPlusGeneral

A faithful modern port of ManaitaPlus from Minecraft 1.7.10.

## Regarding Source Code Prior to Version 3.0
This is the source code repository for version 3.0 and later. If you want to view the source code repository for versions prior to 3.0, please follow this link:
https://github.com/gengyoubo/ManaitaPlusGeneral

## 26.3 port

This workspace now targets **Minecraft 26.3 / NeoForge 26.3.0.48-beta** (Java 25),
built with ModDevGradle. `common` holds the loader-independent game logic and
`neoforge` holds the NeoForge platform code; the shared classes are bundled into
the platform jar.

The `fabric` and `forge` source trees are kept for reference but are no longer
part of the build — they still target Minecraft 1.21.1 and would need their own
port.

Notable 26.3 API migrations baked into this port:
- `ResourceLocation` -> `Identifier`, `GuiGraphics` -> `GuiGraphicsExtractor`,
  `MultiBufferSource` -> `SubmitNodeCollector`.
- Tools/armor are plain `Item`s configured via `Item.Properties().sword(...)`,
  `.tool(...)`, `.humanoidArmor(...)` (the old `Tier`/`ArmorItem`/`SwordItem`
  classes are gone); registration goes through
  `DeferredRegister.Items.registerItem` / `Blocks.registerBlock`.
- Block entities save through `ValueInput`/`ValueOutput`; brewing is recipe-based
  (`RecipeType.BREWING`) instead of the deleted `PotionBrewing`.
- Item visuals use model *definitions* in `assets/<ns>/items/<id>.json`
  (`minecraft:range_dispatch` on the `manaita_plus_general:manaita_plus_general_type`
  property) instead of the removed `overrides`/`ItemProperties` predicates.
- Villager trades are data-driven (`data/<ns>/villager_trade/...`).

## Build

Java 25 is required.

```powershell
.\gradlew.bat build
```

The NeoForge jar is written to `neoforge/build/libs/ManaitaPlusGeneral-neoforge-3.0.0.jar`.

Useful tasks: `.\gradlew.bat :neoforge:runClient`, `.\gradlew.bat :neoforge:runData`
(client-side model/blockstate generation) and `.\gradlew.bat :neoforge:runDataServer`
(loot tables).
