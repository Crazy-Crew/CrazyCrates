### Added:
- 1.21 support

### Removed:
- 1.20.6 support

### Changes:
- Updated the output of `/crazycrates additem` again to create an `Items` section with the `Item` you held in your hand
- All pre-existing options in your prizes if they have `DisplayData` will create an `Items` section.
- The purpose of `DisplayData` should only be for what is outputted in the Crate previews however you can still remove `Items` if you want the existing options to function as the prize as well.
```yml
    '7':
      DisplayName: '<red>This is a name.'
      DisplayLore:
        - '<yellow>This is a lore.'
      DisplayEnchantments:
        - 'sharpness:5'
      # This only acts as the base display item which can be modified even after as you can see directly above.
      DisplayData: H4sIAAAAAAAA/53PQQrCMBAF0N8mQo0giEtv4sqFZ3Bb0iRiaDNTkhS8vTaI1KV0OfDfzB8FCOyuOuubi8kzAftXg9pbnIInZ6K+53M3aNO36TENvYttx0+BjeGJMoBKQRkOI5OjnLY4LswwZT1PUc05KSDTwIVAQfrswufOYQHW68Bkf7ZfSr+vq1a6eqUT/7umvPUGDbb6oU0BAAA=
      MaxRange: 100
      Chance: 10
      # This is what the plugin will try to give when winning this prize.
      Items:
        - 'Data:H4sIAAAAAAAA/53PQQrCMBAF0N8mQo0giEtv4sqFZ3Bb0iRiaDNTkhS8vTaI1KV0OfDfzB8FCOyuOuubi8kzAftXg9pbnIInZ6K+53M3aNO36TENvYttx0+BjeGJMoBKQRkOI5OjnLY4LswwZT1PUc05KSDTwIVAQfrswufOYQHW68Bkf7ZfSr+vq1a6eqUT/7umvPUGDbb6oU0BAAA='
```