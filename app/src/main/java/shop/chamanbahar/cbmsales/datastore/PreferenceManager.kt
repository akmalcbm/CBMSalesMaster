package shop.chamanbahar.cbmsales.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {

    private object Keys {
        val LANGUAGE_SELECTED = booleanPreferencesKey("language_selected")
        val LANGUAGE = stringPreferencesKey("language")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

    val isLanguageSelected: Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.LANGUAGE_SELECTED] ?: false
        }

    val settingsFlow: Flow<Pair<String, Boolean>> =
        context.dataStore.data.map { prefs ->
            val lang = prefs[Keys.LANGUAGE] ?: "en"
            val dark = prefs[Keys.DARK_THEME] ?: false
            lang to dark
        }

    suspend fun setLanguageSelected(selected: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE_SELECTED] = selected
        }
    }

    suspend fun saveLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = lang
        }
    }

    suspend fun saveTheme(dark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_THEME] = dark
        }
    }
}
