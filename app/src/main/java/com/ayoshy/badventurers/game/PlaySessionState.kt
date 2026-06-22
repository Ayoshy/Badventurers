package com.ayoshy.badventurers.game

data class PlaySessionState(
    val gold: Int = 9999999,
    val reputation: Int = 17,
    val guildLevel: Int = 3,
    val noticeBoardLevel: Int = 1,
    val heroes: List<Hero> = HeroCatalog.starterHeroes,
    val lootRolls: Int = 0,
    val lootItems: List<LootItem> = emptyList(),
    val pendingLootItems: List<LootItem> = emptyList(),
    val equippedLoot: List<EquippedLoot> = emptyList(),
    val journalEntries: List<JournalEntry> = emptyList(),
    val expedition: ExpeditionRun? = null,
) {
    val phase: PlayPhase
        get() = when {
            expedition == null -> PlayPhase.Idle
            expedition.result == null -> PlayPhase.Running
            else -> PlayPhase.ResultReady
        }

    fun progress(nowMillis: Long): Double {
        val run = expedition ?: return 0.0
        val durationMillis = (run.endsAtMillis - run.startedAtMillis).coerceAtLeast(1L)
        return when {
            nowMillis <= run.startedAtMillis -> 0.0
            nowMillis >= run.endsAtMillis -> 1.0
            else -> ((nowMillis - run.startedAtMillis).toDouble() / durationMillis).coerceIn(0.0, 1.0)
        }
    }

    fun startQuest(nowMillis: Long, quest: Quest, party: List<Hero> = heroes): PlaySessionState {
        if (phase != PlayPhase.Idle) return this
        val selectedParty = selectedPartyForQuest(quest, party)
        return copy(
            expedition = ExpeditionRun(
                quest = quest,
                partyHeroIds = selectedParty.map { it.id },
                startedAtMillis = nowMillis,
                endsAtMillis = nowMillis + quest.durationSeconds * 1000L,
            ),
        )
    }

    fun tick(
        nowMillis: Long,
        engine: ExpeditionEngine,
        party: List<Hero>,
    ): PlaySessionState {
        val run = expedition ?: return this
        if (run.result != null || nowMillis < run.endsAtMillis) return this
        return copy(expedition = run.copy(result = engine.resolve(party = partyForRun(run, party), quest = run.quest, equipment = equippedLoot)))
    }

    fun finishQuestNow(
        engine: ExpeditionEngine,
        party: List<Hero>,
        roll: Int? = null,
    ): PlaySessionState {
        val run = expedition ?: return this
        if (run.result != null) return this
        val result = if (roll == null) {
            engine.resolve(party = partyForRun(run, party), quest = run.quest, equipment = equippedLoot)
        } else {
            engine.resolve(party = partyForRun(run, party), quest = run.quest, roll = roll, equipment = equippedLoot)
        }
        return copy(expedition = run.copy(result = result))
    }

    fun collectResult(party: List<Hero> = heroes): PlaySessionState {
        val run = expedition ?: return this
        val result = run.result ?: return this
        val noticeBoardBonus = result.reward.gold * (noticeBoardLevel - 1) / 10
        val generatedLoot = LootGenerator.generate(result.reward.lootRolls, seed = lootSeed(result))
        val generatedJournal = JournalGenerator.generate(result, partyForRun(run, party))
        return copy(
            gold = gold + result.reward.gold + noticeBoardBonus,
            lootRolls = lootRolls + result.reward.lootRolls,
            pendingLootItems = pendingLootItems + generatedLoot,
            journalEntries = (journalEntries + generatedJournal).takeLast(12),
            expedition = null,
        )
    }

    fun selectedPartyForQuest(quest: Quest, party: List<Hero>): List<Hero> {
        val rosterIds = heroes.map { it.id }.toSet()
        return party
            .distinctBy { it.id }
            .filter { it.id in rosterIds }
            .take(quest.partySlots.coerceAtLeast(1))
    }

    private fun partyForRun(run: ExpeditionRun, fallbackParty: List<Hero>): List<Hero> {
        val savedParty = run.partyHeroIds.mapNotNull { heroId -> heroes.firstOrNull { it.id == heroId } }
        return selectedPartyForQuest(run.quest, savedParty.ifEmpty { fallbackParty })
    }

    private fun lootSeed(result: ExpeditionResult): Int =
        gold + lootRolls * 31 + noticeBoardLevel * 101 + result.scoreMargin

    fun upgradeNoticeBoard(cost: Int = 600): PlaySessionState {
        if (gold < cost) return this
        return copy(
            gold = gold - cost,
            noticeBoardLevel = noticeBoardLevel + 1,
        )
    }

    fun equippedItems(heroId: String): List<LootItem> =
        equippedLoot.filter { it.heroId == heroId }.map { it.item }

    fun equipmentBonus(heroId: String): Int =
        equippedItems(heroId).sumOf { it.bonus }

    fun totalPartyPower(): Int =
        PartyPowerCalculator.totalPower(heroes, equippedLoot)

    fun equipLoot(heroId: String, item: LootItem): PlaySessionState {
        if (heroes.none { it.id == heroId }) return this
        val itemIndex = lootItems.indexOf(item)
        if (itemIndex < 0) return this

        val previousEquipped = equippedLoot.firstOrNull { equipped ->
            equipped.heroId == heroId && equipped.item.slot == item.slot
        }?.item
        val nextInventory = lootItems.toMutableList().also { inventory ->
            inventory.removeAt(itemIndex)
            if (previousEquipped != null) inventory.add(previousEquipped)
        }
        val nextEquipment = equippedLoot
            .filterNot { equipped -> equipped.heroId == heroId && equipped.item.slot == item.slot }
            .plus(EquippedLoot(heroId = heroId, item = item))

        return copy(lootItems = nextInventory, equippedLoot = nextEquipment)
    }

    fun keepPendingLoot(item: LootItem): PlaySessionState {
        val itemIndex = pendingLootItems.indexOf(item)
        if (itemIndex < 0) return this
        val nextPending = pendingLootItems.toMutableList().also { it.removeAt(itemIndex) }
        return copy(
            pendingLootItems = nextPending,
            lootItems = lootItems + item,
        )
    }

    fun sellPendingLoot(item: LootItem): PlaySessionState {
        val itemIndex = pendingLootItems.indexOf(item)
        if (itemIndex < 0) return this
        val nextPending = pendingLootItems.toMutableList().also { it.removeAt(itemIndex) }
        return copy(
            gold = gold + LootEconomy.sellValue(item),
            pendingLootItems = nextPending,
        )
    }
    fun sellLoot(item: LootItem): PlaySessionState {
        val itemIndex = lootItems.indexOf(item)
        if (itemIndex < 0) return this
        val nextInventory = lootItems.toMutableList().also { it.removeAt(itemIndex) }
        return copy(
            gold = gold + LootEconomy.sellValue(item),
            lootItems = nextInventory,
        )
    }

    fun unequipLoot(heroId: String, slot: LootSlot): PlaySessionState {
        val equipped = equippedLoot.firstOrNull { it.heroId == heroId && it.item.slot == slot } ?: return this
        return copy(
            lootItems = lootItems + equipped.item,
            equippedLoot = equippedLoot - equipped,
        )
    }


    fun releaseHero(heroId: String): PlaySessionState {
        if (phase != PlayPhase.Idle || heroes.size <= 1 || heroes.none { it.id == heroId }) return this

        val releasedEquipment = equippedLoot.filter { it.heroId == heroId }
        return copy(
            heroes = heroes.filterNot { it.id == heroId },
            lootItems = lootItems + releasedEquipment.map { it.item },
            equippedLoot = equippedLoot.filterNot { it.heroId == heroId },
        )
    }

    fun recruitHero(seed: Int): HeroRecruitmentResult? {
        if (gold < HeroGacha.RECRUIT_COST) return null

        val hero = HeroGacha.summon(pulls = 1, seed = seed).single()
        val duplicate = heroes.any { it.id == hero.id }
        val reputationReward = if (duplicate) HeroGacha.DUPLICATE_REPUTATION_REWARD else 0
        return HeroRecruitmentResult(
            session = copy(
                gold = gold - HeroGacha.RECRUIT_COST,
                reputation = reputation + reputationReward,
                heroes = if (duplicate) heroes else heroes + hero,
            ),
            hero = hero,
            cost = HeroGacha.RECRUIT_COST,
            duplicate = duplicate,
            reputationReward = reputationReward,
        )
    }

    companion object {
        fun initial(): PlaySessionState = PlaySessionState()
    }
}

data class ExpeditionRun(
    val quest: Quest,
    val partyHeroIds: List<String> = emptyList(),
    val startedAtMillis: Long,
    val endsAtMillis: Long,
    val result: ExpeditionResult? = null,
)

data class HeroRecruitmentResult(
    val session: PlaySessionState,
    val hero: Hero,
    val cost: Int,
    val duplicate: Boolean,
    val reputationReward: Int,
)

sealed interface PlayPhase {
    data object Idle : PlayPhase
    data object Running : PlayPhase
    data object ResultReady : PlayPhase
}
