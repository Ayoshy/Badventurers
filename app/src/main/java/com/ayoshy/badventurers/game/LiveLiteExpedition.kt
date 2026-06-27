package com.ayoshy.badventurers.game

object LiveLiteAssetCategories {
    const val STAGE = "stage"
    const val POSE = "pose"
    const val HAZARD = "hazard"
    const val PLAN = "plan"
    const val CAUSE = "cause"
    const val PASSIVE = "passive"
    const val INTERVENTION = "intervention"
}

object LiveLiteAssetKeys {
    const val STAGE_CAVE = "stage.cave"
    const val STAGE_FOREST = "stage.forest"
    const val STAGE_SWAMP = "stage.swamp"
    const val STAGE_CRYPT = "stage.crypt"
    const val STAGE_SIEGE_CAMP = "stage.siege_camp"
    const val STAGE_RUINS = "stage.ruins"
    const val STAGE_CITY = "stage.city"
    const val STAGE_FORTRESS = "stage.fortress"
    const val STAGE_ROAD = "stage.road"

    const val POSE_IDLE = "pose.idle"
    const val POSE_EFFORT = "pose.effort"
    const val POSE_SUCCESS = "pose.success"
    const val POSE_MISTAKE = "pose.mistake"
    const val POSE_LOOT = "pose.loot"

    const val HAZARD_TRAP = "hazard.trap"
    const val HAZARD_PAPERWORK = "hazard.paperwork"
    const val HAZARD_CURSE = "hazard.curse"
    const val HAZARD_BANDIT = "hazard.bandit"
    const val HAZARD_UNDEAD = "hazard.undead"
    const val HAZARD_MAGIC = "hazard.magic"
    const val HAZARD_SIEGE = "hazard.siege"
    const val HAZARD_HEIST = "hazard.heist"
    const val HAZARD_OBSTACLE = "hazard.obstacle"
    const val HAZARD_ROUTE = "hazard.route"
    const val HAZARD_SWAMP = "hazard.swamp"
    const val HAZARD_GUARD = "hazard.guard"

    const val CAUSE_PLAN = "cause.plan"
    const val CAUSE_HERO_SPECIAL = "cause.hero_special"
    const val CAUSE_FACILITY = "cause.facility"
    const val CAUSE_ACHIEVEMENT = "cause.achievement"

    const val PASSIVE_GUILD_INCOME = "passive.guild_income"
    const val PASSIVE_CORE_CREW = "passive.core_crew"
    const val PASSIVE_FACILITY = "passive.facility"
    const val PASSIVE_QUEST_REGION = "passive.quest_region"

    fun plan(planId: String): String = "plan.${planId.toAssetSuffix()}"
}

data class LiveLiteAssetRef(
    val category: String,
    val key: String,
)

data class LiveLiteStageTheme(
    val biome: String,
    val theme: String,
    val asset: LiveLiteAssetRef,
)

enum class LiveLiteHeroPose(val assetKey: String) {
    Idle(LiveLiteAssetKeys.POSE_IDLE),
    Effort(LiveLiteAssetKeys.POSE_EFFORT),
    Success(LiveLiteAssetKeys.POSE_SUCCESS),
    Mistake(LiveLiteAssetKeys.POSE_MISTAKE),
    Loot(LiveLiteAssetKeys.POSE_LOOT),
}

data class LiveLiteHeroPoseSet(
    val heroId: String,
    val heroName: String,
    val heroClass: HeroClass,
    val poses: Map<LiveLiteHeroPose, LiveLiteAssetRef> = LiveLiteHeroPose.values().associateWith { pose ->
        LiveLiteAssetRef(LiveLiteAssetCategories.POSE, pose.assetKey)
    },
)

enum class LiveLiteWatchBeatKind {
    Plan,
    Hazard,
    HeroAction,
    ResultCause,
    PassiveReport,
}

data class LiveLiteWatchBeat(
    val index: Int,
    val secondOffset: Int,
    val kind: LiveLiteWatchBeatKind,
    val asset: LiveLiteAssetRef,
    val heroId: String? = null,
    val pose: LiveLiteHeroPose? = null,
)

