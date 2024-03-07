## Additions:
* Added extra placeholders to all messages. The messages.yml will update with new comments showing what each message can use.
* Add config.yml/messages.yml to `examples` folder which auto-generate on reload.

## Changes:
* A few straggling placeholders that were still capitalized like %Crate% or %Player% have been made lowercase, double-check your configs.
* %Keys%, %Keys_Physical% and %Keys_Total% have been made lowercase like %keys% instead. Please double-check your lores.

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