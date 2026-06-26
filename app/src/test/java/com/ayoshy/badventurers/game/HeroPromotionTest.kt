package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Test

class HeroPromotionTest {
    @Test
    fun promotionRankAddsBoundedClassAndTraitStats() {
        val brugg = HeroCatalog.byId.getValue("brugg").toHero()
        val promoted = HeroPromotion.previewPromoted(brugg, targetRank = 1)

        assertEquals(brugg.level, promoted.level)
        assertEquals(brugg.xp, promoted.xp)
        assertEquals(1, promoted.promotionRank)
        assertEquals(brugg.stats.force + 3, promoted.stats.force)
        assertEquals(brugg.stats.endurance + 2, promoted.stats.endurance)
        assertEquals(brugg.stats.ego + 1, promoted.stats.ego)
        assertEquals(brugg.stats.total + 6, promoted.stats.total)
    }

    @Test
    fun promotionPreviewClampsToMvpRankCap() {
        val mira = HeroCatalog.byId.getValue("mira").toHero()
        val maxed = HeroPromotion.previewPromoted(mira, targetRank = HeroPromotion.MAX_RANK)
        val overCap = HeroPromotion.previewPromoted(mira, targetRank = HeroPromotion.MAX_RANK + 10)

        assertEquals(maxed, overCap)
        assertEquals(HeroPromotion.MAX_RANK, maxed.promotionRank)
        assertEquals(mira.stats.total + HeroPromotion.MAX_RANK * 6, maxed.stats.total)
    }

    @Test
    fun promotionPreviewOnlyAppliesMissingRanks() {
        val nell = HeroCatalog.byId.getValue("nell").toHero()
        val rankOne = HeroPromotion.previewPromoted(nell, targetRank = 1)
        val rankTwo = HeroPromotion.previewPromoted(rankOne, targetRank = 2)

        assertEquals(2, rankTwo.promotionRank)
        assertEquals(rankOne.stats.total + 6, rankTwo.stats.total)
    }

    @Test
    fun promoteReturnsNullAtRankCap() {
        val maxed = HeroPromotion.previewPromoted(
            HeroCatalog.byId.getValue("mira").toHero(),
            targetRank = HeroPromotion.MAX_RANK,
        )

        assertEquals(null, HeroPromotion.promote(maxed))
    }
}