data class LiveLiteWatchModel(
    val questId: String,
    val planId: String,
    val stage: LiveLiteStageTheme,
    val estimate: ExpeditionEstimate,
    val heroPoses: List<LiveLiteHeroPoseSet>,
    val hazardAssets: List<LiveLiteAssetRef>,
    val planAsset: LiveLiteAssetRef,
    val resultCauseAssets: List<LiveLiteAssetRef>,
    val passiveReportAsset: LiveLiteAssetRef,
    val beats: List<LiveLiteWatchBeat>,
    val interventions: List<LiveLiteInterventionDefinition>,
)

enum class LiveLiteInterventionKind(val assetKey: String) {
    SpendSupply("intervention.spend_supply"),
    SaferRoute("intervention.safer_route"),
    PushForLoot("intervention.push_for_loot"),
    EncourageHero("intervention.encourage_hero"),
    HeroTrick("intervention.hero_trick"),
}

data class LiveLiteInterventionEffect(
    val scoreBonus: Int = 0,
    val riskPenaltyDelta: Int = 0,
    val goldBonusPercent: Int = 0,
    val successLootBonus: Int = 0,
    val xpBonus: Int = 0,
    val durationSecondsDelta: Int = 0,
) {
    operator fun plus(other: LiveLiteInterventionEffect): LiveLiteInterventionEffect = LiveLiteInterventionEffect(
        scoreBonus = scoreBonus + other.scoreBonus,
        riskPenaltyDelta = riskPenaltyDelta + other.riskPenaltyDelta,
        goldBonusPercent = goldBonusPercent + other.goldBonusPercent,
        successLootBonus = successLootBonus + other.successLootBonus,
        xpBonus = xpBonus + other.xpBonus,
        durationSecondsDelta = durationSecondsDelta + other.durationSecondsDelta,
    )

    fun capped(): LiveLiteInterventionEffect = LiveLiteInterventionEffect(
        scoreBonus = scoreBonus.coerceIn(
            LiveLiteInterventionBounds.MIN_TOTAL_SCORE_DELTA,
            LiveLiteInterventionBounds.MAX_TOTAL_SCORE_BONUS,
        ),
        riskPenaltyDelta = riskPenaltyDelta.coerceIn(
            -LiveLiteInterventionBounds.MAX_TOTAL_RISK_REDUCTION,
            LiveLiteInterventionBounds.MAX_TOTAL_RISK_INCREASE,
        ),
        goldBonusPercent = goldBonusPercent.coerceIn(
            LiveLiteInterventionBounds.MIN_TOTAL_GOLD_DELTA_PERCENT,
            LiveLiteInterventionBounds.MAX_TOTAL_GOLD_BONUS_PERCENT,
        ),
        successLootBonus = successLootBonus.coerceIn(0, LiveLiteInterventionBounds.MAX_TOTAL_SUCCESS_LOOT_BONUS),
        xpBonus = xpBonus.coerceIn(0, LiveLiteInterventionBounds.MAX_TOTAL_XP_BONUS),
        durationSecondsDelta = durationSecondsDelta.coerceIn(
            LiveLiteInterventionBounds.MIN_TOTAL_DURATION_SECONDS_DELTA,
            LiveLiteInterventionBounds.MAX_TOTAL_DURATION_SECONDS_DELTA,
        ),
    )
}

object LiveLiteInterventionBounds {
    const val MIN_SINGLE_SCORE_DELTA = -6
    const val MAX_SINGLE_SCORE_BONUS = 8
    const val MAX_SINGLE_RISK_REDUCTION = 6
    const val MAX_SINGLE_RISK_INCREASE = 6
    const val MIN_SINGLE_GOLD_DELTA_PERCENT = -6
    const val MAX_SINGLE_GOLD_BONUS_PERCENT = 8
    const val MAX_SINGLE_SUCCESS_LOOT_BONUS = 1
    const val MAX_SINGLE_XP_BONUS = 3
    const val MAX_SINGLE_DURATION_SECONDS_DELTA = 30

