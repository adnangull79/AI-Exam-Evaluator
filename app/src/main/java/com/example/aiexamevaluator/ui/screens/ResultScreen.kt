package com.example.aiexamevaluator.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiexamevaluator.data.model.EvaluationResponse
import com.example.aiexamevaluator.data.model.Question
import com.example.aiexamevaluator.utils.PdfReportGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
private fun rememberSafeInteractionSource() = remember { MutableInteractionSource() }

// ── Colours ────────────────────────────────────────────────────────────────────
private val Green       = Color(0xFF1B5E3C)
private val GreenLight  = Color(0xFFE8F5E9)
private val CardBg      = Color.White
private val BorderColor = Color(0xFFE0E0E0)
private val TextDark    = Color(0xFF1A1A1A)
private val TextGray    = Color(0xFF757575)
private val RedColor    = Color(0xFFC62828)
private val RedBg       = Color(0xFFFFEBEE)
private val OrangeColor = Color(0xFFE65100)
private val OrangeBg    = Color(0xFFFFF3E0)
private val BlueColor   = Color(0xFF1565C0)
private val BlueBg      = Color(0xFFE3F2FD)
private val TrackGray   = Color(0xFFE0E0E0)

// ── Filter ─────────────────────────────────────────────────────────────────────
private enum class Filter { ALL, CORRECT, INCORRECT, PARTIAL }

