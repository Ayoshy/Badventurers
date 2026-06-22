# Tools And Workflow

Last updated: 2026-06-21

## Source Of Truth

- Code: GitHub repository `Ayoshy/Badventurers`.
- Product docs: Markdown files in this repo.
- Task tracking: GitHub Issues first.
- Kanban board: GitHub Projects recommended once the initial issues exist.

## Why Not Start With A Separate PM App?

For a small mobile game, GitHub Issues plus a GitHub Project is enough at the beginning. It keeps product decisions, code, tickets, and release work in the same place. Linear, Trello, or Notion can be added later if the project becomes team-heavy or content-heavy.

## Recommended GitHub Project Columns

- Backlog
- Ready
- In Progress
- Review
- Done

## Labels To Add

- `type:feature`
- `type:bug`
- `type:docs`
- `type:content`
- `type:tech`
- `area:android`
- `area:gameplay`
- `area:monetization`
- `area:localization`
- `phase:mvp`
- `priority:p0`
- `priority:p1`
- `priority:p2`

## Working Rhythm

- Keep main branch releasable.
- Use small branches per feature.
- Open PRs even for solo work when changes are non-trivial.
- Update docs when a decision changes.
- Add tests for formulas and persistence before tuning.

## Codex Editing On Windows

This project has repeatedly hit Windows sandbox / ACL failures when `apply_patch` tries to edit files created by agents, Gradle, Android Studio, or prior elevated shell commands.

Operational rule for Codex on this repo:

- Use targeted PowerShell edits directly for known ACL-sensitive files instead of trying `apply_patch` first.
- Keep edits narrow: read file, replace an exact block or append a small documented section, write file, then immediately re-read the changed file.
- Do not use broad rewrites, generated formatting churn, or destructive cleanup as a workaround.
- If a PowerShell edit cannot find its exact target block, stop and inspect the file instead of guessing.
- Continue to verify with `git status --short`, relevant file reads, and Gradle/tests when code changed.

## Definition Of Done

A task is done when:

- The change builds locally.
- Relevant tests pass or the absence of tests is called out.
- User-visible text supports English and French if applicable.
- The related issue has acceptance criteria checked off.
- Any design or product decision is reflected in docs.

## Optional External Tools

- Figma or Penpot for UI mockups if we want visual polish before implementation.
- Google Drive only if we want shareable business docs outside GitHub.
- Firebase later for crash reporting and analytics if we decide it is worth the dependency.
- Play Console and AdMob only when the core loop has survived prototype testing.