    const val MIN_TOTAL_SCORE_DELTA = -6
    const val MAX_TOTAL_SCORE_BONUS = 12
    const val MAX_TOTAL_RISK_REDUCTION = 8
    const val MAX_TOTAL_RISK_INCREASE = 6
    const val MIN_TOTAL_GOLD_DELTA_PERCENT = -10
    const val MAX_TOTAL_GOLD_BONUS_PERCENT = 10
    const val MAX_TOTAL_SUCCESS_LOOT_BONUS = 1
    const val MAX_TOTAL_XP_BONUS = 4
    const val MIN_TOTAL_DURATION_SECONDS_DELTA = 0
    const val MAX_TOTAL_DURATION_SECONDS_DELTA = 45
    const val MAX_SELECTED_INTERVENTIONS = 1
}

data class LiveLiteInterventionDefinition(
    val kind: LiveLiteInterventionKind,
    val asset: LiveLiteAssetRef = LiveLiteAssetRef(LiveLiteAssetCategories.INTERVENTION, kind.assetKey),
    val targetHeroId: String? = null,
    val targetHeroName: String? = null,
    val enabled: Boolean = true,
    val maxUses: Int = 1,
    val upside: LiveLiteInterventionEffect,
    val downside: LiveLiteInterventionEffect,
) {
    val effect: LiveLiteInterventionEffect = (upside + downside).capped()
    val hasTradeoff: Boolean = upside != LiveLiteInterventionEffect() && downside != LiveLiteInterventionEffect()
}

data class LiveLiteInterventionPreview(
    val basePartyPower: Int,
    val adjustedPartyPower: Int,
    val baseTargetPower: Int,
    val adjustedTargetPower: Int,
    val baseSuccessChancePercent: Int,
    val adjustedSuccessChancePercent: Int,
    val baseDurationSeconds: Int,
    val adjustedDurationSeconds: Int,
    val selectedInterventionCount: Int,
    val maxSelectedInterventions: Int,
    val effect: LiveLiteInterventionEffect,
)

object LiveLiteExpedition {
    fun buildWatchModel(
        run: ExpeditionRun,
        party: List<Hero>,
        equipment: List<EquippedLoot> = emptyList(),
        facilityPowerBonus: Int = 0,
    ): LiveLiteWatchModel {
        val quest = run.quest
        val runParty = partyForRun(run, party)
        val plan = ExpeditionPlanCatalog.planForQuest(run.planId, quest)
        val estimate = ExpeditionEstimator.estimate(
            party = runParty,
            quest = quest,
            equipment = equipment,
            facilityPowerBonus = facilityPowerBonus,
            planId = plan.id,
        )
        val stage = stageThemeFor(quest)
        val hazards = hazardAssetsFor(quest)
        val planAsset = LiveLiteAssetRef(LiveLiteAssetCategories.PLAN, LiveLiteAssetKeys.plan(plan.id))
        val resultCauses = resultCauseAssetsFor(run = run.copy(planId = plan.id), party = runParty, facilityPowerBonus = facilityPowerBonus)
        val passiveAsset = LiveLiteAssetRef(LiveLiteAssetCategories.PASSIVE, LiveLiteAssetKeys.PASSIVE_GUILD_INCOME)
        val interventions = interventionDefinitions(party = runParty, quest = quest)

        return LiveLiteWatchModel(
            questId = quest.id,
            planId = plan.id,
            stage = stage,
            estimate = estimate,
            heroPoses = runParty.map { hero ->
                LiveLiteHeroPoseSet(
                    heroId = hero.id,
                    heroName = hero.name,
                    heroClass = hero.heroClass,
                )
            },
            hazardAssets = hazards,
            planAsset = planAsset,
            resultCauseAssets = resultCauses,
            passiveReportAsset = passiveAsset,
            beats = beatsFor(
                run = run.copy(planId = plan.id),
                party = runParty,
                estimate = estimate,
                hazards = hazards,
                planAsset = planAsset,
                resultCauseAssets = resultCauses,
                passiveAsset = passiveAsset,
            ),
            interventions = interventions,
        )
    }

