- Added the ability to hide specific components in the new `Items` section
```yml
      Items:
        1ca8f0ac:
          material: diamond_helmet
          # The name of the item.
          name: <red>Diamond Helmet
          # The configuration section for enchantments, minecraft: is not needed in front of the enchantment.
          # A list of enchantments https://jd.papermc.io/paper/1.21.4/io/papermc/paper/registry/keys/EnchantmentKeys.html
          # enchantment: amount
          enchantments:
            unbreaking: 3
            protection: 3
          # A list of components to hide.
          # https://jd.papermc.io/paper/1.21.5/io/papermc/paper/registry/keys/DataComponentTypeKeys.html, Grab the value from the description without minecraft:
          hidden-components:
            - "enchantments"
```
- Fixed a potential exception if the item is not player head