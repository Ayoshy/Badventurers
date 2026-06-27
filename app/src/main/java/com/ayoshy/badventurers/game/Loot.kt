package com.ayoshy.badventurers.game

import kotlin.random.Random

enum class LootRarity {
    Common,
    Uncommon,
    Rare,
    Epic,
    Relic,
}

val LootRarity.statSlotCount: Int
    get() = when (this) {
        LootRarity.Common -> 1
        LootRarity.Uncommon -> 2
        LootRarity.Rare -> 3
        LootRarity.Epic -> 4
        LootRarity.Relic -> 5
    }

enum class LootSlot {
    Weapon,
    Armor,
    Footwear,
    Trinket,
    Headgear,
    Consumable,
}

enum class LootIcon {
    Boots,
    Ring,
    Helmet,
    Weapon,
    Spoon,
    Hood,
    Tankard,
    Potion,
    Blade,
}

data class LootDefinition(
    val id: String,
    val name: String,
    val rarity: LootRarity,
    val slot: LootSlot,
    val icon: LootIcon,
)

data class LootItem(
    val id: String,
    val name: String,
    val rarity: LootRarity,
    val slot: LootSlot,
    val stats: List<StatBonus> = emptyList(),
    val bonus: Int = stats.sumOf { it.value },
    val icon: LootIcon,
) {
    val isPerfect: Boolean
        get() = stats.size == rarity.statSlotCount && stats.all { it.value == LootGenerator.MAX_STAT_VALUE }
}

