## Additions:
* Added extra placeholders to all messages. The messages.yml will update with new comments showing what each message can use.
* Add config.yml/messages.yml to `examples` folder which auto-generate on reload.

## Changes:
* All internal placeholders in the plugin have been changed.
  * %player% is now {player}
  * %crate% is now {crate}
  * %amount% is now {amount}
  * %key% is now {key}
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
* Play sounds in /crazycrates admin when a player gets virtual/physical keys.
* Change from sending messages in chat for /crazycrates admin to using action bars.
* Optimize item meta checks.
* If the crate main menu is turned off, Trying to do /crates set menu will send you a message saying you can't.
* If the crate main menu is disabled, /crates will simply open the help message instead.

## Fixes:
* Remove player from crate/page/preview arrays on inventory close event as it wasn't before.
* Fix the player getting a preview message if they weren't in the anyway preview when you did /crates reload.

## Other:
* [Feature Requests](https://github.com/Crazy-Crew/CrazyCrates/issues)
* [Bug Reports](https://github.com/Crazy-Crew/CrazyCrates/issues)