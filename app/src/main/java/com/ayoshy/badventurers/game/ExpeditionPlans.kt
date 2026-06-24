package com.ayoshy.badventurers.game

import kotlin.math.roundToInt

data class ExpeditionPlan(
    val id: String,
    val title: String,
    val summary: String,
    val modifiers: ExpeditionPlanModifiers = ExpeditionPlanModifiers(),
)

data class ExpeditionPlanModifiers(
    val durationPercentDelta: Int = 0,
    val scoreBonus: Int = 0,
    val riskPenaltyDelta: Int = 0,
    val goldBonusPercent: Int = 0,
    val successLootBonus: Int = 0,
    val greatSuccessMarginDelta: Int = 0,
)

object ExpeditionPlanCatalog {
    const val defaultPlanId: String = "standard_contract"
    const val defaultPlayerPlanId: String = "rush_the_job"

    const val rushTheJobId: String = "rush_the_job"
    const val safetyFirstId: String = "safety_first"
    const val lootPriorityId: String = "loot_priority"
    const val auditEverythingId: String = "audit_everything"

    private val standard = ExpeditionPlan(
        id = defaultPlanId,
        title = "Standard Contract",
        summary = "No clever clause, no heroic accounting, no obvious lawsuit.",
    )

    private val rushTheJob = ExpeditionPlan(
        id = rushTheJobId,
        title = "Rush the Job",
        summary = "Return faster and squeeze a little more gold out of the panic, at the cost of risk.",
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = -30,
            scoreBonus = -5,
            riskPenaltyDelta = 12,
            goldBonusPercent = 5,
        ),
    )

    private val safetyFirst = ExpeditionPlan(
        id = safetyFirstId,
        title = "Safety First",
        summary = "Reduce risk for important attempts, but make spectacular victories harder to justify.",
        modifiers = ExpeditionPlanModifiers(
            riskPenaltyDelta = -10,
            greatSuccessMarginDelta = 25,
        ),
    )

    private val lootPriority = ExpeditionPlan(
        id = lootPriorityId,
        title = "Loot Priority",
        summary = "Ask the party to check every suspicious crate. The job gets harder, but success pays in items.",
        modifiers = ExpeditionPlanModifiers(
            scoreBonus = -12,
            riskPenaltyDelta = 6,
            successLootBonus = 1,
        ),
    )

    private val auditEverything = ExpeditionPlan(
        id = auditEverythingId,
        title = "Audit Everything",
        summary = "Take longer and weaponize paperwork for better gold on properly documented trouble.",
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 25,
            riskPenaltyDelta = 4,
            goldBonusPercent = 20,
        ),
    )

    private val visiblePlans = listOf(rushTheJob, safetyFirst, lootPriority, auditEverything)
    val all: List<ExpeditionPlan> = listOf(standard) + visiblePlans
    private val byId = all.associateBy { it.id }

    fun byId(planId: String?): ExpeditionPlan = byId[planId] ?: standard

    fun coercePlanId(planId: String?): String = byId(planId).id

    fun availableFor(quest: Quest): List<ExpeditionPlan> = visiblePlans

    fun selectedPlanForUi(planId: String?, quest: Quest): ExpeditionPlan {
        val available = availableFor(quest)
        return available.firstOrNull { it.id == planId }
            ?: available.firstOrNull { it.id == defaultPlayerPlanId }
            ?: standard
    }

    fun modifiersFor(planId: String?, quest: Quest): ExpeditionPlanModifiers = byId(planId).modifiers

    fun durationSeconds(quest: Quest, planId: String?): Int {
        val durationPercent = 100 + modifiersFor(planId, quest).durationPercentDelta
        return (quest.durationSeconds * durationPercent / 100.0)
            .roundToInt()
            .coerceAtLeast(MIN_EXPEDITION_DURATION_SECONDS)
    }

    private const val MIN_EXPEDITION_DURATION_SECONDS = 15
}
