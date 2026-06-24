package com.ayoshy.badventurers.game

data class PlaySessionSnapshot(
    val version: Int,
    val gold: Int,
    val reputation: Int,
    val guildLevel: Int,
    val completedQuestCount: Int,
    val noticeBoardLevel: Int,
    val trainingYardLevel: Int,
    val bunkRoomLevel: Int,
    val lootRolls: Int,
    val heroIds: List<String> = HeroCatalog.starterHeroes.map { it.id },
    val heroProgress: List<HeroProgressSnapshot> = emptyList(),
    val lootItems: List<LootItemSnapshot> = emptyList(),
    val pendingLootItems: List<LootItemSnapshot> = emptyList(),
    val pendingLootKeepLimit: Int = 0,
    val pendingLootKeptCount: Int = 0,
    val equippedLoot: List<EquippedLootSnapshot> = emptyList(),
    val journalEntries: List<JournalEntrySnapshot> = emptyList(),
    val expedition: ExpeditionRunSnapshot? = null,
    val achievementProgress: List<AchievementProgress> = emptyList(),
) {
    fun toState(): PlaySessionState {
        val progressByHeroId = heroProgress.associateBy { it.heroId }
        val restoredHeroes = heroIds
            .mapNotNull { heroId ->
                HeroCatalog.byId[heroId]?.toHero()?.let { hero ->
                    progressByHeroId[heroId]?.applyTo(hero) ?: hero
                }
            }
            .ifEmpty { HeroCatalog.starterHeroes }

        val migratedNoticeBoardLevel = migratedFacilityLevel(GuildFacility.NoticeBoard, noticeBoardLevel)
        val migratedTrainingYardLevel = migratedFacilityLevel(GuildFacility.TrainingYard, trainingYardLevel)
        val migratedBunkRoomLevel = migratedFacilityLevel(GuildFacility.BunkRoom, bunkRoomLevel)
        val restoredPendingLootItems = pendingLootItems.map { it.toItem() }
        val restoredPendingLootKeepLimit = if (restoredPendingLootItems.isEmpty()) {
            0
        } else {
            pendingLootKeepLimit.coerceAtLeast(1)
        }
        val restoredPendingLootKeptCount = if (restoredPendingLootItems.isEmpty()) {
            0
        } else {
            pendingLootKeptCount.coerceIn(0, restoredPendingLootKeepLimit)
        }

        return PlaySessionState(
            gold = gold,
            reputation = reputation,
            guildLevel = guildLevel,
            completedQuestCount = completedQuestCount,
            noticeBoardLevel = migratedNoticeBoardLevel,
            trainingYardLevel = migratedTrainingYardLevel,
            bunkRoomLevel = migratedBunkRoomLevel,
            heroes = restoredHeroes,
            lootRolls = lootRolls,
            lootItems = lootItems.map { it.toItem() },
            pendingLootItems = restoredPendingLootItems,
            pendingLootKeepLimit = restoredPendingLootKeepLimit,
            pendingLootKeptCount = restoredPendingLootKeptCount,
            equippedLoot = equippedLoot.mapNotNull { it.toEquippedLoot(restoredHeroes) },
            journalEntries = journalEntries.map { it.toEntry() },
            expedition = expedition?.toRun(),
            achievementProgress = AchievementCatalog.normalizeProgress(achievementProgress),
        )
    }

    companion object {
        const val CURRENT_VERSION = 12

        fun initial(): PlaySessionSnapshot {
            return fromState(PlaySessionState.initial())
        }

        fun fromState(state: PlaySessionState): PlaySessionSnapshot {
            return PlaySessionSnapshot(
                version = CURRENT_VERSION,
                gold = state.gold,
                reputation = state.reputation,
                guildLevel = state.guildLevel,
                completedQuestCount = state.completedQuestCount,
                noticeBoardLevel = state.noticeBoardLevel,
                trainingYardLevel = state.trainingYardLevel,
                bunkRoomLevel = state.bunkRoomLevel,
                lootRolls = state.lootRolls,
                heroIds = state.heroes.map { it.id },
                heroProgress = state.heroes.map { HeroProgressSnapshot.fromHero(it) },
                lootItems = state.lootItems.map { LootItemSnapshot.fromItem(it) },
                pendingLootItems = state.pendingLootItems.map { LootItemSnapshot.fromItem(it) },
                pendingLootKeepLimit = state.pendingLootEffectiveKeepLimit(),
                pendingLootKeptCount = state.pendingLootSelectedCount(),
                equippedLoot = state.equippedLoot.map { EquippedLootSnapshot.fromEquippedLoot(it) },
                journalEntries = state.journalEntries.map { JournalEntrySnapshot.fromEntry(it) },
                expedition = state.expedition?.let { ExpeditionRunSnapshot.fromRun(it) },
                achievementProgress = AchievementCatalog.normalizeProgress(state.achievementProgress),
            )
        }

        private fun migratedFacilityLevel(facility: GuildFacility, level: Int): Int {
            val definition = GuildFacilityCatalog.definition(facility)
            return level.coerceIn(1, definition.maxLevel)
        }
    }
}

