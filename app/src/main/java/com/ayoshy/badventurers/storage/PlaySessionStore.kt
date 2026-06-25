package com.ayoshy.badventurers.storage

import android.content.Context
import android.util.Log
import com.ayoshy.badventurers.game.PlaySessionSnapshot
import com.ayoshy.badventurers.game.PlaySessionState

class PlaySessionStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun loadState(): PlaySessionState {
        val encoded = preferences.getString(KEY_SESSION, null) ?: return PlaySessionState.initial()
        val snapshot = PlaySessionSnapshotJson.decode(encoded)
        if (snapshot == null) {
            PlaySessionSnapshotJson.lastDecodeFailure?.let { failure ->
                Log.w(TAG, "Failed to decode saved session; starting a fresh session. $failure")
            }
            return PlaySessionState.initial()
        }
        return snapshot.toState()
    }

    fun saveState(state: PlaySessionState) {
        preferences.edit()
            .putString(KEY_SESSION, PlaySessionSnapshotJson.encode(PlaySessionSnapshot.fromState(state)))
            .commit()
    }

    private companion object {
        const val PREFERENCES_NAME = "badventurers_session"
        const val KEY_SESSION = "session_snapshot"
        const val TAG = "PlaySessionStore"
    }
}