### Changes:
- Updated the /crazycrates additem command to support any item you can make in-game, it will still allow you to apply other configuration to the item added like your own custom lore.
- This change required a few other tweaks in some areas so please report any oddities to our Github Issues tab.
```yml
    '7':
      DisplayName: '<red>This is a name.'
      DisplayLore:
        - '<yellow>This is a lore.'
      DisplayEnchantments:
        - 'sharpness:5'
      DisplayData: H4sIAAAAAAAA/53PQQrCMBAF0N8mQo0giEtv4sqFZ3Bb0iRiaDNTkhS8vTaI1KV0OfDfzB8FCOyuOuubi8kzAftXg9pbnIInZ6K+53M3aNO36TENvYttx0+BjeGJMoBKQRkOI5OjnLY4LswwZT1PUc05KSDTwIVAQfrswufOYQHW68Bkf7ZfSr+vq1a6eqUT/7umvPUGDbb6oU0BAAA=
      MaxRange: 100
      Chance: 10
```
- Note: Items used still have different restrictions like Shulker Boxes do not glow for example. If you can't do it in Vanilla Minecraft through /minecraft:give, our plugin can't either.
- Deprecated usage of `Lore` in prizes, You will be given a warning in console if using. `Lore` will be removed in the next major version of Minecraft (1.22)