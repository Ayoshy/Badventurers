package com.ayoshy.badventurers.game



import kotlin.math.min

import kotlin.random.Random



private const val PASSIVE_INCIDENT_SHORT_OFFLINE_DELAY_SECONDS = 10 * 60L

private const val PASSIVE_INCIDENT_ONE_HOUR_SECONDS = 60 * 60L

private const val PASSIVE_INCIDENT_TWO_HOURS_SECONDS = 2 * PASSIVE_INCIDENT_ONE_HOUR_SECONDS

private const val PASSIVE_INCIDENT_SECOND_CHANCE_PERCENT = 45

private const val PASSIVE_INCIDENT_MAX_GOLD_REWARD = 8





data class PassiveIncidentReward(

    val gold: Int = 0,

    val reputation: Int = 0,

    val specialContracts: Int = 0,

) {

    init {

        require(gold in 0..PASSIVE_INCIDENT_MAX_GOLD_REWARD)

        require(reputation in 0..1)

        require(specialContracts in 0..1)

    }

}



data class PassiveIncident(

    val id: String,

    val text: String,

    val reward: PassiveIncidentReward = PassiveIncidentReward(),

)



private data class PassiveIncidentBlueprint(

    val id: String,

    val text: String,

    val reward: PassiveIncidentReward,

)



object PassiveIncidentGenerator {

    fun generate(session: PlaySessionState, nowMillis: Long): List<PassiveIncident> {

        val run = session.expedition ?: return emptyList()

        if (run.result == null) return emptyList()



        val elapsedSeconds = ((nowMillis - run.startedAtMillis).coerceAtLeast(0L) / 1000L)

        val random = Random(session.passiveIncidentSeed(run, nowMillis))

        val targetCount = incidentCount(session, elapsedSeconds, random)

        if (targetCount == 0) return emptyList()



        val candidates = incidentCandidates(session = session, run = run, random = random)

        if (candidates.isEmpty()) return emptyList()



        return candidates

            .shuffled(random)

            .take(min(targetCount, candidates.size))

            .map { candidate ->

                PassiveIncident(

                    id = candidate.id,

                    text = candidate.text,

                    reward = candidate.reward,

                )

            }

    }



    private fun incidentCount(session: PlaySessionState, elapsedSeconds: Long, random: Random): Int {

        if (elapsedSeconds < PASSIVE_INCIDENT_SHORT_OFFLINE_DELAY_SECONDS) return 0



        val hasCoreCrew = session.normalizedCoreCrewHeroIds().isNotEmpty()
        val hasPassiveScouting = ScoutTableIntel.behavior(session.scoutTableLevel).enablesPassiveScoutingIncidents

        if (elapsedSeconds < PASSIVE_INCIDENT_ONE_HOUR_SECONDS) return 0



        var count = if (hasCoreCrew || hasPassiveScouting) 1 else 0

        if (elapsedSeconds >= PASSIVE_INCIDENT_TWO_HOURS_SECONDS && random.nextInt(100) < PASSIVE_INCIDENT_SECOND_CHANCE_PERCENT) {

            count += 1

        }



        return min(2, count)

    }



    private fun incidentCandidates(

        session: PlaySessionState,

        run: ExpeditionRun,

        random: Random,

    ): List<PassiveIncidentBlueprint> {

        val runQuest = run.quest

        val candidates = mutableListOf<PassiveIncidentBlueprint>()



        addCoreCrewIncident(session, random, candidates)

        addFacilityIncident(session, random, candidates)

        addQuestRegionIncident(session, runQuest, random, candidates)



        if (candidates.isEmpty()) {

            addGuildFallbackIncident(session, random, candidates)

        }



        return candidates

    }