object LootCatalog {
    val items = listOf(
        LootDefinition("weapon_bent_spoon", "Bent Spoon", LootRarity.Common, LootSlot.Weapon, LootIcon.Spoon),
        LootDefinition("weapon_fork_spear", "Fork Spear", LootRarity.Common, LootSlot.Weapon, LootIcon.Weapon),
        LootDefinition("weapon_moon_axe", "Moon Axe", LootRarity.Common, LootSlot.Weapon, LootIcon.Weapon),
        LootDefinition("weapon_nibblade", "Nibblade", LootRarity.Common, LootSlot.Weapon, LootIcon.Blade),
        LootDefinition("weapon_toast_mace", "Toast Spoon", LootRarity.Common, LootSlot.Weapon, LootIcon.Spoon),
        LootDefinition("armor_patch_hood", "Patch Cloak", LootRarity.Common, LootSlot.Armor, LootIcon.Hood),
        LootDefinition("armor_moss_coat", "Moss Coat", LootRarity.Common, LootSlot.Armor, LootIcon.Hood),
        LootDefinition("armor_winged_boots", "Winged Boots", LootRarity.Common, LootSlot.Footwear, LootIcon.Boots),
        LootDefinition("armor_travel_boots", "Travel Boots", LootRarity.Common, LootSlot.Footwear, LootIcon.Boots),
        LootDefinition("trinket_lucky_ring", "Lucky Ring", LootRarity.Common, LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("trinket_pocket_ring", "Pocket Ring", LootRarity.Common, LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("trinket_spare_ring", "Spare Ring", LootRarity.Common, LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("trinket_dusty_ring", "Dusty Ring", LootRarity.Common, LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("trinket_quiet_ring", "Quiet Ring", LootRarity.Common, LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("armor_panic_helm", "Panic Helm", LootRarity.Common, LootSlot.Headgear, LootIcon.Helmet),
        LootDefinition("headgear_soup_helm", "Soup Helm", LootRarity.Common, LootSlot.Headgear, LootIcon.Helmet),
        LootDefinition("headgear_wobble_cap", "Wobble Cap", LootRarity.Common, LootSlot.Headgear, LootIcon.Hood),
        LootDefinition("headgear_paper_crown", "Paper Crown", LootRarity.Common, LootSlot.Headgear, LootIcon.Helmet),
        LootDefinition("headgear_lantern_hat", "Lantern Hat", LootRarity.Common, LootSlot.Headgear, LootIcon.Helmet),
        LootDefinition("headgear_grin_hood", "Grin Hood", LootRarity.Common, LootSlot.Headgear, LootIcon.Hood),
        LootDefinition("consumable_stale_potion", "Stale Potion", LootRarity.Common, LootSlot.Consumable, LootIcon.Potion),
        LootDefinition("consumable_brave_brew", "Brave Brew", LootRarity.Common, LootSlot.Consumable, LootIcon.Tankard),
        LootDefinition("consumable_tiny_flask", "Tiny Flask", LootRarity.Common, LootSlot.Consumable, LootIcon.Potion),
        LootDefinition("consumable_odd_elixir", "Odd Elixir", LootRarity.Common, LootSlot.Consumable, LootIcon.Potion),
        LootDefinition("consumable_snap_tonic", "Snap Tonic", LootRarity.Common, LootSlot.Consumable, LootIcon.Potion),
        LootDefinition("weapon_receipt_cutter", "Receipt Cutter", LootRarity.Uncommon, LootSlot.Weapon, LootIcon.Blade),
        LootDefinition("weapon_mop_halberd", "Mop Halberd", LootRarity.Uncommon, LootSlot.Weapon, LootIcon.Weapon),
        LootDefinition("armor_buttoned_barrel", "Buttoned Barrel", LootRarity.Uncommon, LootSlot.Armor, LootIcon.Hood),
        LootDefinition("footwear_squeaky_greaves", "Squeaky Greaves", LootRarity.Uncommon, LootSlot.Footwear, LootIcon.Boots),
        LootDefinition("trinket_queue_token", "Queue Token", LootRarity.Uncommon, LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("headgear_bucket_visor", "Bucket Visor", LootRarity.Uncommon, LootSlot.Headgear, LootIcon.Helmet),
        LootDefinition("consumable_warmish_tea", "Warmish Tea", LootRarity.Uncommon, LootSlot.Consumable, LootIcon.Tankard),
        LootDefinition("consumable_pickle_potion", "Pickle Potion", LootRarity.Uncommon, LootSlot.Consumable, LootIcon.Potion),
        LootDefinition("weapon_fine_print_rapier", "Fine Print Rapier", LootRarity.Rare, LootSlot.Weapon, LootIcon.Blade),
        LootDefinition("weapon_taxman_gavel", "Taxman Gavel", LootRarity.Rare, LootSlot.Weapon, LootIcon.Weapon),
        LootDefinition("armor_invoice_mail", "Invoice Mail", LootRarity.Rare, LootSlot.Armor, LootIcon.Hood),
        LootDefinition("footwear_witness_slippers", "Witness Slippers", LootRarity.Rare, LootSlot.Footwear, LootIcon.Boots),
        LootDefinition("trinket_overtime_hourglass", "Overtime Hourglass", LootRarity.Rare, LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("headgear_notary_wig", "Notary Wig", LootRarity.Rare, LootSlot.Headgear, LootIcon.Hood),
        LootDefinition("consumable_bottled_courage", "Bottled Courage", LootRarity.Rare, LootSlot.Consumable, LootIcon.Potion),
        LootDefinition("trinket_minor_oracle_receipt", "Minor Oracle Receipt", LootRarity.Rare, LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("weapon_auditors_halberd", "Auditors Halberd", LootRarity.Epic, LootSlot.Weapon, LootIcon.Weapon),
        LootDefinition("weapon_dragon_stamp", "Dragon Stamp", LootRarity.Epic, LootSlot.Weapon, LootIcon.Weapon),
        LootDefinition("armor_misfiled_aegis", "Misfiled Aegis", LootRarity.Epic, LootSlot.Armor, LootIcon.Hood),
        LootDefinition("footwear_plausible_denial_boots", "Boots of Plausible Denial", LootRarity.Epic, LootSlot.Footwear, LootIcon.Boots),
        LootDefinition("trinket_bell_of_last_call", "Bell of Last Call", LootRarity.Epic, LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("headgear_emergency_minutes_crown", "Crown of Emergency Minutes", LootRarity.Epic, LootSlot.Headgear, LootIcon.Helmet),
        LootDefinition("consumable_second_chance_soup", "Soup of Second Chances", LootRarity.Epic, LootSlot.Consumable, LootIcon.Tankard),
        LootDefinition("trinket_contract_knot", "Contract Knot", LootRarity.Epic, LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("weapon_spoon_final_notice", "Spoon of Final Notice", LootRarity.Relic, LootSlot.Weapon, LootIcon.Spoon),
        LootDefinition("weapon_moonlit_receipt_cleaver", "Moonlit Receipt Cleaver", LootRarity.Relic, LootSlot.Weapon, LootIcon.Blade),
        LootDefinition("armor_many_signatures_cloak", "Cloak of Many Signatures", LootRarity.Relic, LootSlot.Armor, LootIcon.Hood),
        LootDefinition("footwear_inevitable_return_sandals", "Sandals of Inevitable Return", LootRarity.Relic, LootSlot.Footwear, LootIcon.Boots),
        LootDefinition("trinket_perpetual_queue_ring", "Ring of Perpetual Queue", LootRarity.Relic, LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("headgear_halo_compliance", "Halo of Compliance", LootRarity.Relic, LootSlot.Headgear, LootIcon.Helmet),
        LootDefinition("consumable_absolute_maybe_elixir", "Elixir of Absolute Maybe", LootRarity.Relic, LootSlot.Consumable, LootIcon.Potion),
        LootDefinition("trinket_unpaid_charter_seal", "Unpaid Charter Seal", LootRarity.Relic, LootSlot.Trinket, LootIcon.Ring),
    )

    val byId: Map<String, LootDefinition> = items.associateBy { it.id }
    val bySlot: Map<LootSlot, List<LootDefinition>> = items.groupBy { it.slot }
    val byRarity: Map<LootRarity, List<LootDefinition>> = items.groupBy { it.rarity }

    init {
        require(items.size == byId.size) { "Loot definition ids must be unique." }
        require(LootSlot.values().all { bySlot[it]?.isNotEmpty() == true }) { "Every loot slot needs catalog entries." }
        require(LootRarity.values().all { byRarity[it]?.isNotEmpty() == true }) { "Every loot rarity needs catalog entries." }
    }
}

object LootEconomy {
    fun sellValue(item: LootItem): Int {
        val rarityValue = when (item.rarity) {
            LootRarity.Common -> 25
            LootRarity.Uncommon -> 45
            LootRarity.Rare -> 80
            LootRarity.Epic -> 140
            LootRarity.Relic -> 240
        }
        return rarityValue + item.bonus * 20
    }

    fun rerollCost(item: LootItem, armoryForgeLevel: Int): Int {
        val forgeDiscountPercent = (armoryForgeLevel.coerceAtLeast(0) * 8).coerceAtMost(32)
        val discounted = sellValue(item) * (100 - forgeDiscountPercent) / 100
        return discounted.coerceAtLeast(20)
    }
}

object LootGenerator {
    data class LootRarityProfile(val rarityWeights: List<Pair<LootRarity, Int>>) {
        val totalWeight: Int
            get() = rarityWeights.sumOf { it.second }

        val rareWeight: Int
            get() = rarityWeights.firstOrNull { (rarity, _) -> rarity == LootRarity.Rare }?.second ?: 0

        val epicWeight: Int
            get() = rarityWeights.firstOrNull { (rarity, _) -> rarity == LootRarity.Epic }?.second ?: 0

        val rareOrBetterWeight: Int
            get() = rarityWeights
                .filter { (rarity, _) -> rarity >= LootRarity.Rare }
                .sumOf { it.second }

        init {
            require(rarityWeights.isNotEmpty()) { "Loot rarity profiles need at least one rarity." }
            require(rarityWeights.all { (_, weight) -> weight > 0 }) { "Loot rarity weights must be positive." }
            require(rarityWeights.map { it.first }.toSet().size == rarityWeights.size) { "Loot rarity profiles cannot duplicate rarities." }
        }
    }

    const val MAX_STAT_VALUE = 10
    const val RARE_LOOT_COMPLETED_QUEST_THRESHOLD = 8
    const val EPIC_LOOT_COMPLETED_QUEST_THRESHOLD = 13

    val baseLootProfile = LootRarityProfile(
        listOf(
            LootRarity.Common to 75,
            LootRarity.Uncommon to 25,
        ),
    )

    val palier2RareLootProfile = LootRarityProfile(
        listOf(
            LootRarity.Common to 65,
            LootRarity.Uncommon to 25,
            LootRarity.Rare to 10,
        ),
    )

    val palier3EpicLootProfile = LootRarityProfile(
        listOf(
            LootRarity.Common to 55,
            LootRarity.Uncommon to 25,
            LootRarity.Rare to 15,
            LootRarity.Epic to 5,
        ),
    )

    fun isRareLootUnlocked(completedQuestCount: Int): Boolean =
        completedQuestCount >= RARE_LOOT_COMPLETED_QUEST_THRESHOLD

    fun isEpicLootUnlocked(completedQuestCount: Int): Boolean =
        completedQuestCount >= EPIC_LOOT_COMPLETED_QUEST_THRESHOLD

    fun lootProfileForProgress(completedQuestCount: Int): LootRarityProfile =
        when {
            isEpicLootUnlocked(completedQuestCount) -> palier3EpicLootProfile
            isRareLootUnlocked(completedQuestCount) -> palier2RareLootProfile
            else -> baseLootProfile
        }

    fun lootProfileWithArmoryForgeBonus(
        profile: LootRarityProfile,
        armoryForgeLevel: Int,
    ): LootRarityProfile {
        val level = armoryForgeLevel.coerceAtLeast(0)
        if (level <= 0) return profile

        val highestUnlockedRarity = profile.rarityWeights.maxBy { (rarity, _) -> rarity }.first
        val bump = level * 5
        val commonWeight = profile.rarityWeights.firstOrNull { (rarity, _) -> rarity == LootRarity.Common }?.second ?: 0
        val actualBump = bump.coerceAtMost((commonWeight - 1).coerceAtLeast(0))
        if (actualBump <= 0) return profile

        return LootRarityProfile(
            profile.rarityWeights.map { (rarity, weight) ->
                when (rarity) {
                    LootRarity.Common -> rarity to weight - actualBump
                    highestUnlockedRarity -> rarity to weight + actualBump
                    else -> rarity to weight
                }
            },
        )
    }

    private val statValueWeights = listOf(
        1 to 22,
        2 to 19,
        3 to 16,
        4 to 13,
        5 to 10,
        6 to 8,
        7 to 5,
        8 to 4,
        9 to 2,
        10 to 1,
    )

    fun rerollItem(item: LootItem, seed: Int): LootItem =
        rerollItem(item, Random(seed))

    fun rerollItem(item: LootItem, random: Random): LootItem {
        val stats = rerolledStats(item.rarity, item.stats, random)
        return item.copy(
            stats = stats,
            bonus = stats.sumOf { it.value },
        )
    }

    fun generate(rolls: Int, seed: Int = 0): List<LootItem> =
        generate(rolls, Random(seed), baseLootProfile)

    fun generate(
        rolls: Int,
        seed: Int,
        lootProfile: LootRarityProfile,
    ): List<LootItem> = generate(rolls, Random(seed), lootProfile)

    fun generate(
        rolls: Int,
        random: Random,
        lootProfile: LootRarityProfile = baseLootProfile,
    ): List<LootItem> {
        if (rolls <= 0) return emptyList()

        return List(rolls) {
            val rarity = random.nextWeightedEnum(lootProfile.rarityWeights)
            val definition = random.nextEnum(LootCatalog.byRarity.getValue(rarity))
            val stats = rollStats(definition.rarity, random)

            LootItem(
                id = definition.id,
                name = definition.name,
                rarity = definition.rarity,
                slot = definition.slot,
                stats = stats,
                bonus = stats.sumOf { it.value },
                icon = definition.icon,
            )
        }
    }

    private fun rerolledStats(rarity: LootRarity, previousStats: List<StatBonus>, random: Random): List<StatBonus> {
        var stats = rollStats(rarity, random)
        var attempts = 0
        while (stats == previousStats && attempts < 3) {
            stats = rollStats(rarity, random)
            attempts += 1
        }
        return stats
    }

    private fun rollStats(rarity: LootRarity, random: Random): List<StatBonus> {
        val remainingStats = StatType.values().toMutableList()
        return List(rarity.statSlotCount) {
            val stat = random.nextEnum(remainingStats)
            remainingStats.remove(stat)
            StatBonus(type = stat, value = random.nextWeightedEnum(statValueWeights))
        }
    }

    private fun <T> Random.nextEnum(values: List<T>): T =
        values[nextInt(values.size)]

    private fun <T> Random.nextWeightedEnum(values: List<Pair<T, Int>>): T {
        val totalWeight = values.sumOf { it.second }
        var roll = nextInt(totalWeight)

        for ((value, weight) in values) {
            if (roll < weight) return value
            roll -= weight
        }

        return values.last().first
    }
}