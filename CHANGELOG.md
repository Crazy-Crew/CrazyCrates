### Added:
- Ability the ability to make items glow in `Items`
```yml
Items:
  - 'Item:spawner, Glowing:true'
```

### Fixed:
- The config option for `verbose-logging` was not applied to some parts of the plugin.

### Changes:
- No longer add the contents of `DisplayData` to the `Items` section on `/crazycrates reload`
- Lowercase shield pattern types and colors which also fixed a display issue, so previous shield pattern/color configs work. They no longer need to be typed like GRADIENT_UP:LIGHT_GRAY, you can simply type gradient_up:light_gray