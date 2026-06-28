# Sable Hero Animation Style Test

Last updated: 2026-06-27

## Decision

This Sable test is the current approved direction for BV-048 style exploration: a detailed portrait can coexist with a simpler, chibi-like Live-Lite sprite, close to the classic RPG split between portrait and field model. The sprite only needs to read as the hero/class at phone size; for Sable, reading as a coherent Ninja is acceptable as long as the same head, hood, costume, palette, scarf, clasp, and daggers persist across every pose.

This is not accepted runtime animation coverage yet. It is a visual-method proof before producing final sheets or updating the manifest to `accepted`.

## Tools Used

- Built-in Codex `image_gen` image generation.
- Project-local copy of the generated source image under `docs/art/hero-animations/sable/`.
- PowerShell with `System.Drawing` for local post-processing:
  - sample the flat chroma-key background from the top-left pixel;
  - remove near-magenta background pixels to alpha;
  - split the six-pose horizontal sheet into per-pose crops;
  - generate a 64 px and 48 px phone-readability preview.

## Prompt Technique

Generate one horizontal key-pose sheet first, not a full animation pack. The prompt must lock identity before motion:

- single hero only;
- six full-body poses in one row: idle, walk, fight, hurt/down, celebrate, loot/interact;
- same exact head, hood, mask, costume, palette, scarf, clasp, and daggers in every pose;
- polished 16-bit-inspired pixel art with chunky readable shapes;
- no portrait frame, UI card, labels, text, or background props;
- perfectly flat solid `#ff00ff` chroma-key background;
- do not use `#ff00ff` inside the character.

## Output Files

- `sable-keypose-source.png`: generated source sheet.
- `sable-keypose-cutout.png`: chroma-keyed source with alpha.
- `sable-keypose-idle.png`
- `sable-keypose-walk.png`
- `sable-keypose-fight.png`
- `sable-keypose-hurt_down.png`
- `sable-keypose-celebrate.png`
- `sable-keypose-loot_interact.png`
- `sable-keypose-phone-preview.png`: 64 px and 48 px readability check.

## Next Production Step

If this method remains accepted after review, use it for the first real pack by generating or editing one hero at a time, then converting approved key poses into consistent state sheets. Do not batch the roster until one full hero pack passes visual review, manifest validation, and Live-Lite runtime evidence.
## Candidate V1 Full Animation Sheet

After the key-pose direction was accepted, the next pass used the same prompt discipline to generate a full six-row by six-column animation sheet candidate. This pass is still review art, not runtime-accepted coverage.

Additional prompt constraints used for this pass:

- six horizontal rows by six columns;
- rows are ordered as `idle`, `walk`, `fight`, `hurt/down`, `celebrate`, `loot/interact`;
- keep Sable facing right by default;
- keep baseline and proportions consistent enough that the rows can be split into state strips;
- define the motion in the prompt, not only the pose names: breathing/scarf fidget, step cycle, dagger slash, hit-to-down settle, small victory jump, crouch/search interaction.

Additional output files:

- `sable-animation-sheet-candidate-v1-source.png`: generated full animation sheet source.
- `sable-animation-sheet-candidate-v1-cutout.png`: chroma-keyed full sheet with alpha.
- `sable-animation-v1-idle.png`
- `sable-animation-v1-walk.png`
- `sable-animation-v1-fight.png`
- `sable-animation-v1-hurt_dead.png`
- `sable-animation-v1-celebrate.png`
- `sable-animation-v1-loot_interact.png`
- `sable-animation-v1-phone-preview.png`: 64 px and 48 px frame readability review.

If the visual review passes, the next technical step is to normalize each state strip to the manifest's frame cell size before exporting under `app/src/main/res-heroes/drawable-nodpi/`.
## Runtime Live Test Export

The live test exports the candidate sheet into Android-ready strips without marking the pack final accepted.

Tool added:

- `tools/normalize-hero-animation-sheet.ps1`

Normalization behavior:

