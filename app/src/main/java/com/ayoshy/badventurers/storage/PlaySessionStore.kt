package com.ayoshy.badventurers.storage

import android.content.Context
import com.ayoshy.badventurers.game.EquippedLootSnapshot
import com.ayoshy.badventurers.game.ExpeditionOutcome
import com.ayoshy.badventurers.game.ExpeditionResultSnapshot
import com.ayoshy.badventurers.game.ExpeditionRunSnapshot
import com.ayoshy.badventurers.game.HeroCatalog
import com.ayoshy.badventurers.game.JournalEntrySnapshot
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

class PlaySessionStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun loadState(): PlaySessionState {
        val encoded = preferences.getString(KEY_SESSION, null) ?: return PlaySessionState.initial()
        return decodeSnapshot(encoded)?.toState() ?: PlaySessionState.initial()
    }

    fun saveState(state: PlaySessionState) {
        preferences.edit()
            .putString(KEY_SESSION, encodeSnapshot(PlaySessionSnapshot.fromState(state)))
            .commit()
    }

    private fun encodeSnapshot(snapshot: PlaySessionSnapshot): String = JSONObject()
        .put("version", snapshot.version)
        .put("gold", snapshot.gold)
        .put("reputation", snapshot.reputation)
        .put("guildLevel", snapshot.guildLevel)
        .put("noticeBoardLevel", snapshot.noticeBoardLevel)
        .put("lootRolls", snapshot.lootRolls)
        .put("heroIds", JSONArray().also { array -> snapshot.heroIds.forEach { array.put(it) } })
        .put("lootItems", JSONArray().also { array -> snapshot.lootItems.forEach { array.put(encodeLootItem(it)) } })
        .put("pendingLootItems", JSONArray().also { array -> snapshot.pendingLootItems.forEach { array.put(encodeLootItem(it)) } })
        .put("equippedLoot", JSONArray().also { array -> snapshot.equippedLoot.forEach { array.put(encodeEquippedLoot(it)) } })
        .put("journalEntries", JSONArray().also { array -> snapshot.journalEntries.forEach { array.put(encodeJournalEntry(it)) } })
        .put("expedition", snapshot.expedition?.let { encodeExpedition(it) })
        .toString()

    private fun decodeSnapshot(encoded: String): PlaySessionSnapshot? = try {
        val json = JSONObject(encoded)
        PlaySessionSnapshot(
            version = json.optInt("version", 1),
            gold = json.optInt("gold", PlaySessionState.initial().gold),
            reputation = json.optInt("reputation", PlaySessionState.initial().reputation),
            guildLevel = json.optInt("guildLevel", PlaySessionState.initial().guildLevel),
            noticeBoardLevel = json.optInt("noticeBoardLevel", PlaySessionState.initial().noticeBoardLevel),
            lootRolls = json.optInt("lootRolls", PlaySessionState.initial().lootRolls),
            heroIds = decodeStringArray(json.optJSONArray("heroIds")).ifEmpty { HeroCatalog.starterHeroes.map { it.id } },
            lootItems = decodeObjectArray(json.optJSONArray("lootItems"), ::decodeLootItem),
            pendingLootItems = decodeObjectArray(json.optJSONArray("pendingLootItems"), ::decodeLootItem),
            equippedLoot = decodeObjectArray(json.optJSONArray("equippedLoot"), ::decodeEquippedLoot),
            journalEntries = decodeObjectArray(json.optJSONArray("journalEntries"), ::decodeJournalEntry),
            expedition = json.optJSONObject("expedition")?.let(::decodeExpedition),
        )
    } catch (_: Exception) {
        null
    }

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
        .put("result", run.result?.let { encodeResult(it) })

    private fun decodeExpedition(json: JSONObject): ExpeditionRunSnapshot = ExpeditionRunSnapshot(
        questId = json.optString("questId"),
        partyHeroIds = decodeStringArray(json.optJSONArray("partyHeroIds")),
        startedAtMillis = json.optLong("startedAtMillis"),
        endsAtMillis = json.optLong("endsAtMillis"),
        result = json.optJSONObject("result")?.let(::decodeResult),
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

    private companion object {
        const val PREFERENCES_NAME = "badventurers_session"
        const val KEY_SESSION = "session_snapshot"
    }
}