    fun stageThemeFor(quest: Quest): LiveLiteStageTheme {
        val stage = when {
            quest.hasAny(QuestTag.Cave) -> Triple("cave", LiveLiteAssetKeys.STAGE_CAVE, LiveLiteAssetCategories.STAGE)
            quest.hasAny(QuestTag.Swamp, QuestTag.Poison, QuestTag.Rot) -> Triple("swamp", LiveLiteAssetKeys.STAGE_SWAMP, LiveLiteAssetCategories.STAGE)
            quest.hasAny(QuestTag.Undead, QuestTag.Curse, QuestTag.Debt) -> Triple("crypt", LiveLiteAssetKeys.STAGE_CRYPT, LiveLiteAssetCategories.STAGE)
            quest.hasAny(QuestTag.Siege, QuestTag.Camp) -> Triple("siege_camp", LiveLiteAssetKeys.STAGE_SIEGE_CAMP, LiveLiteAssetCategories.STAGE)
            quest.hasAny(QuestTag.Wilderness, QuestTag.Hunt, QuestTag.Exploration) -> Triple("forest", LiveLiteAssetKeys.STAGE_FOREST, LiveLiteAssetCategories.STAGE)
            quest.hasAny(QuestTag.Magic, QuestTag.Ancient) -> Triple("ruins", LiveLiteAssetKeys.STAGE_RUINS, LiveLiteAssetCategories.STAGE)
            quest.hasAny(QuestTag.Urban, QuestTag.Bandit, QuestTag.Stealth, QuestTag.Heist, QuestTag.Contract, QuestTag.Paperwork) -> Triple("city", LiveLiteAssetKeys.STAGE_CITY, LiveLiteAssetCategories.STAGE)
            quest.hasAny(QuestTag.Wall, QuestTag.Obstacle, QuestTag.Breach, QuestTag.Collapse) -> Triple("fortress", LiveLiteAssetKeys.STAGE_FORTRESS, LiveLiteAssetCategories.STAGE)
            else -> Triple("road", LiveLiteAssetKeys.STAGE_ROAD, LiveLiteAssetCategories.STAGE)
        }
        return LiveLiteStageTheme(
            biome = stage.first,
            theme = themeFor(quest),
            asset = LiveLiteAssetRef(stage.third, stage.second),
        )
    }

    fun hazardAssetsFor(quest: Quest): List<LiveLiteAssetRef> {
        val keys = listOfNotNull(
            LiveLiteAssetKeys.HAZARD_TRAP.takeIf { quest.hasAny(QuestTag.Trap) },
            LiveLiteAssetKeys.HAZARD_PAPERWORK.takeIf { quest.hasAny(QuestTag.Paperwork, QuestTag.Contract, QuestTag.Debt) },
            LiveLiteAssetKeys.HAZARD_CURSE.takeIf { quest.hasAny(QuestTag.Curse, QuestTag.Poison, QuestTag.Rot) },
            LiveLiteAssetKeys.HAZARD_BANDIT.takeIf { quest.hasAny(QuestTag.Bandit) },
            LiveLiteAssetKeys.HAZARD_UNDEAD.takeIf { quest.hasAny(QuestTag.Undead) },
            LiveLiteAssetKeys.HAZARD_MAGIC.takeIf { quest.hasAny(QuestTag.Magic, QuestTag.Ancient) },
            LiveLiteAssetKeys.HAZARD_SIEGE.takeIf { quest.hasAny(QuestTag.Siege, QuestTag.Camp) },
            LiveLiteAssetKeys.HAZARD_HEIST.takeIf { quest.hasAny(QuestTag.Heist, QuestTag.Stealth) },
            LiveLiteAssetKeys.HAZARD_OBSTACLE.takeIf { quest.hasAny(QuestTag.Wall, QuestTag.Obstacle, QuestTag.Breach, QuestTag.Collapse) },
            LiveLiteAssetKeys.HAZARD_ROUTE.takeIf { quest.hasAny(QuestTag.Wilderness, QuestTag.Hunt, QuestTag.Exploration) },
            LiveLiteAssetKeys.HAZARD_SWAMP.takeIf { quest.hasAny(QuestTag.Swamp) },
            LiveLiteAssetKeys.HAZARD_GUARD.takeIf { quest.hasAny(QuestTag.Escort, QuestTag.Guard) },
        ).ifEmpty { listOf(LiveLiteAssetKeys.HAZARD_ROUTE) }

        return keys.distinct().take(MAX_HAZARD_ASSETS).map { key -> LiveLiteAssetRef(LiveLiteAssetCategories.HAZARD, key) }
    }

