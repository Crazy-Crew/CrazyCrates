### Added:
- Ability to migrate crate configurations from ExcellentCrates.
  - active locations from ExcellentCrates are also migrated!

### Changes:
- Check if the prizes section is empty before opening a crate, previews unneeded chance calculation.
- Overhauled the `/crazycrates migrate` command, sends a more detailed message of what was migrated. 
  - files that show up red failed to migrate while files that are green succeeded
  - it also tells you the migration type you picked when running the command