package com.ayoshy.badventurers.game

import kotlin.math.max

data class ExpeditionSpecialModifiers(
    val scoreBonus: Int = 0,
    val riskPenaltyReduction: Int = 0,
    val minimumRoll: Int = 0,
    val goldBonusPercent: Int = 0,
    val successLootBonus: Int = 0,
    val xpBonus: Int = 0,
    val preventsRidiculousFailure: Boolean = false,
) {
    operator fun plus(other: ExpeditionSpecialModifiers): ExpeditionSpecialModifiers = ExpeditionSpecialModifiers(
        scoreBonus = scoreBonus + other.scoreBonus,
        riskPenaltyReduction = riskPenaltyReduction + other.riskPenaltyReduction,
        minimumRoll = max(minimumRoll, other.minimumRoll),
        goldBonusPercent = goldBonusPercent + other.goldBonusPercent,
        successLootBonus = (successLootBonus + other.successLootBonus).coerceAtMost(2),
        xpBonus = xpBonus + other.xpBonus,
        preventsRidiculousFailure = preventsRidiculousFailure || other.preventsRidiculousFailure,
    )
}

object HeroSpecialCatalog {
    fun specialForHero(heroId: String): HeroSpecial = when (heroId) {
        "brugg" -> HeroSpecial.RamshackleCharge
        "mira" -> HeroSpecial.GlyphReader
        "nell" -> HeroSpecial.LightFingers
        "darrik" -> HeroSpecial.HumanWall
        "quill" -> HeroSpecial.AggressiveMinutes
        "orla" -> HeroSpecial.TerrainManual
        "pippa" -> HeroSpecial.UnstableLuck
        "vex" -> HeroSpecial.DirtyJackpot
        "bramble" -> HeroSpecial.FreshTrail
        "pax" -> HeroSpecial.CleanBlessing
        "sable" -> HeroSpecial.NoTrace
        "morrow" -> HeroSpecial.NecroLever
        "ledger" -> HeroSpecial.HostileAudit
        "paladin" -> HeroSpecial.UnbreakableOath
        "comptable" -> HeroSpecial.BalancedBooks
        "jardinier" -> HeroSpecial.GreenThumb
        "chevalier_de_la_mort" -> HeroSpecial.DeathDiscount
        "chef_cuistot" -> HeroSpecial.MoraleRations
        "expert_en_demolition" -> HeroSpecial.PlanBExplosives
        "elementaire_de_sel" -> HeroSpecial.PreservationSalt
        "troll_stupide" -> HeroSpecial.CreativeMisunderstanding
        else -> HeroSpecial.RamshackleCharge
    }

    fun modifiersFor(party: List<Hero>, quest: Quest): ExpeditionSpecialModifiers =
        party.fold(ExpeditionSpecialModifiers()) { modifiers, hero -> modifiers + modifierFor(hero, quest) }

    fun activeHeroes(party: List<Hero>, quest: Quest): List<Hero> =
        party.filter { modifierFor(it, quest) != ExpeditionSpecialModifiers() }

    fun isLootRecoverySpecial(special: HeroSpecial): Boolean = special in lootRecoverySpecials

    private val lootRecoverySpecials = setOf(
        HeroSpecial.LightFingers,
        HeroSpecial.DirtyJackpot,
        HeroSpecial.PreservationSalt,
    )

