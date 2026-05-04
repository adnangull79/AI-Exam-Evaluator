package com.example.aiexamevaluator.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage

// ─────────────────────────────────────────────────────────────────────────────
// SAFE clickable helper — NO ripple, NO indication.
// This is the ONLY clickable pattern used in this file to avoid the
// "clickable only supports IndicationNodeFactory" crash that happens when
// androidx.compose.material (M2) ripple leaks into a Material3 project.
// ─────────────────────────────────────────────────────────────────────────────
private fun Modifier.safeClickable(onClick: () -> Unit): Modifier =
    this.clickable(
        interactionSource = MutableInteractionSource(),
        indication = null,
        onClick = onClick
    )

private fun appendUrisIfWithinLimit(current: List<Uri>, newUris: List<Uri>, max: Int): List<Uri> =
    if (current.size + newUris.size <= max) current + newUris else current

// ─────────────────────────────────────────────────────────────────────────────
// EvaluationScreen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun EvaluationScreen(onBackClick: () -> Unit = {}) {

    val primaryGreen = Color(0xFF1B5E3C)

    var isMcqSelected by remember { mutableStateOf(true) }
    var studentName   by remember { mutableStateOf("") }
    var rollNumber    by remember { mutableStateOf("") }
    var totalMarks    by remember { mutableStateOf("") }
    var questionImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var answerImages   by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val mcqImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            answerImages = appendUrisIfWithinLimit(answerImages, uris, 10)
        }
    }

    val questionImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            questionImages = appendUrisIfWithinLimit(questionImages, uris, 2)
        }
    }

    val answerImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            answerImages = appendUrisIfWithinLimit(answerImages, uris, 10)
        }
    }

    Scaffold(
        topBar          = { EvaluationTopBar(onBackClick) },
        containerColor  = Color(0xFFF5F7F6)
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            EvaluationTabs(primaryGreen, isMcqSelected) { isMcqSelected = it }

            Spacer(modifier = Modifier.height(16.dp))

            if (isMcqSelected) {
                UploadSectionWithPreview(
                    title    = "MCQ Answer Sheet",
                    subtitle = "Upload MCQ sheets (max 10 images)",
                    images   = answerImages,
                    max      = 10,
                    onAdd    = { mcqImagePickerLauncher.launch("image/*") },
                    onRemove = { answerImages = answerImages - it }
                )
            } else {
                UploadSectionWithPreview(
                    title    = "Question Paper",
                    subtitle = "Upload max 2 images",
                    images   = questionImages,
                    max      = 2,
                    onAdd    = { questionImagePickerLauncher.launch("image/*") },
                    onRemove = { questionImages = questionImages - it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                UploadSectionWithPreview(
                    title    = "Answer Sheets",
                    subtitle = "Upload max 10 images",
                    images   = answerImages,
                    max      = 10,
                    onAdd    = { answerImagePickerLauncher.launch("image/*") },
                    onRemove = { answerImages = answerImages - it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Student Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value         = studentName,
                onValueChange = { studentName = it },
                label         = { Text("Student Name") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = primaryGreen,
                    unfocusedTextColor = primaryGreen,
                    cursorColor = primaryGreen,
                    focusedBorderColor = primaryGreen,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value         = rollNumber,
                onValueChange = { rollNumber = it },
                label         = { Text("Roll Number") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = primaryGreen,
                    unfocusedTextColor = primaryGreen,
                    cursorColor = primaryGreen,
                    focusedBorderColor = primaryGreen,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value         = totalMarks,
                onValueChange = { totalMarks = it },
                label         = { Text("Total Marks") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = primaryGreen,
                    unfocusedTextColor = primaryGreen,
                    cursorColor = primaryGreen,
                    focusedBorderColor = primaryGreen,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick  = { /* TODO: trigger evaluation */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
            ) {
                Text("Evaluate", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Upload section with image preview grid (Row+chunked, no LazyGrid)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun UploadSectionWithPreview(
    title    : String,
    subtitle : String,
    images   : List<Uri>,
    max      : Int,
    onAdd    : () -> Unit,
    onRemove : (Uri) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {

        // ── Header row ────────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text     = "${images.size}/$max",
                fontSize = 12.sp,
                color    = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Empty state ───────────────────────────────────────────────────────
        if (images.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📂", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tap 'Add Images' to upload", fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            // ── Image grid (3 per row, no LazyGrid) ──────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                images.chunked(3).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowItems.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .size(84.dp)
                                    .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = item,
                                    contentDescription = "Selected image",
                                    modifier = Modifier.matchParentSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // ── SAFE remove button — uses safeClickable ──
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(22.dp)
                                        .background(Color(0xFFE53935), RoundedCornerShape(50))
                                        .safeClickable { onRemove(item) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector        = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint               = Color.White,
                                        modifier           = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        // Fill remaining cells so the row always looks aligned
                        val emptyCells = 3 - rowItems.size
                        repeat(emptyCells) {
                            Spacer(modifier = Modifier.size(84.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Add Images button — uses Material3 Button (safe by default) ───────
        Button(
            onClick  = onAdd,
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(50),
            enabled  = images.size < max
        ) {
            Text(if (images.size < max) "Add Images" else "Max limit reached")
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top bar — SAFE back button using safeClickable
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun EvaluationTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // ── SAFE back icon ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(40.dp)
                .safeClickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint               = Color(0xFF1B5E3C)
            )
        }

        Text(
            text       = "Evaluation",
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp,
            color      = Color(0xFF1B5E3C)
        )

        Icon(
            imageVector        = Icons.Default.Person,
            contentDescription = null,
            tint               = Color(0xFF1B5E3C)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tabs — SAFE tab switching using safeClickable
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun EvaluationTabs(
    primaryGreen  : Color,
    isMcqSelected : Boolean,
    onChange      : (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
            .padding(4.dp)
    ) {
        TabItem(
            modifier  = Modifier.weight(1f),
            text      = "Objective (MCQ)",
            selected  = isMcqSelected,
            color     = primaryGreen,
            onClick   = { onChange(true) }
        )
        TabItem(
            modifier  = Modifier.weight(1f),
            text      = "Subjective",
            selected  = !isMcqSelected,
            color     = primaryGreen,
            onClick   = { onChange(false) }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Individual tab item — uses safeClickable to prevent ripple crash
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun TabItem(
    modifier : Modifier = Modifier,
    text     : String,
    selected : Boolean,
    color    : Color,
    onClick  : () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = if (selected) color else Color.Transparent,
                shape = RoundedCornerShape(50)
            )
            // ── SAFE: no ripple, no indication ────────────────────────────
            .safeClickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = text,
            color      = if (selected) Color.White else Color(0xFF424242),
            fontSize   = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}