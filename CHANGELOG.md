# Changelog

All notable changes to this mod are documented here.
This project adheres to the [KeepAChangelog Convention](https://keepachangelog.com/en/1.1.0/).

## [0.6.60] - 2026-11-21

### Fixed
- Chain blade sweep cooldown was persisted as an absolute wall-clock timestamp, so a clock adjustment or a client/server clock mismatch could leave the weapon locked out for an arbitrary amount of time. It is now stored as a world-tick deadline, consistent with every other cooldown in the mod. Legacy epoch values are tolerated.
- Boomerangs and chain blades left behind in another dimension, unloaded together with their chunk, or lost across a save reload could keep occupying their per-tool active projectile slot and block further throws. Active slots are now checked against the world's live entity registry, so stale entries are discarded.
- The per-item throw limit never actually held. Active projectiles were keyed by an object-identity string inside a weak map, so every entry could be reclaimed by any garbage collection and the limit silently stopped applying. Tools now carry a persistent id, so a boomerang holds 1 active projectile per capacity level and a chain blade exactly 1, independently of any other copy of the same tool.
- Heat ray gun overheat smoke was never visible to any client: it was emitted on the server through an API that only notifies client-side world listeners. It is now dispatched as a server particle broadcast, collapsing eight per-particle calls into a single packet.
- The chain blade's chain line left a doubled line width behind in the GL state, affecting every line rendered afterwards. It is now reset.

### Changed
- Nearest-target selection for boomerang bounces and chain blade re-targeting now uses a single pass instead of two full stream traversals or a full list sort. Behaviour is unchanged.
- Removed unused imports and the empty, never-enabled Mixin configuration file.

### Removed
- Leftover `Return Damage` localisation entries. The effect has long been part of the boomerang entity itself.
