# Manaita Plus: Recrafted (MPR)

An unofficial port of Manaita Plus to Minecraft 26.3, based on ManaitaPlusGeneral.

## Licensing

MPR contributions are licensed under GNU GPL version 3 only (`GPL-3.0-only`).
Original ManaitaPlusGeneral code retains its MIT license. Separately licensed
third-party assets keep their existing terms. See [LICENSING.md](LICENSING.md)
for the component boundaries and license texts.

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
  (`minecraft:range_dispatch` on the `manaita_plus_recrafted:manaita_plus_recrafted_type`
  property) instead of the removed `overrides`/`ItemProperties` predicates.
- Villager trades are data-driven (`data/<ns>/villager_trade/...`).

## Build

Java 25 is required.

```powershell
.\gradlew.bat build
```

The NeoForge jar is written to `neoforge/build/libs/ManaitaPlusRecrafted-neoforge-3.1.2.jar`.

The cutting board shield can be held in either hand. It creates an absolute-domain cube of
1×1×1, 3×3×3, 5×5×5, or 7×7×7 around its holder. The domain protects players inside from
all damage, including void and `/kill`, intercepts incoming projectiles, and ejects hostile mobs.
Players inside can fire projectiles outward. There is no need to raise the shield. Press the cutting board mode key (X by default) to cycle the
range, or sneak and press it to toggle floating. Floating is enabled by default: holding the
shield grants Slow Falling, and sneaking in midair holds altitude. The shield has no durability
or shield-disable cooldown.

The cutting board bucket stores up to 10,000 buckets of one type: a bucketable
fluid, powder snow, sulfur cubes, or milk. Press the mode key to cycle its collection cube size (1, 3, 5, 7, 9,
or 11 blocks per side); sneak and press the mode key to cycle between collecting,
releasing one source block, and batch releasing. Collection uses the targeted
block as the center of the cube's top layer. Single release works like a normal
bucket. For batch release, left-click to select the first corner and right-click
the opposite corner. A translucent preview appears before any fluid is released.
Either corner can be clicked again to update the preview; press the mode key to
confirm. The selected cuboid may be up to 32 blocks on each side and 10,000
blocks total. Fluid collection only accepts source blocks; powder snow also
supports area collection and release. Each successful placement consumes one
stored bucket. Right-click an adult, unprimed sulfur cube to collect it, retaining
its name, health, and absorbed block. Sulfur cubes are released one at a time,
and a bucket containing them skips batch-release mode. Powder snow and sulfur
cubes each have matching partial and full bucket textures. Milk is collected by
right-clicking adult cows, mooshrooms, or goats. At size 1, only the clicked
animal is milked. Larger ranges collect one bucket from each adult milk animal
in the same collection cube, using the clicked animal's feet as the center of
its top layer. The mode key also adjusts the range when milk is stored, and
collection stops when the bucket is full. A milk-filled bucket switches
between collection and drinking modes; hold right-click in drinking mode to
consume one stored bucket and clear status effects, keeping the cutting board
bucket itself. Milk cannot be released individually or in a region and also has
matching partial and full textures. Drinking the last bucket returns to collection mode.

Useful tasks: `.\gradlew.bat :neoforge:runClient`, `.\gradlew.bat :neoforge:runData`
(client-side model/blockstate generation) and `.\gradlew.bat :neoforge:runDataServer`
(loot tables).
