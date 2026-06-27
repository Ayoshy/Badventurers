package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LiveLiteExpeditionTest {
    @Test
    fun watchModelGenerationIsDeterministicAndUsesStableAssetKeys() {
        val party = SeedGame.heroes.take(3)
        val run = ExpeditionRun(
            quest = SeedGame.firstQuest,
            partyHeroIds = party.map { it.id },
            startedAtMillis = 1_000L,
            endsAtMillis = 46_000L,
            planId = ExpeditionPlanCatalog.rushTheJobId,
        )

        val first = LiveLiteExpedition.buildWatchModel(run = run, party = party, facilityPowerBonus = 3)
        val second = LiveLiteExpedition.buildWatchModel(run = run, party = party, facilityPowerBonus = 3)

        assertEquals(first, second)
        assertEquals("cave", first.stage.biome)
        assertEquals(LiveLiteAssetKeys.STAGE_CAVE, first.stage.asset.key)
        assertEquals("plan.rush_the_job", first.planAsset.key)
        assertEquals(LiveLiteAssetKeys.PASSIVE_GUILD_INCOME, first.passiveReportAsset.key)
        assertTrue(first.hazardAssets.any { it.key == LiveLiteAssetKeys.HAZARD_TRAP })
        assertTrue(first.resultCauseAssets.any { it.key == LiveLiteAssetKeys.CAUSE_PLAN })
        assertTrue(first.resultCauseAssets.any { it.key == LiveLiteAssetKeys.CAUSE_HERO_SPECIAL })
        assertTrue(first.resultCauseAssets.any { it.key == LiveLiteAssetKeys.CAUSE_FACILITY })
        assertTrue(first.heroPoses.all { poseSet ->
            poseSet.poses.getValue(LiveLiteHeroPose.Idle).key == LiveLiteAssetKeys.POSE_IDLE
        })
        assertEquals(LiveLiteWatchBeatKind.Plan, first.beats.first().kind)
        assertEquals(LiveLiteWatchBeatKind.PassiveReport, first.beats.last().kind)
    }

    @Test
    fun interventionDefinitionsExposeFiveStableOptionalActions() {
        val party = SeedGame.heroes.take(3)
        val definitions = LiveLiteExpedition.interventionDefinitions(party = party, quest = SeedGame.firstQuest)

        assertEquals(
            listOf(
                "intervention.spend_supply",
                "intervention.safer_route",
                "intervention.push_for_loot",
                "intervention.encourage_hero",
                "intervention.hero_trick",
            ),
            definitions.map { it.kind.assetKey },
        )
        assertTrue(definitions.all { it.maxUses == 1 })
        assertTrue(definitions.all { it.asset.category == LiveLiteAssetCategories.INTERVENTION })
        assertTrue(definitions.first { it.kind == LiveLiteInterventionKind.EncourageHero }.targetHeroId != null)
        assertEquals("brugg", definitions.first { it.kind == LiveLiteInterventionKind.HeroTrick }.targetHeroId)
    }

    @Test
    fun combinedInterventionBonusesStaySmallAndOptional() {
        val party = SeedGame.heroes.take(3)
        val definitions = LiveLiteExpedition.interventionDefinitions(party = party, quest = SeedGame.firstQuest)
        val noIntervention = LiveLiteExpedition.combinedInterventionEffect(emptyList())
        val allInterventions = LiveLiteExpedition.combinedInterventionEffect(definitions)

        assertEquals(LiveLiteInterventionEffect(), noIntervention)
        assertTrue(allInterventions.scoreBonus <= LiveLiteInterventionBounds.MAX_TOTAL_SCORE_BONUS)
        assertTrue(allInterventions.scoreBonus >= LiveLiteInterventionBounds.MIN_TOTAL_SCORE_DELTA)
        assertTrue(allInterventions.riskPenaltyDelta <= LiveLiteInterventionBounds.MAX_TOTAL_RISK_INCREASE)
        assertTrue(allInterventions.riskPenaltyDelta >= -LiveLiteInterventionBounds.MAX_TOTAL_RISK_REDUCTION)
        assertTrue(allInterventions.goldBonusPercent <= LiveLiteInterventionBounds.MAX_TOTAL_GOLD_BONUS_PERCENT)
        assertTrue(allInterventions.successLootBonus <= LiveLiteInterventionBounds.MAX_TOTAL_SUCCESS_LOOT_BONUS)
        assertTrue(allInterventions.xpBonus <= LiveLiteInterventionBounds.MAX_TOTAL_XP_BONUS)
        assertTrue(definitions.all { it.effect.scoreBonus <= LiveLiteInterventionBounds.MAX_SINGLE_SCORE_BONUS })
        assertTrue(definitions.all { -it.effect.riskPenaltyDelta <= LiveLiteInterventionBounds.MAX_SINGLE_RISK_REDUCTION })
        assertTrue(definitions.all { it.effect.goldBonusPercent <= LiveLiteInterventionBounds.MAX_SINGLE_GOLD_BONUS_PERCENT })
        assertTrue(definitions.all { it.effect.successLootBonus <= LiveLiteInterventionBounds.MAX_SINGLE_SUCCESS_LOOT_BONUS })
        assertTrue(definitions.all { it.effect.xpBonus <= LiveLiteInterventionBounds.MAX_SINGLE_XP_BONUS })
    }

    @Test
    fun previewUsesEstimatorValuesWithoutResolvingOutcome() {
        val party = SeedGame.heroes.take(3)
        val run = ExpeditionRun(
            quest = SeedGame.firstQuest.copy(difficulty = 150),
            partyHeroIds = party.map { it.id },
            startedAtMillis = 1_000L,
            endsAtMillis = 46_000L,
            planId = ExpeditionPlanCatalog.safetyFirstId,
        )
        val model = LiveLiteExpedition.buildWatchModel(run = run, party = party)
        val preview = LiveLiteExpedition.previewInterventions(
            model = model,
            selectedInterventionKeys = setOf(
                LiveLiteInterventionKind.SpendSupply.assetKey,
                LiveLiteInterventionKind.SaferRoute.assetKey,
            ),
        )

        assertEquals(model.estimate.partyPower, preview.basePartyPower)
        assertEquals(model.estimate.targetPower, preview.baseTargetPower)
        assertTrue(preview.adjustedPartyPower > preview.basePartyPower)
        assertTrue(preview.adjustedTargetPower < preview.baseTargetPower)
        assertTrue(preview.adjustedSuccessChancePercent >= preview.baseSuccessChancePercent)
    }

    @Test
    fun heroTrickIsDisabledWhenNoExistingSpecialApplies() {
        val quest = SeedGame.firstQuest.copy(tags = emptyList(), recommendedHeroIds = emptyList())
        val definitions = LiveLiteExpedition.interventionDefinitions(
            party = listOf(HeroCatalog.byId.getValue("brugg").toHero()),
            quest = quest,
        )
        val trick = definitions.first { it.kind == LiveLiteInterventionKind.HeroTrick }

        assertFalse(trick.enabled)
        assertEquals(LiveLiteInterventionEffect(), trick.effect)
    }
}