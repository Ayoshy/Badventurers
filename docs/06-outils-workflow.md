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

