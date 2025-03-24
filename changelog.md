- /crazycrates debug will tell you no prizes can be won if the item sections are empty, or if there are no prizes.

## New Item Format
This is only for the new item format behind a toggle in config.yml

### Fixes
- Fixed an issue with items having blank names with the new item format (not the old format)
    - This happens only when `name` is not present in the `Items` section

### Additions
- Added the ability to have multiple potion effects for a potion, or tipped arrows
  - Potion effects are measured in seconds, https://minecraft.wiki/w/Calculators/Tick