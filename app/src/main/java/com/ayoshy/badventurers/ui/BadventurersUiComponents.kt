package com.ayoshy.badventurers.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ayoshy.badventurers.R
import com.ayoshy.badventurers.game.AchievementCatalog
import com.ayoshy.badventurers.game.AchievementCategory
import com.ayoshy.badventurers.game.AchievementDefinition
import com.ayoshy.badventurers.game.AchievementFeature
import com.ayoshy.badventurers.game.ExpeditionEngine
import com.ayoshy.badventurers.game.ExpeditionEstimator
import com.ayoshy.badventurers.game.ExpeditionOutcome
import com.ayoshy.badventurers.game.ExpeditionPlan
import com.ayoshy.badventurers.game.ExpeditionPlanCatalog
import com.ayoshy.badventurers.game.ExpeditionResult
import com.ayoshy.badventurers.game.FakeRewardedAdService
import com.ayoshy.badventurers.game.GuildFacility
import com.ayoshy.badventurers.game.Hero
import com.ayoshy.badventurers.game.HeroCatalog
import com.ayoshy.badventurers.game.HeroClass
import com.ayoshy.badventurers.game.HeroGacha
import com.ayoshy.badventurers.game.HeroProgression
import com.ayoshy.badventurers.game.HeroRecommendation
import com.ayoshy.badventurers.game.HeroRecommendationScorer
import com.ayoshy.badventurers.game.HeroRarity
import com.ayoshy.badventurers.game.HeroLevelRewardType
import com.ayoshy.badventurers.game.HeroLevelRewardUnlock
import com.ayoshy.badventurers.game.HeroXpPreview
import com.ayoshy.badventurers.game.HeroSpecial
import com.ayoshy.badventurers.game.HeroSpecialCatalog
import com.ayoshy.badventurers.game.HeroRecruitmentResult
import com.ayoshy.badventurers.game.JournalEntry
import com.ayoshy.badventurers.game.LootCarryBreakdown
import com.ayoshy.badventurers.game.LootEconomy
import com.ayoshy.badventurers.game.LootIcon
import com.ayoshy.badventurers.game.LootItem
import com.ayoshy.badventurers.game.LootRarity
import com.ayoshy.badventurers.game.LootSlot
import com.ayoshy.badventurers.game.PartyPowerCalculator
import com.ayoshy.badventurers.game.PassiveIncomeReport
import com.ayoshy.badventurers.game.PassiveIncident
import com.ayoshy.badventurers.game.PassiveIncidentReward
import com.ayoshy.badventurers.game.PlayPhase
import com.ayoshy.badventurers.game.PlaySessionState
import com.ayoshy.badventurers.game.ProgressionAdvice
import com.ayoshy.badventurers.game.ProgressionAdviceKind
import com.ayoshy.badventurers.game.ProgressionAdvisor
import com.ayoshy.badventurers.game.RecruitmentTicketCatalog
import com.ayoshy.badventurers.game.RecruitmentTicketMetadata
import com.ayoshy.badventurers.game.Quest
import com.ayoshy.badventurers.game.QuestDifficultyTier
import com.ayoshy.badventurers.game.QuestRisk
import com.ayoshy.badventurers.game.QuestTag
import com.ayoshy.badventurers.game.QuestUnlockCondition
import com.ayoshy.badventurers.game.ResultCause
import com.ayoshy.badventurers.game.ResultCauseFacility
import com.ayoshy.badventurers.game.ResultCauseGenerator
import com.ayoshy.badventurers.game.ResultCauseKind
import com.ayoshy.badventurers.game.SeedGame
import com.ayoshy.badventurers.game.StatBonus
import com.ayoshy.badventurers.game.StatType
import com.ayoshy.badventurers.game.Trait
import kotlinx.coroutines.delay

@Composable
internal fun ScreenScaffold(title: String, status: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = title, color = Color(0xFFFFF0BD), fontSize = 19.sp, fontWeight = FontWeight.Black)
            Text(text = status, color = Color(0xFFE2CF93), fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@Composable
internal fun DarkPanel(title: String, body: String, footer: @Composable ColumnScope.() -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xDD24251F)),
    ) {
        Column(modifier = Modifier.padding(11.dp)) {
            Text(text = title, color = Color(0xFFFFF1C0), fontWeight = FontWeight.Black)
            Spacer(Modifier.height(5.dp))
            Text(text = body, color = Color(0xFFDED0A2), fontSize = 13.sp, lineHeight = 17.sp)
            footer()
        }
    }
}

@Composable
internal fun PaperPanel(title: String, body: String, footer: @Composable ColumnScope.() -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF4FFF1BF)),
    ) {
        Column(modifier = Modifier.padding(11.dp)) {
            Text(text = title, color = Color(0xFF211F1A), fontWeight = FontWeight.Black)
            Spacer(Modifier.height(5.dp))
            Text(text = body, color = Color(0xFF756B54), fontSize = 13.sp, lineHeight = 17.sp)
            footer()
        }
    }
}

@Composable
internal fun ActionRow(
    primaryLabel: String,
    secondaryLabel: String? = null,
    onPrimary: () -> Unit,
    onSecondary: (() -> Unit)? = null,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
) {
    val secondaryText = secondaryLabel
    val secondaryAction = onSecondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = onPrimary,
            enabled = primaryEnabled,
            modifier = if (secondaryText != null && secondaryAction != null) {
                Modifier.weight(1f)
            } else {
                Modifier.fillMaxWidth()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F695C)),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(primaryLabel, fontWeight = FontWeight.Black)
        }
        if (secondaryText != null && secondaryAction != null) {
            Button(
                onClick = secondaryAction,
                enabled = secondaryEnabled,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDEC777), contentColor = Color(0xFF211F1A)),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(secondaryText, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
internal fun ProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(9.dp)
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(Color(0x6650472F)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(Color(0xFF2F695C)),
        )
    }
}

@Composable
internal fun ArtSheet(resourceId: Int, aspectRatio: Float) {
    Image(
        painter = painterResource(resourceId),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF171813)),
        contentScale = ContentScale.Crop,
    )
    Spacer(Modifier.height(10.dp))
}

@Composable
internal fun JournalLine(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 7.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF4FFF1BF)),
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(34.dp)
                    .background(Color(0xFFD0A24A)),
            )
            Spacer(Modifier.width(8.dp))
            Text(text = text, color = Color(0xFF493F2B), fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
internal fun InfoRow(title: String, detail: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF4FFF1BF)),
    ) {
        Row(
            modifier = Modifier.padding(9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color(0xFF211F1A), fontWeight = FontWeight.Black, maxLines = 1)
                Text(
                    text = detail,
                    color = Color(0xFF756B54),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(text = value, color = Color(0xFF211F1A), fontWeight = FontWeight.Black)
        }
    }
}