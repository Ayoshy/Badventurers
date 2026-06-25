package com.ayoshy.badventurers.game

enum class GuildFacility {
    NoticeBoard,
    TrainingYard,
    BunkRoom,
    ArmoryForge,
    Infirmary,
    ScoutTable,
    TavernKitchen,
    AccountantOffice,
}

enum class GuildFacilityEffect {
    QuestUnlocks,
    PartySlots,
    HeroPower,
    HeroXpGain,
    RiskMitigation,
    ExpeditionDuration,
    PlanWarnings,
    UnlockPreviews,
    PassiveScouting,
    LootQuality,
    OfflineCap,
    RecruitPoolQuality,
    GoldReputationYield,
}

data class GuildFacilityUnlockRequirement(
    val minReputation: Int = 0,
    val minCompletedQuestCount: Int = 0,
    val minGuildLevel: Int = 1,
    val requiredFacilityLevels: Map<GuildFacility, Int> = emptyMap(),
)

data class GuildFacilityCostCurve(
    val base: Int,
    val step: Int,
) {
    fun costForLevel(level: Int): Int =
        base + (level - 1).coerceAtLeast(0) * step
}

data class GuildFacilityDefinition(
    val facility: GuildFacility,
    val id: String,
    val maxLevel: Int,
    val cost: GuildFacilityCostCurve,
    val unlockRequirement: GuildFacilityUnlockRequirement = GuildFacilityUnlockRequirement(),
    val effects: List<GuildFacilityEffect>,
    val achievementFacilityId: String = id,
    val implemented: Boolean = false,
) {
    fun costForNextLevel(currentLevel: Int): Int? =
        if (currentLevel >= maxLevel) null else cost.costForLevel(currentLevel.coerceAtLeast(1))
}

data class GuildFacilityUpgradeState(
    val definition: GuildFacilityDefinition,
    val level: Int,
    val unlocked: Boolean,
    val maxed: Boolean,
    val nextCost: Int?,
    val missingGold: Int,
    val unmetRequirement: GuildFacilityUnlockRequirement?,
)

enum class ScoutPlanWarningType {
    HigherRisk,
    LowerPower,
    LongerDuration,
    HarderGreatSuccess,
    LowerGold,
}

data class ScoutPlanWarning(
    val type: ScoutPlanWarningType,
    val amount: Int,
)

data class ScoutTableLevelBehavior(
    val revealedPlanWarnings: Int,
    val revealsUnlockPreviews: Boolean,
    val enablesPassiveScoutingIncidents: Boolean,
)

object ScoutTableIntel {
    fun behavior(level: Int): ScoutTableLevelBehavior {
        val safeLevel = level.coerceAtLeast(0)
        return ScoutTableLevelBehavior(
            revealedPlanWarnings = when {
                safeLevel <= 0 -> 0
                safeLevel == 1 -> 1
                safeLevel == 2 -> 2
                else -> 3
            },
            revealsUnlockPreviews = safeLevel >= 2,
            enablesPassiveScoutingIncidents = safeLevel > 0,
        )
    }

    fun planWarningsFor(level: Int, plan: ExpeditionPlan, quest: Quest): List<ScoutPlanWarning> {
        val behavior = behavior(level)
        if (behavior.revealedPlanWarnings == 0) return emptyList()

        val modifiers = ExpeditionPlanCatalog.modifiersFor(plan.id, quest)
        val warnings = buildList {
            if (modifiers.riskPenaltyDelta > 0) {
                add(ScoutPlanWarning(ScoutPlanWarningType.HigherRisk, modifiers.riskPenaltyDelta))
            }
            if (modifiers.scoreBonus < 0) {
                add(ScoutPlanWarning(ScoutPlanWarningType.LowerPower, -modifiers.scoreBonus))
            }
            if (modifiers.durationPercentDelta > 0) {
                add(ScoutPlanWarning(ScoutPlanWarningType.LongerDuration, modifiers.durationPercentDelta))
            }
            if (modifiers.greatSuccessMarginDelta > 0) {
                add(ScoutPlanWarning(ScoutPlanWarningType.HarderGreatSuccess, modifiers.greatSuccessMarginDelta))
            }
            if (modifiers.goldBonusPercent < 0) {
                add(ScoutPlanWarning(ScoutPlanWarningType.LowerGold, -modifiers.goldBonusPercent))
            }
        }
        return warnings.take(behavior.revealedPlanWarnings)
    }
}

