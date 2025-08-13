package shop.chamanbahar.cbmsales.helper

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun InfoBadgeTop(
    label: String,
    value: String,
    background: Color = MaterialTheme.colorScheme.primaryContainer,       // ✅ Theme-aware background
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer    // ✅ Theme-aware text
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = background,
        modifier = Modifier
            .padding(4.dp)
            .width(90.dp)
            .height(56.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                color = contentColor,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = value,
                color = contentColor,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
