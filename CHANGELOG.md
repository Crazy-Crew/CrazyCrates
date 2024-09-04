### Fixed:
- Fixed an issue with some commands not properly validating virtual keys

### Deprecation
- Deprecated `PlayerPrizeEvent(player, crate, crateName, prize)`
  - Please use `PlayerPrizeEvent(player, crate, prize)` as the crateName is already passed through via the `crate` object.