object GuildFacilityCatalog {
    val definitions: List<GuildFacilityDefinition> = listOf(
        GuildFacilityDefinition(
            facility = GuildFacility.NoticeBoard,
            id = "notice_board",
            maxLevel = 5,
            cost = GuildFacilityCostCurve(base = 600, step = 250),
            effects = listOf(GuildFacilityEffect.QuestUnlocks, GuildFacilityEffect.GoldReputationYield),
            achievementFacilityId = AchievementCatalog.NOTICE_BOARD_FACILITY,
            implemented = true,
        ),
        GuildFacilityDefinition(
            facility = GuildFacility.TrainingYard,
            id = "training_yard",
            maxLevel = 5,
            cost = GuildFacilityCostCurve(base = 450, step = 300),
            effects = listOf(GuildFacilityEffect.HeroPower, GuildFacilityEffect.HeroXpGain, GuildFacilityEffect.RiskMitigation),
            achievementFacilityId = AchievementCatalog.TRAINING_YARD_FACILITY,
            implemented = true,
        ),
        GuildFacilityDefinition(
            facility = GuildFacility.BunkRoom,
            id = "bunk_room",
            maxLevel = 4,
            cost = GuildFacilityCostCurve(base = 750, step = 450),
            effects = listOf(GuildFacilityEffect.PartySlots),
            achievementFacilityId = AchievementCatalog.BUNK_ROOM_FACILITY,
            implemented = true,
        ),
        GuildFacilityDefinition(
            facility = GuildFacility.ArmoryForge,
            id = "armory_forge",
            maxLevel = 4,
            cost = GuildFacilityCostCurve(base = 900, step = 450),
            unlockRequirement = GuildFacilityUnlockRequirement(
                minCompletedQuestCount = 3,
                requiredFacilityLevels = mapOf(GuildFacility.NoticeBoard to 2),
            ),
            effects = listOf(GuildFacilityEffect.LootQuality),
        ),
        GuildFacilityDefinition(
            facility = GuildFacility.Infirmary,
            id = "infirmary",
            maxLevel = 3,
            cost = GuildFacilityCostCurve(base = 800, step = 400),
            unlockRequirement = GuildFacilityUnlockRequirement(minCompletedQuestCount = 2),
            effects = listOf(GuildFacilityEffect.RiskMitigation),
        ),
        GuildFacilityDefinition(
            facility = GuildFacility.ScoutTable,
            id = "scout_table",
            maxLevel = 4,
            cost = GuildFacilityCostCurve(base = 700, step = 350),
            unlockRequirement = GuildFacilityUnlockRequirement(
                minReputation = 8,
                requiredFacilityLevels = mapOf(GuildFacility.NoticeBoard to 2),
            ),
            effects = listOf(
                GuildFacilityEffect.ExpeditionDuration,
                GuildFacilityEffect.PlanWarnings,
                GuildFacilityEffect.UnlockPreviews,
                GuildFacilityEffect.PassiveScouting,
            ),
            implemented = true,
        ),
        GuildFacilityDefinition(
            facility = GuildFacility.TavernKitchen,
            id = "tavern_kitchen",
            maxLevel = 4,
            cost = GuildFacilityCostCurve(base = 650, step = 350),
            unlockRequirement = GuildFacilityUnlockRequirement(
                minCompletedQuestCount = 3,
                requiredFacilityLevels = mapOf(GuildFacility.TrainingYard to 2),
            ),
            effects = listOf(GuildFacilityEffect.HeroXpGain, GuildFacilityEffect.OfflineCap),
        ),
        GuildFacilityDefinition(
            facility = GuildFacility.AccountantOffice,
            id = "accountant_office",
            maxLevel = 3,
            cost = GuildFacilityCostCurve(base = 1_100, step = 550),
            unlockRequirement = GuildFacilityUnlockRequirement(
                minReputation = 12,
                requiredFacilityLevels = mapOf(GuildFacility.NoticeBoard to 2),
            ),
            effects = listOf(GuildFacilityEffect.GoldReputationYield, GuildFacilityEffect.RecruitPoolQuality),
        ),
    )

    val byFacility: Map<GuildFacility, GuildFacilityDefinition> =
        definitions.associateBy { it.facility }

    init {
        require(definitions.size == byFacility.size) { "Guild facility ids must be unique." }
        require(GuildFacility.values().all { it in byFacility }) { "Every guild facility needs a definition." }
    }

    fun definition(facility: GuildFacility): GuildFacilityDefinition =
        byFacility.getValue(facility)

    fun upgradeState(facility: GuildFacility, state: PlaySessionState): GuildFacilityUpgradeState {
        val definition = definition(facility)
        val level = state.facilityLevel(facility)
        val nextCost = definition.costForNextLevel(level)
        val unmetRequirement = definition.unlockRequirement.takeUnless { isRequirementMet(it, state) }
        val unlocked = unmetRequirement == null
        return GuildFacilityUpgradeState(
            definition = definition,
            level = level,
            unlocked = unlocked,
            maxed = nextCost == null,
            nextCost = nextCost,
            missingGold = nextCost?.let { (it - state.gold).coerceAtLeast(0) } ?: 0,
            unmetRequirement = unmetRequirement,
        )
    }

    fun upgradeStates(state: PlaySessionState): List<GuildFacilityUpgradeState> =
        definitions.map { definition -> upgradeState(definition.facility, state) }

    fun isRequirementMet(requirement: GuildFacilityUnlockRequirement, state: PlaySessionState): Boolean =
        state.reputation >= requirement.minReputation &&
            state.completedQuestCount >= requirement.minCompletedQuestCount &&
            state.guildLevel >= requirement.minGuildLevel &&
            requirement.requiredFacilityLevels.all { (facility, level) -> state.facilityLevel(facility) >= level }
}
