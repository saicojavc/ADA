package com.saico.ada.ui.event.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saico.ada.domain.model.Event
import com.saico.ada.domain.model.EventCategory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AddEditEventDialog(
    event: Event? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, LocalDateTime, LocalDateTime, EventCategory) -> Unit
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var startTime by remember { mutableStateOf(event?.startTime ?: LocalDateTime.now()) }
    var endTime by remember { mutableStateOf(event?.endTime ?: LocalDateTime.now().plusHours(1)) }
    var category by remember { mutableStateOf(event?.category ?: EventCategory.UNCATEGORIZED) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (event == null) "Add Event" else "Edit Event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Simple text-based time entry for now to keep it functional
                // In a real app, use TimePicker
                Text("Start: ${startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}")
                Text("End: ${endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}")
                
                Text("Category will be auto-assigned if 'Uncategorized' is selected.")
                
                EventCategory.values().forEach { cat ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        RadioButton(
                            selected = category == cat,
                            onClick = { category = cat }
                        )
                        Text(cat.name, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, description, startTime, endTime, category) },
                enabled = title.isNotBlank()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
