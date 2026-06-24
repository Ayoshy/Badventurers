package com.ayoshy.badventurers.storage

import com.ayoshy.badventurers.game.AchievementProgress
import com.ayoshy.badventurers.game.EquippedLootSnapshot
import com.ayoshy.badventurers.game.ExpeditionOutcome
import com.ayoshy.badventurers.game.ExpeditionPlanCatalog
import com.ayoshy.badventurers.game.ExpeditionResultSnapshot
import com.ayoshy.badventurers.game.ExpeditionRunSnapshot
import com.ayoshy.badventurers.game.HeroCatalog
import com.ayoshy.badventurers.game.HeroProgressSnapshot
import com.ayoshy.badventurers.game.JournalEntrySnapshot
import com.ayoshy.badventurers.game.LootCarryBreakdown
import com.ayoshy.badventurers.game.LootIcon
import com.ayoshy.badventurers.game.LootItemSnapshot
import com.ayoshy.badventurers.game.LootRarity
import com.ayoshy.badventurers.game.LootSlot
import com.ayoshy.badventurers.game.PlaySessionSnapshot
import com.ayoshy.badventurers.game.PlaySessionState
import com.ayoshy.badventurers.game.RewardSnapshot
import com.ayoshy.badventurers.game.StatBonusSnapshot
import com.ayoshy.badventurers.game.StatType
import org.json.JSONArray
import org.json.JSONObject

internal object PlaySessionSnapshotJson {
    fun encode(snapshot: PlaySessionSnapshot): String = JSONObject()
        .put("version", snapshot.version)
        .put("gold", snapshot.gold)
        .put("reputation", snapshot.reputation)
        .put("guildLevel", snapshot.guildLevel)
        .put("completedQuestCount", snapshot.completedQuestCount)
        .put("noticeBoardLevel", snapshot.noticeBoardLevel)
        .put("trainingYardLevel", snapshot.trainingYardLevel)
        .put("bunkRoomLevel", snapshot.bunkRoomLevel)
        .put("lootRolls", snapshot.lootRolls)
        .put("heroIds", JSONArray().also { array -> snapshot.heroIds.forEach { array.put(it) } })
        .put("heroProgress", JSONArray().also { array -> snapshot.heroProgress.forEach { array.put(encodeHeroProgress(it)) } })
        .put("lootItems", JSONArray().also { array -> snapshot.lootItems.forEach { array.put(encodeLootItem(it)) } })
        .put("pendingLootItems", JSONArray().also { array -> snapshot.pendingLootItems.forEach { array.put(encodeLootItem(it)) } })
        .put("pendingLootKeepLimit", snapshot.pendingLootKeepLimit)
        .put("pendingLootKeptCount", snapshot.pendingLootKeptCount)
        .put("pendingLootCarryBreakdown", encodeLootCarryBreakdown(snapshot.pendingLootCarryBreakdown))
        .put("equippedLoot", JSONArray().also { array -> snapshot.equippedLoot.forEach { array.put(encodeEquippedLoot(it)) } })
        .put("journalEntries", JSONArray().also { array -> snapshot.journalEntries.forEach { array.put(encodeJournalEntry(it)) } })
        .put("expedition", snapshot.expedition?.let { encodeExpedition(it) })
        .put("achievementProgress", JSONArray().also { array -> snapshot.achievementProgress.forEach { array.put(encodeAchievementProgress(it)) } })
        .toString()

