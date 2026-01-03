package com.ozodrukh.feature.profile.data.datasource

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.ozodrukh.feature.profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SharedPreferencesProfileDataSource(
    context: Context,
    private val gson: Gson
) : ProfileLocalDataSource {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _profileFlow = MutableStateFlow<UserProfile?>(null)

    init {
        val savedJson = prefs.getString(KEY_PROFILE, null)
        if (savedJson != null) {
            try {
                _profileFlow.value = gson.fromJson(savedJson, UserProfile::class.java)
            } catch (e: Exception) {
                prefs.edit().remove(KEY_PROFILE).apply()
            }
        }
    }

    override fun observeProfile(): Flow<UserProfile?> {
        return _profileFlow.asStateFlow()
    }

    override suspend fun saveProfile(profile: UserProfile) {
        val json = gson.toJson(profile)
        prefs.edit().putString(KEY_PROFILE, json).apply()
        _profileFlow.update { profile }
    }

    override suspend fun clearProfile() {
        prefs.edit().remove(KEY_PROFILE).apply()
        _profileFlow.update { null }
    }

    override suspend fun getProfile(): UserProfile? {
        return _profileFlow.value
    }

    companion object {
        private const val PREFS_NAME = "profile_local_data"
        private const val KEY_PROFILE = "user_profile_data"
    }
}
