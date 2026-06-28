# BV-048 Sable Animation Handoff - 2026-06-27

This note exists so a fresh Codex thread can resume BV-048 without relying on chat context.

## Ticket Context

BV-048 is the active hero-animation quality ticket: replace portrait-shake prototype animation with real character-only sprite animation. Sable was chosen as the single-hero proof before spending time on the starter trio or the full roster.

Do not mark BV-048 accepted from the current Sable result. Sable is only `candidate_bv048`.

## User Direction Captured

Accepted direction:

- The style shift is promising: detailed portrait art can coexist with a simpler/chibi Live-Lite sprite, similar in spirit to old JRPG portrait-versus-field-sprite separation.
- Sable does not need to read as the exact portrait at tiny size; reading clearly as a ninja is acceptable.
- Character consistency still matters: head/costume/palette/scarf/clasp/daggers should remain stable across states.

Rejected direction:

- The initial Brugg/Mira/Nell generated strips were unacceptable as final art: they looked like portrait/card shake, not character animation.
- The first Sable runtime walk was unacceptable: it preserved source horizontal drift and cell bleed, so it looked like one Sable sliding left with a second partial Sable visible.
- The cleaned Sable walk was still too jittery in live review.
- The Live-Lite progress marker looked like a floating dot above Sable's head.
- Bright magenta chroma-key residue was visible in the runtime sprite.

## What Was Generated

Source and review assets live under:

- `docs/art/hero-animations/sable/`

Important files:

- `sable-keypose-source.png`: first generated style/key-pose source.
- `sable-keypose-cutout.png`: chroma-keyed alpha version.
- `sable-keypose-phone-preview.png`: phone-size proof for the accepted style direction.
- `sable-animation-sheet-candidate-v1-source.png`: six-row by six-column generated animation-sheet candidate.
- `sable-animation-sheet-candidate-v1-cutout.png`: alpha cutout of that sheet.
- `sable-animation-v1-*.png`: row/state strips from the candidate sheet.
- `sable-runtime-normalized-preview.png`: first runtime normalized preview; contains the originally bad walk issue.
- `sable-runtime-walk-before-cleanup.png`: preserved bad runtime walk with drift/bleed.
- `sable-runtime-walk-cleaned.png`: first cleaned/recentered walk strip.
- `sable-runtime-walk-before-stabilize.png`: preserved walk before the later stabilization pass.
- `sable-runtime-walk-stabilized.png`: current stabilized walk strip.
- `sable-runtime-stabilized-preview.png`: preview after marker/magenta/jitter fixes.
- `sable-runtime-*-before-anchorfix.png`: preserved runtime strips before the full-state anchor fix.
- `sable-runtime-*-anchored.png`: full-state anchor-fixed runtime strips copied into Android resources.
- `sable-runtime-anchorfix-preview.png`: current post-anchor-fix runtime preview.
- `sable-method.md`: running method notes.

Runtime Android assets added under:

- `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_idle.png`
- `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_walk.png`
- `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_fight.png`
- `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_hurt_dead.png`
- `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_celebrate.png`
- `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_loot_interact.png`

Local-only evidence under `artifacts/screenshots/`:

- `sable-live-watch.png`, `sable-live-watch-2.png`: first runtime evidence, before later fixes.
- `sable-live-watch-cleaned.png`, `sable-live-watch-cleaned-2.png`: after drift/bleed cleanup.
- `sable-live-stabilized.png`: latest screenshot after marker/magenta/jitter fixes.
- `sable-live-stabilized.mp4`: latest 6-second emulator screenrecord.
- `badventurers_session_before_sable_live.xml`: backup of emulator SharedPreferences before save injection.
- `badventurers_session_sable_live.xml`: injected save with Sable-only `moonlit_smuggler_run` expedition.

Note: `artifacts/screenshots/` is ignored/local evidence. Do not assume these files will exist on a fresh clone.

## Code and Data Changes

Runtime wiring:

