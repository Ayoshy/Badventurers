package com.ayoshy.badventurers.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RecruitmentTicketsTest {
    private val catalog = RecruitmentTicketCatalog.tickets
    private val roster = HeroCatalog.heroes.map { it.toHero() }

    @Test
    fun catalogContainsAllTicketTypes() {
        assertEquals(6, catalog.size)
        assertEquals(
            setOf(
                RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID,
                RecruitmentTicketCatalog.SPECIALIST_INVITATION_ID,
                RecruitmentTicketCatalog.RARE_CONTRACT_TICKET_ID,
                RecruitmentTicketCatalog.EPIC_LIABILITY_WRIT_ID,
                RecruitmentTicketCatalog.VETERAN_TICKET_ID,
                RecruitmentTicketCatalog.BLANK_CONTRACT_ID,
            ),
            catalog.map { it.id }.toSet(),
        )

        val blank = RecruitmentTicketCatalog.byId.getValue(RecruitmentTicketCatalog.BLANK_CONTRACT_ID)
        assertFalse(blank.isRecruitmentTicket)
        assertEquals(5, RecruitmentTicketCatalog.recruitmentTickets.size)
    }

    @Test
    fun basicVoucherMatchesHeroGachaSinglePullForSeed() {
        val ticketId = RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID
        val noRoster = emptyList<Hero>()

        for (seed in 0..20) {
            val summon = HeroGacha.summon(1, seed).single()
            val resolved = RecruitmentTicketResolver.resolve(ticketId, seed, noRoster)

            assertFalse(resolved.ticket.isBlankContract)
            assertEquals(summon, requireNotNull(resolved.hero))
        }
    }

    @Test
    fun specialistInvitationRestrictsToConfiguredClasses() {
        val ticket = RecruitmentTicketCatalog.byId.getValue(RecruitmentTicketCatalog.SPECIALIST_INVITATION_ID)
        val expectedClasses = setOf(HeroClass.Bruiser, HeroClass.ApprenticeMage, HeroClass.BardAccountant)
        assertEquals(expectedClasses, ticket.specialistHeroClasses)

        for (seed in 0..40) {
            val resolved = RecruitmentTicketResolver.resolve(ticket, seed, roster)

            assertNotNull(resolved.hero)
            assertTrue(resolved.hero!!.heroClass in expectedClasses)
            assertEquals(HeroGacha.DUPLICATE_REPUTATION_REWARD, resolved.reputationReward)
            assertTrue(resolved.duplicate)
        }
    }

    @Test
    fun rareAndEpicTicketsGuaranteeMinimumRarity() {
        val rareTicket = RecruitmentTicketCatalog.byId.getValue(RecruitmentTicketCatalog.RARE_CONTRACT_TICKET_ID)
        val epicTicket = RecruitmentTicketCatalog.byId.getValue(RecruitmentTicketCatalog.EPIC_LIABILITY_WRIT_ID)

        for (seed in 0..50) {
            val rareResult = RecruitmentTicketResolver.resolve(rareTicket, seed, emptyList())
            val epicResult = RecruitmentTicketResolver.resolve(epicTicket, seed, emptyList())

            assertNotNull(rareResult.hero)
            assertNotNull(epicResult.hero)
            assertTrue(requireNotNull(rareResult.hero).rarity >= HeroRarity.Rare)
            assertTrue(requireNotNull(epicResult.hero).rarity >= HeroRarity.Epic)
        }
    }

    @Test
    fun veteranTicketEnforcesMinimumLevelWithoutMutatingCatalog() {
        val ticket = RecruitmentTicketCatalog.byId.getValue(RecruitmentTicketCatalog.VETERAN_TICKET_ID)
        var sawAdjustedHero = false

        for (seed in 0..500) {
            val resolved = RecruitmentTicketResolver.resolve(ticket, seed, emptyList())
            val resolvedHero = requireNotNull(resolved.hero)
            val originalHero = HeroCatalog.byId.getValue(resolvedHero.id)

            assertTrue(resolvedHero.level >= 4)
            if (originalHero.level < 4) {
                sawAdjustedHero = true
                assertEquals(4, resolvedHero.level)
            }
        }

        assertTrue(sawAdjustedHero)
    }

    @Test
    fun blankContractIsNonRecruit() {
        val ticket = RecruitmentTicketCatalog.byId.getValue(RecruitmentTicketCatalog.BLANK_CONTRACT_ID)
        val result = RecruitmentTicketResolver.resolve(ticket, seed = 42, roster = roster)

        assertEquals(ticket, result.ticket)
        assertEquals(null, result.hero)
        assertEquals(false, result.duplicate)
        assertEquals(0, result.reputationReward)
        assertTrue(result.ticket.isBlankContract)
    }

    @Test
    fun duplicateSummonGetsReputationRewardFromConstant() {
        for (ticket in RecruitmentTicketCatalog.recruitmentTickets) {
            val result = RecruitmentTicketResolver.resolve(ticket, seed = 8, roster = roster)

            assertTrue(result.duplicate)
            assertEquals(HeroGacha.DUPLICATE_REPUTATION_REWARD, result.reputationReward)
        }
    }

    @Test
    fun resolveIsDeterministicForTicketSeedAndRoster() {
        val seeds = listOf(1, 7, 19, 42)
        for (ticket in catalog) {
            for (seed in seeds) {
                val expected = RecruitmentTicketResolver.resolve(ticket, seed, roster)
                val same = RecruitmentTicketResolver.resolve(ticket, seed, roster)
                val alias = RecruitmentTicketRecruiter.resolve(ticket.id, seed, roster)

                assertEquals(expected, same)
                assertEquals(expected, alias)
            }
        }
    }

    @Test
    fun normalizedInventoryIncludesOnlyKnownTicketsAndDropsUnknownKeys() {
        val normalized = RecruitmentTicketCatalog.normalizedInventory(
            mapOf(
                RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID to 3,
                RecruitmentTicketCatalog.SPECIALIST_INVITATION_ID to -2,
                RecruitmentTicketCatalog.BLANK_CONTRACT_ID to 1,
                "ghost_ticket" to 99,
            ),
        )

        assertEquals(2, normalized.size)
        assertEquals(3, normalized[RecruitmentTicketCatalog.BASIC_HIRING_VOUCHER_ID])
        assertEquals(null, normalized[RecruitmentTicketCatalog.SPECIALIST_INVITATION_ID])
        assertEquals(1, normalized[RecruitmentTicketCatalog.BLANK_CONTRACT_ID])
        assertEquals(null, normalized[RecruitmentTicketCatalog.RARE_CONTRACT_TICKET_ID])
        assertEquals(null, normalized["ghost_ticket"])
    }
}