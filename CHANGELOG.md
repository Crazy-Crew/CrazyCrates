## Breaking Changes:
**Do not use this instantly on a live server, This is your warning, and I am not responsible for anything.**

Warning, Any keys given to a player will likely not work on older versions. This is not backwards compatible.<br>
I recommend taking a backup of your server prior and restore if needed.<br>
Physical Keys and the data to identify what is a physical key has changed.<br>
Older keys will still work temporarily but please get your players to trade your old physical keys for new ones.

* Replaced all instances of NBT-API with PersistentDataContainer
* We do not check if a key has lore/names when checking if you have a valid key because that makes the point of PersistentDataContainer redundant.
* The ItemBuilder has been updated to a slightly more modern version in preparation for MiniMessage support.

## Additions:
* Added extra placeholders to all messages. The messages.yml will update with new comments showing what each message can use.
* Add config.yml/messages.yml to `examples` folder which auto-generate on reload.
* Added a toggle to allow legacy key checks for niche use cases.
* Added default commands to run per crate if no prize commands are found similar to `Prize-Messages`
* Added %reward_stripped% which returns a stripped version of the reward for plugins like DiscordSRV

## Changes:
* All internal placeholders used in config.yml and messages.yml have changed.
  * %player% is now {player}
  * %crate% is now {crate}
  * %amount% is now {amount}
  * %key% is now {key}
  * %keys% is now {keys}
  * %page% is now {page}
  * %prefix% is now {prefix}
  * %world% is now {world}
  * %cratetype% is now {cratetype}
  * %prize% is now {prize}
  * %number% is now {number}
  * %keytype% is now {keytype}
  * %usage% is now {usage}
  * %key-amount% is now {key_amount}
  * %crates_opened% is now {crates_opened}
  * %id% is now {id}

## Enhancements:
* Added a warning on startup if the spawn protection in server.properties isn't 0.
* Prevent pistons from moving blocks if they are a crate.
* Play sounds in /crazycrates admin when a player gets virtual/physical keys.
* Change from sending messages in chat for /crazycrates admin to using action bars.
* Optimize item meta checks.
* If the crate main menu is turned off, Trying to do /crates set menu will send you a message saying you can't.
* If the crate main menu is disabled, /crates will simply open the help message instead.

## API:
* Deprecated and marked for removal `CrazyCratesService` and `ICrazyCrates`
  * The example plugin has been updated. https://github.com/Crazy-Crew/ExamplePlugin
* What else will be added in the API? Ways to add your own crate locations, view current crate locations or listen to events etc.

## Fixes:
* Remove player from crate/page/preview arrays on inventory close event as it wasn't before.
* Fix the player getting a preview message if they weren't in the preview when you did /crates reload.
* Fixed a bug related to CrateOnTheGo where the event would fire twice using 2 of your crates.

## Other:
* [Feature Requests](https://github.com/Crazy-Crew/CrazyCrates/issues)
* [Bug Reports](https://github.com/Crazy-Crew/CrazyCrates/issues)
