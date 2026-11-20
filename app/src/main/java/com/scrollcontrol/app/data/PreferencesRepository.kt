package com.scrollcontrol.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "scroll_control_prefs")

data class BlockerSettings(
    val isBlockerEnabled: Boolean = true,
    val allowReelsInDm: Boolean = true,
    val blockNextReelScrollInDm: Boolean = true,
    val blockExploreTab: Boolean = false,
    val blockHomeFeed: Boolean = true,
    val reelsBlockedCount: Int = 0
)

class PreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val IS_BLOCKER_ENABLED = booleanPreferencesKey("is_blocker_enabled")
        val ALLOW_REELS_IN_DM = booleanPreferencesKey("allow_reels_in_dm")
        val BLOCK_NEXT_REEL_SCROLL = booleanPreferencesKey("block_next_reel_scroll")
        val BLOCK_EXPLORE_TAB = booleanPreferencesKey("block_explore_tab")
        val BLOCK_HOME_FEED = booleanPreferencesKey("block_home_feed")
        val REELS_BLOCKED_COUNT = intPreferencesKey("reels_blocked_count")
    }

    val settingsFlow: Flow<BlockerSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            BlockerSettings(
                isBlockerEnabled = preferences[PreferencesKeys.IS_BLOCKER_ENABLED] ?: true,
                allowReelsInDm = preferences[PreferencesKeys.ALLOW_REELS_IN_DM] ?: true,
                blockNextReelScrollInDm = preferences[PreferencesKeys.BLOCK_NEXT_REEL_SCROLL] ?: true,
                blockExploreTab = preferences[PreferencesKeys.BLOCK_EXPLORE_TAB] ?: false,
                blockHomeFeed = preferences[PreferencesKeys.BLOCK_HOME_FEED] ?: true,
                reelsBlockedCount = preferences[PreferencesKeys.REELS_BLOCKED_COUNT] ?: 0
            )
        }

    suspend fun setBlockerEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_BLOCKER_ENABLED] = enabled }
    }

    suspend fun setAllowReelsInDm(allow: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ALLOW_REELS_IN_DM] = allow }
    }

    suspend fun setBlockNextReelScrollInDm(block: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.BLOCK_NEXT_REEL_SCROLL] = block }
    }

    suspend fun setBlockExploreTab(block: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.BLOCK_EXPLORE_TAB] = block }
    }

    suspend fun setBlockHomeFeed(block: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.BLOCK_HOME_FEED] = block }
    }

    suspend fun incrementReelsBlockedCount() {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.REELS_BLOCKED_COUNT] ?: 0
            preferences[PreferencesKeys.REELS_BLOCKED_COUNT] = current + 1
        }
    }

    suspend fun resetStats() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REELS_BLOCKED_COUNT] = 0
        }
    }
}
