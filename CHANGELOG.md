### Changes:
- Updated `use-different-random` comment in `config.yml`.
- Add more verbose logging, and an extra safety net to Casino Crate.
  - This requires `is_verbose` set to true in `vital.yml`

### Fixed:
- Properly including calculate the total weight of the tiers for casino crate.
- Properly calculate the per tier prize pools total weight, for things like Casino or Cosmic Crate.