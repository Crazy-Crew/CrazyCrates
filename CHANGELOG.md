### Added:
- Ability to set update intervals for holograms, -1 disables it.
- /crazycrates migrate <crate> <type> which should update materials/enchants/trim materials to mojang mapped,

### Changes:
- Bumped crazycrates api to 0.7
- War/Cosmic Crate listeners for PlayerCloseEvent have been combined and had unnecessary method calls reduced.
- Updated to [2.2.0](https://modrinth.com/plugin/fancyholograms/version/2.2.0) FancyHolograms, they made a breaking change in the api so all previous versions of FancyHolograms will no longer work.

### Fixed:
- War Crate no longer bugs out if you close the inventory.