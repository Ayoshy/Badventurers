package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class JournalGeneratorTest {
    private fun result(
        outcome: ExpeditionOutcome,
        gold: Int = 0,
        lootRolls: Int = 0,
    ): ExpeditionResult =
        ExpeditionResult(
            outcome = outcome,
            reward = Reward(gold = gold, xp = 0, lootRolls = lootRolls),
            scoreMargin = 0,
        )

    @Test
    fun eachOutcomeChangesTheJournalText() {
        val texts = ExpeditionOutcome.entries.map { outcome ->
            JournalGenerator.generate(result(outcome), SeedGame.heroes).first().text
        }

        assertEquals(ExpeditionOutcome.entries.size, texts.toSet().size)
    }

    @Test
    fun emptyPartyDoesNotCrashAndStillProducesAtLeastTwoLines() {
        val entries = JournalGenerator.generate(result(ExpeditionOutcome.Success), emptyList())

        assertTrue(entries.size >= 2)
    }

    @Test
    fun rewardLineAppearsWhenGoldOrLootExists() {
        val entries = JournalGenerator.generate(
            result(ExpeditionOutcome.PartialSuccess, gold = 12, lootRolls = 1),
            emptyList(),
        )

        assertTrue(entries.any { it.id.startsWith("reward-") })
        assertTrue(entries.any { it.text.contains("gold") })
    }

    @Test
    fun questLineAppearsWhenQuestProvided() {
        val entries = JournalGenerator.generate(result(ExpeditionOutcome.Success), SeedGame.heroes, SeedGame.firstQuest)

        assertTrue(entries.any { it.id == "quest-cave_minor_regrets" })
        assertTrue(entries.any { it.text.contains("Cave") })
    }

    @Test
    fun everyCurrentQuestHasSpecificQuestJournalLine() {
        SeedGame.quests.forEach { quest ->
            val entries = JournalGenerator.generate(result(ExpeditionOutcome.Success), SeedGame.heroes, quest)
            val questLine = entries.single { it.id == "quest-${quest.id}" }

            assertTrue("${quest.id} is still using fallback journal copy", !questLine.text.contains("suspiciously specific note"))
        }
    }

    @Test
    fun activeSpecialLineAppearsWhenPartyMatchesQuestTags() {
        val entries = JournalGenerator.generate(result(ExpeditionOutcome.Success), SeedGame.heroes, SeedGame.firstQuest)
        val special = entries.single { it.id == "special-brugg-cave_minor_regrets" }

        assertTrue(special.text.contains("Brugg"))
    }

    @Test
    fun inactiveSpecialsDoNotAddSpecialLine() {
        val entries = JournalGenerator.generate(
            result(ExpeditionOutcome.Success),
            listOf(HeroCatalog.byId.getValue("mira").toHero()),
            SeedGame.firstQuest,
        )

        assertTrue(entries.none { it.id.startsWith("special-") })
    }
    @Test
    fun idsAreStableAndNonEmpty() {
        val first = JournalGenerator.generate(result(ExpeditionOutcome.Failure, gold = 3), SeedGame.heroes.take(1))
        val second = JournalGenerator.generate(result(ExpeditionOutcome.Failure, gold = 3), SeedGame.heroes.take(1))

        assertTrue(first.all { it.id.isNotBlank() })
        assertEquals(first.map { it.id }, second.map { it.id })
    }
}
