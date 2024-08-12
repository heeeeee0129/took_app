package com.example.took_app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userData")

class UserDataStore(private val context: Context) {
    private val dataStore: DataStore<Preferences> = context.dataStore

    private object PreferencesKeys {
        val FIRST_FLAG = booleanPreferencesKey("first_flag")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val LOGIN_TYPE = stringPreferencesKey("login_type")
        val NICKNAME = stringPreferencesKey("nickname")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_PASSWORD = stringPreferencesKey("user_password")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_SEQ = longPreferencesKey("user_seq")
    }

    suspend fun saveIsLoggedIn(isLoggedIn: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
            }
        }
    }

    suspend fun getIsLoggedIn(): Boolean {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.IS_LOGGED_IN] ?: false
        }
    }

    suspend fun saveAccessToken(token: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[PreferencesKeys.ACCESS_TOKEN] = token
            }
        }
    }

    suspend fun getAccessToken(): String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.ACCESS_TOKEN]
        }
    }

    suspend fun saveRefreshToken(token: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[PreferencesKeys.REFRESH_TOKEN] = token
            }
        }
    }

    suspend fun getRefreshToken(): String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.REFRESH_TOKEN]
        }
    }

    suspend fun saveLoginType(type: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[PreferencesKeys.LOGIN_TYPE] = type
            }
        }
    }

    suspend fun getLoginType(): String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.LOGIN_TYPE]
        }
    }

    suspend fun saveNickname(data: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[PreferencesKeys.NICKNAME] = data
            }
        }
    }

    suspend fun getNickname(): String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.NICKNAME]
        }
    }

    suspend fun saveFirstFlag(flag: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[PreferencesKeys.FIRST_FLAG] = flag
            }
        }
    }

    suspend fun getFirstFlag(): Boolean {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.FIRST_FLAG] ?: false
        }
    }

    suspend fun saveUserId(id: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[PreferencesKeys.USER_ID] = id
            }
        }
    }

    suspend fun getUserId(): String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.USER_ID]
        }
    }

    suspend fun saveUserPassword(password: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[PreferencesKeys.USER_PASSWORD] = password
            }
        }
    }

    suspend fun getUserPassword(): String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.USER_PASSWORD]
        }
    }

    // userSeq 저장 함수 추가
    suspend fun saveUserSeq(seq: Long) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[PreferencesKeys.USER_SEQ] = seq
            }
        }
    }

    // userSeq 불러오기 함수 추가
    suspend fun getUserSeq(): Long? {
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