    fun resultCauseAssetsFor(
        run: ExpeditionRun,
        party: List<Hero>,
        facilityPowerBonus: Int = 0,
    ): List<LiveLiteAssetRef> {
        val keys = mutableListOf<String>()
        if (run.planId != ExpeditionPlanCatalog.defaultPlanId) {
            keys += LiveLiteAssetKeys.CAUSE_PLAN
        }
        if (HeroSpecialCatalog.activeHeroes(party, run.quest).isNotEmpty()) {
            keys += LiveLiteAssetKeys.CAUSE_HERO_SPECIAL
        }
        if (facilityPowerBonus > 0) {
            keys += LiveLiteAssetKeys.CAUSE_FACILITY
        }
        return keys.ifEmpty { listOf(LiveLiteAssetKeys.CAUSE_PLAN) }
            .distinct()
            .map { key -> LiveLiteAssetRef(LiveLiteAssetCategories.CAUSE, key) }
    }

    fun interventionDefinitions(
        party: List<Hero>,
        quest: Quest,
    ): List<LiveLiteInterventionDefinition> {
        val encouragementTarget = party.minWithOrNull(compareBy<Hero> { PartyPowerCalculator.basePower(it) }.thenBy { it.id })
        val trickHero = HeroSpecialCatalog.activeHeroes(party, quest).firstOrNull()
        val trickUpside = trickHero?.let { heroTrickUpside(it, quest) } ?: LiveLiteInterventionEffect()
        return listOf(
            LiveLiteInterventionDefinition(
                kind = LiveLiteInterventionKind.SpendSupply,
                upside = LiveLiteInterventionEffect(scoreBonus = 6, xpBonus = 1),
                downside = LiveLiteInterventionEffect(riskPenaltyDelta = 2, durationSecondsDelta = 10),
            ),
            LiveLiteInterventionDefinition(
                kind = LiveLiteInterventionKind.SaferRoute,
                upside = LiveLiteInterventionEffect(riskPenaltyDelta = -6),
                downside = LiveLiteInterventionEffect(scoreBonus = -3, goldBonusPercent = -5, durationSecondsDelta = 15),
            ),
            LiveLiteInterventionDefinition(
                kind = LiveLiteInterventionKind.PushForLoot,
                upside = LiveLiteInterventionEffect(successLootBonus = 1),
                downside = LiveLiteInterventionEffect(scoreBonus = -4, riskPenaltyDelta = 6, durationSecondsDelta = 12),
            ),
            LiveLiteInterventionDefinition(
                kind = LiveLiteInterventionKind.EncourageHero,
                targetHeroId = encouragementTarget?.id,
                targetHeroName = encouragementTarget?.name,
                enabled = encouragementTarget != null,
                upside = if (encouragementTarget == null) {
                    LiveLiteInterventionEffect()
                } else {
                    LiveLiteInterventionEffect(scoreBonus = 5, xpBonus = 2)
                },
                downside = if (encouragementTarget == null) {
                    LiveLiteInterventionEffect()
                } else {
                    LiveLiteInterventionEffect(scoreBonus = -2, durationSecondsDelta = 8)
                },
            ),
            LiveLiteInterventionDefinition(
                kind = LiveLiteInterventionKind.HeroTrick,
                targetHeroId = trickHero?.id,
                targetHeroName = trickHero?.name,
                enabled = trickHero != null,
                upside = trickUpside,
                downside = heroTrickDownside(trickUpside),
            ),
        )
    }

    fun selectedInterventions(
        model: LiveLiteWatchModel,
        selectedInterventionKeys: Set<String>,
    ): List<LiveLiteInterventionDefinition> = model.interventions
        .filter { it.enabled && it.kind.assetKey in selectedInterventionKeys }
        .take(LiveLiteInterventionBounds.MAX_SELECTED_INTERVENTIONS)

