## Changes
### Configuration changes
- `ChestLines` has been renamed to `Rows` in the crate config files, [5eec53b](https://github.com/Crazy-Crew/CrazyCrates/commit/5eec53b)
  - All existing configurations should continue to work, You can run `/crazyrates migrate -mt CratesDeprecated` to change this, or simply use Find and Replace.

### Other Changes
- Added the ability to customize the row/column of the back/next and menu button.
- Added the ability to customize the CSGO animation to either have the top/bottom be static or dynamic. [95de51e](https://github.com/Crazy-Crew/CrazyCrates/commit/95de41e)
  - If you run /crazycrates migrate -mt CratesDeprecated, The existing options you need will generate. 
  - Please refer to the `examples/crates/CrateExample.yml` folder for a full example of how to do it. 
  - It supports the same structure as the new way to do items in the `Items` section which can be found on the documentation.
- Run the command on click in the Crate Menu, instead of on open.
  - [6c580b1](https://github.com/Crazy-Crew/CrazyCrates/commit/6c580b1)
- Added %chance% / %weight% to the prize/default message.
- Added command syntax suggestions to all existing commands.
- Return "N/A" if the file is empty, because files#getFirst() throws an error because no element found.
- Utilize built in methods from CMI/DecentHolograms to handle coloring the messages for better compatibility with their plugins.
  - This will maybe also allow other plugin specific features like CMI's countless other features for holograms. 
- Remove the brackets from custom name for the item displayed above QuickCrate.
- Fill the bottom border with air to prevent item overfill with the buttons.
- Added toggle to disable opening tracking crate.
  - [76625ba](https://github.com/Crazy-Crew/CrazyCrates/commit/76625ba961367be51a75fc907faa75ad87676b79)

## Fixed
- Fixed an error with WonderCrate.yml on first install, because a prize was lacking an `Items` section.
- Fixed a potential memory leak caused by using double brace initializers.
- Fixed multiple bugs with PlaceholderAPI support.
- Fixed a typo in CosmicCrateManager. [#873](https://github.com/Crazy-Crew/CrazyCrates/commit/876f3a7)
- Fixed a typo with %crate_opened_raw%.
  - [1ef6553](https://github.com/Crazy-Crew/CrazyCrates/commit/1ef6553)
- Fixed an issue with items dropping on Folia. [#856](https://github.com/Crazy-Crew/CrazyCrates/pull/856)
- Fixed an issue by using #runNow instead of #run which calls the internal void method.
- Fixed multiple issues with migration types.
- Fixed multiple issues on Folia due to not using schedulers when needed.
  - [0e139f6](https://github.com/Crazy-Crew/CrazyCrates/commit/0e139f6)
- Fixed an issue with potion color not applying with the new items section.
- Fixed an issue with the shield banners using the new item format.
- Fixed crates debug command.

As always, Report 🐛 to https://github.com/Crazy-Crew/CrazyCrates/issues