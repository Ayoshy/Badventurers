# Achievement System Plan

Last updated: 2026-06-23

## Goal

Add a long-term "hauts faits" system that rewards the normal idle loop without turning the game into a checklist. Achievements should give satisfying one-time rewards, bounded permanent bonuses, and unlock selected guild, hero, and reward possibilities.

The system should feel like a slightly official guild ledger: proud, useful, and suspiciously over-filed.

## Design Pillars

1. **Reward the loop already in place.** Expeditions, loot, upgrades, recruitment, and return sessions should naturally feed progress.
2. **Use unlocks more than raw power.** The best rewards open new options: guild facilities, hero promotion, reward choice, better quest visibility.
3. **Keep bonuses bounded.** Permanent numerical bonuses should be additive, capped, and mostly granted by meta milestones rather than every badge.
4. **Never require ads.** Rewarded ads can remain optional, but no achievement should require watching one.
5. **Make failure funny and useful.** Bad outcomes can unlock pity systems and rare comedy badges without making failure optimal.

## Visuals

Early concept-art files were removed from the active docs tree during docs cleanup. Treat this document as system design intent; runtime achievement art lives under Android resources when integrated.

## Core Model

Use two layers:

- **Badges:** individual achievements with progress, completion, claim state, icon, and one-time reward.
- **Guild Charter Seals:** meta points earned by claiming badges. Seal milestones unlock bigger systems and controlled permanent bonuses.

This avoids a balance problem where 80 tiny achievements all stack into runaway gold, power, or loot multipliers.

## Reward Types

| Reward type | Use | Balance rule |
| --- | --- | --- |
| Gold | Early dopamine and small catch-up | 0.5x to 2x the current quest reward band |
| Reputation | Unlock pressure relief | Small amounts, mostly for onboarding and duplicate-friendly paths |
| Loot rolls | Reward collection and quest mastery | One-time, never paid-gated |
| Charter seals | Meta progression | Main reward for most badges |
| Guild unlock | New facility, facility cap, or utility | Major milestones only |
| Hero unlock | Promotion, training option, recruit pool expansion | Major milestones and hero category badges |
| Reward unlock | Loot choice, improved result reveal, pity chest | Major milestones, bounded frequency |
| Cosmetic/trophy | Screen flair and completion identity | Safe for secret or silly achievements |

## Charter Milestones

| Seals | Unlock | Gameplay effect |
| ---: | --- | --- |
| 1 | Trophy Ledger | Achievement screen, progress cards, first claim flow |
| 4 | Insurance Desk | Failures and ridiculous failures get improved pity rewards |
| 8 | Hero Mentorship | Unlock hero promotion/training hooks |
| 12 | Reward Choice | On great success, choose 1 of 2 loot candidates when available |
| 18 | Advanced Contracts | Unlock special quest conditions and better quest recommendations |
| 25 | Guild Charter Bonuses | Long-term capped bonuses and future prestige bridge |

## MVP Badge Set

Start with 24 badges. That is enough to make the screen feel real without flooding content or localization.