    fun decode(encoded: String): PlaySessionSnapshot? = try {
        val json = JSONObject(encoded)
        val initial = PlaySessionState.initial()
        PlaySessionSnapshot(
            version = json.optInt("version", 1),
            gold = json.optInt("gold", initial.gold),
            reputation = json.optInt("reputation", initial.reputation),
            guildLevel = json.optInt("guildLevel", initial.guildLevel),
            completedQuestCount = json.optInt("completedQuestCount", initial.completedQuestCount),
            noticeBoardLevel = json.optInt("noticeBoardLevel", initial.noticeBoardLevel),
            trainingYardLevel = json.optInt("trainingYardLevel", initial.trainingYardLevel),
            bunkRoomLevel = json.optInt("bunkRoomLevel", initial.bunkRoomLevel),
            lootRolls = json.optInt("lootRolls", initial.lootRolls),
            heroIds = decodeStringArray(json.optJSONArray("heroIds")).ifEmpty { HeroCatalog.starterHeroes.map { it.id } },
            heroProgress = decodeObjectArray(json.optJSONArray("heroProgress"), ::decodeHeroProgress),
            lootItems = decodeObjectArray(json.optJSONArray("lootItems"), ::decodeLootItem),
            pendingLootItems = decodeObjectArray(json.optJSONArray("pendingLootItems"), ::decodeLootItem),
            pendingLootKeepLimit = json.optInt("pendingLootKeepLimit", 0),
            pendingLootKeptCount = json.optInt("pendingLootKeptCount", 0),
            pendingLootCarryBreakdown = json.optJSONObject("pendingLootCarryBreakdown")?.let(::decodeLootCarryBreakdown) ?: LootCarryBreakdown(),
            equippedLoot = decodeObjectArray(json.optJSONArray("equippedLoot"), ::decodeEquippedLoot),
            journalEntries = decodeObjectArray(json.optJSONArray("journalEntries"), ::decodeJournalEntry),
            expedition = json.optJSONObject("expedition")?.let(::decodeExpedition),
            achievementProgress = decodeObjectArray(json.optJSONArray("achievementProgress"), ::decodeAchievementProgress),
        )
    } catch (_: Exception) {
        null
    }

    private fun encodeHeroProgress(progress: HeroProgressSnapshot): JSONObject = JSONObject()
        .put("heroId", progress.heroId)
        .put("level", progress.level)
        .put("xp", progress.xp)

    private fun decodeHeroProgress(json: JSONObject): HeroProgressSnapshot? {
        val heroId = json.optString("heroId").takeIf { it.isNotBlank() } ?: return null
        return HeroProgressSnapshot(
            heroId = heroId,
            level = json.optInt("level", 1),
            xp = json.optInt("xp"),
        )
    }

    private fun encodeAchievementProgress(progress: AchievementProgress): JSONObject = JSONObject()
        .put("achievementId", progress.achievementId)
        .put("current", progress.current)
        .put("completedAtMillis", progress.completedAtMillis ?: JSONObject.NULL)
        .put("claimedAtMillis", progress.claimedAtMillis ?: JSONObject.NULL)
        .put("seen", progress.seen)

    private fun decodeAchievementProgress(json: JSONObject): AchievementProgress? {
        val achievementId = json.optString("achievementId").takeIf { it.isNotBlank() } ?: return null
        return AchievementProgress(
            achievementId = achievementId,
            current = json.optInt("current"),
            completedAtMillis = nullableLong(json, "completedAtMillis"),
            claimedAtMillis = nullableLong(json, "claimedAtMillis"),
            seen = json.optBoolean("seen"),
        )
    }

    private fun encodeLootCarryBreakdown(breakdown: LootCarryBreakdown): JSONObject = JSONObject()
        .put("base", breakdown.base)
        .put("bunkRoom", breakdown.bunkRoom)
        .put("veteran", breakdown.veteran)
        .put("specialist", breakdown.specialist)

    private fun decodeLootCarryBreakdown(json: JSONObject): LootCarryBreakdown = LootCarryBreakdown(
        base = json.optInt("base"),
        bunkRoom = json.optInt("bunkRoom"),
        veteran = json.optInt("veteran"),
        specialist = json.optInt("specialist"),
    )

    private fun encodeLootItem(item: LootItemSnapshot): JSONObject = JSONObject()
        .put("id", item.id)
        .put("name", item.name)
        .put("rarity", item.rarity.name)
        .put("slot", item.slot.name)
        .put("stats", JSONArray().also { array -> item.stats.forEach { array.put(encodeStatBonus(it)) } })
        .put("bonus", item.bonus)
        .put("icon", item.icon.name)

    private fun decodeLootItem(json: JSONObject): LootItemSnapshot? {
        val rarity = enumValueOrNull<LootRarity>(json.optString("rarity")) ?: return null
        val slot = enumValueOrNull<LootSlot>(json.optString("slot")) ?: return null
        val icon = enumValueOrNull<LootIcon>(json.optString("icon")) ?: return null
        val stats = decodeObjectArray(json.optJSONArray("stats"), ::decodeStatBonus)
        return LootItemSnapshot(
            id = json.optString("id"),
            name = json.optString("name"),
            rarity = rarity,
            slot = slot,
            stats = stats,
            bonus = json.optInt("bonus"),
            icon = icon,
        )
    }

