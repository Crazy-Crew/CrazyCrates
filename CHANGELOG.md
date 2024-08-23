### Added:
- Added a toggle, which allows you to revert the chance made previously.
  - `crazycrates.open.<crate_name>` -> `crazycrates.deny.open.<crate_name>`
- The configuration option can be found in the `config.yml` i.e. `root.use-new-permission-system` which defaults to false
  - This option is subject for removal however toggled like this for now, `false` means the old system i.e. `crazycrates.open.<crate_name>` is back.
  - It'll be removed in the next version of Minecraft!