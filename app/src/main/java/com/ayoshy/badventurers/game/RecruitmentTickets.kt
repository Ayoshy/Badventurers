package com.ayoshy.badventurers.game

import kotlin.random.Random

data class RecruitmentTicketMetadata(
    val id: String,
    val title: String,
    val summary: String,
    val guaranteedMinimumRarity: HeroRarity? = null,
    val minimumHeroLevel: Int = 1,
    val specialistHeroClasses: Set<HeroClass> = emptySet(),
    val isRecruitmentTicket: Boolean = true,
) {
    val isBlankContract: Boolean
        get() = !isRecruitmentTicket
}

data class RecruitmentTicketResult(
    val ticket: RecruitmentTicketMetadata,
    val hero: Hero?,
    val duplicate: Boolean,
    val reputationReward: Int,
)

object RecruitmentTicketCatalog {
    const val BASIC_HIRING_VOUCHER_ID = "basic_hiring_voucher"
    const val SPECIALIST_INVITATION_ID = "specialist_invitation"
    const val RARE_CONTRACT_TICKET_ID = "rare_contract_ticket"
    const val EPIC_LIABILITY_WRIT_ID = "epic_liability_writ"
    const val VETERAN_TICKET_ID = "veteran_ticket"
    const val BLANK_CONTRACT_ID = "blank_contract"

    val tickets = listOf(
        RecruitmentTicketMetadata(
            id = BASIC_HIRING_VOUCHER_ID,
            title = "Basic Hiring Voucher",
            summary = "A normal recruitment pull from the current hero catalog.",
        ),
        RecruitmentTicketMetadata(
            id = SPECIALIST_INVITATION_ID,
            title = "Specialist Invitation",
            summary = "Summons from a specialist-focused class pool.",
            specialistHeroClasses = setOf(HeroClass.Bruiser, HeroClass.ApprenticeMage, HeroClass.BardAccountant),
        ),
        RecruitmentTicketMetadata(
            id = RARE_CONTRACT_TICKET_ID,
            title = "Rare Contract Ticket",
            summary = "Guarantees a Rare or better hero.",
            guaranteedMinimumRarity = HeroRarity.Rare,
        ),
        RecruitmentTicketMetadata(
            id = EPIC_LIABILITY_WRIT_ID,
            title = "Epic Liability Writ",
            summary = "Guarantees an Epic or better hero.",
            guaranteedMinimumRarity = HeroRarity.Epic,
        ),
        RecruitmentTicketMetadata(
            id = VETERAN_TICKET_ID,
            title = "Veteran Ticket",
            summary = "A normal pull, but recruits at Veteran tier level minimum 4.",
            minimumHeroLevel = 4,
        ),
        RecruitmentTicketMetadata(
            id = BLANK_CONTRACT_ID,
            title = "Blank Contract",
            summary = "No recruitment. Useful for emptying ticket stacks without changing your roster.",
            isRecruitmentTicket = false,
        ),
    )

    val byId: Map<String, RecruitmentTicketMetadata> = tickets.associateBy { it.id }

    val recruitmentTickets: List<RecruitmentTicketMetadata>
        get() = tickets.filter { it.isRecruitmentTicket }

    fun normalizedInventory(inventory: Map<String, Int> = emptyMap()): Map<String, Int> = tickets
        .mapNotNull { ticket ->
            val count = inventory[ticket.id]?.coerceAtLeast(0) ?: 0
            if (count > 0) ticket.id to count else null
        }
        .toMap()

    fun addToInventory(
        current: Map<String, Int>,
        additions: Map<String, Int>,
    ): Map<String, Int> {
        val next = normalizedInventory(current).toMutableMap()
        additions.forEach { (ticketId, count) ->
            if (ticketId in byId && count > 0) {
                next[ticketId] = (next[ticketId] ?: 0) + count
            }
        }
        return normalizedInventory(next)
    }

