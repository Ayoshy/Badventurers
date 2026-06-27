package com.ayoshy.badventurers.game

data class GuildReputationTier(
    val id: String,
    val rank: Int,
    val badge: String,
    val minReputation: Int,
)

data class GuildReputationProgress(
    val currentTier: GuildReputationTier,
    val nextTier: GuildReputationTier?,
    val reputation: Int,
    val pointsIntoTier: Int,
    val pointsToNextTier: Int,
    val progressFraction: Float,
) {
    val atMaxTier: Boolean get() = nextTier == null
}

object GuildReputationLadder {
    val tiers: List<GuildReputationTier> = listOf(
        GuildReputationTier(id = "folding_table_charter", rank = 1, badge = "I", minReputation = 0),
        GuildReputationTier(id = "back_room_permit", rank = 2, badge = "II", minReputation = 3),
        GuildReputationTier(id = "notice_board_office", rank = 3, badge = "III", minReputation = 8),
        GuildReputationTier(id = "licensed_guild_desk", rank = 4, badge = "IV", minReputation = 15),
        GuildReputationTier(id = "regional_liability_hall", rank = 5, badge = "V", minReputation = 30),
        GuildReputationTier(id = "crown_adjacent_franchise", rank = 6, badge = "VI", minReputation = 48),
        GuildReputationTier(id = "sideways_tower_chapter", rank = 7, badge = "VII", minReputation = 60),
    )

    init {
        require(tiers.isNotEmpty()) { "Guild reputation ladder needs at least one tier." }
        require(tiers.first().minReputation == 0) { "First guild reputation tier must start at 0." }
        require(tiers.zipWithNext().all { (left, right) -> left.minReputation < right.minReputation }) {
            "Guild reputation tiers must have increasing thresholds."
        }
        require(tiers.map { it.id }.distinct().size == tiers.size) { "Guild reputation tier ids must be unique." }
        require(tiers.map { it.rank }.distinct().size == tiers.size) { "Guild reputation ranks must be unique." }
    }

    fun tierFor(reputation: Int): GuildReputationTier {
        val safeReputation = reputation.coerceAtLeast(0)
        return tiers.last { tier -> safeReputation >= tier.minReputation }
    }

    fun nextTierFor(reputation: Int): GuildReputationTier? {
        val safeReputation = reputation.coerceAtLeast(0)
        return tiers.firstOrNull { tier -> safeReputation < tier.minReputation }
    }

    fun progressFor(reputation: Int): GuildReputationProgress {
        val safeReputation = reputation.coerceAtLeast(0)
        val current = tierFor(safeReputation)
        val next = nextTierFor(safeReputation)
        val tierSpan = next?.let { it.minReputation - current.minReputation }?.coerceAtLeast(1)
        val pointsIntoTier = safeReputation - current.minReputation
        val pointsToNext = next?.let { (it.minReputation - safeReputation).coerceAtLeast(0) } ?: 0
        val progress = tierSpan?.let { (pointsIntoTier.toFloat() / it.toFloat()).coerceIn(0f, 1f) } ?: 1f
        return GuildReputationProgress(
            currentTier = current,
            nextTier = next,
            reputation = safeReputation,
            pointsIntoTier = pointsIntoTier,
            pointsToNextTier = pointsToNext,
            progressFraction = progress,
        )
    }
}

fun PlaySessionState.guildReputationProgress(): GuildReputationProgress =
    GuildReputationLadder.progressFor(reputation)

fun PlaySessionState.guildReputationRank(): Int =
    GuildReputationLadder.tierFor(reputation).rank