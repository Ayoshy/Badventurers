# BV-018 Guild Hub Functional Art Mockup

Goal: replace the Guild tab's text-dashboard feel with a portrait illustrated hub where the artwork itself carries the main navigation.

## Required Hotspots

| Hotspot | Purpose | Art Requirement |
| --- | --- | --- |
| Quest map table | Open quests / expedition flow | Lower-left foreground, maps and contracts, never cropped. |
| Core crew counter | Manage/show assigned Core Crew | Mid/lower guild counter with 3-4 clear hero placement spots. |
| Guild charter board | Achievements / charter progress | Upper-middle notice board with medals, wax seals, claim/progress glow space. |
| Loot storage | Open loot / pending rewards | Lower-right compact treasure and gear nook, readable at phone size. |
| Facilities wall | Open upgrades / facilities | Mid-right workshop/armor/training/bunk visual cluster. |

## UI Safe Areas

- Top: thin resource HUD only.
- Center: mostly unobstructed room/floor space so the background remains visible.
- Bottom: compact status strip or selected-hotspot drawer.
- No large permanent cards on the default Guild view.

## Draft Files

- `guild-hub-functional-art-v1.png`: generated concept art draft.
- `guild-hub-hotspot-overlay-v1.png`: rough first tappable-zone overlay for discussion. Too broad; kept only as a rejected comparison.
- `guild-hub-hotspot-overlay-v2.png`: tighter object-shaped hotspot overlay for discussion.
- `guild-hub-hotspots-v2.json`: normalized draft hitmap for eventual Compose implementation.

## Mapping Rule

The actual runtime mapping should follow props, not page sections. A hotspot can receive a small finger-friendly tap slop in code, but it should not capture the central floor, decorative margins, balconies, or unrelated furniture. The player should feel like they tapped the notice board, map table, crew desk, loot cache, or facilities rack, not a giant invisible rectangle.

Core Crew needs special treatment: the desk opens the Core Crew screen, while 3-4 explicit hero slots should be visually available for assigned hero standees or portraits. The room floor in front of the desk remains dead space unless a later feature gives it a clear purpose.

## Next Art Iteration Notes

- Keep all gameplay landmarks inside mobile safe zones.
- Make Core Crew placement more explicit than normal furniture.
- Keep the Quest table and Loot nook fully visible, not edge-cropped.
- Maintain Badventurers identity: warm tavern light, moss cloth, brass, burgundy, cheap fantasy bureaucracy, no readable text.
- This is not accepted runtime art yet; it is a direction/mocking artifact.