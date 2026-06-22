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

data class LootItem(
    val name: String,
    val rarity: LootRarity,
    val slot: LootSlot,
    val bonus: Int,
)

object LootGenerator {
    private val rarityWeights = listOf(
        LootRarity.Common to 60,
        LootRarity.Uncommon to 25,
        LootRarity.Rare to 10,
        LootRarity.Epic to 4,
        LootRarity.Relic to 1,
    )

    private val weaponNames = listOf("Bent Sword", "Fork Spear", "Moon Axe", "Nibblade", "Toast Mace")
    private val armorNames = listOf("Tin Vest", "Patch Mail", "Desk Plate", "Moss Coat", "Panic Shell")
    private val trinketNames = listOf("Lucky Button", "Pocket Charm", "Spare Ring", "Dust Coin", "Quiet Bell")
    private val headgearNames = listOf("Soup Helm", "Wobble Cap", "Paper Crown", "Lantern Hat", "Grin Hood")
    private val consumableNames = listOf("Stale Potion", "Brave Biscuit", "Tiny Flask", "Odd Elixir", "Snap Tonic")

    fun generate(rolls: Int, seed: Int = 0): List<LootItem> =
        generate(rolls, Random(seed))

    fun generate(rolls: Int, random: Random): List<LootItem> {
        if (rolls <= 0) return emptyList()

        return List(rolls) {
            val slot = random.nextEnum(LootSlot.values().asList())
            val rarity = random.nextWeightedEnum(rarityWeights)
            val name = random.nextNameForSlot(slot)
            val bonus = bonusFor(rarity, slot)

            LootItem(
                name = name,
                rarity = rarity,
                slot = slot,
                bonus = bonus,
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

    private fun Random.nextNameForSlot(slot: LootSlot): String =
        when (slot) {
            LootSlot.Weapon -> weaponNames[nextInt(weaponNames.size)]
            LootSlot.Armor -> armorNames[nextInt(armorNames.size)]
            LootSlot.Trinket -> trinketNames[nextInt(trinketNames.size)]
            LootSlot.Headgear -> headgearNames[nextInt(headgearNames.size)]
            LootSlot.Consumable -> consumableNames[nextInt(consumableNames.size)]
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

