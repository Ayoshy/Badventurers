package com.ayoshy.badventurers.game

import kotlin.math.roundToInt

private const val SPECIAL_CONTRACT_PLAN_COST = 1

data class ExpeditionPlan(
    val id: String,
    val title: String,
    val summary: String,
    val questIds: Set<String> = emptySet(),
    val modifiers: ExpeditionPlanModifiers = ExpeditionPlanModifiers(),
    val requiresSpecialContract: Boolean = false,
) {
    val specialContractCost: Int
        get() = if (requiresSpecialContract) SPECIAL_CONTRACT_PLAN_COST else 0
}

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
    const val sealSideTunnelId: String = "seal_side_tunnel"
    const val followWorstMapId: String = "follow_worst_map"
    const val demandReceiptsId: String = "demand_receipts"
    const val blessTheBrineId: String = "bless_the_brine"
    const val moonlessShortcutId: String = "moonless_shortcut"
    const val rationTheBiscuitsId: String = "ration_the_biscuits"
    const val bringDoorFormsId: String = "bring_door_forms"
    const val itemizedLastRitesId: String = "itemized_last_rites"
    const val paperworkTollId: String = "paperwork_toll_of_chaos_clause"
    const val caravanManifestId: String = "licensed_guild_caravan_haunt_clause"
    const val notaryNightPatrolId: String = "notary_night_patrol_clause"
    const val inspectorateBanquetId: String = "inspectorate_cove_banquet_clause"
    const val weddingOathLedgerId: String = "wedding_oath_ledger"
    const val sunkenTollDredgeId: String = "sunken_toll_dredge"
    const val crownReceiptSubpoenaId: String = "crown_receipt_subpoena"
    const val sidewaysTowerBraceId: String = "sideways_tower_brace"

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

    private val sealSideTunnel = ExpeditionPlan(
        id = sealSideTunnelId,
        title = "Seal the Side Tunnel",
        summary = "Spend time closing the suspicious route before it becomes the main route.",
        questIds = setOf("cave_minor_regrets"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 15,
            scoreBonus = 10,
            riskPenaltyDelta = -8,
        ),
        requiresSpecialContract = true,
    )

    private val followWorstMap = ExpeditionPlan(
        id = followWorstMapId,
        title = "Follow the Worst Map",
        summary = "Trust the map everyone hates. It is faster, stranger, and occasionally full of loot.",
        questIds = setOf("forest_of_wrong_turns"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = -15,
            scoreBonus = -4,
            riskPenaltyDelta = 8,
            successLootBonus = 1,
        ),
        requiresSpecialContract = true,
    )

    private val demandReceipts = ExpeditionPlan(
        id = demandReceiptsId,
        title = "Demand Receipts",
        summary = "Fight bandit bureaucracy with better bureaucracy and a very sharp stamp.",
        questIds = setOf("bandit_tax_office"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 20,
            scoreBonus = 6,
            riskPenaltyDelta = 4,
            goldBonusPercent = 30,
            greatSuccessMarginDelta = 10,
        ),
        requiresSpecialContract = true,
    )

    private val blessTheBrine = ExpeditionPlan(
        id = blessTheBrineId,
        title = "Bless the Brine",
        summary = "Turn swamp water into a defensive asset, or at least a less personal insult.",
        questIds = setOf("salted_swamp_chapel"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 10,
            scoreBonus = 12,
            riskPenaltyDelta = -6,
            greatSuccessMarginDelta = 8,
        ),
        requiresSpecialContract = true,
    )

    private val moonlessShortcut = ExpeditionPlan(
        id = moonlessShortcutId,
        title = "Moonless Shortcut",
        summary = "Cut through darker alleys for quicker delivery and a higher chance of regrettable extras.",
        questIds = setOf("moonlit_smuggler_run"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = -20,
            scoreBonus = -6,
            riskPenaltyDelta = 10,
            successLootBonus = 1,
        ),
        requiresSpecialContract = true,
    )

    private val rationTheBiscuits = ExpeditionPlan(
        id = rationTheBiscuitsId,
        title = "Ration the Biscuits",
        summary = "Stretch the siege supplies into morale, discipline, and several formal biscuit audits.",
        questIds = setOf("the_hungry_siege"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 15,
            scoreBonus = 12,
            riskPenaltyDelta = -6,
            greatSuccessMarginDelta = 10,
        ),
        requiresSpecialContract = true,
    )

    private val bringDoorForms = ExpeditionPlan(
        id = bringDoorFormsId,
        title = "Bring the Door Forms",
        summary = "Confront the final lock with paperwork so complete the hinges feel supervised.",
        questIds = setOf("the_last_locked_door"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 20,
            scoreBonus = 16,
            riskPenaltyDelta = -4,
            goldBonusPercent = 10,
        ),
        requiresSpecialContract = true,
    )

    private val itemizedLastRites = ExpeditionPlan(
        id = itemizedLastRitesId,
        title = "Itemized Last Rites",
        summary = "Read the undead debt ledger aloud and charge a documentation fee.",
        questIds = setOf("crypt_of_unpaid_debts"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 25,
            scoreBonus = 10,
            riskPenaltyDelta = 4,
            goldBonusPercent = 25,
        ),
        requiresSpecialContract = true,
    )

    private val paperworkToll = ExpeditionPlan(
        id = paperworkTollId,
        title = "Stamp the Border Toll",
        summary = "Collect and re-collect signatures before your party even leaves the gate.",
        questIds = setOf("paperwork_toll_of_chaos"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 18,
            scoreBonus = 14,
            riskPenaltyDelta = -6,
            goldBonusPercent = 12,
        ),
        requiresSpecialContract = true,
    )

    private val caravanManifest = ExpeditionPlan(
        id = caravanManifestId,
        title = "Demand a Manifest",
        summary = "Make the caravan submit the same manifest seven times to avoid arguing with a checkpoint captain.",
        questIds = setOf("licensed_guild_caravan_haunt"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = -12,
            scoreBonus = -8,
            riskPenaltyDelta = 8,
            successLootBonus = 1,
        ),
        requiresSpecialContract = true,
    )

    private val notaryNightPatrol = ExpeditionPlan(
        id = notaryNightPatrolId,
        title = "Read the Oath of the Night",
        summary = "Make every night patrol recite the same oath and keep the same route map.",
        questIds = setOf("notary_night_patrol"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 10,
            scoreBonus = 16,
            riskPenaltyDelta = -4,
            greatSuccessMarginDelta = 10,
            goldBonusPercent = 10,
        ),
        requiresSpecialContract = true,
    )

    private val inspectorateBanquet = ExpeditionPlan(
        id = inspectorateBanquetId,
        title = "Comply With Banquet Etiquette",
        summary = "Serve the inspectorate in order and make the paperwork look expensive.",
        questIds = setOf("inspectorate_cove_banquet"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 24,
            scoreBonus = 12,
            riskPenaltyDelta = 3,
            goldBonusPercent = 18,
            greatSuccessMarginDelta = 8,
        ),
        requiresSpecialContract = true,
    )

    private val weddingOathLedger = ExpeditionPlan(
        id = weddingOathLedgerId,
        title = "Audit the Vows",
        summary = "Count every promise as a liability before the cake can testify.",
        questIds = setOf("wedding_with_too_many_oaths"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 18,
            scoreBonus = 16,
            riskPenaltyDelta = -4,
            goldBonusPercent = 10,
            greatSuccessMarginDelta = 8,
        ),
        requiresSpecialContract = true,
    )

    private val sunkenTollDredge = ExpeditionPlan(
        id = sunkenTollDredgeId,
        title = "Dredge the Lost Drawer",
        summary = "Search below the booth for fees, receipts, and whatever is still jingling.",
        questIds = setOf("the_sunken_toll_booth"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 20,
            scoreBonus = -4,
            riskPenaltyDelta = 4,
            goldBonusPercent = 12,
            successLootBonus = 1,
        ),
        requiresSpecialContract = true,
    )

    private val crownReceiptSubpoena = ExpeditionPlan(
        id = crownReceiptSubpoenaId,
        title = "Subpoena the Crown",
        summary = "Turn royal embarrassment into a signed route through the archives.",
        questIds = setOf("the_crowns_missing_receipt"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 15,
            scoreBonus = 10,
            riskPenaltyDelta = -2,
            goldBonusPercent = 25,
            greatSuccessMarginDelta = 6,
        ),
        requiresSpecialContract = true,
    )

    private val sidewaysTowerBrace = ExpeditionPlan(
        id = sidewaysTowerBraceId,
        title = "Brace the Sideways Stairs",
        summary = "Make the tower admit which direction is up before anyone signs the climb.",
        questIds = setOf("the_tower_built_sideways"),
        modifiers = ExpeditionPlanModifiers(
            durationPercentDelta = 25,
            scoreBonus = 22,
            riskPenaltyDelta = -8,
            greatSuccessMarginDelta = 12,
        ),
        requiresSpecialContract = true,
    )

    private val genericPlans = listOf(rushTheJob, safetyFirst, lootPriority, auditEverything)
    private val questSpecificPlans = listOf(
        sealSideTunnel,
        followWorstMap,
        demandReceipts,
        blessTheBrine,
        moonlessShortcut,
        rationTheBiscuits,
        bringDoorForms,
        itemizedLastRites,
        paperworkToll,
        caravanManifest,
        notaryNightPatrol,
        inspectorateBanquet,
        weddingOathLedger,
        sunkenTollDredge,
        crownReceiptSubpoena,
        sidewaysTowerBrace,
    )
    private val visiblePlans = genericPlans + questSpecificPlans
    val all: List<ExpeditionPlan> = listOf(standard) + visiblePlans
    private val byId = all.associateBy { it.id }

    fun byId(planId: String?): ExpeditionPlan = byId[planId] ?: standard

    fun coercePlanId(planId: String?): String = byId(planId).id

    fun requiresSpecialContract(planId: String?, quest: Quest): Boolean = validPlanForQuest(planId, quest).requiresSpecialContract

    fun specialContractCost(planId: String?, quest: Quest): Int = validPlanForQuest(planId, quest).specialContractCost

    fun planForQuest(planId: String?, quest: Quest): ExpeditionPlan = validPlanForQuest(planId, quest)

    fun availableFor(quest: Quest): List<ExpeditionPlan> =
        genericPlans + questSpecificPlans.filter { quest.id in it.questIds }

    fun selectedPlanForUi(planId: String?, quest: Quest): ExpeditionPlan {
        val available = availableFor(quest)
        return available.firstOrNull { it.id == planId }
            ?: available.firstOrNull { it.id == defaultPlayerPlanId }
            ?: standard
    }

    fun modifiersFor(planId: String?, quest: Quest): ExpeditionPlanModifiers {
        val plan = validPlanForQuest(planId, quest)
        return if (plan.requiresSpecialContract) {
            plan.modifiers.copy(
                goldBonusPercent = plan.modifiers.goldBonusPercent + SPECIAL_CONTRACT_GOLD_BONUS_PERCENT,
                successLootBonus = plan.modifiers.successLootBonus + SPECIAL_CONTRACT_SUCCESS_LOOT_BONUS,
            )
        } else {
            plan.modifiers
        }
    }

    private fun validPlanForQuest(planId: String?, quest: Quest): ExpeditionPlan {
        val plan = byId(planId)
        return if (plan.questIds.isNotEmpty() && quest.id !in plan.questIds) standard else plan
    }

    fun durationSeconds(quest: Quest, planId: String?): Int {
        val durationPercent = 100 + modifiersFor(planId, quest).durationPercentDelta
        return (quest.durationSeconds * durationPercent / 100.0)
            .roundToInt()
            .coerceAtLeast(MIN_EXPEDITION_DURATION_SECONDS)
    }

    private const val SPECIAL_CONTRACT_GOLD_BONUS_PERCENT = 75
    private const val SPECIAL_CONTRACT_SUCCESS_LOOT_BONUS = 2
    private const val MIN_EXPEDITION_DURATION_SECONDS = 15
}
