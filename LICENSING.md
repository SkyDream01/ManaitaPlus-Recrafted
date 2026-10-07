# Licensing by component

This repository uses licenses by contribution and component. A file containing
both ported code and new feature code is **not** wholly relicensed by the new
feature's license.

| Material | License | Where to find the terms |
| --- | --- | --- |
| Original ManaitaPlusGeneral code and its Minecraft ports, including the 26.3 port and port-maintenance fixes | MIT | [LICENSE](LICENSE) |
| Newly authored gameplay features and their supporting code and resources, including the Cutting Board Shield | GNU GPL version 3 only (`GPL-3.0-only`) | [LICENSES/GPL-3.0-only.txt](LICENSES/GPL-3.0-only.txt) |
| Baubles-derived code and assets in the legacy source trees | CC BY-NC-SA 3.0 | [Creative Commons legal code](https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode) |
| Other pre-existing assets | CC BY-NC-SA 4.0 | [Creative Commons legal code](https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode) |

The MIT code baseline is the original project and its 26.3 port through commit
`151ebe6`. Later changes that only port, fix, or maintain that existing code
remain MIT. Original new features are GPL-3.0-only unless their own notice says
otherwise. Existing third-party notices and licenses still apply; a port does
not remove them.

## Cutting Board Shield scope

These files were created for the shield and are GPL-3.0-only in full:

- `common/src/main/java/github/com/gengyoubo/common/item/data/IMPGOffhandKey.java`
- `neoforge/src/main/java/github/com/gengyoubo/MPG/item/MPGShieldItem.java`
- `common/src/main/resources/assets/manaita_plus_general/models/item/manaita_shield.json`
- `common/src/main/resources/assets/manaita_plus_general/textures/item/equipment/manaita_shield.png`
- `neoforge/src/main/resources/assets/manaita_plus_general/items/manaita_shield.json`
- `neoforge/src/main/resources/data/manaita_plus_general/recipe/manaita_shield.json`

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
- `common/src/main/resources/assets/manaita_plus_general/lang/en_us.json` — shield translation keys
- `neoforge/src/main/resources/assets/manaita_plus_general/lang/ja_jp.json` — shield translation keys
- `neoforge/src/main/resources/assets/manaita_plus_general/lang/zh_cn.json` — shield translation keys

The key-binding migration and category changes in commit `b9ee770` are fixes
to the existing port, rather than part of the shield feature, and remain MIT.
The later shield model update in commit `faa070c` remains GPL-3.0-only. When
redistributing combined code, retain the MIT notices and comply with the GPL
terms for the GPL-covered portions. The Creative Commons terms above continue
to apply to their respective material.
