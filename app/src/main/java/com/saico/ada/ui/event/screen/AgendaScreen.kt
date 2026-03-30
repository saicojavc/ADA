package com.saico.ada.ui.event.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saico.ada.domain.model.Event
import com.saico.ada.domain.model.EventCategory
import com.saico.ada.domain.model.TimeGap
import com.saico.ada.ui.event.EventViewModel
import com.saico.ada.ui.event.components.AddEditEventDialog
import com.saico.ada.ui.theme.SageGreen
import com.saico.ada.ui.theme.Terracotta
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    viewModel: EventViewModel
) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    val gaps by viewModel.gaps.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<Event?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "ADA Agenda", 
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Terracotta,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            DatePickerHeader(
                selectedDate = selectedDate,
                onDateSelected = viewModel::onDateSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (events.isEmpty() && gaps.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No events for today. Time for some self-care?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    if (gaps.isNotEmpty()) {
                        item {
                            SuggestedBreaksSection(gaps = gaps)
                        }
                    }

                    items(events) { event ->
                        EventItem(
                            event = event,
                            onEdit = { eventToEdit = it },
                            onDelete = { viewModel.deleteEvent(it) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddEditEventDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { title, desc, start, end, cat ->
                    viewModel.addEvent(title, desc, start, end, cat)
                    showAddDialog = false
                }
            )
        }

        eventToEdit?.let { event ->
            AddEditEventDialog(
                event = event,
                onDismiss = { eventToEdit = null },
                onConfirm = { title, desc, start, end, cat ->
                    viewModel.updateEvent(event.copy(
                        title = title,
                        description = desc,
                        startTime = start,
                        endTime = end,
                        category = cat
                    ))
                    eventToEdit = null
                }
            )
        }
    }
}

@Composable
fun SuggestedBreaksSection(gaps: List<TimeGap>) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Suggested Breaks",
            style = MaterialTheme.typography.titleMedium,
            color = SageGreen,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            gaps.take(3).forEach { gap ->
                BreakChip(gap = gap)
            }
        }
    }
}

@Composable
fun BreakChip(gap: TimeGap) {
    Surface(
        color = SageGreen.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SageGreen.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.SelfImprovement,
                contentDescription = null,
                tint = SageGreen,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${gap.durationMinutes} min break at ${gap.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                style = MaterialTheme.typography.bodySmall,
                color = SageGreen
            )
        }
    }
}

@Composable
fun DatePickerHeader(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val today = LocalDate.now()
        for (i in -3..3) {
            val date = today.plusDays(i.toLong())
            DateChip(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
fun DateChip(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("E")
    val dayFormatter = DateTimeFormatter.ofPattern("d")

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                if (isSelected) Terracotta else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.format(formatter),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = date.format(dayFormatter),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EventItem(
    event: Event,
    onEdit: (Event) -> Unit,
    onDelete: (Event) -> Unit
) {
    val categoryColor = when (event.category) {
        EventCategory.WORK -> Terracotta
        EventCategory.HOME -> SageGreen
        EventCategory.WELLNESS -> Color(0xFFF2CC8F) // Mellow Yellow
        EventCategory.MATERNITY -> Color(0xFFE9C46A)
        EventCategory.UNCATEGORIZED -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(categoryColor, RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${event.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${event.endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                if (event.description.isNotEmpty()) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        maxLines = 1
                    )
                }
            }
            IconButton(onClick = { onEdit(event) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SageGreen)
            }
            IconButton(onClick = { onDelete(event) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Terracotta)
            }
        }
    }
}
