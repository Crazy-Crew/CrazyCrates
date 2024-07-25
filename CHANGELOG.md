### Added:
- Added missing configurable messages to places around the plugin, all messages should now be configurable.
- Added a new config option where you can decide to send messages in chat or in the actionbar.
  - Messages that send a list to chat will by default never be sent to actionbar as it would not look pretty.
- Added a new placeholder, `{required_amount}` to `crates.requirements.not-enough-keys`
- Added another new placeholder, `{key}` to `crates.requirements.not-enough-keys`, [#756](https://github.com/Crazy-Crew/CrazyCrates/issues/756)
  - This placeholder returns the name of the key.

### Changes:
- The permission check for whether a player can open a crate has been changed.
  - `crazycrates.open.<crate_name>` is now `crazycrates.deny.open.<crate_name>`
  - The crate name is case-sensitive, so it must match exactly the crate name in the `crates` folder
  - If the file name is CrateBeans.yml, it must be `crazycrates.deny.open.CrateBeans`
- If a message in the `messages.yml` is blank, it will not send the message.
- Update default message for `crates.crate-no-permission`
- Update some comments because of grammar.
- Update logger message when the `CrateOpenEvent` is cancelled to be more verbose.

### Deprecations:
- Deprecated `{key_amount}` and replaced it with `{required_amount}` in `crates.requirements.not-enough-keys`
  - `{key_amount}` will stop working in the next major version of Minecraft.