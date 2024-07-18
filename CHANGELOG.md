### Fixed:
- Re-worked how display names are handled for items/previews, This format now works properly and stacks with vanilla items.
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
- Applied a bandaid to quadcrates
- Use correct crate name for {crate} when a crate location already exists in `/crazycrates set <crate>`
- Fixed npe with /keys view, player name wasn't supplied, so it freaked out.
- Fixed npe with placeholder parsing in messages.

### Changes:
- Optimize display reward above quad/quick crate