### Added:
- Bumped minecraft version to 1.21.1
  - You should update to 1.21.1 as soon as possible, there is no changes to prevent any plugins from being used on 1.21.1
  - This version is a minor version fixing bugs, it's plug and play.

### Changes:
- Properly relocate the command framework
- CsgoCrate now when the task is finished, sets an item below and above the prize you won.
  - In the future, this will be configurable. I just don't know what to call it.
- Updated permission description
- Updated exclude.give-all to default to FALSE, [#774](https://github.com/Crazy-Crew/CrazyCrates/pull/774)
- Bumped CMI dependencies

### Fixed:
- Typo in config options
- Adventure api issue [#770](https://github.com/Crazy-Crew/CrazyCrates/pull/770)
- CsgoCrate was missing 2 glass panes [#772](https://github.com/Crazy-Crew/CrazyCrates/pull/772)
- CsgoCrate was not animating the glass panes