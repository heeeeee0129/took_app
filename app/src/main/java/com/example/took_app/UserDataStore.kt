package com.example.took_app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userData")

class UserDataStore() {
    private val context = App.context()
    private val dataStore: DataStore<Preferences> = context.dataStore

    private object PreferencesKeys {
        val TOKEN = stringPreferencesKey("token")
        val USER_SEQ = stringPreferencesKey("user_seq")
    }

    suspend fun saveToken(token: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[PreferencesKeys.TOKEN] = token
            }
        }
    }

    suspend fun getToken(): String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.TOKEN]
        }
    }

    suspend fun saveUserSeq(seq: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[PreferencesKeys.USER_SEQ] = seq
            }
        }
    }

    suspend fun getUserSeq(): String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.USER_SEQ]
        }
    }


    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref.clear()
            }
        }
    }
}
