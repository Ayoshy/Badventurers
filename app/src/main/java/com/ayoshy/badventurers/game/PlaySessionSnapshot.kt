package com.ayoshy.badventurers.game

data class PlaySessionSnapshot(
    val version: Int,
    val gold: Int,
    val reputation: Int,
    val guildLevel: Int,
    val noticeBoardLevel: Int,
    val lootRolls: Int,
    val heroIds: List<String> = HeroCatalog.starterHeroes.map { it.id },
    val lootItems: List<LootItemSnapshot> = emptyList(),
    val pendingLootItems: List<LootItemSnapshot> = emptyList(),
    val equippedLoot: List<EquippedLootSnapshot> = emptyList(),
    val journalEntries: List<JournalEntrySnapshot> = emptyList(),
    val expedition: ExpeditionRunSnapshot? = null,
) {
    fun toState(): PlaySessionState {
        val restoredHeroes = heroIds
            .mapNotNull { HeroCatalog.byId[it]?.toHero() }
            .ifEmpty { HeroCatalog.starterHeroes }

        return PlaySessionState(
            gold = gold,
            reputation = reputation,
            guildLevel = guildLevel,
            noticeBoardLevel = noticeBoardLevel,
            heroes = restoredHeroes,
            lootRolls = lootRolls,
            lootItems = lootItems.map { it.toItem() },
            pendingLootItems = pendingLootItems.map { it.toItem() },
            equippedLoot = equippedLoot.mapNotNull { it.toEquippedLoot(restoredHeroes) },
            journalEntries = journalEntries.map { it.toEntry() },
            expedition = expedition?.toRun(),
        )
    }

    companion object {
        const val CURRENT_VERSION = 5

        fun initial(): PlaySessionSnapshot {
            return fromState(PlaySessionState.initial())
        }

        fun fromState(state: PlaySessionState): PlaySessionSnapshot {
            return PlaySessionSnapshot(
                version = CURRENT_VERSION,
                gold = state.gold,
                reputation = state.reputation,
                guildLevel = state.guildLevel,
                noticeBoardLevel = state.noticeBoardLevel,
                lootRolls = state.lootRolls,
                heroIds = state.heroes.map { it.id },
                lootItems = state.lootItems.map { LootItemSnapshot.fromItem(it) },
                pendingLootItems = state.pendingLootItems.map { LootItemSnapshot.fromItem(it) },
                equippedLoot = state.equippedLoot.map { EquippedLootSnapshot.fromEquippedLoot(it) },
                journalEntries = state.journalEntries.map { JournalEntrySnapshot.fromEntry(it) },
                expedition = state.expedition?.let { ExpeditionRunSnapshot.fromRun(it) },
            )
        }
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
        return LootItem(
            id = id,
            name = name,
            rarity = rarity,
            slot = slot,
            stats = restoredStats,
            bonus = if (restoredStats.isEmpty()) bonus else restoredStats.sumOf { it.value },
            icon = icon,
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
) {
    fun toRun(): ExpeditionRun? {
        val quest = SeedGame.questById[questId] ?: return null
        return ExpeditionRun(
            quest = quest,
            partyHeroIds = partyHeroIds,
            startedAtMillis = startedAtMillis,
            endsAtMillis = endsAtMillis,
            result = result?.toResult(),
        )
    }

    companion object {
        fun fromRun(run: ExpeditionRun): ExpeditionRunSnapshot = ExpeditionRunSnapshot(
            questId = run.quest.id,
            partyHeroIds = run.partyHeroIds,
            startedAtMillis = run.startedAtMillis,
            endsAtMillis = run.endsAtMillis,
            result = run.result?.let { ExpeditionResultSnapshot.fromResult(it) },
        )
    }
}