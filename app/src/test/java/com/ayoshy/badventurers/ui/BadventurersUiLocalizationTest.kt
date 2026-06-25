package com.ayoshy.badventurers.ui

import com.ayoshy.badventurers.R
import com.ayoshy.badventurers.game.ExpeditionPlanCatalog
import com.ayoshy.badventurers.game.SeedGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BadventurersUiLocalizationTest {
    @Test
    fun everyQuestHasUiLocalizedTextMapping() {
        assertEquals(
            SeedGame.quests.map { it.id }.toSet(),
            localizedQuestTextIds,
        )
    }

    @Test
    fun everyExpeditionPlanHasUiLocalizedTextMapping() {
        assertEquals(
            ExpeditionPlanCatalog.all.map { it.id }.toSet(),
            localizedExpeditionPlanTextIds,
        )
    }

    @Test
    fun everyQuestHasResolvedBannerArtReference() {
        SeedGame.quests.forEach { quest ->
            val reference = questBannerArtReference(quest)

            assertNotEquals("${quest.id} should resolve a drawable resource", 0, reference.resourceId)
            if (reference.resourceId == R.drawable.quest_banner_03) {
                assertTrue("${quest.id} fallback banner needs an art brief", reference.temporaryBrief.orEmpty().isNotBlank())
            }
        }
    }

    @Test
    fun licensedTroubleAndRegionalLiabilityFallbackBannersHaveTemporaryBriefs() {
        val questsWithBriefs = SeedGame.quests
            .filter { questBannerArtReference(it).temporaryBrief != null }
            .map { it.id }
            .toSet()

        assertEquals(
            setOf(
                "paperwork_toll_of_chaos",
                "licensed_guild_caravan_haunt",
                "notary_night_patrol",
                "inspectorate_cove_banquet",
                "wedding_with_too_many_oaths",
                "the_sunken_toll_booth",
                "the_crowns_missing_receipt",
                "the_tower_built_sideways",
            ),
            questsWithBriefs,
        )
    }

}
