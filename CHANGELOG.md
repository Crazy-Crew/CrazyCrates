### Added:
- Added missing configurable messages to places around the plugin, all messages should now be configurable.
- Added a new config option where you can decide to send messages in chat or in the actionbar.
  - Messages that send a list to chat will by default never be sent to actionbar as it would not look pretty.
- Added a new placeholder, `{required_amount}` to `crates.requirements.not-enough-keys`
- Added another new placeholder, `{key}` to `crates.requirements.not-enough-keys`, [#756](https://github.com/Crazy-Crew/CrazyCrates/issues/756)
  - This placeholder returns the name of the key.
- Added a new migration type which converts deprecated fields in the crate files.
- Added 2 new toggles to the `config.yml` which you can find at the top of the file.
  - The `use-old-editor` requires `use-minimessage` to be false as it's uses legacy color codes.
- Added per prize broadcast, this will send a message to every player on the server.
```yml
    '5':
      # The display name of the item.
      DisplayName: "<yellow>$1,000"
      # The item to display in the gui.
      # The enchanted book will function with the enchants properly in an anvil.
      DisplayItem: "sunflower"
      # Prize settings
      Settings:
        # The custom model data of the item, -1 is disabled.
        Custom-Model-Data: -1
        # Broadcast a message to the server
        Broadcast:
          # If the messages should be sent.
          Toggle: false
          # The messages to broadcast.
          Messages:
            - '<red>%player% won the prize <yellow>%reward%.'
          # If the player has this permission, they don't get the broadcast.
          Permission: 'your_permission' 
```
- Added optional arg for `Player` with crazycrates debug, so you can use it in console.
- Added missing message notifying an item was added using /crates additem

### Changes:
- The permission check for whether a player can open a crate has been changed.
  - `crazycrates.open.<crate_name>` is now `crazycrates.deny.open.<crate_name>`
  - The crate name is case-sensitive, so it must match exactly the crate name in the `crates` folder
  - If the file name is CrateBeans.yml, it must be `crazycrates.deny.open.CrateBeans`
- If a message in the `messages.yml` is blank, it will not send the message.
- Update default message for `crates.crate-no-permission`
- Update some comments because of grammar.
- Update logger message when the `CrateOpenEvent` is cancelled to be more verbose.

### Fixed:
- Wheel Crate animation now spins properly. [#764](https://github.com/Crazy-Crew/CrazyCrates/pull/764)
- Roulette Crate inventory size is now normal. [#765](https://github.com/Crazy-Crew/CrazyCrates/pull/765)
- Don't give 2 prizes if the editor items isn't empty.
- Casino/Cosmic crate tier previews would share total items causing pagination to appear despite the inventory not being full.

### Deprecations:
- Deprecated `{key_amount}` and replaced it with `{required_amount}` in `crates.requirements.not-enough-keys`
  - `{key_amount}` will stop working in the next major version of Minecraft.