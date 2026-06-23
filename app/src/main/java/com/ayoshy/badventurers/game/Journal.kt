package com.ayoshy.badventurers.game

data class JournalEntry(
    val id: String,
    val text: String,
)

object JournalGenerator {
    fun generate(result: ExpeditionResult, party: List<Hero>, quest: Quest? = null): List<JournalEntry> {
        val entries = mutableListOf<JournalEntry>()

        entries += JournalEntry(
            id = "outcome-${result.outcome.name.lowercase()}",
            text = outcomeText(result.outcome),
        )

        if (quest != null) {
            entries += JournalEntry(
                id = "quest-${quest.id}",
                text = questText(quest),
            )
            entries += HeroSpecialCatalog.activeHeroes(party, quest)
                .take(2)
                .map { hero ->
                    JournalEntry(
                        id = "special-${hero.id}-${quest.id}",
                        text = specialText(hero, quest),
                    )
                }
        }
        party.firstOrNull()?.let { hero ->
            entries += JournalEntry(
                id = "hero-${hero.id}",
                text = heroText(hero),
            )
        }

        if (result.reward.gold > 0 || result.reward.lootRolls > 0) {
            entries += JournalEntry(
                id = "reward-${result.reward.gold}-${result.reward.lootRolls}",
                text = rewardText(result.reward),
            )
        }

        if (entries.size < 2) {
            entries += JournalEntry(
                id = "fallback-empty-ledger",
                text = "The ledger stayed rude and empty.",
            )
        }

        return entries
    }

    private fun outcomeText(outcome: ExpeditionOutcome): String =
        when (outcome) {
            ExpeditionOutcome.GreatSuccess -> "We won. The cave disagreed."
            ExpeditionOutcome.Success -> "The job was done. Sadly, on purpose."
            ExpeditionOutcome.PartialSuccess -> "We half-won, which still counts as a bruise."
            ExpeditionOutcome.Failure -> "The expedition failed. The mud took notes."
            ExpeditionOutcome.RidiculousFailure -> "Even the map filed a complaint."
        }

    private fun heroText(hero: Hero): String =
        when (hero.heroClass) {
            HeroClass.Bruiser -> "${hero.name} hit the problem until it left."
            HeroClass.ApprenticeMage -> "${hero.name} murmured at the walls."
            HeroClass.Rogueish -> "${hero.name} was seen near the missing part."
            HeroClass.BardAccountant -> "${hero.name} balanced the disaster."
            HeroClass.Ninja -> "${hero.name} vanished, then invoiced the smoke."
            HeroClass.Hunter -> "${hero.name} tracked the trail back to lunch."
            HeroClass.Priest -> "${hero.name} healed morale with stern paperwork."
            HeroClass.Necromancer -> "${hero.name} raised spirits. Some had opinions."
            HeroClass.Paladin -> "${hero.name} held the line and the line thanked him."
            HeroClass.Accountant -> "${hero.name} balanced the cave until it owed us money."
            HeroClass.Gardener -> "${hero.name} pruned the disaster into something tidy."
            HeroClass.DeathKnight -> "${hero.name} negotiated with death and got a discount."
            HeroClass.Chef -> "${hero.name} seasoned the expedition until it behaved."
            HeroClass.DemolitionExpert -> "${hero.name} improved the floor plan with explosives."
            HeroClass.SaltElemental -> "${hero.name} salted the trail, the wounds, and the paperwork."
            HeroClass.StupidTroll -> "${hero.name} solved the problem by misunderstanding it aggressively."
        }

    private fun rewardText(reward: Reward): String =
        buildString {
            append("Reward: ")
            append(reward.gold)
            append(" gold")
            if (reward.lootRolls > 0) {
                append(", ")
                append(reward.lootRolls)
                append(" loot items")
            }
            append(".")
        }
    private fun questText(quest: Quest): String =
        when (quest.id) {
            "cave_minor_regrets" -> "The Cave of Minor Regrets apologized by dropping loose change."
            "forest_of_wrong_turns" -> "The forest rearranged itself, then denied everything."
            "bandit_tax_office" -> "The bandit forms were stamped with unnecessary menace."
            "salted_swamp_chapel" -> "The chapel bell rang once and leaked brine."
            "moonlit_smuggler_run" -> "The moon pretended not to see the cargo."
            "the_hungry_siege" -> "The bread-crate barricade held, mostly out of spite."
            "the_last_locked_door" -> "The last locked door considered the paperwork and blinked first."
            "crypt_of_unpaid_debts" -> "The crypt itemized every scream and added interest."
            else -> "${quest.title} left a suspiciously specific note."
        }

    private fun specialText(hero: Hero, quest: Quest): String =
        when (hero.special) {
            HeroSpecial.RamshackleCharge -> "${hero.name}'s charge found the shortest route through the warning signs."
            HeroSpecial.GlyphReader -> "${hero.name} read the glyphs aloud until they corrected themselves."
            HeroSpecial.LightFingers -> "${hero.name} found a hidden pocket in the mission plan."
            HeroSpecial.HumanWall -> "${hero.name} stood where danger expected a door."
            HeroSpecial.AggressiveMinutes -> "${hero.name} turned the paperwork into a timed sport."
            HeroSpecial.TerrainManual -> "${hero.name} cited the terrain manual and the trap looked embarrassed."
            HeroSpecial.UnstableLuck -> "${hero.name}'s luck tripped over a better outcome."
            HeroSpecial.DirtyJackpot -> "${hero.name} made the shadows pay a convenience fee."
            HeroSpecial.FreshTrail -> "${hero.name} found the trail hiding behind the obvious trail."
            HeroSpecial.CleanBlessing -> "${hero.name} cleaned the curse until it lost confidence."
            HeroSpecial.NoTrace -> "${hero.name} left no trace, except a very smug invoice."
            HeroSpecial.NecroLever -> "${hero.name} asked the dead for leverage. They had notes."
            HeroSpecial.HostileAudit -> "${hero.name} audited the threat until it produced gold."
            HeroSpecial.UnbreakableOath -> "${hero.name}'s oath blocked the worst idea in the room."
            HeroSpecial.BalancedBooks -> "${hero.name} balanced the books and unbalanced the enemy."
            HeroSpecial.GreenThumb -> "${hero.name} pruned the battlefield into something passable."
            HeroSpecial.DeathDiscount -> "${hero.name} negotiated the death fees down to bruises."
            HeroSpecial.MoraleRations -> "${hero.name} distributed snacks until morale became structural."
            HeroSpecial.PlanBExplosives -> "${hero.name} introduced Plan B and removed several Plan A problems."
            HeroSpecial.PreservationSalt -> "${hero.name} preserved the evidence, the snacks, and most of the party."
            HeroSpecial.CreativeMisunderstanding -> if (quest.hasAny(QuestTag.Paperwork, QuestTag.Contract)) {
                "${hero.name} misunderstood the forms in a way the forms did not enjoy."
            } else {
                "${hero.name} misunderstood the problem so hard it became simpler."
            }
        }
}
