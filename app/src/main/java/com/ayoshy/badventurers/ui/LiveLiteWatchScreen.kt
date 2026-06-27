package com.ayoshy.badventurers.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayoshy.badventurers.R
import com.ayoshy.badventurers.game.ExpeditionPlanCatalog
import com.ayoshy.badventurers.game.LiveLiteAssetRef
import com.ayoshy.badventurers.game.LiveLiteExpedition
import com.ayoshy.badventurers.game.LiveLiteHeroPose
import com.ayoshy.badventurers.game.LiveLiteInterventionDefinition
import com.ayoshy.badventurers.game.LiveLiteInterventionEffect
import com.ayoshy.badventurers.game.LiveLiteInterventionKind
import com.ayoshy.badventurers.game.LiveLiteInterventionPreview
import com.ayoshy.badventurers.game.LiveLiteWatchBeat
import com.ayoshy.badventurers.game.LiveLiteWatchBeatKind
import com.ayoshy.badventurers.game.LiveLiteWatchModel
import com.ayoshy.badventurers.game.PlayPhase
import com.ayoshy.badventurers.game.PlaySessionState

@Composable
internal fun LiveLiteWatchScreen(
    session: PlaySessionState,
    nowMillis: Long,
    onBack: () -> Unit,
    onViewReport: () -> Unit,
) {
    val run = session.expedition
    if (run == null) {
        ScreenScaffold(
            title = stringResource(R.string.live_lite_title),
            status = phaseStatus(session.phase),
        ) {
            DarkPanel(
                title = stringResource(R.string.live_lite_no_run_title),
                body = stringResource(R.string.live_lite_no_run_body),
            )
            ActionRow(primaryLabel = stringResource(R.string.guild_home_title), onPrimary = onBack)
        }
        return
    }

    val model = LiveLiteExpedition.buildWatchModel(
        run = run,
        party = session.heroes,
        equipment = session.equippedLoot,
        facilityPowerBonus = session.expeditionFacilityPowerBonus(run.planId),
    )
    var selectedInterventionKeys by rememberSaveable(run.startedAtMillis, run.quest.id, run.planId) {
        mutableStateOf(emptyList<String>())
    }
    val selectedKeySet = selectedInterventionKeys.toSet()
    val preview = LiveLiteExpedition.previewInterventions(model, selectedKeySet)
    val progress = session.progress(nowMillis).toFloat()
    val status = when (session.phase) {
        PlayPhase.Running -> stringResource(R.string.live_lite_status_running, remainingSeconds(session, nowMillis).toInt().coerceAtLeast(0))
        PlayPhase.ResultReady -> stringResource(R.string.live_lite_status_ready)
        PlayPhase.Idle -> phaseStatus(session.phase)
    }

    ScreenScaffold(title = stringResource(R.string.live_lite_title), status = status) {
        LiveLiteStageScene(
            stage = model.stage,
            progress = progress,
            heroCount = model.heroPoses.size,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        LiveLiteForecastPanel(
            session = session,
            model = model,
            preview = preview,
            progress = progress,
        )
        LiveLiteInterventionPanel(
            session = session,
            model = model,
            selectedKeys = selectedKeySet,
            onToggle = { key ->
                selectedInterventionKeys = if (key in selectedKeySet) {
                    emptyList()
                } else {
                    listOf(key)
                }
            },
        )
        LiveLiteBeatPanel(model = model)
        ActionRow(
            primaryLabel = stringResource(R.string.guild_home_title),
            secondaryLabel = if (session.phase == PlayPhase.ResultReady) stringResource(R.string.view_report_action) else null,
            onPrimary = onBack,
            onSecondary = if (session.phase == PlayPhase.ResultReady) onViewReport else null,
        )
    }
}

@Composable
private fun LiveLiteForecastPanel(
    session: PlaySessionState,
    model: LiveLiteWatchModel,
    preview: LiveLiteInterventionPreview,
    progress: Float,
) {
    val plan = ExpeditionPlanCatalog.planForQuest(model.planId, session.expedition?.quest ?: return)
    DarkPanel(
        title = stringResource(R.string.live_lite_stage_title, questTitle(session.expedition.quest)),
        body = stringResource(
            R.string.live_lite_stage_body,
            expeditionPlanTitle(plan),
            model.stage.theme,
        ),
    ) {
        Spacer(Modifier.height(8.dp))
        ProgressBar(progress = progress)
        Spacer(Modifier.height(7.dp))
        Text(
            text = stringResource(
                R.string.live_lite_odds_body,
                preview.baseSuccessChancePercent,
                preview.adjustedSuccessChancePercent,
                preview.basePartyPower,
                preview.adjustedPartyPower,
                preview.baseTargetPower,
                preview.adjustedTargetPower,
                preview.baseDurationSeconds,
                preview.adjustedDurationSeconds,
            ),
            color = Color(0xFFFFE4A6),
            fontSize = 12.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun LiveLiteInterventionPanel(
    session: PlaySessionState,
    model: LiveLiteWatchModel,
    selectedKeys: Set<String>,
    onToggle: (String) -> Unit,
) {
    DarkPanel(
        title = stringResource(R.string.live_lite_interventions_title),
        body = stringResource(R.string.live_lite_interventions_body),
    ) {
        Spacer(Modifier.height(8.dp))
        model.interventions.forEach { intervention ->
            LiveLiteInterventionRow(
                intervention = intervention,
                supplies = session.supplies,
                selected = intervention.kind.assetKey in selectedKeys,
                onToggle = { onToggle(intervention.kind.assetKey) },
            )
        }
    }
}

@Composable
private fun LiveLiteInterventionRow(
    intervention: LiveLiteInterventionDefinition,
    supplies: Int,
    selected: Boolean,
    onToggle: () -> Unit,
) {
    val hasSupply = intervention.kind != LiveLiteInterventionKind.SpendSupply || supplies > 0
    val enabled = intervention.enabled && hasSupply
    val shape = RoundedCornerShape(8.dp)
    val borderColor = when {
        selected -> Color(0xFFFFD36D)
        enabled -> Color(0x5534B08A)
        else -> Color(0x33FFF1BF)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 7.dp)
            .clip(shape)
            .clickable(enabled = enabled, onClick = onToggle),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFF2B3728) else Color(0xAA171813)),
        border = BorderStroke(1.dp, borderColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(9.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LiveLiteAssetGlyph(asset = intervention.asset, modifier = Modifier.size(42.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = liveLiteInterventionLabel(intervention),
                        color = Color(0xFFFFF0BD),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = when {
                            !enabled -> stringResource(R.string.live_lite_intervention_unavailable)
                            selected -> stringResource(R.string.live_lite_intervention_selected)
                            else -> stringResource(R.string.live_lite_intervention_ready)
                        },
                        color = if (selected) Color(0xFFFFD36D) else Color(0xFFD7C891),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
                Text(
                    text = liveLiteInterventionDetail(intervention, supplies),
                    color = Color(0xFFD9C99A),
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun LiveLiteBeatPanel(model: LiveLiteWatchModel) {
    DarkPanel(
        title = stringResource(R.string.live_lite_beats_title),
        body = stringResource(R.string.live_lite_beats_body),
    ) {
        Spacer(Modifier.height(8.dp))
        model.beats.take(7).forEach { beat ->
            LiveLiteBeatRow(beat = beat, model = model)
        }
    }
}

@Composable
private fun LiveLiteBeatRow(beat: LiveLiteWatchBeat, model: LiveLiteWatchModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LiveLiteAssetGlyph(asset = beat.asset, modifier = Modifier.size(38.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = liveLiteBeatTitle(beat, model),
                color = Color(0xFFFFF0BD),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = liveLiteBeatDetail(beat),
                color = Color(0xFFD9C99A),
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = stringResource(R.string.live_lite_beat_second_value, beat.secondOffset),
            color = Color(0xFFD7C891),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
        )
    }
}

@Composable
private fun liveLiteInterventionLabel(intervention: LiveLiteInterventionDefinition): String = when (intervention.kind) {
    LiveLiteInterventionKind.SpendSupply -> stringResource(R.string.live_lite_intervention_spend_supply)
    LiveLiteInterventionKind.SaferRoute -> stringResource(R.string.live_lite_intervention_safer_route)
    LiveLiteInterventionKind.PushForLoot -> stringResource(R.string.live_lite_intervention_push_for_loot)
    LiveLiteInterventionKind.EncourageHero -> stringResource(
        R.string.live_lite_intervention_encourage_hero,
        intervention.targetHeroName ?: stringResource(R.string.live_lite_intervention_someone),
    )
    LiveLiteInterventionKind.HeroTrick -> stringResource(
        R.string.live_lite_intervention_hero_trick,
        intervention.targetHeroName ?: stringResource(R.string.live_lite_intervention_someone),
    )
}

@Composable
private fun liveLiteInterventionDetail(intervention: LiveLiteInterventionDefinition, supplies: Int): String {
    if (intervention.kind == LiveLiteInterventionKind.SpendSupply && supplies <= 0) {
        return stringResource(R.string.live_lite_intervention_need_supply)
    }
    if (!intervention.hasTradeoff) {
        return stringResource(R.string.live_lite_intervention_no_effect)
    }
    return stringResource(
        R.string.live_lite_intervention_tradeoff,
        liveLiteEffectSummary(intervention.upside),
        liveLiteEffectSummary(intervention.downside),
    )
}

@Composable
private fun liveLiteEffectSummary(effect: LiveLiteInterventionEffect): String = listOfNotNull(
    stringResource(R.string.live_lite_effect_score, signedValue(effect.scoreBonus)).takeIf { effect.scoreBonus != 0 },
    stringResource(R.string.live_lite_effect_risk, signedValue(effect.riskPenaltyDelta)).takeIf { effect.riskPenaltyDelta != 0 },
    stringResource(R.string.live_lite_effect_gold, signedPercent(effect.goldBonusPercent)).takeIf { effect.goldBonusPercent != 0 },
    stringResource(R.string.live_lite_effect_loot, effect.successLootBonus).takeIf { effect.successLootBonus > 0 },
    stringResource(R.string.live_lite_effect_xp, effect.xpBonus).takeIf { effect.xpBonus > 0 },
    stringResource(R.string.live_lite_effect_time, signedSeconds(effect.durationSecondsDelta)).takeIf { effect.durationSecondsDelta != 0 },
).joinToString(separator = " / ")

@Composable
private fun liveLiteBeatTitle(beat: LiveLiteWatchBeat, model: LiveLiteWatchModel): String = when (beat.kind) {
    LiveLiteWatchBeatKind.Plan -> stringResource(R.string.live_lite_beat_plan)
    LiveLiteWatchBeatKind.Hazard -> stringResource(R.string.live_lite_beat_hazard)
    LiveLiteWatchBeatKind.HeroAction -> stringResource(
        R.string.live_lite_beat_hero_action,
        model.heroPoses.firstOrNull { it.heroId == beat.heroId }?.heroName ?: stringResource(R.string.live_lite_intervention_someone),
    )
    LiveLiteWatchBeatKind.ResultCause -> stringResource(R.string.live_lite_beat_result_cause)
    LiveLiteWatchBeatKind.PassiveReport -> stringResource(R.string.live_lite_beat_passive)
}

@Composable
private fun liveLiteBeatDetail(beat: LiveLiteWatchBeat): String = when (beat.kind) {
    LiveLiteWatchBeatKind.Plan -> stringResource(R.string.live_lite_beat_plan_detail)
    LiveLiteWatchBeatKind.Hazard -> stringResource(R.string.live_lite_beat_hazard_detail)
    LiveLiteWatchBeatKind.HeroAction -> stringResource(R.string.live_lite_beat_pose_detail, liveLitePoseLabel(beat.pose))
    LiveLiteWatchBeatKind.ResultCause -> stringResource(R.string.live_lite_beat_result_detail)
    LiveLiteWatchBeatKind.PassiveReport -> stringResource(R.string.live_lite_beat_passive_detail)
}

@Composable
private fun liveLitePoseLabel(pose: LiveLiteHeroPose?): String = when (pose) {
    LiveLiteHeroPose.Idle -> stringResource(R.string.live_lite_pose_idle)
    LiveLiteHeroPose.Effort -> stringResource(R.string.live_lite_pose_effort)
    LiveLiteHeroPose.Success -> stringResource(R.string.live_lite_pose_success)
    LiveLiteHeroPose.Mistake -> stringResource(R.string.live_lite_pose_mistake)
    LiveLiteHeroPose.Loot -> stringResource(R.string.live_lite_pose_loot)
    null -> stringResource(R.string.live_lite_pose_idle)
}

private fun signedValue(value: Int): String = if (value > 0) "+$value" else value.toString()

private fun signedPercent(value: Int): String = "${signedValue(value)}%"

private fun signedSeconds(value: Int): String = "${signedValue(value)}s"