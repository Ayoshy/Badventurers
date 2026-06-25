package com.ayoshy.badventurers.game

enum class AchievementCategory {
    Quest,
    Guild,
    Hero,
    Loot,
    Result,
    Idle,
    Secret,
}

enum class AchievementVisibility {
    Visible,
    HiddenUntilProgress,
    SecretUntilComplete,
}

enum class AchievementFeature {
    TrophyLedger,
    InsuranceDesk,
    HeroMentorship,
    RewardChoice,
    AdvancedContracts,
    GuildCharterBonuses,
}

data class AchievementDefinition(
    val id: String,
    val category: AchievementCategory,
    val title: String,
    val summary: String,
    val target: Int,
    val sealReward: Int,
    val reward: AchievementReward = AchievementReward.None,
    val visibility: AchievementVisibility = AchievementVisibility.Visible,
)

data class CharterMilestone(
    val sealsRequired: Int,
    val feature: AchievementFeature,
    val title: String,
    val summary: String,
)

data class AchievementProgress(
    val achievementId: String,
    val current: Int = 0,
    val completedAtMillis: Long? = null,
    val claimedAtMillis: Long? = null,
    val seen: Boolean = false,
) {
    fun isCompleted(definition: AchievementDefinition): Boolean =
        completedAtMillis != null || current >= definition.target

    val isClaimed: Boolean
        get() = claimedAtMillis != null
}

sealed interface AchievementReward {
    data class Currency(
        val gold: Int = 0,
        val reputation: Int = 0,
        val lootRolls: Int = 0,
    ) : AchievementReward

    data class Tickets(val tickets: Map<String, Int> = emptyMap()) : AchievementReward

    data class Composite(val rewards: List<AchievementReward>) : AchievementReward
    data object None : AchievementReward
}

sealed interface AchievementEvent {
    data class QuestCollected(
        val quest: Quest,
        val result: ExpeditionResult,
        val partyHeroIds: List<String>,
    ) : AchievementEvent

    data class LootKept(val item: LootItem) : AchievementEvent
    data class LootEquipped(val heroId: String, val item: LootItem) : AchievementEvent
    data class HeroRecruited(val hero: Hero, val duplicate: Boolean) : AchievementEvent
    data class FacilityUpgraded(val facilityId: String, val level: Int) : AchievementEvent
    data object OfflineCollected : AchievementEvent
}

object AchievementCatalog {
    const val NOTICE_BOARD_FACILITY = "notice_board"
    const val TRAINING_YARD_FACILITY = "training_yard"
    const val BUNK_ROOM_FACILITY = "bunk_room"

