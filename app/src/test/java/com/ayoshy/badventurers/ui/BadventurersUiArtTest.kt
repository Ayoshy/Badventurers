package com.ayoshy.badventurers.ui

import com.ayoshy.badventurers.R
import com.ayoshy.badventurers.game.LootCatalog
import com.ayoshy.badventurers.game.LootItem
import com.ayoshy.badventurers.game.LootRarity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BadventurersUiArtTest {
    @Test
    fun commonLootArtResourcesCoverCommonCatalog() {
        val expectedIds = LootCatalog.byRarity.getValue(LootRarity.Common)
            .map { it.id }
            .toSet()

        assertEquals(expectedIds, commonLootArtResourceIds.keys)
        commonLootArtResourceIds.forEach { (id, resourceId) ->
            assertNotEquals("$id should resolve to a packaged loot art resource.", 0, resourceId)
        }
    }

    @Test
    fun commonLootUsesItemSpecificArt() {
        val definition = LootCatalog.byId.getValue("weapon_bent_spoon")
        val item = definition.toTestItem()

        assertEquals(R.drawable.loot_art_weapon_bent_spoon, lootArtResource(item))
        assertNotEquals(lootIconResource(item.icon), lootArtResource(item))
    }

    @Test
    fun nonCommonLootFallsBackToLegacyIcon() {
        val definition = LootCatalog.byRarity.getValue(LootRarity.Uncommon).first()
        val item = definition.toTestItem()

        assertEquals(lootIconResource(definition.icon), lootArtResource(item))
    }
}

private fun com.ayoshy.badventurers.game.LootDefinition.toTestItem(): LootItem = LootItem(
    id = id,
    name = name,
    rarity = rarity,
    slot = slot,
    icon = icon,
)