| ID | Category | Condition | Reward | Unlock |
| --- | --- | --- | --- | --- |
| first_expedition | Quest | Complete any quest once | 100 gold, 1 seal | Trophy Ledger |
| came_back_alive | Quest | Get first Success or Great Success | 1 seal | none |
| professional_regret_i | Quest | Complete 3 quests | 1 reputation, 1 seal | none |
| professional_regret_ii | Quest | Complete 10 quests | 2 reputation, 2 seals | none |
| tour_of_bad_ideas | Quest | Complete each seed quest once | 3 seals | Advanced Contracts progress |
| cave_paperwork | Quest | Complete 3 Cave or Paperwork-tagged quests | 1 seal | quest recommendation hint |
| all_risk_no_notes | Quest | Complete a High risk quest | 2 seals, 1 loot roll | none |
| almost_competent | Result | Get a Great Success | 1 seal, bonus gold | Reward Choice progress |
| educational_failure | Result | Get first Failure | pity gold, 1 seal | Insurance Desk progress |
| historic_misread | Result | Get first Ridiculous Failure | cosmetic trophy, 1 seal | Insurance Desk progress |
| payroll_problem | Guild | Hold 1,500 gold once | 1 seal | none |
| noticeable_board | Guild | Upgrade Notice Board to 2 | 1 seal | Advanced Contracts progress |
| training_incident | Guild | Upgrade Training Yard to 2 | 1 seal | Hero Mentorship progress |
| too_many_beds | Guild | Upgrade Bunk Room to 2 | 1 seal | party planning hint |
| suspiciously_funded | Guild | Buy any 3 facility upgrades | 2 seals | facility cap preview |
| first_hire | Hero | Recruit one hero | 1 seal | none |
| tiny_hr_department | Hero | Own 5 heroes | 1 seal | Hero Mentorship progress |
| duplicate_form | Hero | Pull a duplicate recruit | reputation, 1 seal | duplicate pity explanation |
| specialist_roster | Hero | Own heroes covering 5 classes | 2 seals | recruit pool preview |
| keep_the_spoon | Loot | Keep 5 loot items | 1 seal | none |
| equipped_for_concern | Loot | Equip 3 heroes | 1 seal | equip suggestions highlight |
| shiny_enough | Loot | Keep first Rare or better item | 1 seal, gold | none |
| very_much_legendary | Loot | Keep first top-tier item | 3 seals, trophy | future prestige bridge |
| welcome_back_audit | Idle | Collect after offline completion | 1 seal | Offline Summary polish |

## Bonus Design

Avoid direct bonus spam on every badge. Use milestone bonuses like this:

| Bonus | Source | Cap |
| --- | --- | --- |
| Quest gold bonus | Guild Charter Bonus nodes | +10% total |
| Pity gold bonus | Insurance Desk levels | +30% pity reward total |
| Training power bonus | Hero Mentorship levels | +12 flat party power total |
| Loot choice frequency | Reward Choice levels | Great Success only, max 1 choice per result |
| Quest visibility | Advanced Contracts | Shows better hints, not raw power |

All numerical bonuses should be additive. Do not multiply Notice Board, achievement, ad, and event bonuses together.

## UX Plan

### Entry Point

Do not add a sixth bottom-nav tab for MVP. Add:

- a "Trophy Ledger" card on Guild Home after the first expedition;
- a secondary entry from Upgrades;
- red/gold badge dots on Guild and Upgrades when something is claimable.

### Achievement Screen

Recommended sections:

- **Featured:** claimable and nearly complete achievements.
- **Categories:** Quest, Guild, Hero, Loot, Result, Idle.
- **Charter:** seal milestone ladder with upcoming unlock.
- **Trophies:** completed badges and secret/funny discoveries.

### Completion Timing

Show completion feedback at collection points, not mid-timer:

- quest/result badges after `collectResult`;
- loot badges after keep/equip/sell;
- guild badges after facility upgrades;
- hero badges after recruitment;
- idle badges after offline collection.

Use a compact toast for badge completion and a larger banner only when a milestone unlocks a new system.

## Technical Plan

### Data Structures

Add these domain types under `game/`:

```kotlin
enum class AchievementCategory { Quest, Guild, Hero, Loot, Result, Idle, Secret }
enum class AchievementVisibility { Visible, HiddenUntilProgress, SecretUntilComplete }

data class AchievementDefinition(
    val id: String,
    val category: AchievementCategory,
    val target: Int,
    val sealReward: Int,
    val titleKey: String,
    val descriptionKey: String,
    val reward: AchievementReward,
    val visibility: AchievementVisibility = AchievementVisibility.Visible,
)

data class AchievementProgress(
    val achievementId: String,
    val current: Int = 0,
    val completedAtMillis: Long? = null,
    val claimedAtMillis: Long? = null,
    val seen: Boolean = false,
)
```

Use a sealed reward model:

```kotlin
sealed interface AchievementReward {
    data class Currency(val gold: Int = 0, val reputation: Int = 0, val lootRolls: Int = 0) : AchievementReward
    data class Unlock(val unlockId: String) : AchievementReward
    data class Composite(val rewards: List<AchievementReward>) : AchievementReward
    data object None : AchievementReward
}
```