    val definitions = listOf(
        AchievementDefinition(
            id = "first_expedition",
            category = AchievementCategory.Quest,
            title = "First Filed Disaster",
            summary = "Complete any expedition. The guild calls this a process.",
            target = 1,
            sealReward = 1,
            reward = AchievementReward.Currency(gold = 100),
        ),
        AchievementDefinition(
            id = "came_back_alive",
            category = AchievementCategory.Quest,
            title = "Came Back Alive",
            summary = "Earn a Success or Great Success.",
            target = 1,
            sealReward = 1,
        ),
        AchievementDefinition(
            id = "professional_regret_i",
            category = AchievementCategory.Quest,
            title = "Professional Regret I",
            summary = "Complete 3 expeditions.",
            target = 3,
            sealReward = 1,
            reward = AchievementReward.Currency(reputation = 1),
        ),
        AchievementDefinition(
            id = "professional_regret_ii",
            category = AchievementCategory.Quest,
            title = "Professional Regret II",
            summary = "Complete 10 expeditions.",
            target = 10,
            sealReward = 2,
            reward = AchievementReward.Currency(reputation = 2),
        ),
        AchievementDefinition(
            id = "tour_of_bad_ideas",
            category = AchievementCategory.Quest,
            title = "Tour Of Bad Ideas",
            summary = "Complete 8 expeditions. The map stopped asking questions.",
            target = 8,
            sealReward = 2,
            reward = AchievementReward.Currency(lootRolls = 1),
        ),
        AchievementDefinition(
            id = "all_risk_no_notes",
            category = AchievementCategory.Quest,
            title = "All Risk, No Notes",
            summary = "Complete a high-risk expedition.",
            target = 1,
            sealReward = 2,
            reward = AchievementReward.Currency(lootRolls = 1),
        ),
        AchievementDefinition(
            id = "almost_competent",
            category = AchievementCategory.Result,
            title = "Almost Competent",
            summary = "Earn a Great Success.",
            target = 1,
            sealReward = 1,
            reward = AchievementReward.Currency(gold = 150),
        ),
        AchievementDefinition(
            id = "educational_failure",
            category = AchievementCategory.Result,
            title = "Educational Failure",
            summary = "Fail an expedition. The lesson has been laminated.",
            target = 1,
            sealReward = 1,
            reward = AchievementReward.Currency(gold = 80),
        ),
        AchievementDefinition(
            id = "historic_misread",
            category = AchievementCategory.Result,
            title = "Historic Misread",
            summary = "Suffer a Ridiculous Failure.",
            target = 1,
            sealReward = 1,
            visibility = AchievementVisibility.HiddenUntilProgress,
        ),
        AchievementDefinition(
            id = "payroll_problem",
            category = AchievementCategory.Guild,
            title = "Payroll Problem",
            summary = "Hold 1,500 gold at once.",
            target = 1_500,
            sealReward = 1,
        ),
        AchievementDefinition(
            id = "noticeable_board",
            category = AchievementCategory.Guild,
            title = "Noticeably Boarded",
            summary = "Upgrade the Notice Board to level 2.",
            target = 2,
            sealReward = 1,
        ),
        AchievementDefinition(
            id = "training_incident",
            category = AchievementCategory.Guild,
            title = "Training Incident",
            summary = "Upgrade the Training Yard to level 2.",
            target = 2,
            sealReward = 1,
        ),
        AchievementDefinition(
            id = "too_many_beds",
            category = AchievementCategory.Guild,
            title = "Too Many Beds",
            summary = "Upgrade the Bunk Room to level 2.",
            target = 2,
            sealReward = 1,
        ),
        AchievementDefinition(
            id = "suspiciously_funded",
            category = AchievementCategory.Guild,
            title = "Suspiciously Funded",
            summary = "Buy 3 total facility upgrades.",
            target = 3,
            sealReward = 2,
            reward = AchievementReward.Composite(
                rewards = listOf(
                    AchievementReward.Currency(gold = 250),
                    AchievementReward.Tickets(
                        tickets = mapOf(RecruitmentTicketCatalog.RARE_CONTRACT_TICKET_ID to 1),
                    ),
                ),
            ),
        ),
        AchievementDefinition(
            id = "first_hire",
            category = AchievementCategory.Hero,
            title = "First New Hire",
            summary = "Recruit a hero after opening the guild.",
            target = 1,
            sealReward = 1,
            reward = AchievementReward.Tickets(
                tickets = mapOf(RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID to 1),
            ),
        ),
        AchievementDefinition(
            id = "tiny_hr_department",
            category = AchievementCategory.Hero,
            title = "Tiny HR Department",
            summary = "Own 5 heroes.",
            target = 5,
            sealReward = 1,
            reward = AchievementReward.Tickets(
                tickets = mapOf(RecruitmentTicketCatalog.SPECIALIST_INVITATION_ID to 1),
            ),
        ),
        AchievementDefinition(
            id = "duplicate_form",
            category = AchievementCategory.Hero,
            title = "Duplicate Form",
            summary = "Recruit a duplicate hero and convert the contract into reputation.",
            target = 1,
            sealReward = 1,
            reward = AchievementReward.Currency(reputation = 1),
        ),
        AchievementDefinition(
            id = "specialist_roster",
            category = AchievementCategory.Hero,
            title = "Specialist Roster",
            summary = "Own heroes from 5 different classes.",
            target = 5,
            sealReward = 2,
        ),
        AchievementDefinition(
            id = "keep_the_spoon",
            category = AchievementCategory.Loot,
            title = "Keep The Spoon",
            summary = "Bank 5 loot items.",
            target = 5,
            sealReward = 1,
        ),
        AchievementDefinition(
            id = "equipped_for_concern",
            category = AchievementCategory.Loot,
            title = "Equipped For Concern",
            summary = "Equip loot on 3 different heroes.",
            target = 3,
            sealReward = 1,
        ),
        AchievementDefinition(
            id = "shiny_enough",
            category = AchievementCategory.Loot,
            title = "Shiny Enough",
            summary = "Keep or equip a Rare-or-better item.",
            target = 1,
            sealReward = 1,
            reward = AchievementReward.Currency(gold = 120),
        ),
        AchievementDefinition(
            id = "very_much_legendary",
            category = AchievementCategory.Loot,
            title = "Very Much Legendary",
            summary = "Keep or equip a Relic item.",
            target = 1,
            sealReward = 3,
            visibility = AchievementVisibility.HiddenUntilProgress,
        ),
        AchievementDefinition(
            id = "welcome_back_audit",
            category = AchievementCategory.Idle,
            title = "Welcome Back Audit",
            summary = "Collect an offline report.",
            target = 1,
            sealReward = 1,
        ),
    )

