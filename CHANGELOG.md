### Fixed:
- Issue with ItemBuilder not allowing further modification of the Oraxen items.

<details>
<summary>Previous Update</summary>

### Changes:
- When interacting with a crate previously, it would check both virtual and physical keys, but it was a coin flip on what it would use, the interaction functions the same now as `/crates` in terms of hierarchy... physical keys are checked first then virtual keys.
- If you hold a key that can't open the crate you are looking at, it will instead default to checking your virtual keys if the config option is enabled, if a key is found... it will open the crate using virtual keys. as always, please report any bugs.

### Fixed:
- Cancel the key check event if the checks find a player does not have enough keys.
- Simplify key checks so virtual keys can work, this removes an unneeded physical key check as we were checking it twice? why...
- Config comments for `physical-crate-accepts-physical-keys` and `virtual-crate-accepts-physical-keys` in `config.yml` were incorrect.
</details>