### Events

Keep achievement updates event-driven:

```kotlin
sealed interface AchievementEvent {
    data class QuestCollected(val quest: Quest, val result: ExpeditionResult, val partyHeroIds: List<String>) : AchievementEvent
    data class LootKept(val item: LootItem) : AchievementEvent
    data class LootEquipped(val heroId: String, val item: LootItem) : AchievementEvent
    data class HeroRecruited(val result: HeroRecruitmentResult) : AchievementEvent
    data class FacilityUpgraded(val facilityId: String, val level: Int) : AchievementEvent
    data class OfflineCollected(val completedRuns: Int) : AchievementEvent
}
```

`AchievementTracker.apply(state, event, nowMillis)` returns the next achievement state plus newly completed ids. The UI can then show toasts or banners without embedding achievement logic in Compose.

### State And Save

Extend `PlaySessionState` with:

- `achievementProgress: List<AchievementProgress>`;
- `claimedAchievementIds` only if it simplifies JSON migration;
- `charterSealsEarned`;
- `unlockedAchievementFeatures: Set<String>` or a small typed unlock state.

Extend `PlaySessionSnapshot.CURRENT_VERSION` and `PlaySessionStore` with tolerant JSON decode defaults, matching the existing save style.

### Integration Points

| Existing action | Achievement event |
| --- | --- |
| `collectResult` | `QuestCollected` |
| `keepPendingLoot` | `LootKept` |
| `equipLoot` | `LootEquipped` |
| `recruitHero` | `HeroRecruited` |
| `upgradeNoticeBoard` | `FacilityUpgraded("notice_board", level)` |
| `upgradeTrainingYard` | `FacilityUpgraded("training_yard", level)` |
| `upgradeBunkRoom` | `FacilityUpgraded("bunk_room", level)` |
| future offline summary claim | `OfflineCollected` |

### Quest Unlock Reuse

The project already has `completedQuestCount` and `QuestUnlockRequirement`. Achievements should reuse the same progression language instead of inventing incompatible gates.

Later, add achievement-based quest conditions only when needed:

```kotlin
data class QuestUnlockCondition(
    val minReputation: Int = 0,
    val minCompletedQuestCount: Int = 0,
    val minNoticeBoardLevel: Int = 0,
    val minTrainingYardLevel: Int = 0,
    val minBunkRoomLevel: Int = 0,
    val requiredAchievementId: String? = null,
    val requiredUnlockId: String? = null,
)
```

## Implementation Phases

### Phase A: Data-Only MVP

- Add achievement definitions and tracker.
- Track the 24 MVP badges.
- Add save migration and tests.
- No new complex UI yet: expose counts and claimable ids.

### Phase B: Claim Flow And UI

- Add Trophy Ledger screen.
- Add claimable badge dots on Guild/Upgrades.
- Add compact completion toasts.
- Add Charter milestone strip.

### Phase C: Real Unlocks

- Wire Insurance Desk pity bonus.
- Wire Hero Mentorship hooks.
- Wire Reward Choice on Great Success.
- Add Advanced Contracts conditions.

### Phase D: Visual Polish

- Slice/recreate badge icons from the concept sheet.
- Add unlock banner to result screen.
- Add Trophy Ledger background inspired by the hall concept.
- Add small animations: medal pop, seal count tick, unlock glow.

## Test Plan

- Completing a quest increments quest badges once.
- Claiming a badge grants rewards exactly once.
- Save/load preserves progress, claimed state, and unlocks.
- Hidden achievements reveal only under the intended conditions.
- Charter milestones unlock at the exact seal thresholds.
- Bonus math stays additive and capped.
- No achievement depends on rewarded ads.

## Open Decisions

- Should Charter Seals be spendable, or purely cumulative milestone points? Recommendation: cumulative for MVP.
- Should secret achievements count toward seal thresholds? Recommendation: yes, but low seal value.
- Should the Trophy Ledger be an Upgrades sub-screen or a Guild modal? Recommendation: full screen reachable from Guild and Upgrades.
- Should achievement rewards auto-claim? Recommendation: no; manual claim feels better and makes unlock moments clearer.