    val byId: Map<String, AchievementDefinition> = definitions.associateBy { it.id }

    val milestones = listOf(
        CharterMilestone(
            sealsRequired = 1,
            feature = AchievementFeature.TrophyLedger,
            title = "Trophy Ledger",
            summary = "Achievement tracking and claims are now officially over-filed.",
        ),
        CharterMilestone(
            sealsRequired = 4,
            feature = AchievementFeature.InsuranceDesk,
            title = "Insurance Desk",
            summary = "Failures and ridiculous failures gain improved pity gold.",
        ),
        CharterMilestone(
            sealsRequired = 8,
            feature = AchievementFeature.HeroMentorship,
            title = "Hero Mentorship",
            summary = "Training adds a small charter power bonus.",
        ),
        CharterMilestone(
            sealsRequired = 12,
            feature = AchievementFeature.RewardChoice,
            title = "Reward Choice",
            summary = "Great Success results gain an extra loot candidate.",
        ),
        CharterMilestone(
            sealsRequired = 18,
            feature = AchievementFeature.AdvancedContracts,
            title = "Advanced Contracts",
            summary = "The guild can surface more ambitious contract goals.",
        ),
        CharterMilestone(
            sealsRequired = 25,
            feature = AchievementFeature.GuildCharterBonuses,
            title = "Guild Charter Bonuses",
            summary = "Quest gold gains a small capped charter bonus.",
        ),
    )

    init {
        require(definitions.size == byId.size) { "Achievement ids must be unique." }
    }

    fun initialProgress(): List<AchievementProgress> =
        definitions.map { AchievementProgress(achievementId = it.id) }

    fun normalizeProgress(progress: List<AchievementProgress>): List<AchievementProgress> {
        val byProgressId = progress.associateBy { it.achievementId }
        return definitions.map { definition ->
            val saved = byProgressId[definition.id]
            if (saved == null) {
                AchievementProgress(achievementId = definition.id)
            } else {
                val normalizedCurrent = saved.current.coerceIn(0, definition.target)
                saved.copy(
                    current = normalizedCurrent,
                    completedAtMillis = saved.completedAtMillis
                        ?: if (normalizedCurrent >= definition.target) 0L else null,
                )
            }
        }
    }

    fun claimedSeals(progress: List<AchievementProgress>): Int =
        progress.sumOf { item ->
            val definition = byId[item.achievementId] ?: return@sumOf 0
            if (item.isClaimed) definition.sealReward else 0
        }

    fun isFeatureUnlocked(progress: List<AchievementProgress>, feature: AchievementFeature): Boolean {
        val seals = claimedSeals(progress)
        return milestones.any { milestone -> milestone.feature == feature && seals >= milestone.sealsRequired }
    }

    fun nextMilestone(progress: List<AchievementProgress>): CharterMilestone? {
        val seals = claimedSeals(progress)
        return milestones.firstOrNull { it.sealsRequired > seals }
    }
}

object AchievementTracker {
    fun applyEvent(
        state: PlaySessionState,
        event: AchievementEvent,
        nowMillis: Long = 0L,
    ): PlaySessionState = refresh(state, event, nowMillis)