- `app/src/main/java/com/ayoshy/badventurers/ui/LiveLiteArt.kt`
  - Added Sable animation resources to `heroAnimationResources`.
  - Removed the route progress marker circles, because in live view they read as a dot floating above the hero.
  - Slowed the shared Live-Lite animation loop from `780 ms` to `960 ms`.
  - Renamed `spreadBehindMarker` to `spreadBehindRouteLead` after removing the marker.

Pipeline/tools:

- `tools/normalize-hero-animation-sheet.ps1`
  - New export tool for six-row by six-column sheets into 128px Android strips.
  - Important rule added: Live-Lite owns horizontal travel; export should not preserve large source horizontal drift.
- `tools/clean-hero-animation-strip.ps1`
  - New cleanup tool to keep main components, remove neighbor-cell bleed, recenter frames, and build a safer strip from selected frames.
  - It refuses in-place overwrite; write to review/temp, then copy into runtime asset.
- `tools/validate-hero-animations.ps1`
  - Updated to support `candidate_bv048` in addition to accepted/missing statuses.
  - Checks accepted/candidate runtime assets for existence and 128px strip shape.

Tracking/catalog:

- `docs/data/hero-animation-manifest.json`: Sable states set to `candidate_bv048`; status definition added.
- `docs/data/heroes.csv`: Sable pack status set to `candidate_bv048`.
- `docs/content/hero-item-catalog.md`: Sable noted as BV-048 live test candidate.
- `docs/work/tasks.json`: BV-048 moved/kept in `doing`, with Sable candidate acceptance items checked.

## Current Visual State

Current Sable runtime strips have no bright magenta residue by the scan used in this thread:

- `hero_anim_sable_*.png`: `magentaLike=0` for all six files.
- Latest live screenshot: `screenshotMagentaLike=0`.

Current Sable runtime strips have been re-anchored after video feedback that the loop still read as sliding left:

- `idle`, `walk`, `fight`, `hurt_dead`, `celebrate`, and `loot_interact` now have measured `centerX` drift between `0.5` and `1 px`.
- `bottomRange=0` for every state after the anchor fix.
- `magentaLike=0` remains true for every runtime strip.
- `tools/validate-hero-animations.ps1` passes with no Sable warnings after the anchor fix.

Current walk is intentionally stabilized rather than authored:

- It is derived from one canonical Sable frame.
- It uses tiny bob/lower-body shifts to avoid the earlier violent jitter.
- It is less wrong, but not a good final walk cycle.

The correct next art step is not more salvage. It is to generate or paint a genuinely coherent Sable walk cycle where each frame is the same character, same scale, same silhouette mass, with pose changes authored around a stable center/baseline.

## Full-State Anchor Fix After Video Feedback

User review of `Screen_recording_20260627_190703.webm` said the result was okayish but still had a left-slide effect during the loop. The measured issue was no longer mostly `walk`; before this fix, Sable candidate states like `idle`, `fight`, `hurt_dead`, `celebrate`, and `loot_interact` still had large per-frame bounds drift.

Correction applied:

- Preserved every pre-fix runtime strip as `sable-runtime-<state>-before-anchorfix.png`.
- Re-ran `tools/clean-hero-animation-strip.ps1` across all six Sable states to recenter each frame and align a stable baseline.
- Saved anchored review outputs as `sable-runtime-<state>-anchored.png`.
- Copied the anchored strips back to `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_<state>.png`.
- Generated `docs/art/hero-animations/sable/sable-runtime-anchorfix-preview.png`.
- Post-fix metric check: `centerX` drift is `0.5-1 px` on every Sable state, `bottomRange=0`, and magenta residue is still `0`.
- Rebuilt and installed the debug APK, refreshed the Sable-only `moonlit_smuggler_run` save, tapped Watch, and captured `artifacts/screenshots/sable-live-anchorfix-watch2.png` plus `artifacts/screenshots/sable-live-anchorfix-watch2.mp4`.
- Final UI dump at `artifacts/screenshots/sable-live-anchorfix-watch2-window.xml` confirmed `Watch=True`, `Moonlit=True`, `Sable=True`, `Smuggler=True`, `Report ready=False`.

