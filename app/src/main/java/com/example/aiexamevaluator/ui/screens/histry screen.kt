package com.example.aiexamevaluator.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiexamevaluator.data.local.EvaluationHistoryEntity
import com.example.aiexamevaluator.viewmodel.EvaluationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
private fun rememberSafeInteractionSource() = remember { MutableInteractionSource() }

// ──────────────────────────────────────────────────────────────────────────────
// HistoryScreen
// ──────────────────────────────────────────────────────────────────────────────
@Composable
fun HistoryScreen(
    viewModel        : EvaluationViewModel,
    onStartEvaluation: () -> Unit,
    onHomeClick      : () -> Unit = {},
    onSettingsClick  : () -> Unit = {},
    onCardClick      : (EvaluationHistoryEntity) -> Unit = {},
    modifier         : Modifier = Modifier,
) {
    val historyList by viewModel.historyList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(setOf<Int>()) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val filtered = remember(historyList, searchQuery) {
        historyList.filter { entity ->
            entity.studentName.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

    fun toggleSelection(entityId: Int) {
        selectedItems = if (selectedItems.contains(entityId)) {
            selectedItems - entityId
        } else {
            selectedItems + entityId
        }
        if (selectedItems.isEmpty()) {
            isSelectionMode = false
        }
    }

    fun enterSelectionMode(entityId: Int) {
        isSelectionMode = true
        selectedItems = selectedItems + entityId
    }

    BackHandler(enabled = isSelectionMode) {
        selectedItems = emptySet()
        isSelectionMode = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top bar
        HistoryTopBar(
            isSelectionMode = isSelectionMode,
            selectedCount = selectedItems.size,
            onDeleteSelected = { showDeleteDialog = true },
            onCancelSelection = {
                selectedItems = emptySet()
                isSelectionMode = false
            }
        )

        // Scrollable body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value         = searchQuery,
                onValueChange = { searchQuery = it },
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = { Text("Search by student name", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon   = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                },
                singleLine    = true,
                shape         = RoundedCornerShape(12.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor     = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor   = MaterialTheme.colorScheme.onSurface,
                    cursorColor          = MaterialTheme.colorScheme.primary,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            // Section header
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Recent Evaluations", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                Text("Latest First",       fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // List or empty state
            if (filtered.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxWidth().height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📋", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text  = if (searchQuery.isBlank()) "No evaluations yet." else "No results found.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                filtered.forEach { entity ->
                    HistoryCard(
                        entity      = entity,
                        isSelected  = selectedItems.contains(entity.id),
                        selectionMode = isSelectionMode,
                        onLongPress = { enterSelectionMode(entity.id) },
                        onCardClick = {
                            if (isSelectionMode) {
                                toggleSelection(entity.id)
                            } else {
                                onCardClick(entity)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // Unified bottom bar — History tab active
        UnifiedBottomBar(
            activeTab       = BottomTab.HISTORY,
            onHistoryClick  = { /* already here */ },
            onFabClick      = onStartEvaluation,
            onSettingsClick = onSettingsClick
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Delete Evaluations?") },
            text = { Text(text = "Are you sure you want to delete the selected evaluations?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedItems.forEach { id ->
                            historyList.find { it.id == id }?.let { viewModel.deleteHistory(it) }
                        }
                        selectedItems = emptySet()
                        isSelectionMode = false
                        showDeleteDialog = false
                    }
                ) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

// ── Top bar ────────────────────────────────────────────────────────────────────
@Composable
private fun HistoryTopBar(
    isSelectionMode: Boolean,
    selectedCount: Int,
    onDeleteSelected: () -> Unit,
    onCancelSelection: () -> Unit,
) {
    val leftInteractionSource = rememberSafeInteractionSource()
    val rightInteractionSource = rememberSafeInteractionSource()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        if (isSelectionMode) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clickable(
                        interactionSource = leftInteractionSource,
                        indication = null
                    ) {
                        onCancelSelection()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        Text(
            text = if (isSelectionMode) "$selectedCount Selected" else "History",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Center),
        )

        if (isSelectionMode) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd)
                    .clickable(
                        interactionSource = rightInteractionSource,
                        indication = null
                    ) {
                        onDeleteSelected()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete selected",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

// ── History card — tappable ────────────────────────────────────────────────────
@Composable
private fun HistoryCard(
    entity     : EvaluationHistoryEntity,
    isSelected : Boolean,
    selectionMode: Boolean,
    onLongPress: () -> Unit,
    onCardClick: () -> Unit,
) {
    val percent = if (entity.totalMarks > 0)
        (entity.totalObtained * 100f / entity.totalMarks).coerceIn(0f, 100f) else 0f

    val dateStr = remember(entity.evaluatedAt) {
        SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(entity.evaluatedAt))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(16.dp)
            )
            .combinedClickable(
                interactionSource = rememberSafeInteractionSource(),
                indication = null,
                onClick = {
                    if (selectionMode) {
                        onCardClick()
                    } else {
                        onCardClick()
                    }
                },
                onLongClick = onLongPress
            )
            .padding(14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text       = entity.studentName.ifBlank { "Unknown" },
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    color      = MaterialTheme.colorScheme.onSurface,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                if (entity.rollNumber.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Roll: ${entity.rollNumber}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    }
                }
                Text(dateStr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                Text("Confidence: ${entity.confidenceScore}%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MiniScoreRing(percent = percent, obtained = entity.totalObtained, total = entity.totalMarks)
            Text("›", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ── Mini score ring — Canvas, no animation ─────────────────────────────────────
@Composable
private fun MiniScoreRing(percent: Float, obtained: Int, total: Int) {
    val ringColor = when {
        percent >= 75f -> MaterialTheme.colorScheme.primary
        percent >= 50f -> Color(0xFFE65100)
        else           -> Color(0xFFC62828)
    }
    val trackColor = MaterialTheme.colorScheme.outlineVariant
    val sweep = (percent / 100f) * 360f

    Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke   = 6.dp.toPx()
            val diameter = size.minDimension - stroke
            val tl       = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val sz       = Size(diameter, diameter)
            drawArc(
                color       = trackColor,
                startAngle  = -90f,
                sweepAngle  = 360f,
                useCenter   = false,
                topLeft     = tl,
                size        = sz,
                style       = Stroke(stroke, cap = StrokeCap.Round)
            )
            if (sweep > 0f) drawArc(
                color       = ringColor,
                startAngle  = -90f,
                sweepAngle  = sweep,
                useCenter   = false,
                topLeft     = tl,
                size        = sz,
                style       = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${percent.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            Text("$obtained / $total", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}