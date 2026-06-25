package com.ayoshy.badventurers.game

object SeedGame {
    val heroes = HeroCatalog.starterHeroes

    val firstQuest = Quest(
        id = "cave_minor_regrets",
        title = "Cave of Minor Regrets",
        summary = "45 s. Low risk. High chance of cave-related paperwork.",
        durationSeconds = 45,
        difficulty = 120,
        risk = QuestRisk.Low,
        baseGold = 320,
        pityGold = 25,
        partySlots = 3,
        tags = listOf(QuestTag.Cave, QuestTag.Breach, QuestTag.Trap, QuestTag.Paperwork, QuestTag.Simple),
        recommendedHeroIds = listOf("brugg", "mira", "nell", "orla", "expert_en_demolition"),
    )

    val quests = listOf(
        firstQuest,
        Quest(
            id = "forest_of_wrong_turns",
            title = "Forest of Wrong Turns",
            summary = "90 s. Medium risk. Bring a map, then someone who distrusts maps.",
            durationSeconds = 90,
            difficulty = 175,
            risk = QuestRisk.Medium,
            baseGold = 520,
            pityGold = 45,
            partySlots = 3,
            tags = listOf(QuestTag.Exploration, QuestTag.Wilderness, QuestTag.Trap, QuestTag.Hunt, QuestTag.Paperwork),
            recommendedHeroIds = listOf("bramble", "nell", "sable", "jardinier", "quill", "comptable"),
            unlockRequirement = QuestUnlockRequirement(
                conditions = listOf(
                    QuestUnlockCondition(minCompletedQuestCount = 1),
                    QuestUnlockCondition(minReputation = 3),
                ),
            ),
        ),
        Quest(
            id = "bandit_tax_office",
            title = "Bandit Tax Office",
            summary = "180 s. Medium risk. Bandits discovered forms and became everyone's problem.",
            durationSeconds = 180,
            difficulty = 215,
            risk = QuestRisk.Medium,
            baseGold = 850,
            pityGold = 80,
            partySlots = 3,
            tags = listOf(QuestTag.Paperwork, QuestTag.Bandit, QuestTag.Contract, QuestTag.Urban, QuestTag.Trap, QuestTag.Stealth),
            recommendedHeroIds = listOf("quill", "ledger", "comptable", "nell", "vex", "sable"),
            unlockRequirement = QuestUnlockRequirement(
                conditions = listOf(
                    QuestUnlockCondition(minCompletedQuestCount = 2),
                    QuestUnlockCondition(minReputation = 8),
                    QuestUnlockCondition(minNoticeBoardLevel = 2),
                ),
            ),
        ),
        Quest(
            id = "salted_swamp_chapel",
            title = "Salted Swamp Chapel",
            summary = "180 s. Medium risk. The holy water is mostly brine and complaints.",
            durationSeconds = 180,
            difficulty = 210,
            risk = QuestRisk.Medium,
            baseGold = 560,
            pityGold = 60,
            partySlots = 4,
            tags = listOf(QuestTag.Swamp, QuestTag.Poison, QuestTag.Rot, QuestTag.Holy, QuestTag.Curse, QuestTag.Paperwork),
            recommendedHeroIds = listOf("pax", "paladin", "elementaire_de_sel", "jardinier", "chef_cuistot", "morrow"),
            unlockRequirement = QuestUnlockRequirement(
                conditions = listOf(
                    QuestUnlockCondition(minCompletedQuestCount = 2),
                    QuestUnlockCondition(minReputation = 10),
                    QuestUnlockCondition(minTrainingYardLevel = 2),
                ),
            ),
        ),
        Quest(
            id = "moonlit_smuggler_run",
            title = "Moonlit Smuggler Run",
            summary = "180 s. Medium risk. The cargo is legal if nobody asks beautifully.",
            durationSeconds = 180,
            difficulty = 225,
            risk = QuestRisk.Medium,
            baseGold = 560,
            pityGold = 45,
            partySlots = 3,
            tags = listOf(QuestTag.Stealth, QuestTag.Heist, QuestTag.Escort, QuestTag.Paperwork, QuestTag.Urban),
            recommendedHeroIds = listOf("nell", "vex", "sable", "bramble", "quill", "comptable"),
            unlockRequirement = QuestUnlockRequirement(
                conditions = listOf(
                    QuestUnlockCondition(minCompletedQuestCount = 3),
                    QuestUnlockCondition(minReputation = 12),
                    QuestUnlockCondition(minNoticeBoardLevel = 2),
                ),
            ),
        ),
        Quest(
            id = "the_hungry_siege",
            title = "The Hungry Siege",
            summary = "180 s. Medium risk. Hold the bread-crate barricade and count the biscuits.",
            durationSeconds = 180,
            difficulty = 240,
            risk = QuestRisk.Medium,
            baseGold = 880,
            pityGold = 110,
            partySlots = 4,
            tags = listOf(QuestTag.Siege, QuestTag.Camp, QuestTag.Guard, QuestTag.LongQuest, QuestTag.Paperwork, QuestTag.Wilderness),
            recommendedHeroIds = listOf("chef_cuistot", "elementaire_de_sel", "bramble", "jardinier", "brugg", "paladin", "quill"),
            unlockRequirement = QuestUnlockRequirement(
                conditions = listOf(
                    QuestUnlockCondition(minCompletedQuestCount = 4),
                    QuestUnlockCondition(minReputation = 15),
                    QuestUnlockCondition(minBunkRoomLevel = 2),
                ),
            ),
        ),
        Quest(
            id = "the_last_locked_door",
            title = "The Last Locked Door",
            summary = "240 s. High risk. Bring lockpicks, forms, and someone the door respects.",
            durationSeconds = 240,
            difficulty = 280,
            risk = QuestRisk.High,
            baseGold = 980,
            pityGold = 95,
            partySlots = 4,
            tags = listOf(QuestTag.Heist, QuestTag.Trap, QuestTag.Magic, QuestTag.Ancient, QuestTag.Paperwork, QuestTag.Wall, QuestTag.Obstacle),
            recommendedHeroIds = listOf("nell", "vex", "sable", "mira", "pippa", "quill", "ledger", "expert_en_demolition"),
            unlockRequirement = QuestUnlockRequirement(
                conditions = listOf(
                    QuestUnlockCondition(minCompletedQuestCount = 5),
                    QuestUnlockCondition(minReputation = 20),
                    QuestUnlockCondition(minTrainingYardLevel = 3),
                ),
            ),
        ),
        Quest(
            id = "crypt_of_unpaid_debts",
            title = "Crypt of Unpaid Debts",
            summary = "180 s. High risk. The dead demand interest and misfiled invoices.",
            durationSeconds = 180,
            difficulty = 290,
            risk = QuestRisk.High,
            baseGold = 760,
            pityGold = 95,
            partySlots = 4,
            tags = listOf(QuestTag.Undead, QuestTag.Debt, QuestTag.Curse, QuestTag.Paperwork, QuestTag.Magic, QuestTag.Trap),
            recommendedHeroIds = listOf("morrow", "chevalier_de_la_mort", "pax", "paladin", "quill", "ledger", "comptable"),
            unlockRequirement = QuestUnlockRequirement(
                conditions = listOf(
                    QuestUnlockCondition(minCompletedQuestCount = 5),
                    QuestUnlockCondition(minReputation = 22),
                    QuestUnlockCondition(minNoticeBoardLevel = 3),
                ),
            ),
        ),

        Quest(
            id = "paperwork_toll_of_chaos",
            title = "Paperwork Toll of Chaos",
            summary = "210 s. Medium risk. The checkpoint inspector loves receipts more than blood.",
            durationSeconds = 210,
            difficulty = 300,
            risk = QuestRisk.Medium,
            baseGold = 920,
            pityGold = 95,
            partySlots = 4,
            tags = listOf(QuestTag.Paperwork, QuestTag.Urban, QuestTag.Trap, QuestTag.Contract, QuestTag.Stealth),
            recommendedHeroIds = listOf("quill", "ledger", "comptable", "nell", "vex", "sable"),
            unlockRequirement = QuestUnlockRequirement(
                conditions = listOf(
                    QuestUnlockCondition(minCompletedQuestCount = 8, minReputation = 30, minNoticeBoardLevel = 2),
                ),
            ),
        ),
        Quest(
            id = "licensed_guild_caravan_haunt",
            title = "Licensed Guild Caravan Haunt",
            summary = "240 s. High risk. A sanctioned caravan still collects penalties in triplicate.",
            durationSeconds = 240,
            difficulty = 318,
            risk = QuestRisk.High,
            baseGold = 1060,
            pityGold = 110,
            partySlots = 4,
            tags = listOf(QuestTag.Contract, QuestTag.Heist, QuestTag.Escort, QuestTag.Paperwork, QuestTag.Urban),
            recommendedHeroIds = listOf("mira", "pax", "vex", "bramble", "jardinier", "quill"),
            unlockRequirement = QuestUnlockRequirement(
                conditions = listOf(
                    QuestUnlockCondition(minCompletedQuestCount = 9, minReputation = 34, minTrainingYardLevel = 2),
                ),
            ),
        ),
        Quest(
            id = "notary_night_patrol",
            title = "Notary Night Patrol",
            summary = "270 s. High risk. Everyone wears a robe, a lantern, and a form in triplicate.",
            durationSeconds = 270,
            difficulty = 312,
            risk = QuestRisk.High,
            baseGold = 1120,
            pityGold = 125,
            partySlots = 4,
            tags = listOf(QuestTag.Contract, QuestTag.Paperwork, QuestTag.Guard, QuestTag.LongQuest, QuestTag.Urban),
            recommendedHeroIds = listOf("ledger", "chevalier_de_la_mort", "paladin", "morrow", "pippa", "vex"),
            unlockRequirement = QuestUnlockRequirement(
                conditions = listOf(
                    QuestUnlockCondition(minCompletedQuestCount = 10, minReputation = 38, minBunkRoomLevel = 2),
                ),
            ),
        ),
        Quest(
            id = "inspectorate_cove_banquet",
            title = "Inspectorate Cove Banquet",
            summary = "300 s. High risk. The official meal is compulsory; forgetting a form starts an emergency audit.",
            durationSeconds = 300,
            difficulty = 336,
            risk = QuestRisk.High,
            baseGold = 1180,
            pityGold = 135,
            partySlots = 5,
            tags = listOf(QuestTag.Siege, QuestTag.Camp, QuestTag.Contract, QuestTag.Paperwork, QuestTag.Urban),
            recommendedHeroIds = listOf("vex", "sable", "ledger", "elementaire_de_sel", "comptable", "brugg", "pippa"),
            unlockRequirement = QuestUnlockRequirement(
                conditions = listOf(
                    QuestUnlockCondition(
                        minCompletedQuestCount = 11,
                        minReputation = 42,
                        minNoticeBoardLevel = 3,
                        minTrainingYardLevel = 3,
                        minBunkRoomLevel = 2,
                    ),
                ),
            ),
        ),
    )

    val questById = quests.associateBy { it.id }

    init {
        require(quests.size == questById.size) { "Quest ids must be unique." }
    }
}
