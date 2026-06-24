package com.ayoshy.badventurers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ayoshy.badventurers.game.ExpeditionEngine
import com.ayoshy.badventurers.game.PlayPhase
import com.ayoshy.badventurers.storage.PlaySessionStore
import com.ayoshy.badventurers.ui.BadventurersApp

class MainActivity : ComponentActivity() {
    private val sessionStore by lazy { PlaySessionStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loadedSession = sessionStore.loadState()
        val startupMillis = System.currentTimeMillis()
        val tickedSession = loadedSession.tick(startupMillis, ExpeditionEngine(), loadedSession.heroes)
        val showOfflineSummary = loadedSession.phase == PlayPhase.Running && tickedSession.phase == PlayPhase.ResultReady
        val initialSession = if (showOfflineSummary) {
            tickedSession.markOfflineReportCollected(startupMillis)
        } else {
            tickedSession
        }
        if (showOfflineSummary) sessionStore.saveState(initialSession)
        setContent {
            BadventurersApp(
                initialSession = initialSession,
                showOfflineSummaryOnStart = showOfflineSummary,
                onSessionChanged = sessionStore::saveState,
            )
        }
    }
}