- input: alpha cutout sheet such as `sable-animation-sheet-candidate-v1-cutout.png`;
- assumes six rows and six columns by default;
- row order: `idle`, `walk`, `fight`, `hurt_dead`, `celebrate`, `loot_interact`;
- finds the foreground bounds in each cell using alpha;
- keeps per-row scale consistent;
- preserves baseline-relative vertical movement so jumps and down poses still move;
- exports Android strips at 128 px frame height under `app/src/main/res-heroes/drawable-nodpi/`;
- generates `sable-runtime-normalized-preview.png` for 64 px / 48 px review.

Runtime candidate exports:

- `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_idle.png`
- `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_walk.png`
- `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_fight.png`
- `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_hurt_dead.png`
- `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_celebrate.png`
- `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_loot_interact.png`

Data status:

- use `candidate_bv048` while the asset is wired for live review but not yet final accepted roster coverage;
- validator must require candidate runtime PNGs to exist and use 128 px frame geometry;
- only switch to `accepted` after visual review and runtime evidence pass.
## Live Runtime Evidence

Sable candidate v1 is wired as a review-only runtime pack using `candidate_bv048` status.

- Runtime resources: `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_{idle,walk,fight,hurt_dead,celebrate,loot_interact}.png`
- Export tool: `tools/normalize-hero-animation-sheet.ps1`
- Phone/runtime preview: `docs/art/hero-animations/sable/sable-runtime-normalized-preview.png`
- Emulator captures: `artifacts/screenshots/sable-live-watch.png`, `artifacts/screenshots/sable-live-watch-2.png`
- Motion proof: fresh Watch UI verification showed `ui_watch=True`, `ui_report_ready=False`, `ui_moonlit=True`, `ui_sable=True`; full Live-Lite stage pixels changed `319/408040` between the two captures.
- Save backup before injection: `artifacts/screenshots/badventurers_session_before_sable_live.xml`

This remains a Sable live candidate for BV-048 review, not an accepted final starter-trio pack.
## Walk Cleanup After Live Review

The first runtime walk export was rejected in live review. It preserved source horizontal drift, and the source row bled across equal-width cells, so the Watch screen read as one Sable sliding left with a second partial Sable visible near the same position.

Correction applied:

- Preserved the bad runtime strip at `docs/art/hero-animations/sable/sable-runtime-walk-before-cleanup.png`.
- Added `tools/clean-hero-animation-strip.ps1` to keep the main frame component, remove neighboring-cell bleed, recenter frames, and replace weak partial frames through an explicit frame map.
- Cleaned walk output: `docs/art/hero-animations/sable/sable-runtime-walk-cleaned.png`.
- Runtime replacement: `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_walk.png`.
- Updated preview: `docs/art/hero-animations/sable/sable-runtime-cleaned-preview.png`.
- Corrected emulator captures: `artifacts/screenshots/sable-live-watch-cleaned.png`, `artifacts/screenshots/sable-live-watch-cleaned-2.png`.
- Motion proof after reinstall: fresh Watch verification showed `ui_watch=True`, `ui_report_ready=False`, `ui_moonlit=True`, `ui_sable=True`; full Live-Lite stage pixels changed `5185/408040` between the two captures.

Pipeline rule added: runtime walk strips should animate pose around a stable center. The Live-Lite lane owns horizontal travel; the strip export must not preserve large source-cell horizontal drift.
## Stabilization After Video Review

A later live screenshot/video review still showed three visible problems:

- the walk read as too jittery at phone size;
- the Live-Lite progress marker looked like a floating dot above Sable's head;
- chroma-key residue left bright magenta pixels in the runtime strips.

Correction applied:

- Removed the Live-Lite route progress marker from `LiveLiteArt.kt`; hero position now carries route progress visually.
- Slowed the shared Live-Lite animation loop from `780 ms` to `960 ms`.
- Removed bright magenta key residue from all six Sable runtime PNGs; validation scan returned `magentaLike=0` for each strip.
- Preserved the pre-stabilized walk at `docs/art/hero-animations/sable/sable-runtime-walk-before-stabilize.png`.
- Replaced runtime walk with `docs/art/hero-animations/sable/sable-runtime-walk-stabilized.png`, a stable centered six-frame strip derived from one canonical Sable frame with only a tiny bob/lower-body shift.
- Updated preview: `docs/art/hero-animations/sable/sable-runtime-stabilized-preview.png`.
- Runtime evidence: `artifacts/screenshots/sable-live-stabilized.png` and `artifacts/screenshots/sable-live-stabilized.mp4`.

