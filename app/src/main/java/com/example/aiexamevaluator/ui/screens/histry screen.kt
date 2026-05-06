package com.example.aiexamevaluator.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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

// ── Safe clickable ─────────────────────────────────────────────────────────────
private fun Modifier.safeClick(onClick: () -> Unit): Modifier =
    this.clickable(
        interactionSource = MutableInteractionSource(),
        indication = null,
        onClick = onClick
    )

// ── Local colours ──────────────────────────────────────────────────────────────
private val PageBg    = Color(0xFFF5F7F6)
private val TextDark  = Color(0xFF1A1A1A)
private val TrackGray = Color(0xFFE0E0E0)

// ── Filter tab enum ────────────────────────────────────────────────────────────
private enum class HistoryTab { ALL, SUBJECTIVE, MCQ }

// ──────────────────────────────────────────────────────────────────────────────
// HistoryScreen
// ──────────────────────────────────────────────────────────────────────────────
@Composable
fun HistoryScreen(
    viewModel        : EvaluationViewModel,
    onStartEvaluation: () -> Unit,
    onHomeClick      : () -> Unit = {},
    onCardClick      : (EvaluationHistoryEntity) -> Unit = {},  // ← NEW
    modifier         : Modifier = Modifier,
) {
    val historyList by viewModel.historyList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var activeTab   by remember { mutableStateOf(HistoryTab.ALL) }

    val filtered = remember(historyList, activeTab, searchQuery) {
        historyList.filter { entity ->
            entity.studentName.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // Top bar
        HistoryTopBar()

        // Scrollable body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search + Filter row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier      = Modifier.weight(1f),
                    placeholder   = { Text("Search by student name", fontSize = 13.sp, color = AppGray) },
                    leadingIcon   = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = AppGray, modifier = Modifier.size(18.dp))
                    },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = AppGreen,
                        unfocusedBorderColor = AppBorder,
                        focusedTextColor     = TextDark,
                        unfocusedTextColor   = TextDark,
                        cursorColor          = AppGreen,
                    )
                )
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .background(AppCardBg, RoundedCornerShape(12.dp))
                        .border(1.dp, AppBorder, RoundedCornerShape(12.dp))
                        .safeClick { }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Filter", fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Medium)
                }
            }

            // Filter tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppCardBg, RoundedCornerShape(12.dp))
                    .border(1.dp, AppBorder, RoundedCornerShape(12.dp))
            ) {
                HistoryTabItem("All Evaluations", activeTab == HistoryTab.ALL,       Modifier.weight(1f)) { activeTab = HistoryTab.ALL }
                Box(modifier = Modifier.width(1.dp).height(44.dp).background(AppBorder))
                HistoryTabItem("Subjective",      activeTab == HistoryTab.SUBJECTIVE, Modifier.weight(1f)) { activeTab = HistoryTab.SUBJECTIVE }
                Box(modifier = Modifier.width(1.dp).height(44.dp).background(AppBorder))
                HistoryTabItem("MCQ",             activeTab == HistoryTab.MCQ,        Modifier.weight(1f)) { activeTab = HistoryTab.MCQ }
            }

            // Section header
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Recent Evaluations", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppGreen)
                Text("Latest First",       fontSize = 12.sp, color = AppGray)
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
                            text     = if (searchQuery.isBlank()) "No evaluations yet." else "No results found.",
                            color    = AppGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                filtered.forEach { entity ->
                    HistoryCard(
                        entity    = entity,
                        onDelete  = { viewModel.deleteHistory(entity) },
                        onCardClick = { onCardClick(entity) }   // ← wire click
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // Unified bottom bar — History tab active
        UnifiedBottomBar(
            activeTab      = BottomTab.HISTORY,
            onHistoryClick = { /* already here */ },
            onFabClick     = onStartEvaluation,
            onSettingsClick= { }
        )
    }
}

// ── Top bar ────────────────────────────────────────────────────────────────────
@Composable
private fun HistoryTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppCardBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Menu, contentDescription = null, tint = AppGreen, modifier = Modifier.size(24.dp))
        Text("History", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppGreen)
        Box(
            modifier         = Modifier.size(36.dp).background(AppGreenLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = AppGreen, modifier = Modifier.size(20.dp))
        }
    }
}

// ── Filter tab item ────────────────────────────────────────────────────────────
@Composable
private fun HistoryTabItem(
    text    : String,
    active  : Boolean,
    modifier: Modifier,
    onClick : () -> Unit,
) {
    Box(
        modifier = modifier
            .background(if (active) AppGreen else Color.Transparent, RoundedCornerShape(11.dp))
            .safeClick { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = text,
            fontSize   = 12.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
            color      = if (active) Color.White else AppGray,
            maxLines   = 1
        )
    }
}

// ── History card — tappable ────────────────────────────────────────────────────
@Composable
private fun HistoryCard(
    entity     : EvaluationHistoryEntity,
    onDelete   : () -> Unit,
    onCardClick: () -> Unit,
) {
    val percent = if (entity.totalMarks > 0)
        (entity.totalObtained * 100f / entity.totalMarks).coerceIn(0f, 100f) else 0f

    val dateStr = remember(entity.evaluatedAt) {
        SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(entity.evaluatedAt))
    }

    val initials = remember(entity.studentName) {
        entity.studentName.trim().split(" ")
            .filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
            .ifBlank { "?" }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppCardBg, RoundedCornerShape(16.dp))
            .border(1.dp, AppBorder, RoundedCornerShape(16.dp))
            .safeClick { onCardClick() }           // ← tapping the card
            .padding(14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {

            // Initials avatar
            Box(
                modifier         = Modifier.size(48.dp).background(AppGreenLight, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AppGreen)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text       = entity.studentName.ifBlank { "Unknown" },
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    color      = TextDark,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                if (entity.rollNumber.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .background(AppGreenLight, RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Roll: ${entity.rollNumber}", fontSize = 10.sp, color = AppGreen, fontWeight = FontWeight.Medium)
                    }
                }
                Text(dateStr, fontSize = 11.sp, color = AppGray, maxLines = 1)
                Text("Confidence: ${entity.confidenceScore}%", fontSize = 11.sp, color = AppGray)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MiniScoreRing(percent = percent, obtained = entity.totalObtained, total = entity.totalMarks)
            Text("›", fontSize = 20.sp, color = AppGray)
        }
    }
}

// ── Mini score ring — Canvas, no animation ─────────────────────────────────────
@Composable
private fun MiniScoreRing(percent: Float, obtained: Int, total: Int) {
    val ringColor = when {
        percent >= 75f -> AppGreen
        percent >= 50f -> Color(0xFFE65100)
        else           -> Color(0xFFC62828)
    }
    val sweep = (percent / 100f) * 360f

    Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke   = 6.dp.toPx()
            val diameter = size.minDimension - stroke
            val tl       = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val sz       = Size(diameter, diameter)
            drawArc(
                color = TrackGray, startAngle = -90f, sweepAngle = 360f,
                useCenter = false, topLeft = tl, size = sz,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
            if (sweep > 0f) drawArc(
                color = ringColor, startAngle = -90f, sweepAngle = sweep,
                useCenter = false, topLeft = tl, size = sz,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${percent.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ringColor)
            Text("$obtained / $total", fontSize = 9.sp, color = AppGray)
        }
    }
}