    private fun addCoreCrewIncident(

        session: PlaySessionState,

        random: Random,

        candidates: MutableList<PassiveIncidentBlueprint>,

    ) {

        val crewHeroes = session

            .normalizedCoreCrewHeroIds()

            .mapNotNull { heroId -> session.heroes.firstOrNull { it.id == heroId } }



        if (crewHeroes.isEmpty()) return



        val hero = crewHeroes[random.nextInt(crewHeroes.size)]

        val templates = listOf(

            "%s filed extra ledgers and found a silver receipt of %s",

            "%s stayed late and stabilized the route through %s",

            "%s traded gossip for quiet progress in %s",

            "%s caught an odd discrepancy in %s and kept the guild stable",

        )

        val target = random.nextInt(4)



        candidates += PassiveIncidentBlueprint(

            id = "crew-${hero.id}",

            text = templates[target].format(hero.name, guildAreaLabel(random, session)),

            reward = modestReward(random),

        )

    }



    private fun addFacilityIncident(

        session: PlaySessionState,

        random: Random,

        candidates: MutableList<PassiveIncidentBlueprint>,

    ) {

        val facilityTemplates = mutableListOf<Triple<String, String, Int>>()



        if (session.noticeBoardLevel > 1) {

            facilityTemplates += Triple(

                "facility-notice-${session.noticeBoardLevel}",

                "Notice Board was up at level %d with cleaner forms",

                session.noticeBoardLevel,

            )

        }

        if (session.trainingYardLevel > 1) {

            facilityTemplates += Triple(

                "facility-training-${session.trainingYardLevel}",

                "Training Yard shifts were rerouted through %d stations",

                session.trainingYardLevel,

            )

        }

        if (session.bunkRoomLevel > 1) {

            facilityTemplates += Triple(

                "facility-bunk-${session.bunkRoomLevel}",

                "Bunk Room opened a quiet watch room on deck %d",

                session.bunkRoomLevel,

            )

        }




        val scoutTableLevel = session.facilityLevel(GuildFacility.ScoutTable)
        if (scoutTableLevel > 0) {
            facilityTemplates += Triple(
                "facility-scout-${scoutTableLevel}",
                "Scout Table marked %d routes as safe-ish before anyone tested them",
                scoutTableLevel,
            )
        }
        val accountantLevel = session.facilityLevel(GuildFacility.AccountantOffice)

        if (accountantLevel > 0) {

            facilityTemplates += Triple(

                "facility-accountant-${accountantLevel}",

                "Accountant Office filed a temporary tax correction and found %d coin-credits",

                accountantLevel * 2,

            )

        }



        if (facilityTemplates.isEmpty()) return



        val selected = facilityTemplates[random.nextInt(facilityTemplates.size)]

        candidates += PassiveIncidentBlueprint(

            id = selected.first,

            text = if (selected.first.startsWith("facility-accountant")) {

                selected.second.format(selected.third)

            } else {

                selected.second.format(selected.third)

            },

            reward = modestReward(random),

        )

    }



    private fun addQuestRegionIncident(

        session: PlaySessionState,

        runQuest: Quest,

        random: Random,

        candidates: MutableList<PassiveIncidentBlueprint>,

    ) {

        val availableQuests = SeedGame.quests

            .asSequence()

            .filter { it.id != runQuest.id }

            .filter(session::isQuestUnlocked)

            .toList()

        if (availableQuests.isEmpty()) return



        val selectedQuest = availableQuests[random.nextInt(availableQuests.size)]

        val tag = selectedQuest.tags.getOrNull(random.nextInt(selectedQuest.tags.size.coerceAtLeast(1)))
            ?: QuestTag.Simple

        val route = if (selectedQuest.title == selectedQuest.id) selectedQuest.id else selectedQuest.title



        candidates += PassiveIncidentBlueprint(

            id = "quest-${selectedQuest.id}-${tag.name}",

            text = "%s found a quiet way through %s while checking %s routes".format(

                route,

                routeTagLabel(tag),

                route,

            ),

            reward = modestReward(random),

        )

    }



