package shop.chamanbahar.cbmsales.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("app_settings")

object PreferenceKeys {
    val LANGUAGE = stringPreferencesKey("language")
    val LANGUAGE_SELECTED = booleanPreferencesKey("language_selected")
    val THEME_DARK = booleanPreferencesKey("theme_dark")
}

class PreferenceManager(private val context: Context) {

    val settingsFlow = context.dataStore.data.map { prefs ->
        val lang = prefs[PreferenceKeys.LANGUAGE] ?: "en"
        val darkTheme = prefs[PreferenceKeys.THEME_DARK] ?: false
        lang to darkTheme
    }

    suspend fun saveLanguage(lang: String) {
        context.dataStore.edit { it[PreferenceKeys.LANGUAGE] = lang }
    }

    val isLanguageSelected = context.dataStore.data.map {
        it[PreferenceKeys.LANGUAGE_SELECTED] ?: false
    }

    suspend fun setLanguageSelected(selected: Boolean) {
        context.dataStore.edit {
            it[PreferenceKeys.LANGUAGE_SELECTED] = selected
        }
    }

    suspend fun saveTheme(isDark: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.THEME_DARK] = isDark }
    }
}
