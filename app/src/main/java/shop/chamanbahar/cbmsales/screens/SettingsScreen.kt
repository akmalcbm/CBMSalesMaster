package shop.chamanbahar.cbmsales.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import shop.chamanbahar.cbmsales.viewmodel.SettingsViewModel
import androidx.compose.ui.res.stringResource
import shop.chamanbahar.cbmsales.R

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val lang by viewModel.language.collectAsState()
    val dark by viewModel.darkTheme.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = stringResource(R.string.language), style = MaterialTheme.typography.titleMedium)
        Row {
            RadioButton(selected = lang == "en", onClick = { viewModel.setLanguage("en") })
            Text("English", modifier = Modifier.padding(start = 8.dp))
        }
        Row {
            RadioButton(selected = lang == "hi", onClick = { viewModel.setLanguage("hi") })
            Text("हिंदी", modifier = Modifier.padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.theme), style = MaterialTheme.typography.titleMedium)
        Row {
            RadioButton(selected = !dark, onClick = { viewModel.setTheme(false) })
            Text(text = stringResource(R.string.light))
        }
        Row {
            RadioButton(selected = dark, onClick = { viewModel.setTheme(true) })
            Text(text = stringResource(R.string.dark))
        }
    }
}