    fun combinedInterventionEffect(definitions: List<LiveLiteInterventionDefinition>): LiveLiteInterventionEffect =
        definitions
            .filter { it.enabled }
            .take(LiveLiteInterventionBounds.MAX_SELECTED_INTERVENTIONS)
            .fold(LiveLiteInterventionEffect()) { total, definition -> total + definition.effect }
            .capped()

    fun previewInterventions(
        model: LiveLiteWatchModel,
        selectedInterventionKeys: Set<String>,
    ): LiveLiteInterventionPreview {
        val selected = selectedInterventions(model, selectedInterventionKeys)
        val effect = combinedInterventionEffect(selected)
        val adjustedPartyPower = (model.estimate.partyPower + effect.scoreBonus).coerceAtLeast(0)
        val adjustedTargetPower = (model.estimate.targetPower + effect.riskPenaltyDelta).coerceAtLeast(0)
        val adjustedDurationSeconds = (model.estimate.durationSeconds + effect.durationSecondsDelta).coerceAtLeast(0)
        val adjustedChance = successChancePercent(
            partyPower = adjustedPartyPower,
            targetPower = adjustedTargetPower,
            minimumRoll = model.estimate.minimumRoll,
        )
        return LiveLiteInterventionPreview(
            basePartyPower = model.estimate.partyPower,
            adjustedPartyPower = adjustedPartyPower,
            baseTargetPower = model.estimate.targetPower,
            adjustedTargetPower = adjustedTargetPower,
            baseSuccessChancePercent = model.estimate.successChancePercent,
            adjustedSuccessChancePercent = adjustedChance,
            baseDurationSeconds = model.estimate.durationSeconds,
            adjustedDurationSeconds = adjustedDurationSeconds,
            selectedInterventionCount = selected.size,
            maxSelectedInterventions = LiveLiteInterventionBounds.MAX_SELECTED_INTERVENTIONS,
            effect = effect,
        )
    }

    private fun partyForRun(run: ExpeditionRun, party: List<Hero>): List<Hero> {
        val byId = party.associateBy { it.id }
        val selected = run.partyHeroIds.mapNotNull { heroId -> byId[heroId] }
        return selected.ifEmpty { party.take(run.quest.partySlots) }
    }

    private fun beatsFor(
        run: ExpeditionRun,
        party: List<Hero>,
        estimate: ExpeditionEstimate,
        hazards: List<LiveLiteAssetRef>,
        planAsset: LiveLiteAssetRef,
        resultCauseAssets: List<LiveLiteAssetRef>,
        passiveAsset: LiveLiteAssetRef,
    ): List<LiveLiteWatchBeat> {
        val beats = mutableListOf<LiveLiteWatchBeat>()
        beats += LiveLiteWatchBeat(
            index = beats.size,
            secondOffset = beats.size * BEAT_SPACING_SECONDS,
            kind = LiveLiteWatchBeatKind.Plan,
            asset = planAsset,
        )
        hazards.take(2).forEach { hazard ->
            beats += LiveLiteWatchBeat(
                index = beats.size,
                secondOffset = beats.size * BEAT_SPACING_SECONDS,
                kind = LiveLiteWatchBeatKind.Hazard,
                asset = hazard,
            )
        }
        party.forEachIndexed { heroIndex, hero ->
            val pose = poseFor(heroIndex = heroIndex, partySize = party.size, run = run, estimate = estimate)
            beats += LiveLiteWatchBeat(
                index = beats.size,
                secondOffset = beats.size * BEAT_SPACING_SECONDS,
                kind = LiveLiteWatchBeatKind.HeroAction,
                asset = LiveLiteAssetRef(LiveLiteAssetCategories.POSE, pose.assetKey),
                heroId = hero.id,
                pose = pose,
            )
        }
        beats += LiveLiteWatchBeat(
            index = beats.size,
            secondOffset = beats.size * BEAT_SPACING_SECONDS,
            kind = LiveLiteWatchBeatKind.ResultCause,
            asset = resultCauseAssets.first(),
        )
        beats += LiveLiteWatchBeat(
            index = beats.size,
            secondOffset = beats.size * BEAT_SPACING_SECONDS,
            kind = LiveLiteWatchBeatKind.PassiveReport,
            asset = passiveAsset,
        )
        return beats
    }

