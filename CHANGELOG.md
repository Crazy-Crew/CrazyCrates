### Fixed:
- Re-worked how display names are handled for items/previews

```yml
    '1':
      # The item to display/give in the gui.
      DisplayItem: "diamond"
      # The amount to display/give
      DisplayAmount: 3
      # The max range i.e. 25/100 = 15% chance to win.
      MaxRange: 100
      # The chance to win i.e. 25%
      Chance: 25
```
This format now works properly and stacks with vanilla items.