// ──────────────────────────────────────────────────────────────────────────────
// ResultScreen
// ──────────────────────────────────────────────────────────────────────────────
@Composable
fun ResultScreen(
    studentName      : String,
    rollNumber       : String,
    totalMarks       : Int,
    result           : EvaluationResponse,
    onEvaluateAnother: () -> Unit,
    evaluatedAt      : Long = System.currentTimeMillis(),  // ← for PDF date
    modifier         : Modifier = Modifier,
) {
    val context        = LocalContext.current
    val scope          = rememberCoroutineScope()

    // PDF generation state
    var isPdfGenerating by remember { mutableStateOf(false) }

    val questions      = result.questions
    val correctCount   = questions.count { it.marksObtained == it.maxMarks && it.maxMarks > 0 }
    val incorrectCount = questions.count { it.marksObtained == 0 }
    val partialCount   = questions.count { it.marksObtained > 0 && it.marksObtained < it.maxMarks }

    val percent = if (totalMarks > 0)
        (result.totalObtained * 100f / totalMarks).coerceIn(0f, 100f) else 0f

    val dateStr = remember(evaluatedAt) {
        SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(evaluatedAt))
    }

    var filter by remember { mutableStateOf(Filter.ALL) }

    val shown = remember(filter, questions) {
        when (filter) {
            Filter.ALL       -> questions
            Filter.CORRECT   -> questions.filter { it.marksObtained == it.maxMarks && it.maxMarks > 0 }
            Filter.INCORRECT -> questions.filter { it.marksObtained == 0 }
            Filter.PARTIAL   -> questions.filter { it.marksObtained > 0 && it.marksObtained < it.maxMarks }
        }
    }

    // ── PDF generation handler ────────────────────────────────────────────────
    val onGeneratePdf: () -> Unit = {
        if (!isPdfGenerating) {
            isPdfGenerating = true
            scope.launch {
                val fileName = withContext(Dispatchers.IO) {
                    PdfReportGenerator.generate(
                        context     = context,
                        studentName = studentName,
                        rollNumber  = rollNumber,
                        totalMarks  = totalMarks,
                        result      = result,
                        evaluatedAt = evaluatedAt,
                    )
                }
                isPdfGenerating = false
                if (fileName != null) {
                    Toast.makeText(context, "Report saved to Downloads:\n$fileName", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Failed to generate report. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7F6))
    ) {
        // Top bar
        ResultTopBar(onBack = onEvaluateAnother)

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                studentName = studentName,
                dateStr     = dateStr,
                obtained    = result.totalObtained,
                total       = totalMarks,
                percent     = percent,
            )

            StatsRow(correctCount, incorrectCount, partialCount, questions.size)

            if (result.overallFeedback.isNotBlank()) {
                FeedbackCard(result.overallFeedback)
            }

            Text(
                text       = "Question Analysis",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                color      = TextDark,
            )

            FilterRow(
                active         = filter,
                correctCount   = correctCount,
                incorrectCount = incorrectCount,
                partialCount   = partialCount,
                onSelect       = { filter = it }
            )

            if (shown.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxWidth().height(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No questions in this category.", color = TextGray, fontSize = 13.sp)
                }
            } else {
                shown.forEach { q -> QuestionCard(q) }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // Bottom bar
        ResultBottomBar(
            onEvaluateAnother = onEvaluateAnother,
            onGeneratePdf     = onGeneratePdf,
            isPdfGenerating   = isPdfGenerating,
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Top bar
// ──────────────────────────────────────────────────────────────────────────────
@Composable
private fun ResultTopBar(onBack: () -> Unit) {
    val backInteractionSource = rememberSafeInteractionSource()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Box(
            modifier         = Modifier.size(40.dp).clickable(
                interactionSource = backInteractionSource,
                indication = null
            ) {
                onBack()
            },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint               = Green
            )
        }
        Text(
            text = "Evaluation Result",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Green,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Bottom bar — Generate Report + Evaluate Another
// ──────────────────────────────────────────────────────────────────────────────
@Composable
private fun ResultBottomBar(
    onEvaluateAnother: () -> Unit,
    onGeneratePdf    : () -> Unit,
    isPdfGenerating  : Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Generate Report button — shows spinner while generating
        OutlinedButton(
            onClick  = onGeneratePdf,
            modifier = Modifier.weight(1f).height(48.dp),
            shape    = RoundedCornerShape(50),
            enabled  = !isPdfGenerating,
        ) {
            if (isPdfGenerating) {
                CircularProgressIndicator(
                    color    = Green,
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Saving...", fontSize = 12.sp, color = TextGray)
            } else {
                Text("Generate Report", fontSize = 12.sp, color = TextDark)
            }
        }

        Button(
            onClick  = onEvaluateAnother,
            modifier = Modifier.weight(1f).height(48.dp),
            shape    = RoundedCornerShape(50),
            colors   = ButtonDefaults.buttonColors(containerColor = Green)
        ) {
            Text("Evaluate Another", color = Color.White, fontSize = 12.sp)
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Summary card
// ──────────────────────────────────────────────────────────────────────────────
@Composable
private fun SummaryCard(
    studentName: String,
    dateStr    : String,
    obtained   : Int,
    total      : Int,
    percent    : Float,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Evaluation Completed!", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Green)
            Text("Here's the detailed analysis.", fontSize = 11.sp, color = TextGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Student Name", fontSize = 10.sp, color = TextGray)
            Text(studentName.ifBlank { "—" }, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextDark)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Evaluated On", fontSize = 10.sp, color = TextGray)
            Text(dateStr, fontSize = 12.sp, color = TextDark)
        }
        Spacer(modifier = Modifier.width(12.dp))
        ScoreRing(percent = percent, obtained = obtained, total = total)
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Score ring
// ──────────────────────────────────────────────────────────────────────────────
@Composable
private fun ScoreRing(percent: Float, obtained: Int, total: Int) {
    val sweep = (percent / 100f) * 300f
    Box(modifier = Modifier.size(110.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke   = 11.dp.toPx()
            val diameter = size.minDimension - stroke
            val tl       = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val sz       = Size(diameter, diameter)
            drawArc(color = TrackGray, startAngle = 120f, sweepAngle = 300f, useCenter = false, topLeft = tl, size = sz, style = Stroke(stroke, cap = StrokeCap.Round))
            if (sweep > 0f) drawArc(color = Green, startAngle = 120f, sweepAngle = sweep, useCenter = false, topLeft = tl, size = sz, style = Stroke(stroke, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${percent.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Green)
            Text("$obtained / $total", fontSize = 11.sp, color = TextGray, textAlign = TextAlign.Center)
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Stats row
// ──────────────────────────────────────────────────────────────────────────────
@Composable
private fun StatsRow(correct: Int, incorrect: Int, partial: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg, RoundedCornerShape(16.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        StatCell("✅", "Correct",   correct,   Color(0xFF2E7D32))
        Box(modifier = Modifier.width(1.dp).height(36.dp).background(BorderColor))
        StatCell("❌", "Incorrect", incorrect, RedColor)
        Box(modifier = Modifier.width(1.dp).height(36.dp).background(BorderColor))
        StatCell("⚠️", "Partial",   partial,   OrangeColor)
        Box(modifier = Modifier.width(1.dp).height(36.dp).background(BorderColor))
        StatCell("ℹ️", "Total",     total,     BlueColor)
    }
}

@Composable
private fun StatCell(emoji: String, label: String, count: Int, countColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 16.sp)
        Text(label, fontSize = 10.sp, color = TextGray)
        Text("$count", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = countColor)
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Feedback card
// ──────────────────────────────────────────────────────────────────────────────
@Composable
private fun FeedbackCard(feedback: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenLight, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Text("💬  Overall Feedback", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Green)
        Spacer(modifier = Modifier.height(6.dp))
        Text(feedback, fontSize = 13.sp, color = TextDark, lineHeight = 19.sp)
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Filter row
// ──────────────────────────────────────────────────────────────────────────────
@Composable
private fun FilterRow(
    active        : Filter,
    correctCount  : Int,
    incorrectCount: Int,
    partialCount  : Int,
    onSelect      : (Filter) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        FilterChip("All",             active == Filter.ALL,       { onSelect(Filter.ALL) },       Modifier.weight(1f))
        FilterChip("✓$correctCount",  active == Filter.CORRECT,   { onSelect(Filter.CORRECT) },   Modifier.weight(1f))
        FilterChip("✗$incorrectCount",active == Filter.INCORRECT, { onSelect(Filter.INCORRECT) }, Modifier.weight(1f))
        FilterChip("~$partialCount",  active == Filter.PARTIAL,   { onSelect(Filter.PARTIAL) },   Modifier.weight(1f))
    }
}

@Composable
private fun FilterChip(label: String, active: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val chipInteractionSource = rememberSafeInteractionSource()

    Box(
        modifier = modifier
            .background(if (active) Green else CardBg, RoundedCornerShape(50))
            .border(1.dp, if (active) Green else BorderColor, RoundedCornerShape(50))
            .clickable(
                interactionSource = chipInteractionSource,
                indication = null
            ) {
                onClick()
            }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            fontSize   = 11.sp,
            color      = if (active) Color.White else TextGray,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
            textAlign  = TextAlign.Center,
            maxLines   = 1
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Question card
// ──────────────────────────────────────────────────────────────────────────────
@Composable
private fun QuestionCard(q: Question) {
    val isCorrect   = q.marksObtained == q.maxMarks && q.maxMarks > 0
    val isIncorrect = q.marksObtained == 0
    val statusLabel = when { isCorrect -> "Correct"; isIncorrect -> "Incorrect"; else -> "Partial" }
    val statusColor = when { isCorrect -> Color(0xFF2E7D32); isIncorrect -> RedColor; else -> OrangeColor }
    val statusBg    = when { isCorrect -> GreenLight; isIncorrect -> RedBg; else -> OrangeBg }

    var expanded by remember { mutableStateOf(true) }
    val cardInteractionSource = rememberSafeInteractionSource()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg, RoundedCornerShape(16.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = cardInteractionSource,
                    indication = null
                ) {
                    expanded = !expanded
                }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier         = Modifier.size(36.dp).background(GreenLight, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Q${q.questionNumber}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Green)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("${q.maxMarks} Marks", fontSize = 11.sp, color = TextGray)
                    if (q.suggestion.isNotBlank()) {
                        Text(q.suggestion, fontSize = 12.sp, color = TextDark, maxLines = 2, lineHeight = 17.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(statusLabel, fontSize = 10.sp, color = statusColor, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("${q.marksObtained} / ${q.maxMarks}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = statusColor)
            }
        }

        if (expanded) {
            HorizontalDivider(color = BorderColor, thickness = 1.dp)
            Column(
                modifier            = Modifier.fillMaxWidth().padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (q.correctPoints.isNotEmpty()) {
                    DetailBox("✅", "What's Correct",        Color(0xFF2E7D32), GreenLight, q.correctPoints, Color(0xFF1B5E20))
                }
                if (q.mistakes.isNotEmpty()) {
                    DetailBox(
                        "❌",
                        when { isCorrect -> "Why No Marks Deducted"; isIncorrect -> "What's Incorrect"; else -> "Why ${q.maxMarks - q.marksObtained} Mark(s) Deducted" },
                        RedColor, RedBg, q.mistakes, Color(0xFF7F0000)
                    )
                }
                val reason = q.reasoning.trim()
                if (reason.isNotBlank() && reason != "Attempted" && reason != "Not Attempted") {
                    DetailBox("💡", "What You Should Include", BlueColor, BlueBg, listOf(reason), Color(0xFF0D3C61))
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Detail box
// ──────────────────────────────────────────────────────────────────────────────
@Composable
private fun DetailBox(emoji: String, heading: String, hColor: Color, bg: Color, items: List<String>, tColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(heading, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = hColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        items.forEach { point ->
            if (point.isNotBlank()) {
                Text("• $point", fontSize = 12.sp, color = tColor, lineHeight = 18.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
            }
        }
    }
}