    private fun poseFor(
        heroIndex: Int,
        partySize: Int,
        run: ExpeditionRun,
        estimate: ExpeditionEstimate,
    ): LiveLiteHeroPose {
        val outcome = run.result?.outcome
        return when {
            outcome == ExpeditionOutcome.GreatSuccess && heroIndex == partySize - 1 -> LiveLiteHeroPose.Loot
            outcome == ExpeditionOutcome.GreatSuccess || outcome == ExpeditionOutcome.Success -> LiveLiteHeroPose.Success
            outcome == ExpeditionOutcome.Failure || outcome == ExpeditionOutcome.RidiculousFailure -> {
                if (heroIndex == 0) LiveLiteHeroPose.Mistake else LiveLiteHeroPose.Effort
            }
            estimate.successChancePercent >= 75 && heroIndex == partySize - 1 -> LiveLiteHeroPose.Success
            estimate.successChancePercent < 35 && heroIndex == 0 -> LiveLiteHeroPose.Mistake
            run.quest.hasAny(QuestTag.Heist, QuestTag.Stealth) && heroIndex == partySize - 1 -> LiveLiteHeroPose.Loot
            else -> LiveLiteHeroPose.Effort
        }
    }

    private fun heroTrickUpside(hero: Hero, quest: Quest): LiveLiteInterventionEffect {
        val modifiers = HeroSpecialCatalog.modifiersFor(listOf(hero), quest)
        val effect = LiveLiteInterventionEffect(
            scoreBonus = when {
                modifiers.scoreBonus > 0 -> 6
                modifiers.minimumRoll > 0 -> 4
                else -> 0
            },
            riskPenaltyDelta = if (modifiers.riskPenaltyReduction > 0 || modifiers.preventsRidiculousFailure) -4 else 0,
            goldBonusPercent = if (modifiers.goldBonusPercent > 0) 5 else 0,
            successLootBonus = if (modifiers.successLootBonus > 0) 1 else 0,
            xpBonus = if (modifiers.xpBonus > 0) 2 else 0,
        )
        return if (effect == LiveLiteInterventionEffect()) {
            LiveLiteInterventionEffect(scoreBonus = 3)
        } else {
            effect
        }
    }

    private fun heroTrickDownside(upside: LiveLiteInterventionEffect): LiveLiteInterventionEffect =
        if (upside == LiveLiteInterventionEffect()) {
            LiveLiteInterventionEffect()
        } else {
            LiveLiteInterventionEffect(
                riskPenaltyDelta = if (upside.riskPenaltyDelta < 0) 2 else 3,
                goldBonusPercent = if (upside.goldBonusPercent > 0 || upside.successLootBonus > 0) -4 else 0,
                durationSecondsDelta = 10,
            )
        }

    private fun themeFor(quest: Quest): String = when {
        quest.hasAny(QuestTag.Paperwork, QuestTag.Contract, QuestTag.Debt) -> "bureaucracy"
        quest.hasAny(QuestTag.Curse, QuestTag.Undead, QuestTag.Rot) -> "haunted"
        quest.hasAny(QuestTag.Heist, QuestTag.Stealth) -> "sneaky"
        quest.hasAny(QuestTag.Siege, QuestTag.Camp, QuestTag.Guard, QuestTag.Escort) -> "defensive"
        quest.hasAny(QuestTag.Magic, QuestTag.Ancient) -> "arcane"
        else -> "default"
    }

    private fun successChancePercent(partyPower: Int, targetPower: Int, minimumRoll: Int): Int {
        val requiredRoll = targetPower - partyPower
        return when {
            requiredRoll <= minimumRoll -> 100
            requiredRoll > 100 -> 0
            else -> ((101 - requiredRoll) * 100 / 101).coerceIn(0, 100)
        }
    }

    private const val MAX_HAZARD_ASSETS = 3
    private const val BEAT_SPACING_SECONDS = 6
}

private fun String.toAssetSuffix(): String = lowercase().map { char ->
    if (char.isLetterOrDigit() || char == '_') char else '_'
}.joinToString(separator = "")