data class HeroProgressSnapshot(
    val heroId: String,
    val level: Int,
    val xp: Int,
) {
    fun applyTo(hero: Hero): Hero = HeroProgression.withProgress(hero, level, xp)

    companion object {
        fun fromHero(hero: Hero): HeroProgressSnapshot = HeroProgressSnapshot(
            heroId = hero.id,
            level = hero.level,
            xp = hero.xp,
        )
    }
}

data class LootItemSnapshot(
    val id: String,
    val name: String,
    val rarity: LootRarity,
    val slot: LootSlot,
    val stats: List<StatBonusSnapshot> = emptyList(),
    val bonus: Int,
    val icon: LootIcon,
) {
    fun toItem(): LootItem {
        val restoredStats = stats.map { it.toStatBonus() }
        val catalogDefinition = LootCatalog.byId[id]
        return LootItem(
            id = id,
            name = catalogDefinition?.name ?: name,
            rarity = rarity,
            slot = catalogDefinition?.slot ?: slot,
            stats = restoredStats,
            bonus = if (restoredStats.isEmpty()) bonus else restoredStats.sumOf { it.value },
            icon = catalogDefinition?.icon ?: icon,
        )
    }

    companion object {
        fun fromItem(item: LootItem): LootItemSnapshot = LootItemSnapshot(
            id = item.id,
            name = item.name,
            rarity = item.rarity,
            slot = item.slot,
            stats = item.stats.map { StatBonusSnapshot.fromStatBonus(it) },
            bonus = item.bonus,
            icon = item.icon,
        )
    }
}

data class StatBonusSnapshot(
    val type: StatType,
    val value: Int,
) {
    fun toStatBonus(): StatBonus = StatBonus(type = type, value = value)

    companion object {
        fun fromStatBonus(stat: StatBonus): StatBonusSnapshot = StatBonusSnapshot(
            type = stat.type,
            value = stat.value,
        )
    }
}

data class EquippedLootSnapshot(
    val heroId: String,
    val item: LootItemSnapshot,
) {
    fun toEquippedLoot(heroes: List<Hero>): EquippedLoot? {
        if (heroes.none { it.id == heroId }) return null
        return EquippedLoot(heroId = heroId, item = item.toItem())
    }

    companion object {
        fun fromEquippedLoot(equipped: EquippedLoot): EquippedLootSnapshot = EquippedLootSnapshot(
            heroId = equipped.heroId,
            item = LootItemSnapshot.fromItem(equipped.item),
        )
    }
}

data class JournalEntrySnapshot(
    val id: String,
    val text: String,
) {
    fun toEntry(): JournalEntry = JournalEntry(id = id, text = text)

    companion object {
        fun fromEntry(entry: JournalEntry): JournalEntrySnapshot = JournalEntrySnapshot(
            id = entry.id,
            text = entry.text,
        )
    }
}

data class RewardSnapshot(
    val gold: Int,
    val xp: Int,
    val lootRolls: Int,
) {
    fun toReward(): Reward = Reward(gold = gold, xp = xp, lootRolls = lootRolls)

    companion object {
        fun fromReward(reward: Reward): RewardSnapshot = RewardSnapshot(
            gold = reward.gold,
            xp = reward.xp,
            lootRolls = reward.lootRolls,
        )
    }
}

data class ExpeditionResultSnapshot(
    val outcome: ExpeditionOutcome,
    val reward: RewardSnapshot,
    val scoreMargin: Int,
) {
    fun toResult(): ExpeditionResult = ExpeditionResult(
        outcome = outcome,
        reward = reward.toReward(),
        scoreMargin = scoreMargin,
    )

    companion object {
        fun fromResult(result: ExpeditionResult): ExpeditionResultSnapshot = ExpeditionResultSnapshot(
            outcome = result.outcome,
            reward = RewardSnapshot.fromReward(result.reward),
            scoreMargin = result.scoreMargin,
        )
    }
}

data class ExpeditionRunSnapshot(
    val questId: String,
    val partyHeroIds: List<String> = emptyList(),
    val startedAtMillis: Long,
    val endsAtMillis: Long,
    val result: ExpeditionResultSnapshot? = null,
    val planId: String = ExpeditionPlanCatalog.defaultPlanId,
) {
    fun toRun(): ExpeditionRun? {
        val quest = SeedGame.questById[questId] ?: return null
        return ExpeditionRun(
            quest = quest,
            partyHeroIds = partyHeroIds,
            startedAtMillis = startedAtMillis,
            endsAtMillis = endsAtMillis,
            result = result?.toResult(),
            planId = ExpeditionPlanCatalog.coercePlanId(planId),
        )
    }

    companion object {
        fun fromRun(run: ExpeditionRun): ExpeditionRunSnapshot = ExpeditionRunSnapshot(
            questId = run.quest.id,
            partyHeroIds = run.partyHeroIds,
            startedAtMillis = run.startedAtMillis,
            endsAtMillis = run.endsAtMillis,
            result = run.result?.let { ExpeditionResultSnapshot.fromResult(it) },
            planId = ExpeditionPlanCatalog.coercePlanId(run.planId),
        )
    }
}
