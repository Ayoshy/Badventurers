# Kanban

Last updated: 2026-06-22

This file is the repo-local board. GitHub Issues should mirror these items so the work can be tracked outside the codebase too.

## Columns

- Backlog: idea is useful, not ready.
- Ready: clear enough to implement.
- In Progress: actively being worked.
- Review: implemented, needs validation.
- Done: merged or accepted.

## Now

- [x] Create repository.
- [x] Define product direction.
- [x] Add project documentation.
- [x] Validate low-fidelity UX flow.
- [x] Validate Batch 01 art direction.
- [x] Add art-integrated HTML visual prototype.
- [x] Scaffold Android project.
- [x] Verify local Android test/build.
- [x] Add first in-memory playable loop.
- [x] Add ADB emulator capture script.
- [x] Install current build on emulator.
- [x] Capture current emulator screen through ADB.
- [x] Add save snapshot foundation for `PlaySessionState`.
- [x] Add basic loot item generation.
- [x] Add journal event generation from expedition results.
- [x] Smoke install and capture the integrated build on emulator.
- [x] Commit and push the save/loot/journal slice.

## Next

- [ ] Integrate Android local persistence using `PlaySessionSnapshot`.
- [ ] Replace remaining raw loot-roll UI with a small inventory list.
- [ ] Localize generated journal/loot display text or route generated copy through templates.
- [ ] Slice Batch 01 sheets into Android-ready hero, quest, loot, and resource assets.
- [ ] Add a repeatable emulator smoke-test checklist.
- [ ] Create initial GitHub issues once GitHub CLI/connector access works.

## Blocked

- [ ] Create initial GitHub issues from this board. Blocked: `gh`/GitHub connector unavailable in the agent environment.

## Later

- [ ] Add hero roster interactions and equipment UI.
- [ ] Add deeper guild upgrade effects.
- [ ] Add offline progress summary.
- [ ] Add fake rewarded ad service.
- [ ] Add AdMob rewarded ads.
- [ ] Add Google Play Billing no-ads product.
- [ ] Add store listing assets.
- [ ] Add beta analytics and crash reporting.
- [ ] Investigate Codex `view_image` ACL failure on workspace screenshots.

## Initial Issue Candidates

1. Install and smoke-test current Android build on emulator.
2. Create initial GitHub issues from the repo-local Kanban.
3. Add local save for `PlaySessionState`.
4. Replace raw loot rolls with generated loot item basics.
5. Generate journal entries from expedition outcomes.
6. Slice Batch 01 artwork into production Android assets.
7. Add emulator screenshot capture to the regular QA loop.
8. Add hero roster interactions and equipment basics.
9. Add fake rewarded ad reward flow.
10. Prepare closed-test Play Store checklist.