package shop.chamanbahar.cbmsales.helper

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerField(
    selectedDate: Long,                  // ✅ store date as Long
    onDateSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // ✅ Format date for display
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val dateText = remember(selectedDate) { dateFormatter.format(Date(selectedDate)) }

    // 📌 TextField (read-only) that triggers the DatePicker
    OutlinedTextField(
        value = dateText,
        onValueChange = {},
        label = { Text("Order Date") },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
            }
        }
    )

    // 📌 Native Android Date Picker Dialog
    if (showDialog) {
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val pickedCalendar = Calendar.getInstance()
                pickedCalendar.set(year, month, dayOfMonth)
                onDateSelected(pickedCalendar.timeInMillis)   // ✅ returns Long
                showDialog = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()

        // ✅ Prevent reopening immediately
        showDialog = false
    }
}
