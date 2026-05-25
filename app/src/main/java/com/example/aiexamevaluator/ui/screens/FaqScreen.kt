package com.example.aiexamevaluator.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PrimaryGreen = Color(0xFF0B6E4F)
private val LightGreen = Color(0xFFEAF6F0)
private val DarkText = Color(0xFF1B1B1B)
private val LightText = Color(0xFF7A7A7A)
private val CardBg = Color.White
private val BorderColor = Color(0xFFE0E0E0)

private data class FaqItem(
    val question: String,
    val answer: String,
)

@Composable
private fun rememberSafeInteractionSource() = remember { MutableInteractionSource() }

@Composable
fun FaqScreen(
    onBackClick: () -> Unit,
) {
    val faqs = listOf(
        FaqItem(
            "What is AIExamEvaluator?",
            "AIExamEvaluator is an Android application that assists teachers in evaluating examination papers and generating detailed assessment reports."
        ),
        FaqItem(
            "Does the application support subjective questions?",
            "Yes. The application supports both subjective and objective examination assessment workflows."
        ),
        FaqItem(
            "Can evaluation reports be stored?",
            "Yes. Evaluations are stored locally through Room Database and can be reviewed later through the history section."
        ),
        FaqItem(
            "Can reports be exported?",
            "Yes. The application supports report generation and export functionality."
        ),
        FaqItem(
            "Do I need an internet connection?",
            "Internet connectivity may be required for AI assisted evaluation features depending on the configured backend services."
        ),
        FaqItem(
            "Can I review previous evaluations?",
            "Yes. The History section allows teachers to revisit previously generated evaluations."
        ),
        FaqItem(
            "Is my data secure?",
            "User authentication and structured data management are used to improve data security and account protection."
        ),
        FaqItem(
            "Can I evaluate multiple pages?",
            "Yes. The system supports uploading and evaluating multiple answer sheet pages."
        ),
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { FaqTopBar(onBackClick = onBackClick) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(faqs.size) { index ->
                FaqCard(faqs[index])
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }
        }
    }
}

@Composable
private fun FaqTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = rememberSafeInteractionSource(),
                    indication = null,
                ) { onBackClick() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Text(
            text = "Frequently Asked Questions",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
private fun FaqCard(item: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = rememberSafeInteractionSource(),
                    indication = null,
                ) { expanded = !expanded }
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.question,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(LightGreen),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            if (expanded) {
                Text(
                    text = item.answer,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                )
            }
        }
    }
}