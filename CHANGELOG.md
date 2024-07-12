### Fixed:
- Apply `MaxStackSize` to the player's inventory when using Player#addItem, so now instead of 99 items popping up in the inventory if giving 99 keys or any items, it'll split 64/35
  - Spigot for some odd reason made Player#addItem use the max stack size for the inventories. #hardforkwhen

### Changes:
- Simplified parsing messages internally with placeholders