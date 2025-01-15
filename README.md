<center><div align="center">

![CrazyCrates](https://raw.githubusercontent.com/Crazy-Crew/Branding/main/crazycrates/banner/webp/banner.webp)

[![][build-shield]][build-url]
[![][discord-shield]][discord-url]
[![][contributors-shield]][contributors-url]
[![][forks-shield]][forks-url]
[![][stars-shield]][stars-url]
[![][issues-shield]][issues-url]
[![][license-shield]][license-url]
[![][codefactor-shield]][codefactor-url]

</div></center>

CrazyCrates is a crates plugin for Paper based servers that lets you add unlimited crates. It allows you to bring something to the server to spice up your economy and to give your players something to brag about.

## Building
CrazyCrates requires gradle 8.12 to build the plugin.

### Requirements
* Java 21 JDK or newer
* Knowledge of Git

### Compiling from source
```sh
git clone https://github.com/Crazy-Crew/CrazyCrates.git
cd CrazyCrates
./gradlew assemble
```
You'll find the jar in the `jars` folder.

### Contributing
#### Pull Requests
If you have made any changes or improvements which you think could be beneficial to others, please make a pull request, so that the plugin can be improved for everyone using it. (we especially like bug fixes \o/).

##### A list of pointers when editing existing classes
* Copy the style of code in the class you are editing.
* No extra lines at the end of files.
* No extra lines between imports.
* No wildcard imports.

#### Project Structure
The project has been separated into multiple modules for preemptively supporting other platforms.
* Api Module - This module is the API used by other plugins that wish to properly integrate with our plugin, and receive data from CrazyCrates for use in their own plugins. No implementation details are in this module.
* Core Module - This module handles a small portion of the implementation for CrazyCrates, mainly the configuration files. and independent enums or utilities.
  * The module does not yet handle implementation details for each platform.
* Paper Module - This module currently is what provides the implementation for the `API Module`, I have not written an exact abstract module to sit between, and reduce more duplicated code between platforms.
  * Pull Requests that attempt to implement the remaining work of an abstract layer for multi-platform support will likely be ignored, I would like to handle that when the time comes.

#### License
CrazyCrates is a proud user of the MIT license, You can take a little peak at [LICENSE](https://github.com/Crazy-Crew/CrazyCrates/blob/master/LICENSE)

[contributors-shield]: https://img.shields.io/github/contributors/Crazy-Crew/CrazyCrates.svg?style=flat&logo=appveyor
[contributors-url]: https://github.com/Crazy-Crew/CrazyCrates/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/Crazy-Crew/CrazyCrates.svg?style=flat&logo=appveyor
[forks-url]: https://github.com/Crazy-Crew/CrazyCrates/network/members
[stars-shield]: https://img.shields.io/github/stars/Crazy-Crew/CrazyCrates.svg?style=flat&logo=appveyor
[stars-url]: https://github.com/Crazy-Crew/CrazyCrates/stargazers
[issues-shield]: https://img.shields.io/github/issues/Crazy-Crew/CrazyCrates.svg?style=flat&logo=appveyor
[issues-url]: https://github.com/Crazy-Crew/CrazyCrates/issues
[license-shield]: https://img.shields.io/github/license/Crazy-Crew/CrazyCrates.svg?style=flat&logo=appveyor
[license-url]: https://github.com/Crazy-Crew/CrazyCrates/blob/main/LICENSE
[build-shield]: https://ci.crazycrew.us/job/CrazyCrates//badge/icon
[build-url]: https://ci.crazycrew.us/job/CrazyCrates
[discord-shield]: https://img.shields.io/discord/182615261403283459.svg?label=discord&logo=discord
[discord-url]: https://discord.gg/badbones-s-live-chat-182615261403283459
[codefactor-shield]: https://www.codefactor.io/repository/github/crazy-crew/crazycrates/badge
[codefactor-url]: https://www.codefactor.io/repository/github/crazy-crew/crazycrates