- Fixed an issue with Glowing:true/false again which caused items to show up as red terracotta
- Added a new way to define color/rgb for things like potions, leather_armor etc which should fix the `potion#10040:229,164,229` issue trying to stack model data and color/rgb
```yml
    "8":
      # The name of the item to display in the gui.
      DisplayName: "<red>Red Potion"
      # The item to display in the gui.
      DisplayItem: "potion"
      # Prize display preview settings
      Settings:
        # The custom model data of the item, -1 is disabled.
        Custom-Model-Data: -1
        # The rgb used for the potion.
        RGB: '229,164,229'
        # Color: RED
      # The amount to display in the gui.
      DisplayAmount: 1
      # The lower the number, the less likely to win it.
      Weight: 15.0
      # The list of items to win.
      Items:
        - "Item:potion, RGB:229,164,229" # Color:RED
```