    private fun modifierFor(hero: Hero, quest: Quest): ExpeditionSpecialModifiers = when (hero.special) {
        HeroSpecial.RamshackleCharge -> if (quest.hasAny(QuestTag.Breach, QuestTag.Wall, QuestTag.Obstacle, QuestTag.Simple)) {
            ExpeditionSpecialModifiers(scoreBonus = 20)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.GlyphReader -> if (quest.hasAny(QuestTag.Magic, QuestTag.Ancient)) {
            ExpeditionSpecialModifiers(scoreBonus = 12, minimumRoll = 30)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.LightFingers -> if (quest.hasAny(QuestTag.Stealth, QuestTag.Heist)) {
            ExpeditionSpecialModifiers(scoreBonus = 12, successLootBonus = 1)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.HumanWall -> if (quest.hasAny(QuestTag.Escort, QuestTag.Guard, QuestTag.Siege, QuestTag.Camp)) {
            ExpeditionSpecialModifiers(riskPenaltyReduction = 8)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.AggressiveMinutes -> if (quest.hasAny(QuestTag.Paperwork, QuestTag.Contract)) {
            ExpeditionSpecialModifiers(scoreBonus = 10, goldBonusPercent = 15)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.TerrainManual -> if (quest.hasAny(QuestTag.Trap, QuestTag.Exploration)) {
            ExpeditionSpecialModifiers(scoreBonus = 16, riskPenaltyReduction = 4)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.UnstableLuck -> if (quest.hasAny(QuestTag.Magic, QuestTag.Ancient, QuestTag.Heist)) {
            ExpeditionSpecialModifiers(minimumRoll = 40, successLootBonus = 1)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.DirtyJackpot -> if (quest.hasAny(QuestTag.Heist, QuestTag.Stealth)) {
            ExpeditionSpecialModifiers(scoreBonus = 10, goldBonusPercent = 10, successLootBonus = 1)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.FreshTrail -> if (quest.hasAny(QuestTag.Wilderness, QuestTag.Hunt, QuestTag.Exploration)) {
            ExpeditionSpecialModifiers(scoreBonus = 22)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.CleanBlessing -> if (quest.hasAny(QuestTag.Curse, QuestTag.Poison, QuestTag.Undead, QuestTag.Holy, QuestTag.Rot)) {
            ExpeditionSpecialModifiers(riskPenaltyReduction = 8, preventsRidiculousFailure = true)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.NoTrace -> if (quest.hasAny(QuestTag.Stealth, QuestTag.Heist)) {
            ExpeditionSpecialModifiers(scoreBonus = 14, riskPenaltyReduction = 8)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.NecroLever -> if (quest.hasAny(QuestTag.Undead, QuestTag.Curse, QuestTag.Debt)) {
            ExpeditionSpecialModifiers(scoreBonus = 24, xpBonus = 4)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.HostileAudit -> if (quest.hasAny(QuestTag.Paperwork, QuestTag.Contract, QuestTag.Debt)) {
            ExpeditionSpecialModifiers(scoreBonus = 15, goldBonusPercent = 20)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.UnbreakableOath -> if (quest.hasAny(QuestTag.Holy, QuestTag.Curse, QuestTag.Escort, QuestTag.Guard)) {
            ExpeditionSpecialModifiers(riskPenaltyReduction = 8, preventsRidiculousFailure = true)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.BalancedBooks -> if (quest.hasAny(QuestTag.Paperwork, QuestTag.Contract, QuestTag.Debt)) {
            ExpeditionSpecialModifiers(minimumRoll = 25, goldBonusPercent = 10)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.GreenThumb -> if (quest.hasAny(QuestTag.Wilderness, QuestTag.Rot, QuestTag.Swamp)) {
            ExpeditionSpecialModifiers(scoreBonus = 18, riskPenaltyReduction = 4)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.DeathDiscount -> if (quest.hasAny(QuestTag.Undead, QuestTag.Curse, QuestTag.Debt)) {
            ExpeditionSpecialModifiers(scoreBonus = 20, preventsRidiculousFailure = true)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.MoraleRations -> if (quest.hasAny(QuestTag.LongQuest, QuestTag.Siege, QuestTag.Camp)) {
            ExpeditionSpecialModifiers(riskPenaltyReduction = 8, xpBonus = 4)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.PlanBExplosives -> if (quest.hasAny(QuestTag.Obstacle, QuestTag.Wall, QuestTag.Collapse, QuestTag.Breach)) {
            ExpeditionSpecialModifiers(scoreBonus = 30)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.PreservationSalt -> if (quest.hasAny(QuestTag.Swamp, QuestTag.Poison, QuestTag.Rot)) {
            ExpeditionSpecialModifiers(scoreBonus = 20, successLootBonus = 1)
        } else ExpeditionSpecialModifiers()

        HeroSpecial.CreativeMisunderstanding -> when {
            quest.hasAny(QuestTag.Simple, QuestTag.Breach, QuestTag.Wall, QuestTag.Obstacle) -> ExpeditionSpecialModifiers(scoreBonus = 30)
            quest.hasAny(QuestTag.Paperwork, QuestTag.Contract) -> ExpeditionSpecialModifiers(scoreBonus = -20)
            else -> ExpeditionSpecialModifiers()
        }
    }
}
