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
        LootDefinition("weapon_bent_spoon", "Bent Spoon", LootSlot.Weapon, LootIcon.Spoon),
        LootDefinition("weapon_fork_spear", "Fork Spear", LootSlot.Weapon, LootIcon.Weapon),
        LootDefinition("weapon_moon_axe", "Moon Axe", LootSlot.Weapon, LootIcon.Weapon),
        LootDefinition("weapon_nibblade", "Nibblade", LootSlot.Weapon, LootIcon.Blade),
        LootDefinition("weapon_toast_mace", "Toast Spoon", LootSlot.Weapon, LootIcon.Spoon),

        LootDefinition("armor_patch_hood", "Patch Cloak", LootSlot.Armor, LootIcon.Hood),
        LootDefinition("armor_moss_coat", "Moss Coat", LootSlot.Armor, LootIcon.Hood),

        LootDefinition("armor_winged_boots", "Winged Boots", LootSlot.Footwear, LootIcon.Boots),
        LootDefinition("armor_travel_boots", "Travel Boots", LootSlot.Footwear, LootIcon.Boots),

        LootDefinition("trinket_lucky_ring", "Lucky Ring", LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("trinket_pocket_ring", "Pocket Ring", LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("trinket_spare_ring", "Spare Ring", LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("trinket_dusty_ring", "Dusty Ring", LootSlot.Trinket, LootIcon.Ring),
        LootDefinition("trinket_quiet_ring", "Quiet Ring", LootSlot.Trinket, LootIcon.Ring),

        LootDefinition("armor_panic_helm", "Panic Helm", LootSlot.Headgear, LootIcon.Helmet),
        LootDefinition("headgear_soup_helm", "Soup Helm", LootSlot.Headgear, LootIcon.Helmet),
        LootDefinition("headgear_wobble_cap", "Wobble Cap", LootSlot.Headgear, LootIcon.Hood),
        LootDefinition("headgear_paper_crown", "Paper Crown", LootSlot.Headgear, LootIcon.Helmet),
        LootDefinition("headgear_lantern_hat", "Lantern Hat", LootSlot.Headgear, LootIcon.Helmet),
        LootDefinition("headgear_grin_hood", "Grin Hood", LootSlot.Headgear, LootIcon.Hood),

        LootDefinition("consumable_stale_potion", "Stale Potion", LootSlot.Consumable, LootIcon.Potion),
        LootDefinition("consumable_brave_brew", "Brave Brew", LootSlot.Consumable, LootIcon.Tankard),
        LootDefinition("consumable_tiny_flask", "Tiny Flask", LootSlot.Consumable, LootIcon.Potion),
        LootDefinition("consumable_odd_elixir", "Odd Elixir", LootSlot.Consumable, LootIcon.Potion),
        LootDefinition("consumable_snap_tonic", "Snap Tonic", LootSlot.Consumable, LootIcon.Potion),
    )

    val byId: Map<String, LootDefinition> = items.associateBy { it.id }
    val bySlot: Map<LootSlot, List<LootDefinition>> = items.groupBy { it.slot }

    init {
        require(items.size == byId.size) { "Loot definition ids must be unique." }
        require(LootSlot.values().all { bySlot[it]?.isNotEmpty() == true }) { "Every loot slot needs catalog entries." }
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
}

object LootGenerator {
    const val MAX_STAT_VALUE = 10

    private val rarityWeights = listOf(
        LootRarity.Common to 60,
        LootRarity.Uncommon to 25,
        LootRarity.Rare to 10,
        LootRarity.Epic to 4,
        LootRarity.Relic to 1,
    )

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

    fun generate(rolls: Int, seed: Int = 0): List<LootItem> =
        generate(rolls, Random(seed))

    fun generate(rolls: Int, random: Random): List<LootItem> {
        if (rolls <= 0) return emptyList()

        return List(rolls) {
            val slot = random.nextEnum(LootSlot.values().asList())
            val rarity = random.nextWeightedEnum(rarityWeights)
            val definition = random.nextEnum(LootCatalog.bySlot.getValue(slot))
            val stats = rollStats(rarity, random)

            LootItem(
                id = definition.id,
                name = definition.name,
                rarity = rarity,
                slot = definition.slot,
                stats = stats,
                bonus = stats.sumOf { it.value },
                icon = definition.icon,
            )
        }
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