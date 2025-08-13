package shop.chamanbahar.cbmsales

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import shop.chamanbahar.cbmsales.navigation.AppNavGraph
import shop.chamanbahar.cbmsales.ui.theme.CBMSalesTheme
import shop.chamanbahar.cbmsales.viewmodel.SettingsViewModel
import java.util.*

class App : ComponentActivity() {
    override fun attachBaseContext(base: Context) {
        val config = Configuration(base.resources.configuration)
        val lang = getSavedLanguage(base) // 🔁 Language from preference
        config.setLocale(Locale(lang))
        val newContext = base.createConfigurationContext(config)
        super.attachBaseContext(newContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()

            val lang by settingsViewModel.language.collectAsState()
            val isDark by settingsViewModel.darkTheme.collectAsState()

            SideEffect {
                val locale = Locale(lang)
                Locale.setDefault(locale)
            }

            CBMSalesTheme(useDarkTheme = isDark) {
                AppNavGraph(settingsViewModel = settingsViewModel)
            }
        }
    }

    // 💾 Load default language (optional fallback)
    private fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("language", "en") ?: "en"
    }
}
