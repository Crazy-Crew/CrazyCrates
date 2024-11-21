### Changes:
- Allow mass-open to be used on the following crate types:
  - QuadCrates
  - CasinoCrate
  - CosmicCrate
  - FireCracker
  - QuickCrate
- Updated logging messages for tiers in CasinoCrate
  - Only checking if a tier is null, rather than if the config option is empty, then checking if a tier is null.
- Moved calling the player prize event, and spawning fireworks to the method which gives the prize.

### Fixed:
- The migrator for `ExcellentCrates` was setting to `Chance` not `Weight` in our crate configs during migration.