package shop.chamanbahar.cbmsales.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import shop.chamanbahar.cbmsales.viewmodel.SettingsViewModel

@Composable
fun LanguageSelectorScreen(
    onLanguageSelected: () -> Unit,
    viewModel: SettingsViewModel
) {
    var selectedLang by remember { mutableStateOf("en") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Choose Language", style = MaterialTheme.typography.titleLarge)

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedLang == "en", onClick = { selectedLang = "en" })
            Text("English", Modifier.padding(start = 8.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedLang == "hi", onClick = { selectedLang = "hi" })
            Text("हिंदी", Modifier.padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            viewModel.setLanguage(selectedLang)
            viewModel.markLanguageSelected() // ✅ NEW
            onLanguageSelected()
        }) {
            Text("Continue")
        }
    }
}
