package com.example.aiexamevaluator.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.aiexamevaluator.R
import com.example.aiexamevaluator.data.model.EvaluationResponse
import com.example.aiexamevaluator.viewmodel.EvaluationViewModel

@Composable
private fun rememberSafeInteractionSource() = remember { MutableInteractionSource() }

private fun appendUrisIfWithinLimit(
    current: List<Uri>,
    newUris: List<Uri>,
    max    : Int,
): List<Uri> = if (current.size + newUris.size <= max) current + newUris else current

@Composable
fun EvaluationScreen(
    onBackClick: () -> Unit = {},
    viewModel  : EvaluationViewModel,
) {
    val primaryGreen = Color(0xFF1B5E3C)
    val uiState      by viewModel.uiState.collectAsState()
    val toastContext  = LocalContext.current
    val focusManager  = LocalFocusManager.current

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loading_files),
    )

    val lottieProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations  = LottieConstants.IterateForever,
        isPlaying   = true,
    )

    var isMcqSelected  by remember { mutableStateOf(true) }
    var studentName    by remember { mutableStateOf("") }
    var rollNumber     by remember { mutableStateOf("") }
    var totalMarks     by remember { mutableStateOf("") }
    var questionImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var answerImages   by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var showResult        by remember { mutableStateOf(false) }
    var resultStudentName by remember { mutableStateOf("") }
    var resultRollNumber  by remember { mutableStateOf("") }
    var resultTotalMarks  by remember { mutableStateOf(0) }
    var resultData        by remember { mutableStateOf<EvaluationResponse?>(null) }

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) focusManager.clearFocus()
    }

    LaunchedEffect(uiState.result) {
        val result = uiState.result
        if (result != null) {
            resultStudentName = uiState.studentName
            resultRollNumber  = uiState.rollNumber
            resultTotalMarks  = uiState.totalMarks
            resultData        = result
            showResult        = true
            viewModel.clearResult()
        }
    }

    LaunchedEffect(uiState.error) {
        val error = uiState.error
        if (error != null) {
            Toast.makeText(toastContext, error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    val mcqImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) { uris -> if (uris.isNotEmpty()) answerImages = appendUrisIfWithinLimit(answerImages, uris, 10) }

    val questionImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) { uris -> if (uris.isNotEmpty()) questionImages = appendUrisIfWithinLimit(questionImages, uris, 2) }

    val answerImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) { uris -> if (uris.isNotEmpty()) answerImages = appendUrisIfWithinLimit(answerImages, uris, 10) }

    if (showResult && resultData != null) {
        ResultScreen(
            studentName       = resultStudentName,
            rollNumber        = resultRollNumber,
            totalMarks        = resultTotalMarks,
            result            = resultData!!,
            onEvaluateAnother = onBackClick,
        )
        return
    }

    // ── ✅ Full screen loading — replaces the entire screen, nothing behind visible
    if (uiState.isLoading) {
        val loadingInteractionSource = rememberSafeInteractionSource()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7F6))      // ← same bg as the form, fully opaque
                .clickable(
                    interactionSource = loadingInteractionSource,
                    indication = null
                ) { /* block all taps */ },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                LottieAnimation(
                    composition = composition,
                    progress    = { lottieProgress },
                    modifier    = Modifier
                        .fillMaxWidth()
                        .height(320.dp),            // ← large, full-width animation
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text       = "Evaluating Answer Sheets...",
                    style      = MaterialTheme.typography.titleLarge,
                    color      = primaryGreen,
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text      = "This may take a few seconds",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = Color.Gray,
                    textAlign = TextAlign.Center,
                )
            }
        }
        return   // ← nothing below renders while loading
    }

    // ── Normal form UI (only shown when NOT loading) ──────────────────────────
    Scaffold(
        topBar         = { EvaluationTopBar(onBackClick) },
        containerColor = Color(0xFFF5F7F6),
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
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
                    onRemove = { answerImages = answerImages - it },
                )
            } else {
                UploadSectionWithPreview(
                    title    = "Question Paper",
                    subtitle = "Upload max 2 images",
                    images   = questionImages,
                    max      = 2,
                    onAdd    = { questionImagePickerLauncher.launch("image/*") },
                    onRemove = { questionImages = questionImages - it },
                )
                Spacer(modifier = Modifier.height(16.dp))
                UploadSectionWithPreview(
                    title    = "Answer Sheets",
                    subtitle = "Upload max 10 images",
                    images   = answerImages,
                    max      = 10,
                    onAdd    = { answerImagePickerLauncher.launch("image/*") },
                    onRemove = { answerImages = answerImages - it },
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text       = "Student Information",
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value         = studentName,
                onValueChange = { studentName = it },
                label         = { Text("Student Name") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                colors        = outlinedFieldColors(primaryGreen),
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value         = rollNumber,
                onValueChange = { rollNumber = it },
                label         = { Text("Roll Number") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                colors        = outlinedFieldColors(primaryGreen),
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value         = totalMarks,
                onValueChange = { totalMarks = it },
                label         = { Text("Total Marks") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                colors        = outlinedFieldColors(primaryGreen),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.evaluateExam(
                        questionImages = if (isMcqSelected) emptyList() else questionImages,
                        answerImages   = answerImages,
                        name           = studentName,
                        roll           = rollNumber,
                        totalMarks     = totalMarks.toIntOrNull() ?: 0,
                        isMcq          = isMcqSelected,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape   = RoundedCornerShape(50),
                colors  = ButtonDefaults.buttonColors(containerColor = primaryGreen),
            ) {
                Text("Evaluate", color = Color.White, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun outlinedFieldColors(green: Color) = OutlinedTextFieldDefaults.colors(
    focusedTextColor     = green,
    unfocusedTextColor   = green,
    cursorColor          = green,
    focusedBorderColor   = green,
    unfocusedBorderColor = Color.Gray,
)

@Composable
fun UploadSectionWithPreview(
    title   : String,
    subtitle: String,
    images  : List<Uri>,
    max     : Int,
    onAdd   : () -> Unit,
    onRemove: (Uri) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title,    fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, fontSize   = 12.sp, color = Color.Gray)
            }
            Text(text = "${images.size}/$max", fontSize = 12.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (images.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📂", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tap 'Add Images' to upload", fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                images.chunked(3).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowItems.forEach { uri ->
                            Box(
                                modifier = Modifier
                                    .size(84.dp)
                                    .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                            ) {
                                AsyncImage(
                                    model              = uri,
                                    contentDescription = "Selected image",
                                    modifier           = Modifier.matchParentSize(),
                                    contentScale       = ContentScale.Crop,
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(22.dp)
                                        .background(Color(0xFFE53935), RoundedCornerShape(50))
                                            .clickable(
                                                interactionSource = rememberSafeInteractionSource(),
                                                indication = null
                                            ) { onRemove(uri) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector        = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint               = Color.White,
                                        modifier           = Modifier.size(14.dp),
                                    )
                                }
                            }
                        }
                        repeat(3 - rowItems.size) { Spacer(modifier = Modifier.size(84.dp)) }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick  = onAdd,
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(50),
            enabled  = images.size < max,
        ) {
            Text(if (images.size < max) "Add Images" else "Max limit reached")
        }
    }
}

@Composable
fun EvaluationTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        val backInteractionSource = rememberSafeInteractionSource()

        Box(
            modifier         = Modifier.size(40.dp).clickable(
                interactionSource = backInteractionSource,
                indication = null
            ) { onBackClick() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint               = Color(0xFF1B5E3C),
            )
        }
        Text(
            text       = "Evaluation",
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp,
            color      = Color(0xFF1B5E3C),
        )
        Icon(
            imageVector        = Icons.Default.Person,
            contentDescription = null,
            tint               = Color(0xFF1B5E3C),
        )
    }
}

@Composable
fun EvaluationTabs(
    primaryGreen  : Color,
    isMcqSelected : Boolean,
    onChange      : (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
            .padding(4.dp),
    ) {
        TabItem(Modifier.weight(1f), "Objective (MCQ)", isMcqSelected,  primaryGreen) { onChange(true) }
        TabItem(Modifier.weight(1f), "Subjective",      !isMcqSelected, primaryGreen) { onChange(false) }
    }
}

@Composable
fun TabItem(
    modifier : Modifier = Modifier,
    text     : String,
    selected : Boolean,
    color    : Color,
    onClick  : () -> Unit,
) {
    val tabInteractionSource = rememberSafeInteractionSource()

    Box(
        modifier = modifier
            .background(if (selected) color else Color.Transparent, RoundedCornerShape(50))
            .clickable(
                interactionSource = tabInteractionSource,
                indication = null
            ) { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text       = text,
            color      = if (selected) Color.White else Color(0xFF424242),
            fontSize   = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}