    fun refresh(
        state: PlaySessionState,
        event: AchievementEvent? = null,
        nowMillis: Long = 0L,
    ): PlaySessionState {
        val progress = AchievementCatalog
            .normalizeProgress(state.achievementProgress)
            .associateBy { it.achievementId }
            .toMutableMap()

        fun setProgress(id: String, value: Int) {
            val definition = AchievementCatalog.byId[id] ?: return
            val current = progress[id] ?: AchievementProgress(achievementId = id)
            val nextValue = maxOf(current.current, value).coerceAtMost(definition.target)
            val completedAt = current.completedAtMillis
                ?: if (nextValue >= definition.target) nowMillis else null
            progress[id] = current.copy(current = nextValue, completedAtMillis = completedAt)
        }

        setProgress("first_expedition", state.completedQuestCount)
        setProgress("professional_regret_i", state.completedQuestCount)
        setProgress("professional_regret_ii", state.completedQuestCount)
        setProgress("tour_of_bad_ideas", state.completedQuestCount)
        setProgress("payroll_problem", state.gold)
        setProgress("noticeable_board", state.noticeBoardLevel)
        setProgress("training_incident", state.trainingYardLevel)
        setProgress("too_many_beds", state.bunkRoomLevel)
        setProgress(
            id = "suspiciously_funded",
            value = (state.noticeBoardLevel - 1).coerceAtLeast(0) +
                (state.trainingYardLevel - 1).coerceAtLeast(0) +
                (state.bunkRoomLevel - 1).coerceAtLeast(0),
        )
        setProgress("tiny_hr_department", state.heroes.size)
        setProgress("specialist_roster", state.heroes.map { it.heroClass }.distinct().size)

        val keptOrEquippedLoot = state.lootItems + state.equippedLoot.map { it.item }
        setProgress("keep_the_spoon", keptOrEquippedLoot.size)
        setProgress("equipped_for_concern", state.equippedLoot.map { it.heroId }.distinct().size)
        if (keptOrEquippedLoot.any { it.rarity >= LootRarity.Rare }) setProgress("shiny_enough", 1)
        if (keptOrEquippedLoot.any { it.rarity == LootRarity.Relic }) setProgress("very_much_legendary", 1)

        when (event) {
            is AchievementEvent.QuestCollected -> {
                if (event.result.outcome == ExpeditionOutcome.GreatSuccess ||
                    event.result.outcome == ExpeditionOutcome.Success
                ) {
                    setProgress("came_back_alive", 1)
                }
                if (event.quest.risk == QuestRisk.High) setProgress("all_risk_no_notes", 1)
                if (event.result.outcome == ExpeditionOutcome.GreatSuccess) setProgress("almost_competent", 1)
                if (event.result.outcome == ExpeditionOutcome.Failure) setProgress("educational_failure", 1)
                if (event.result.outcome == ExpeditionOutcome.RidiculousFailure) setProgress("historic_misread", 1)
            }
            is AchievementEvent.LootKept -> {
                if (event.item.rarity >= LootRarity.Rare) setProgress("shiny_enough", 1)
                if (event.item.rarity == LootRarity.Relic) setProgress("very_much_legendary", 1)
            }
            is AchievementEvent.LootEquipped -> {
                if (event.item.rarity >= LootRarity.Rare) setProgress("shiny_enough", 1)
                if (event.item.rarity == LootRarity.Relic) setProgress("very_much_legendary", 1)
            }
            is AchievementEvent.HeroRecruited -> {
                setProgress("first_hire", 1)
                if (event.duplicate) setProgress("duplicate_form", 1)
            }
            is AchievementEvent.FacilityUpgraded -> {
                when (event.facilityId) {
                    AchievementCatalog.NOTICE_BOARD_FACILITY -> setProgress("noticeable_board", event.level)
                    AchievementCatalog.TRAINING_YARD_FACILITY -> setProgress("training_incident", event.level)
                    AchievementCatalog.BUNK_ROOM_FACILITY -> setProgress("too_many_beds", event.level)
                }
            }
            AchievementEvent.OfflineCollected -> setProgress("welcome_back_audit", 1)
            null -> Unit
        }

        return state.copy(achievementProgress = AchievementCatalog.normalizeProgress(progress.values.toList()))
    }
}
