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
        assertTrue(definitions.filter { it.enabled }.all { it.hasTradeoff })
        assertTrue(definitions.first { it.kind == LiveLiteInterventionKind.EncourageHero }.targetHeroId != null)
        assertEquals("brugg", definitions.first { it.kind == LiveLiteInterventionKind.HeroTrick }.targetHeroId)
    }

    @Test
    fun combinedInterventionEffectsAreBoundedAndLimitedToOneChoice() {
        val party = SeedGame.heroes.take(3)
        val definitions = LiveLiteExpedition.interventionDefinitions(party = party, quest = SeedGame.firstQuest)
        val noIntervention = LiveLiteExpedition.combinedInterventionEffect(emptyList())
        val allPressed = LiveLiteExpedition.combinedInterventionEffect(definitions)

        assertEquals(LiveLiteInterventionEffect(), noIntervention)
        assertEquals(definitions.first().effect, allPressed)
        definitions.filter { it.enabled }.forEach { definition ->
            assertTrue(definition.upside != LiveLiteInterventionEffect())
            assertTrue(definition.downside != LiveLiteInterventionEffect())
            assertTrue(definition.effect.scoreBonus <= LiveLiteInterventionBounds.MAX_SINGLE_SCORE_BONUS)
            assertTrue(definition.effect.scoreBonus >= LiveLiteInterventionBounds.MIN_SINGLE_SCORE_DELTA)
            assertTrue(definition.effect.riskPenaltyDelta <= LiveLiteInterventionBounds.MAX_SINGLE_RISK_INCREASE)
            assertTrue(definition.effect.riskPenaltyDelta >= -LiveLiteInterventionBounds.MAX_SINGLE_RISK_REDUCTION)
            assertTrue(definition.effect.goldBonusPercent <= LiveLiteInterventionBounds.MAX_SINGLE_GOLD_BONUS_PERCENT)
            assertTrue(definition.effect.goldBonusPercent >= LiveLiteInterventionBounds.MIN_SINGLE_GOLD_DELTA_PERCENT)
            assertTrue(definition.effect.successLootBonus <= LiveLiteInterventionBounds.MAX_SINGLE_SUCCESS_LOOT_BONUS)
            assertTrue(definition.effect.xpBonus <= LiveLiteInterventionBounds.MAX_SINGLE_XP_BONUS)
            assertTrue(definition.effect.durationSecondsDelta <= LiveLiteInterventionBounds.MAX_SINGLE_DURATION_SECONDS_DELTA)
        }
    }

    @Test
    fun pushForLootTradesLootForHarderOddsAndMoreTime() {
        val party = SeedGame.heroes.take(3)
        val baselinePower = ExpeditionEstimator.estimate(party = party, quest = SeedGame.firstQuest).partyPower
        val run = ExpeditionRun(
            quest = SeedGame.firstQuest.copy(difficulty = baselinePower + 20),
            partyHeroIds = party.map { it.id },
            startedAtMillis = 1_000L,
            endsAtMillis = 46_000L,
            planId = ExpeditionPlanCatalog.defaultPlanId,
        )
        val model = LiveLiteExpedition.buildWatchModel(run = run, party = party)
        val preview = LiveLiteExpedition.previewInterventions(
            model = model,
            selectedInterventionKeys = setOf(LiveLiteInterventionKind.PushForLoot.assetKey),
        )

        assertEquals(model.estimate.partyPower, preview.basePartyPower)
        assertEquals(model.estimate.targetPower, preview.baseTargetPower)
        assertEquals(1, preview.effect.successLootBonus)
        assertTrue(preview.adjustedPartyPower < preview.basePartyPower)
        assertTrue(preview.adjustedTargetPower > preview.baseTargetPower)
        assertTrue(preview.adjustedDurationSeconds > preview.baseDurationSeconds)
        assertTrue(preview.adjustedSuccessChancePercent < preview.baseSuccessChancePercent)
    }

    @Test
    fun saferRouteTradesRiskForPowerRewardAndTime() {
        val party = SeedGame.heroes.take(3)
        val run = ExpeditionRun(
            quest = SeedGame.firstQuest.copy(difficulty = 90),
            partyHeroIds = party.map { it.id },
            startedAtMillis = 1_000L,
            endsAtMillis = 46_000L,
            planId = ExpeditionPlanCatalog.defaultPlanId,
        )
        val model = LiveLiteExpedition.buildWatchModel(run = run, party = party)
        val preview = LiveLiteExpedition.previewInterventions(
            model = model,
            selectedInterventionKeys = setOf(LiveLiteInterventionKind.SaferRoute.assetKey),
        )

        assertTrue(preview.adjustedTargetPower < preview.baseTargetPower)
        assertTrue(preview.adjustedPartyPower < preview.basePartyPower)
        assertTrue(preview.effect.goldBonusPercent < 0)
        assertTrue(preview.adjustedDurationSeconds > preview.baseDurationSeconds)
    }

    @Test
    fun noInterventionPreviewKeepsBaselineUnchanged() {
        val party = SeedGame.heroes.take(3)
        val run = ExpeditionRun(
            quest = SeedGame.firstQuest,
            partyHeroIds = party.map { it.id },
            startedAtMillis = 1_000L,
            endsAtMillis = 46_000L,
            planId = ExpeditionPlanCatalog.defaultPlanId,
        )
        val model = LiveLiteExpedition.buildWatchModel(run = run, party = party)
        val preview = LiveLiteExpedition.previewInterventions(model = model, selectedInterventionKeys = emptySet())

        assertEquals(0, preview.selectedInterventionCount)
        assertEquals(LiveLiteInterventionEffect(), preview.effect)
        assertEquals(model.estimate.partyPower, preview.adjustedPartyPower)
        assertEquals(model.estimate.targetPower, preview.adjustedTargetPower)
        assertEquals(model.estimate.successChancePercent, preview.adjustedSuccessChancePercent)
        assertEquals(model.estimate.durationSeconds, preview.adjustedDurationSeconds)
    }

    @Test
    fun previewIgnoresExtraSelectedButtonsAfterLimit() {
        val party = SeedGame.heroes.take(3)
        val run = ExpeditionRun(
            quest = SeedGame.firstQuest,
            partyHeroIds = party.map { it.id },
            startedAtMillis = 1_000L,
            endsAtMillis = 46_000L,
            planId = ExpeditionPlanCatalog.defaultPlanId,
        )
        val model = LiveLiteExpedition.buildWatchModel(run = run, party = party)
        val allKeys = model.interventions.map { it.kind.assetKey }.toSet()
        val preview = LiveLiteExpedition.previewInterventions(model = model, selectedInterventionKeys = allKeys)

        assertEquals(LiveLiteInterventionBounds.MAX_SELECTED_INTERVENTIONS, preview.maxSelectedInterventions)
        assertEquals(1, preview.selectedInterventionCount)
        assertEquals(model.interventions.first().effect, preview.effect)
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
        assertEquals(LiveLiteInterventionEffect(), trick.upside)
        assertEquals(LiveLiteInterventionEffect(), trick.downside)
        assertEquals(LiveLiteInterventionEffect(), trick.effect)
    }
}