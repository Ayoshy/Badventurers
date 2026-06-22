package com.ayoshy.badventurers.game

data class JournalEntry(
    val id: String,
    val text: String,
)

object JournalGenerator {
    fun generate(result: ExpeditionResult, party: List<Hero>): List<JournalEntry> {
        val entries = mutableListOf<JournalEntry>()

        entries += JournalEntry(
            id = "outcome-${result.outcome.name.lowercase()}",
            text = outcomeText(result.outcome),
        )

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
}
