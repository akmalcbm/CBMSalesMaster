package shop.chamanbahar.cbmsales.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import shop.chamanbahar.cbmsales.datastore.PreferenceManager

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val prefManager = PreferenceManager(app)

    private val _language = MutableStateFlow("en")
    val language: StateFlow<String> = _language

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme

    private val _languageSelected = MutableStateFlow(false)
    val languageSelected: StateFlow<Boolean> = _languageSelected

    init {
        viewModelScope.launch {
            prefManager.settingsFlow.collect { (lang, dark) ->
                _language.value = lang
                _darkTheme.value = dark
            }

            prefManager.isLanguageSelected.collect {
                _languageSelected.value = it
            }
        }

    }

    fun setLanguage(lang: String) = viewModelScope.launch {
        prefManager.saveLanguage(lang)
    }

    fun markLanguageSelected() = viewModelScope.launch {
        prefManager.setLanguageSelected(true)
    }

    fun setTheme(dark: Boolean) = viewModelScope.launch {
        prefManager.saveTheme(dark)
    }
}
