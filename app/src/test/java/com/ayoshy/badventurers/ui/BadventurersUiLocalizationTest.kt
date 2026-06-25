package com.ayoshy.badventurers.ui

import com.ayoshy.badventurers.game.ExpeditionPlanCatalog
import com.ayoshy.badventurers.game.SeedGame
import org.junit.Assert.assertEquals
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
}