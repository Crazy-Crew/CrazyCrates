- Added the ability to use `Item Models` which was introduced in 1.21.4
  - This acts as replacement to `Custom-Model-Data` wherever you see it, It's up to the plugin/resource pack you use to find the new id.
    - oraxen:emerald_helmet
    - nexo:emerald_helmet
    - itemsadder:emerald_helmet
  - Above are examples of namespace:id.
```yml
  PhysicalKey:
    # Name of the Key.
    Name: "<bold><gradient:#084CFB:#ADF3FD>Wonder Crate Key</gradient></bold>"
    # Lore of the Key.
    Lore:
      - "<gradient:#084CFB:#ADF3FD>A fancy key to open a wonderful crate!</gradient>"
    # The item the key is.
    Item: "blue_dye"
    # The item model, Mojang introduced this in 1.21.4... this replaces custom model data!
    # Set this to blank for it to do nothing.
    # The format is namespace:id
    Model:
      # The namespace i.e. nexo
      Namespace: ""
      # The id i.e. emerald_helmet
      Id: ""
    # Makes the key look enchanted.
    Glowing: true 
```
- Added the ability to display potion effects in item previews
```yml
# This is an example of the new crate format.
Crate:
  # A list of prizes.
  Prizes:
    "1":
      # The name of the item to display in the gui.
      DisplayName: "<gradient:light_purple:dark_purple>Witch's Potion"
      # The item to display in the gui.
      DisplayItem: "potion"
      # The configuration section for potions, minecraft: is not needed in front of the potion.
      # A list of potion effects https://jd.papermc.io/paper/1.21.4/io/papermc/paper/registry/keys/MobEffectKeys.html
      DisplayPotions:
        # This must be the potion name typed exactly as it is, A list of potions linked above.
        poison:
          # The length of the potion in seconds.
          # https://minecraft.wiki/w/Calculators/Tick
          duration: 20
          # The strength of the potion.
          level: 5
        absorption:
          duration: 30
          level: 5
      # The amount to display in the gui.
      DisplayAmount: 1
      # The lower the number, the less likely to win it.
      Weight: 1.0 
```
- Custom-Model-Data, or custom-model-data can accept numbers or strings due to recent changes in 1.21.4.
- Fixed the /crates additem command