This is still candidate salvage art, not final accepted animation.

## Stills-Based Runtime Rebuild

User feedback on `Screen_recording_20260627_201704.webm` still showed two problems after the anchor-fix salvage: magenta streaks above Sable at the start, and a slide-like loop across animations. The user then confirmed the source sheet already had still poses for each animation row, so the fix moved away from salvaging the previous runtime strips.

Correction applied:

- Added `tools/build-sable-animation-from-stills.ps1`.
- Source: `docs/art/hero-animations/sable/sable-animation-sheet-candidate-v1-source.png`.
- The exporter reads the 6x6 still sheet, removes broad magenta-like chroma key, computes a stable upper-body anchor per cell, applies one global scale, and renders all six 128 px runtime strips with a fixed baseline.
- Outputs replaced `app/src/main/res-heroes/drawable-nodpi/hero_anim_sable_{idle,walk,fight,hurt_dead,celebrate,loot_interact}.png`.
- Review output: `docs/art/hero-animations/sable/sable-runtime-stills-preview.png`.
- Metric output: `docs/art/hero-animations/sable/sable-runtime-stills-metrics.csv`.

Post-export metrics from `sable-runtime-stills-metrics.csv`:

- All Sable runtime strips: `magentaPixels=0`.
- Foot/baseline drift: `0 px` for idle/walk/fight/celebrate, `1 px` for hurt/dead and loot/interact.
- Runtime anchor placement is fixed by construction; remaining body-center range comes from the pose silhouettes themselves, especially attack/fall/loot poses.

Runtime verification after rebuild:

- `tools/validate-hero-animations.ps1` passes; broad magenta warnings remain only on Mira `prototype_rejected_bv048` assets, not Sable.
- `gradlew.bat test assembleDebug lintDebug` passes.
- `gradlew.bat installDebug` passes.
- Refreshed Sable-only active save: `artifacts/screenshots/badventurers_session_sable_live_stills.xml`.
- Captured Watch video: `artifacts/screenshots/sable-stills-live.mp4`.
- Captured Watch frames: `artifacts/screenshots/sable-stills-frame-0.png` through `sable-stills-frame-5.png`.
- UI dump: `artifacts/screenshots/sable-stills-watch-window.xml`.
- Scene capture analysis over the Watch stage bounds found `magentaLike=0` in every captured frame.

This is a better Sable candidate because it uses the authored still poses instead of preserving old strip offsets. It is still not an accepted starter-trio delivery; visual review should decide whether the still-pose loop feels animated enough before scaling the method to Brugg/Mira/Nell.
## Ghost And Residual Magenta Fix

User review of `Screen_recording_20260627_211159.webm` and the attached crop showed that the Live-Lite timeline movement was now acceptable, but the sprite still had three runtime-artifact problems:

- two small horizontal components above Sable's head;
- a ghost-like half-body component drifting behind/left of the main body on some frames;
- faint magenta fringe around the silhouette.

Correction applied:

- Updated `tools/build-sable-animation-from-stills.ps1` so the stills export now runs a post-render component cleanup per 128 px frame.
- The cleanup removes broad/strict magenta-like pixels, drops detached components above the body, and keeps the centered main component instead of detached half-body bleed from the source cell.
- Re-exported all six Sable runtime strips from `sable-animation-sheet-candidate-v1-source.png`.
- Updated review preview: `docs/art/hero-animations/sable/sable-runtime-ghostfix-preview.png`.
- Updated `tools/validate-hero-animations.ps1` to use the same stricter magenta-like scan that caught the visible purple fringe.

Post-fix component probe:

- `walk`: `magentaStrict=0` and `comps=1` for all six frames; the two y=29 top-line components are gone.
- `idle`, `fight`, `hurt_dead`, `celebrate`, `loot_interact`: `magentaStrict=0` across all frames.
- The large frame-5 left-side ghost components in `idle`, `walk`, `fight`, `hurt_dead`, `celebrate`, and `loot_interact` were removed by keeping the centered main component.

Verification:

