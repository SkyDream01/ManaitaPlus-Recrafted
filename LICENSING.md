# Licensing by component

Manaita Plus: Recrafted (MPR) contributions are licensed under GNU GPL version
3 only (`GPL-3.0-only`). The original project remains under MIT. Third-party
material retains its own license. A file containing material from more than
one source is not wholly relicensed by this project.

| Material | License | Where to find the terms |
| --- | --- | --- |
| Original ManaitaPlusGeneral code, the 26.3 port through commit `151ebe6`, and previously MIT-licensed maintenance fixes | MIT | [LICENSES/MIT-original.txt](LICENSES/MIT-original.txt) |
| MPR-authored code, gameplay features, and supporting resources, including the Cutting Board Shield and Cutting Board Bucket code | GNU GPL version 3 only (`GPL-3.0-only`) | [LICENSE](LICENSE) |
| Baubles-derived code and assets in the legacy source trees | CC BY-NC-SA 3.0 | [Creative Commons legal code](https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode) |
| Other pre-existing assets | CC BY-NC-SA 4.0 | [Creative Commons legal code](https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode) |

The root [LICENSE](LICENSE) states the GPLv3 terms for MPR contributions; it does
not replace the original project's MIT notice or any third-party license.
Earlier changes already released under MIT remain MIT. New MPR contributions
are GPL-3.0-only unless a file-level notice says otherwise. Existing third-party
notices and licenses still apply.

## Cutting Board Shield scope

These files were created for the shield and are GPL-3.0-only in full:

- `common/src/main/java/github/com/gengyoubo/common/item/data/IMPGOffhandKey.java`
- `neoforge/src/main/java/github/com/gengyoubo/MPG/item/MPGShieldItem.java`
- `common/src/main/resources/assets/manaita_plus_recrafted/models/item/manaita_shield.json`
- `common/src/main/resources/assets/manaita_plus_recrafted/textures/item/equipment/manaita_shield.png`
- `neoforge/src/main/resources/assets/manaita_plus_recrafted/items/manaita_shield.json`
- `neoforge/src/main/resources/data/manaita_plus_recrafted/recipe/manaita_shield.json`

The shield-specific additions within the following existing files are also
GPL-3.0-only. Their previously existing code or translations retain their
earlier terms:

- `common/src/main/java/github/com/gengyoubo/common/event/MPGClientEventLogic.java` — offhand mode-key handling
- `common/src/main/java/github/com/gengyoubo/common/network/MPGKeyPressLogic.java` — offhand mode-key handling
- `common/src/main/java/github/com/gengyoubo/common/registry/MPGRegistryIds.java` — shield identifier
- `common/src/main/java/github/com/gengyoubo/common/util/MPGNBTData.java` — shield data keys
- `neoforge/src/main/java/github/com/gengyoubo/MPG/MPG.java` — shield item registration in the creative tab
- `neoforge/src/main/java/github/com/gengyoubo/MPG/core/MPGItemCore.java` — shield item registration
- `neoforge/src/main/java/github/com/gengyoubo/MPG/event/EventHandler.java` — shield protection and projectile events
- `common/src/main/resources/assets/manaita_plus_recrafted/lang/en_us.json` — shield translation keys
- `neoforge/src/main/resources/assets/manaita_plus_recrafted/lang/ja_jp.json` — shield translation keys
- `neoforge/src/main/resources/assets/manaita_plus_recrafted/lang/zh_cn.json` — shield translation keys

The key-binding migration and category changes in commit `b9ee770` are fixes
to the existing port, rather than part of the shield feature, and remain MIT.
The later shield model update in commit `faa070c` remains GPL-3.0-only. When
redistributing combined code, retain the MIT notices and comply with the GPL
terms for the GPL-covered portions. The Creative Commons terms above continue
to apply to their respective material.

## Cutting Board Bucket texture provenance

The `manaita_bucket_*.png` item textures are pixel edits of Minecraft 26.3's
`bucket.png`, `water_bucket.png`, and `lava_bucket.png`. The original bucket
silhouette remains; the lower metal face is cut out and the upper metal is
darkened. These sprites contain Minecraft-derived pixels rather than wholly
original GPL-only artwork.