    private fun addGuildFallbackIncident(

        session: PlaySessionState,

        random: Random,

        candidates: MutableList<PassiveIncidentBlueprint>,

    ) {

        val facility = when {

            session.noticeBoardLevel > 1 -> "Notice Board"

            session.trainingYardLevel > 1 -> "Training Yard"

            session.bunkRoomLevel > 1 -> "Bunk Room"

            session.scoutTableLevel > 0 -> "Scout Table"

            else -> "guild office"

        }

        val templates = listOf(

            "The %s logged a calm shift and recovered %s",

            "Guild watch found %s near the %s counter",

            "%s had a quiet hour and kept the books balanced",

        )

        val selectedTemplate = templates[random.nextInt(templates.size)]



        candidates += PassiveIncidentBlueprint(

            id = "guild-${facility.lowercase()}",

            text = selectedTemplate.format(facility, guildAreaLabel(random, session)),

            reward = modestReward(random),

        )

    }



    private fun guildAreaLabel(random: Random, session: PlaySessionState): String {

        val locations = listOf(

            "the outer ledgers",

            "a quiet corridor",

            "an untidy pantry room",

            "the hall records shelf",

            "a map rack near the stairs",

        ) +
            (if (session.noticeBoardLevel > 1) listOf("the Notice Board annex") else emptyList()) +
            (if (session.scoutTableLevel > 0) listOf("the Scout Table map pile") else emptyList())

        return locations[random.nextInt(locations.size)]

    }



    private fun routeTagLabel(tag: QuestTag): String = when (tag) {

        QuestTag.Cave -> "cave routes"

        QuestTag.Bandit -> "bandit routes"

        QuestTag.Breach -> "breach points"

        QuestTag.Camp -> "camp trails"

        QuestTag.Collapse -> "collapsed passages"

        QuestTag.Contract -> "contract lanes"

        QuestTag.Curse -> "curse-marked routes"

        QuestTag.Debt -> "debt lanes"

        QuestTag.Escort -> "escort routes"

        QuestTag.Exploration -> "exploration paths"

        QuestTag.Guard -> "guard routes"

        QuestTag.Heist -> "heist routes"

        QuestTag.Holy -> "holy routes"

        QuestTag.Hunt -> "hunting paths"

        QuestTag.LongQuest -> "extended routes"

        QuestTag.Magic -> "arcane wards"

        QuestTag.Obstacle -> "obstacle lanes"

        QuestTag.Paperwork -> "paperwork lanes"

        QuestTag.Poison -> "poisoned byways"

        QuestTag.Rot -> "rotted paths"

        QuestTag.Siege -> "siege routes"

        QuestTag.Stealth -> "stealth corridors"

        QuestTag.Swamp -> "swamp lanes"

        QuestTag.Trap -> "trap trails"

        QuestTag.Undead -> "undead corridors"

        QuestTag.Urban -> "urban routes"

        QuestTag.Wall -> "wall routes"

        QuestTag.Wilderness -> "wilderness corridors"

        QuestTag.Ancient -> "ancient roads"

        QuestTag.Simple -> "simple paths"

    }



    private fun modestReward(random: Random) = PassiveIncidentReward(

        gold = random.nextInt(PASSIVE_INCIDENT_MAX_GOLD_REWARD + 1),

        reputation = if (random.nextInt(6) == 0) 1 else 0,

        specialContracts = if (random.nextInt(10) == 0) 1 else 0,

    )



    private fun PlaySessionState.passiveIncidentSeed(run: ExpeditionRun, nowMillis: Long): Int {

        var seed = run.quest.id.hashCode()

        seed = seed * 31 + mixedLongToInt(run.startedAtMillis)

        seed = seed * 31 + mixedLongToInt(run.endsAtMillis)

        seed = seed * 31 + mixedLongToInt(nowMillis)

        seed = seed * 31 + completedQuestCount

        seed = seed * 31 + gold

        seed = seed * 31 + noticeBoardLevel

        seed = seed * 31 + trainingYardLevel

        seed = seed * 31 + bunkRoomLevel
        seed = seed * 31 + scoutTableLevel
        seed = seed * 31 + normalizedCoreCrewHeroIds().size

        seed = seed * 31 + normalizedCoreCrewHeroIds().hashCode()

        return seed

    }



    private fun mixedLongToInt(value: Long): Int = (value xor (value ushr 32)).toInt()

}