- `tools/validate-hero-animations.ps1` passes. The stricter scan now warns only on `prototype_rejected_bv048` Mira/Nell assets, not Sable.
- `gradlew.bat test assembleDebug lintDebug` passes.
- ADB install/capture for this pass was attempted next but blocked by the Codex escalation usage limit, so fresh emulator video is still pending.

Sable remains `candidate_bv048` pending visual review after the next install/capture.
## Five-Frame Export After Cut Last Column

User review of `sable-runtime-ghostfix-preview.png` showed that the last image of every state was cut in half. The source 6th column is boundary-bled/partial after cleanup: keeping it creates either the previous half-body ghost or a cut Sable frame.

Correction applied:

- Added `OutputColumns=5` to `tools/build-sable-animation-from-stills.ps1`.
- The source sheet still uses 6 columns for cell analysis, but runtime strips now render only source columns `0..4`.
- `SavePreview(...)` now uses the actual strip width instead of hardcoding `6 * 128`.
- Regenerated all six Sable runtime strips at `640x128` (`5` frames each).
- Updated `docs/art/hero-animations/sable/sable-runtime-ghostfix-preview.png` to `640x768`.

Verification:

- Runtime dimensions: every Sable state is `640x128`, `frames=5`.
- `tools/validate-hero-animations.ps1` passes; Sable has no warnings.
- `gradlew.bat test assembleDebug lintDebug` passes.
- `gradlew.bat installDebug` passes.
- Fresh Watch capture: `artifacts/screenshots/sable-5frame-live.mp4`.
- Save used for capture: `artifacts/screenshots/badventurers_session_sable_live_5frame.xml`.

This fix intentionally prefers 5 clean frames over 6 frames with a bad terminal cell. Sable remains `candidate_bv048` pending visual approval.
## What To Avoid Repeating

Do not:

- Treat the current Sable candidate as accepted final animation.
- Reuse the old portrait-shake Brugg/Mira/Nell strips as quality reference.
- Preserve source sheet horizontal offsets as runtime motion; this creates sliding and double-character artifacts.
- Trust a generated 6x6 sheet if cells contain bleed across frame boundaries.
- Leave chroma-key residue checks to visual review only; scan PNG pixels for magenta-like colors.
- Put route/progress markers near/above hero heads in the Live-Lite lane.

## Suggested Next Thread Plan

1. Start from BV-048 and this handoff.
2. Keep Sable as `candidate_bv048`.
3. Decide whether to produce a better authored Sable walk first, or pause Sable and use the learning to generate a cleaner starter-trio prompt/spec.
4. If continuing Sable, make only the `walk` state first and test it live before touching the other states.
5. Add an automatic validator check for detached components / excessive frame bounds variance before any future runtime export is accepted.
6. Once Sable walk is visually acceptable, update `sable-method.md` with the exact prompt/tool recipe that worked.

## Verification Already Run

After the latest stabilization pass, these commands completed successfully:

```powershell
.\tools\validate-hero-animations.ps1
.\gradlew.bat test assembleDebug lintDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell screenrecord --time-limit 6 /sdcard/sable-live-stabilized.mp4
adb shell screenrecord --time-limit 6 /sdcard/sable-live-anchorfix-watch2.mp4
```

Final Android/Live-Lite check after install:

- `ui_watch=True`
- `ui_report_ready=False`
- `ui_moonlit=True`
- `ui_sable=True`
## Working Tree Caveat

At the time this handoff was written, the working tree was still uncommitted.

Relevant expected changes include Sable runtime assets, Sable docs, BV-048 task updates, validator changes, and LiveLiteArt changes.

Earlier in-progress checks showed the starter contact-sheet and phone-preview files as deleted. They are still referenced by the Workboard/script history, so they were restored before publishing and should not be removed as part of BV-048.

## Bottom Line

The current Sable candidate is useful as a learning artifact and runtime plumbing proof, not as final animation art. BV-048 should continue from the lesson: produce one small, coherent, character-only authored walk cycle and validate it live before scaling to starter trio or roster coverage.