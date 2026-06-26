package com.ayoshy.badventurers.ui

import com.ayoshy.badventurers.R
import com.ayoshy.badventurers.game.HeroCatalog
import com.ayoshy.badventurers.game.LootCatalog
import com.ayoshy.badventurers.game.LootItem
import com.ayoshy.badventurers.game.LootRarity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BadventurersUiArtTest {
    @Test
    fun catalogHeroesUseUniquePortraitResources() {
        val portraitsByHero = HeroCatalog.heroes.associate { definition ->
            definition.id to heroPortraitResource(definition.toHero())
        }
        val duplicateGroups = portraitsByHero.entries
            .groupBy({ it.value }, { it.key })
            .filterValues { it.size > 1 }

        assertEquals(
            "Each catalog hero should use a unique portrait resource.",
            emptyMap<Int, List<String>>(),
            duplicateGroups,
        )
        assertEquals(HeroCatalog.heroes.size, portraitsByHero.values.toSet().size)
    }

    @Test
    fun accountantPortraitOwnershipMatchesCatalogIntent() {
        assertEquals(
            R.drawable.hero_portrait_comptable,
            heroPortraitResource(HeroCatalog.byId.getValue("ledger").toHero()),
        )
        assertEquals(
            R.drawable.hero_portrait_tally_noakes,
            heroPortraitResource(HeroCatalog.byId.getValue("comptable").toHero()),
        )
        assertNotEquals(
            heroPortraitResource(HeroCatalog.byId.getValue("ledger").toHero()),
            heroPortraitResource(HeroCatalog.byId.getValue("comptable").toHero()),
        )
    }

    @Test
    fun acceptedLootArtResourcesCoverWholeCatalog() {
        val expectedIds = LootRarity.values()
            .flatMap { LootCatalog.byRarity.getValue(it) }
            .map { it.id }
            .toSet()

        assertEquals(expectedIds, acceptedLootArtResourceIds.keys)
        acceptedLootArtResourceIds.forEach { (id, resourceId) ->
            assertNotEquals("$id should resolve to a packaged loot art resource.", 0, resourceId)
        }
    }

    @Test
    fun acceptedLootUsesItemSpecificArt() {
        val examples = listOf(
            "weapon_bent_spoon" to R.drawable.loot_art_weapon_bent_spoon,
            "weapon_receipt_cutter" to R.drawable.loot_art_weapon_receipt_cutter,
            "weapon_fine_print_rapier" to R.drawable.loot_art_weapon_fine_print_rapier,
            "weapon_auditors_halberd" to R.drawable.loot_art_weapon_auditors_halberd,
            "weapon_spoon_final_notice" to R.drawable.loot_art_weapon_spoon_final_notice,
        )

        examples.forEach { (id, expectedResource) ->
            val definition = LootCatalog.byId.getValue(id)
            val item = definition.toTestItem()

            assertEquals(expectedResource, lootArtResource(item))
            assertNotEquals(lootIconResource(item.icon), lootArtResource(item))
        }
    }

    @Test
    fun acceptedLootDoesNotFallBackToLegacyIcons() {
        LootRarity.values()
            .flatMap { LootCatalog.byRarity.getValue(it) }
            .forEach { definition ->
                val item = definition.toTestItem()

                assertNotEquals(definition.id, lootIconResource(definition.icon), lootArtResource(item))
            }
    }
}

private fun com.ayoshy.badventurers.game.LootDefinition.toTestItem(): LootItem = LootItem(
    id = id,
    name = name,
    rarity = rarity,
    slot = slot,
    icon = icon,
)