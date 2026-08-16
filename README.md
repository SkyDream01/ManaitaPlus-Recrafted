# ManaitaPlusGeneral Architectury workspace

This workspace consolidates the Minecraft 1.21.1 Fabric, Forge, and NeoForge
sources into one Gradle build.

## Modules

- `common`: shared Architectury bootstrap and future shared game logic.
- `fabric`: the existing Fabric implementation, connected to `common`.
- `neoforge`: the existing NeoForge implementation, connected to `common`.
- `forge`: the existing Forge implementation built by Architectury Loom.

Architectury API 13 does not publish/support a Forge 1.21.1 platform module.
For that reason the Forge implementation participates in the same root build
but does not consume `common` or Architectury API. Fabric and NeoForge use the
official Architectury API artifacts.

## Build

Java 21 is required.

```powershell
.\gradlew.bat build
```

Platform jars are written under each platform module's `build/libs` directory.
