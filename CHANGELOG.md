# Changelog

All notable changes to this mod are documented here.
This project adheres to the [KeepAChangelog Convention](https://keepachangelog.com/en/1.1.0/).

## [0.6.59] - 2026-11-20

### Fixed
- Chain blade sweep cooldown was persisted as an absolute wall-clock timestamp, so a clock adjustment or a client/server clock mismatch could leave the weapon locked out for an arbitrary amount of time. It is now stored as a world-tick deadline, consistent with every other cooldown in the mod. Legacy epoch values are tolerated.
- Boomerangs and chain blades left behind in another dimension, or lost across a save reload, could keep occupying their per-tool active projectile slot and block further throws. Entries belonging to a different world are now discarded.
- Heat ray gun overheat smoke was never visible to any client: it was emitted on the server through an API that only notifies client-side world listeners. It is now dispatched as a server particle broadcast, collapsing eight per-particle calls into a single packet.

### Changed
- Nearest-target selection for boomerang bounces and chain blade re-targeting now uses a single pass instead of two full stream traversals or a full list sort. Behaviour is unchanged.
