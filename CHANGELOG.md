### Removed:
- All legacy color codes are removed, I do not want to maintain it anymore.

### Fixed:
- CSGO Crate animation was delayed by 1 tick, for some reason.

### Changes:
- Updated Vital API.
- use getOrDefault on PersistentDataContainer, to avoid some useless NPE checks.
- Added deprecation notices for `Editor-Items`, as I also do not want to maintain that anymore.
  - `Editor-Items` should continue to work, however... no new items can be added with it.
  - I cannot keep `Editor-Items`, if I don't keep legacy color codes which I don't want to keep.
  - A different format may be supplied, if people don't like the base64, however there is tools to decode base64/bytes