    private fun encodeStatBonus(stat: StatBonusSnapshot): JSONObject = JSONObject()
        .put("type", stat.type.name)
        .put("value", stat.value)

    private fun decodeStatBonus(json: JSONObject): StatBonusSnapshot? {
        val type = enumValueOrNull<StatType>(json.optString("type")) ?: return null
        return StatBonusSnapshot(type = type, value = json.optInt("value"))
    }

    private fun encodeEquippedLoot(equipped: EquippedLootSnapshot): JSONObject = JSONObject()
        .put("heroId", equipped.heroId)
        .put("item", encodeLootItem(equipped.item))

    private fun decodeEquippedLoot(json: JSONObject): EquippedLootSnapshot? {
        val item = json.optJSONObject("item")?.let(::decodeLootItem) ?: return null
        val heroId = json.optString("heroId").takeIf { it.isNotBlank() } ?: return null
        return EquippedLootSnapshot(heroId = heroId, item = item)
    }

    private fun encodeJournalEntry(entry: JournalEntrySnapshot): JSONObject = JSONObject()
        .put("id", entry.id)
        .put("text", entry.text)

    private fun decodeJournalEntry(json: JSONObject): JournalEntrySnapshot = JournalEntrySnapshot(
        id = json.optString("id"),
        text = json.optString("text"),
    )

    private fun encodeExpedition(run: ExpeditionRunSnapshot): JSONObject = JSONObject()
        .put("questId", run.questId)
        .put("partyHeroIds", JSONArray().also { array -> run.partyHeroIds.forEach { array.put(it) } })
        .put("startedAtMillis", run.startedAtMillis)
        .put("endsAtMillis", run.endsAtMillis)
        .put("planId", run.planId)
        .put("result", run.result?.let { encodeResult(it) })

    private fun decodeExpedition(json: JSONObject): ExpeditionRunSnapshot = ExpeditionRunSnapshot(
        questId = json.optString("questId"),
        partyHeroIds = decodeStringArray(json.optJSONArray("partyHeroIds")),
        startedAtMillis = json.optLong("startedAtMillis"),
        endsAtMillis = json.optLong("endsAtMillis"),
        result = json.optJSONObject("result")?.let(::decodeResult),
        planId = ExpeditionPlanCatalog.coercePlanId(json.optString("planId", ExpeditionPlanCatalog.defaultPlanId)),
    )

    private fun encodeResult(result: ExpeditionResultSnapshot): JSONObject = JSONObject()
        .put("outcome", result.outcome.name)
        .put("reward", encodeReward(result.reward))
        .put("scoreMargin", result.scoreMargin)

    private fun decodeResult(json: JSONObject): ExpeditionResultSnapshot? {
        val outcome = enumValueOrNull<ExpeditionOutcome>(json.optString("outcome")) ?: return null
        val reward = json.optJSONObject("reward")?.let(::decodeReward) ?: return null
        return ExpeditionResultSnapshot(
            outcome = outcome,
            reward = reward,
            scoreMargin = json.optInt("scoreMargin"),
        )
    }

    private fun encodeReward(reward: RewardSnapshot): JSONObject = JSONObject()
        .put("gold", reward.gold)
        .put("xp", reward.xp)
        .put("lootRolls", reward.lootRolls)

    private fun decodeReward(json: JSONObject): RewardSnapshot = RewardSnapshot(
        gold = json.optInt("gold"),
        xp = json.optInt("xp"),
        lootRolls = json.optInt("lootRolls"),
    )

    private fun nullableLong(json: JSONObject, key: String): Long? =
        if (json.has(key) && !json.isNull(key)) json.optLong(key) else null

    private fun decodeStringArray(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        return (0 until array.length()).mapNotNull { index ->
            array.optString(index).takeIf { it.isNotBlank() }
        }
    }

    private fun <T> decodeObjectArray(array: JSONArray?, decode: (JSONObject) -> T?): List<T> {
        if (array == null) return emptyList()
        return (0 until array.length()).mapNotNull { index ->
            array.optJSONObject(index)?.let(decode)
        }
    }

    private inline fun <reified T : Enum<T>> enumValueOrNull(name: String?): T? =
        enumValues<T>().firstOrNull { it.name == name }
}
