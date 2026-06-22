package com.ayoshy.badventurers.game

import kotlin.random.Random

enum class LootRarity {
    Common,
    Uncommon,
    Rare,
    Epic,
    Relic,
}

enum class LootSlot {
    Weapon,
    Armor,
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
}

data class LootItem(
    val name: String,
    val rarity: LootRarity,
    val slot: LootSlot,
    val bonus: Int,
    val icon: LootIcon,
)

object LootGenerator {
    private data class LootTemplate(
        val name: String,
        val slot: LootSlot,
        val icon: LootIcon,
    )

    private val rarityWeights = listOf(
        LootRarity.Common to 60,
        LootRarity.Uncommon to 25,
        LootRarity.Rare to 10,
        LootRarity.Epic to 4,
        LootRarity.Relic to 1,
    )

    private val templates = mapOf(
        LootSlot.Weapon to listOf(
            LootTemplate("Bent Spoon", LootSlot.Weapon, LootIcon.Spoon),
            LootTemplate("Fork Spear", LootSlot.Weapon, LootIcon.Weapon),
            LootTemplate("Moon Axe", LootSlot.Weapon, LootIcon.Weapon),
            LootTemplate("Nibblade", LootSlot.Weapon, LootIcon.Weapon),
            LootTemplate("Toast Mace", LootSlot.Weapon, LootIcon.Spoon),
        ),
        LootSlot.Armor to listOf(
            LootTemplate("Winged Boots", LootSlot.Armor, LootIcon.Boots),
            LootTemplate("Tin Vest", LootSlot.Armor, LootIcon.Tankard),
            LootTemplate("Patch Mail", LootSlot.Armor, LootIcon.Tankard),
            LootTemplate("Moss Coat", LootSlot.Armor, LootIcon.Hood),
            LootTemplate("Panic Shell", LootSlot.Armor, LootIcon.Helmet),
        ),
        LootSlot.Trinket to listOf(
            LootTemplate("Lucky Button", LootSlot.Trinket, LootIcon.Ring),
            LootTemplate("Pocket Charm", LootSlot.Trinket, LootIcon.Ring),
            LootTemplate("Spare Ring", LootSlot.Trinket, LootIcon.Ring),
            LootTemplate("Dust Coin", LootSlot.Trinket, LootIcon.Ring),
            LootTemplate("Quiet Bell", LootSlot.Trinket, LootIcon.Ring),
        ),
        LootSlot.Headgear to listOf(
            LootTemplate("Soup Helm", LootSlot.Headgear, LootIcon.Helmet),
            LootTemplate("Wobble Cap", LootSlot.Headgear, LootIcon.Hood),
            LootTemplate("Paper Crown", LootSlot.Headgear, LootIcon.Helmet),
            LootTemplate("Lantern Hat", LootSlot.Headgear, LootIcon.Helmet),
            LootTemplate("Grin Hood", LootSlot.Headgear, LootIcon.Hood),
        ),
        LootSlot.Consumable to listOf(
            LootTemplate("Stale Potion", LootSlot.Consumable, LootIcon.Potion),
            LootTemplate("Brave Biscuit", LootSlot.Consumable, LootIcon.Tankard),
            LootTemplate("Tiny Flask", LootSlot.Consumable, LootIcon.Potion),
            LootTemplate("Odd Elixir", LootSlot.Consumable, LootIcon.Potion),
            LootTemplate("Snap Tonic", LootSlot.Consumable, LootIcon.Potion),
        ),
    )

    fun generate(rolls: Int, seed: Int = 0): List<LootItem> =
        generate(rolls, Random(seed))

    fun generate(rolls: Int, random: Random): List<LootItem> {
        if (rolls <= 0) return emptyList()

        return List(rolls) {
            val slot = random.nextEnum(LootSlot.values().asList())
            val rarity = random.nextWeightedEnum(rarityWeights)
            val template = random.nextEnum(templates.getValue(slot))
            val bonus = bonusFor(rarity, slot)

            LootItem(
                name = template.name,
                rarity = rarity,
                slot = template.slot,
                bonus = bonus,
                icon = template.icon,
            )
        }
    }

    private fun bonusFor(rarity: LootRarity, slot: LootSlot): Int {
        val base = when (rarity) {
            LootRarity.Common -> 1
            LootRarity.Uncommon -> 2
            LootRarity.Rare -> 4
            LootRarity.Epic -> 7
            LootRarity.Relic -> 11
        }

        return base + when (slot) {
            LootSlot.Weapon -> 2
            LootSlot.Armor -> 1
            LootSlot.Trinket -> 0
            LootSlot.Headgear -> 0
            LootSlot.Consumable -> -1
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
