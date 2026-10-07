# ManaitaPlusGeneral

An unofficial port of ManaitaPlusGeneral to Minecraft 26.3, based on the original Manaita Plus mod for Minecraft 1.7.10.

## Licensing

The original code and its ports are available under MIT. Newly developed
features, including the Cutting Board Shield, are available under GNU GPL v3
only (`GPL-3.0-only`). Existing Baubles-derived material and other pre-existing
assets retain their separate Creative Commons terms. See [LICENSING.md](LICENSING.md)
for the exact scope and the license texts.

## Regarding Source Code Prior to Version 3.0
This is the source code repository for version 3.0 and later. If you want to view the source code repository for versions prior to 3.0, please follow this link:
https://github.com/gengyoubo/ManaitaPlusGeneral

## 26.3 port

This workspace now targets **Minecraft 26.3 / NeoForge 26.3.0.48-beta** (Java 25),
built with ModDevGradle. `common` holds the loader-independent game logic and
`neoforge` holds the NeoForge platform code; the shared classes are bundled into
the platform jar.

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

The NeoForge jar is written to `neoforge/build/libs/ManaitaPlusGeneral-neoforge-3.1.1.jar`.

The cutting board shield can be held in either hand. It creates an absolute-domain cube of
1×1×1, 3×3×3, 5×5×5, or 7×7×7 around its holder. The domain protects players inside from
all damage, including void and `/kill`, intercepts incoming projectiles, and ejects hostile mobs.
Players inside can fire projectiles outward. There is no need to raise the shield. Press the cutting board mode key (X by default) to cycle the
range, or sneak and press it to toggle floating. Floating is enabled by default: holding the
shield grants Slow Falling, and sneaking in midair holds altitude. The shield has no durability
or shield-disable cooldown.

Useful tasks: `.\gradlew.bat :neoforge:runClient`, `.\gradlew.bat :neoforge:runData`
(client-side model/blockstate generation) and `.\gradlew.bat :neoforge:runDataServer`
(loot tables).
