[![crazycrates](https://raw.githubusercontent.com/RyderBelserion/Assets/main/crazycrew/banners/CrazyCrates.png)](https://modrinth.com/plugin/crazycrates)

<div align="center">

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![GPU License][license-shield]][license-url]
[![Contact][discord-shield]][discord-url]
![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/crazy-crew/crazycrates/features/multi-platform?style=for-the-badge)

  <p align="center">
    This plugin adds the ability to create unlimited crates with an extensive configuration to customize the entirety of the plugin.
    <br />
    <a href="https://github.com/Crazy-Crew/Crazy-Crates/wiki"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/Crazy-Crew/Crazy-Crates/issues">Report Bug</a>
    ·
    <a href="https://github.com/Crazy-Crew/Crazy-Crates/discussions/categories/feature-rquests">Request Feature</a>
    ·
    <a href="https://github.com/Crazy-Crew/Crazy-Crates/discussions/categories/support">Get Support</a>
  </p>
  
  [![download](https://github.com/modrinth/art/blob/main/Branding/Badge/badge-dark__184x72.png)](https://modrinth.com/plugin/crazycrates)

  [![Join us on Discord](https://discord.com/api/guilds/182615261403283459/widget.png?style=banner2)](https://purpurmc.org/discord)

</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#api">API</a></li>
  </ol>
</details>

## Roadmap
Check our public [trello board](https://trello.com/b/AJvEmcbL) for our current plans, any bugs and general tracking of the project progress.

## Getting Started

How to get started with contributing / maintaining your own version of CrazyCrates!

### Prerequisites

A list of things necessary to make sure you can build CrazyCrates properly.
* Intellij IDEA
    * https://www.jetbrains.com/idea/download/
* Gradle

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/Crazy-Crew/Crazy-Crates.git
   ```
2. Open the repository using Intellij IDEA & wait until it finishes downloading/indexing.
3. Run the reobfJar task.
   ```gradle
   ./gradlew reobfJar
   ```

## Contributing

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/yourfeature`)
3. Commit your Changes (`git commit -m 'your amazing feature.'`)
4. Push to the Branch (`git push origin feature/yourfeature`)
5. Open a Pull Request

## License

Distributed under the GPU v3 License. See `LICENSE.MD` for more information.

## API

<details>
  <summary>Maven</summary>

   ```
   <repository>
        <id>crazycrew</id>
        <url>https://repo.badbones69.com/snapshots</url>
   </repository>
  ```
  ```
  <dependency>
      <groupId>com.crazycrates</groupId>
      <artifactId>crazycrates-api</artifactId>
      <version>2.11.1-SNAPSHOT</version>
      <scope>provided</scope>
   </dependency>
  ```

</details>

<details>
  <summary>Gradle</summary>

  ```
   repositories {
       maven("https://repo.badbones69.com/snapshots")
   }
  ```

  ```
   dependencies {
       compileOnly("com.crazycrates:crazycrates-api:2.11.1-SNAPSHOT")
   }
  ```

</details>

## Information
<details>
  <summary>Commands</summary>
  
  <center> <a href="https://github.com/Crazy-Crew/Crazy-Crates/wiki/Commands-and-Permissions">https://github.com/Crazy-Crew/Crazy-Crates/wiki/Commands-and-Permissions</a> </center>
</details>

<details>
  <summary>Dependencies</summary>
  None
</details>

<details>
  <summary>Permissions</summary>

  <center> <a href="https://github.com/Crazy-Crew/Crazy-Crates/wiki/Commands-and-Permissions">https://github.com/Crazy-Crew/Crazy-Crates/wiki/Commands-and-Permissions</a> </center>
</details>

[discord-shield]: https://img.shields.io/discord/182615261403283459.svg?style=for-the-badge
[discord-url]: https://discord.badbones69.com

[contributors-shield]: https://img.shields.io/github/contributors/Crazy-Crew/Crazy-Crates.svg?style=for-the-badge
[contributors-url]: https://github.com/Crazy-Crew/Crazy-Crates/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/Crazy-Crew/Crazy-Crates.svg?style=for-the-badge
[forks-url]: https://github.com/Crazy-Crew/Crazy-Crates/network/members
[stars-shield]: https://img.shields.io/github/stars/Crazy-Crew/Crazy-Crates.svg?style=for-the-badge
[stars-url]: https://github.com/Crazy-Crew/Crazy-Crates/stargazers
[issues-shield]: https://img.shields.io/github/issues/Crazy-Crew/Crazy-Crates.svg?style=for-the-badge
[issues-url]: https://github.com/Crazy-Crew/Crazy-Crates/issues
[license-shield]: https://img.shields.io/github/license/Crazy-Crew/Crazy-Crates.svg?style=for-the-badge
[license-url]: https://github.com/Crazy-Crew/Crazy-Crates/blob/master/LICENSE.MD