    fun consumeFromInventory(current: Map<String, Int>, ticketId: String): Map<String, Int>? {
        if (ticketId !in byId) return null
        val next = normalizedInventory(current).toMutableMap()
        val count = next[ticketId] ?: return null
        if (count <= 0) return null
        if (count == 1) {
            next.remove(ticketId)
        } else {
            next[ticketId] = count - 1
        }
        return normalizedInventory(next)
    }
}

object RecruitmentTicketResolver {
    fun resolve(
        ticketId: String,
        seed: Int,
        roster: List<Hero>,
    ): RecruitmentTicketResult = resolve(
        ticket = RecruitmentTicketCatalog.byId[ticketId]
            ?: error("Unknown ticket id: $ticketId"),
        seed = seed,
        roster = roster,
    )

    fun resolve(
        ticket: RecruitmentTicketMetadata,
        seed: Int,
        roster: List<Hero>,
    ): RecruitmentTicketResult {
        if (!ticket.isRecruitmentTicket) {
            return RecruitmentTicketResult(
                ticket = ticket,
                hero = null,
                duplicate = false,
                reputationReward = 0,
            )
        }

        val random = Random(seed)
        val pool = buildPool(ticket)
        val pulled = summon(pool, random, ticket.guaranteedMinimumRarity)
        val hero = if (pulled.level < ticket.minimumHeroLevel) {
            pulled.toHero().copy(level = ticket.minimumHeroLevel)
        } else {
            pulled.toHero()
        }
        val duplicate = roster.any { it.id == hero.id }
        val reputationReward = if (duplicate) HeroGacha.DUPLICATE_REPUTATION_REWARD else 0

        return RecruitmentTicketResult(
            ticket = ticket,
            hero = hero,
            duplicate = duplicate,
            reputationReward = reputationReward,
        )
    }

    private fun buildPool(ticket: RecruitmentTicketMetadata): List<HeroDefinition> {
        val byClass = if (ticket.specialistHeroClasses.isEmpty()) {
            HeroCatalog.heroes
        } else {
            HeroCatalog.heroes.filter { it.heroClass in ticket.specialistHeroClasses }
        }

        return if (ticket.guaranteedMinimumRarity == null) {
            byClass
        } else {
            val filtered = byClass.filter { it.rarity >= ticket.guaranteedMinimumRarity }
            if (filtered.isNotEmpty()) filtered else byClass
        }
    }

    private fun summon(
        pool: List<HeroDefinition>,
        random: Random,
        minimumRarity: HeroRarity?,
    ): HeroDefinition {
        if (pool.isEmpty()) return HeroCatalog.heroes.random(random)

        val byRarity = pool.groupBy { it.rarity }
        val weightedRarities = if (minimumRarity == null) {
            HeroGacha.rarityWeights
        } else {
            HeroGacha.rarityWeights.filter { (rarity, _) -> rarity >= minimumRarity }
        }
        val weightedAndAvailable = weightedRarities.filter { (rarity, _) -> byRarity[rarity]?.isNotEmpty() == true }
        if (weightedAndAvailable.isEmpty()) return random.nextEnum(pool)

        val rarity = random.nextWeightedEnum(weightedAndAvailable)
        val options = byRarity[rarity] ?: return random.nextEnum(pool)
        return random.nextEnum(options)
    }

    private fun <T> Random.nextEnum(values: List<T>): T = values[nextInt(values.size)]

    private fun <T> Random.nextWeightedEnum(values: List<Pair<T, Int>>): T {
        val totalWeight = values.sumOf { it.second }
        if (totalWeight <= 0) return values.last().first

        var roll = nextInt(totalWeight)
        for ((value, weight) in values) {
            if (roll < weight) return value
            roll -= weight
        }

        return values.last().first
    }
}

object RecruitmentTicketRecruiter {
    fun resolve(
        ticketId: String,
        seed: Int,
        roster: List<Hero>,
    ): RecruitmentTicketResult = RecruitmentTicketResolver.resolve(
        ticketId = ticketId,
        seed = seed,
        roster = roster,
    )
}