This pass intentionally favors visual stability over expressive animation. Sable remains `candidate_bv048`; final acceptance still requires a better authored walk cycle, not a stabilized salvage strip.
## Full-State Anchor Fix After Loop-Slide Feedback

A later video review still showed a left-slide effect during the loop. The remaining drift came from the other candidate strips, not just `walk`: several states still had large per-frame `centerX` movement inside the 128 px cell.

Correction recipe:

```powershell
$states = @("idle", "walk", "fight", "hurt_dead", "celebrate", "loot_interact")
foreach ($state in $states) {
  $src = "app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_${state}.png"
  $backup = "docs/art/hero-animations/sable/sable-runtime-${state}-before-anchorfix.png"
  $out = "docs/art/hero-animations/sable/sable-runtime-${state}-anchored.png"
  Copy-Item -LiteralPath $src -Destination $backup -Force
  .\tools\clean-hero-animation-strip.ps1 -InputPath $src -OutputPath $out -MinComponentPixels 20 -MinUsableMainPixels 500 -MinUsableMainWidth 8 -MaxDetachedCenterDistance 60
  Copy-Item -LiteralPath $out -Destination $src -Force
}
```

Post-fix runtime metrics:

- `idle`: `centerX` drift `1 px`, `bottomRange=0`, `magentaLike=0`.
- `walk`: `centerX` drift `0.5 px`, `bottomRange=0`, `magentaLike=0`.
- `fight`: `centerX` drift `0.5 px`, `bottomRange=0`, `magentaLike=0`.
- `hurt_dead`: `centerX` drift `1 px`, `bottomRange=0`, `magentaLike=0`.
- `celebrate`: `centerX` drift `1 px`, `bottomRange=0`, `magentaLike=0`.
- `loot_interact`: `centerX` drift `1 px`, `bottomRange=0`, `magentaLike=0`.

Preview: `docs/art/hero-animations/sable/sable-runtime-anchorfix-preview.png`.

Validator result: `tools/validate-hero-animations.ps1` passes with no Sable warnings.
## Stills-Based Runtime Export

After live review showed the normalized/salvaged runtime strips still sliding, Sable moved to a deterministic stills-based export:

```powershell
.\tools\build-sable-animation-from-stills.ps1
```

The exporter treats `sable-animation-sheet-candidate-v1-source.png` as six rows of still poses, one row per required runtime state. It removes broad magenta-like chroma, finds a stable body anchor per cell, applies one global scale, and renders 128 px strips with a fixed baseline. This intentionally keeps horizontal travel owned by the Live-Lite stage, not by the sprite sheet.

Current outputs:

- Runtime strips: `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_{idle,walk,fight,hurt_dead,celebrate,loot_interact}.png`
- Review preview: `docs/art/hero-animations/sable/sable-runtime-stills-preview.png`
- Metrics: `docs/art/hero-animations/sable/sable-runtime-stills-metrics.csv`
- Live evidence: `artifacts/screenshots/sable-stills-live.mp4`

Quality gates for this method:

- all runtime strips must have `magentaPixels=0` under the broad magenta-like scan;
- feet/baseline should stay within 0-1 px across frames;
- body travel inside the strip should come from pose silhouette changes, not preserved source-cell offsets;
- keep Sable as `candidate_bv048` until the live loop is visually approved.
Ghost cleanup rule: after rendering each 128 px frame, remove strict magenta-like fringe pixels and discard detached components that sit above the body or form a separate half-body to the left of the centered main component. This rule was added after live review showed two small bars above Sable and a trailing half-character ghost even though the Live-Lite route movement itself was correct.
Five-frame export rule: this Sable source sheet has a bad 6th column in each row after cleanup, so the runtime exporter keeps the 6-column source grid for analysis but renders only columns 0-4. The output strips are `640x128` and Live-Lite derives `frameCount=5` from the PNG width.