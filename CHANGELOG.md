# Changelog

All notable changes to this mod are documented here.
This project adheres to the [KeepAChangelog Convention](https://keepachangelog.com/en/1.1.0/).

## [0.7.1] - 2026-11-25

### Changed
- Material registration was consolidated into a single declarative table. All part stats now go through `TinkerRegistry.addMaterialStats` (fires `StatRegisterEvent`, refuses to silently overwrite registrations from other mods, and patches to existing Tinkers' materials only fill in missing stats). Part items no longer carry their own material whitelists - acceptance now derives from the registered stats alone. Gameplay data is unchanged.

### Fixed
- A boomerang thrown into water without the Fins modifier stalled a few blocks in and hung motionless, then despawned after half a minute and was lost together with the ammo it consumed. The boomerang now turns back the moment it loses way, crawling back to its owner. With Fins it still cruises through water at full speed.

## [0.7.0] - 2026-11-24

### Added
- `Gadgets & Gizmos` / `奇械图鉴`, the addon's own guide book. Crafted shapelessly from a book and a Connector of any material (NBT-insensitive, so every material works with a single recipe), it covers the mod's tools, modifiers and per-part material tables, with bilingual `en_us` and `zh_cn` text.
- Four chapters in the book, all generated from the live Tinkers' registries at open time rather than duplicated as JSON: an introduction, tools (part lists derived from each tool's build components), modifiers, and material tables laid out like the vanilla arrow shaft pages - one icon overview (split across pages when needed) plus three materials per page, with each material's traits hoverable for their description.

### Changed
- The whole book now follows the voice of Materials and You: two-sentence tool blurbs that state what the tool is and which part decides what, modifier pages that pair a one-line summary with a bare-numbers Effects list, and an introduction written as a preface instead of a feature list.
- Copy is sized to the boxes Mantle actually draws - tool blurb 172x50, modifier prose 162x50, effect bullets 96px wide - so nothing is cut off mid-sentence. Chinese pages keep Latin names and ASCII spaces out of running text: the book's line wrapper restarts at every space, which is why "Tinkers' Gadgets" used to break over three lines in the middle of a sentence.
- Introduction paragraphs are separated with the `paragraph` flag instead of empty lines, so they read as paragraphs rather than one block.
- Book-only copy lives in the book's own language files and falls back to the modifier tooltip for locales that have not been written yet.
- The book is a completely separate `BookData` with its own resource repository, so Materials and You is never touched, extended or re-transformed.
- Nothing is parsed until the book is first opened: registering it only adds a reload-aware registry entry, keeping constant overhead at zero.

### Fixed
- Every part had its material overview page twice. `ContentPageIconList.getPagesNeededForItemCount` already creates and appends the pages it returns, so adding them again duplicated each one; the transformer now links the pages the helper made.

## [0.6.61] - 2026-11-22

### Fixed
- A boomerang restored from NBT without a live shooter could crash the server when Fracture triggered its split. The split now aborts safely when no shooter is present.

## [0.6.60] - 2026-11-21

### Fixed
- The per-item throw limit never actually held. Active projectiles were keyed by an object-identity string inside a weak map, so every entry could be reclaimed by any garbage collection and the limit silently stopped applying. Tools now carry a persistent id, so a boomerang holds 1 active projectile per capacity level and a chain blade exactly 1, independently of any other copy of the same tool.
- Boomerangs and chain blades left behind in another dimension, unloaded together with their chunk, or lost across a save reload could keep occupying their per-tool active projectile slot and block further throws. Active slots are now checked against the world's live entity registry, so stale entries are discarded.
- The chain blade's chain line left a doubled line width behind in the GL state, affecting every line rendered afterwards. It is now reset.

### Removed
- Leftover `Return Damage` localisation entries. The effect has long been part of the boomerang entity itself.
- Unused imports and the empty, never-enabled Mixin configuration file.

## [0.6.59] - 2026-11-20

### Fixed
- Chain blade sweep cooldown was persisted as an absolute wall-clock timestamp, so a clock adjustment or a client/server clock mismatch could leave the weapon locked out for an arbitrary amount of time. It is now stored as a world-tick deadline, consistent with every other cooldown in the mod. Legacy epoch values are tolerated.
- Boomerangs and chain blades left behind in another dimension, or lost across a save reload, could keep occupying their per-tool active projectile slot and block further throws. Entries belonging to a different world are now discarded.
- Heat ray gun overheat smoke was never visible to any client: it was emitted on the server through an API that only notifies client-side world listeners. It is now dispatched as a server particle broadcast, collapsing eight per-particle calls into a single packet.

### Changed
- Nearest-target selection for boomerang bounces and chain blade re-targeting now uses a single pass instead of two full stream traversals or a full list sort. Behaviour is unchanged.
