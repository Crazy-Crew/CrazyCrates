### Changes:
- Play knockback/sounds if they don't have the required keys.
#### Cosmic Crate:
- Cosmic Crate was changed back in March for the calculation of tiers to be handled when you open the inventory, so that when picking a mystery crate. The choice would actually matter!
- It has been brought to my attention of being able to skimp it using client side mods. this has been addressed, the mods will no longer be able to skimp it by seeing item differences.
- The calculation still happens but, the picks are stored internally to the player's uuid in a cache which clears when the player quits, the inventory closes or the crate task ends for X reason.