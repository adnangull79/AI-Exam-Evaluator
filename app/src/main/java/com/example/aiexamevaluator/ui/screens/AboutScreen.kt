package com.example.aiexamevaluator.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.unit.Dp
import com.example.aiexamevaluator.R

private val PrimaryGreen = Color(0xFF0B6E4F)
private val LightGreen = Color(0xFFEAF6F0)
private val DarkText = Color(0xFF1B1B1B)
private val LightText = Color(0xFF7A7A7A)
private val CardBg = Color.White
private val BorderColor = Color(0xFFE0E0E0)

@Composable
private fun rememberSafeInteractionSource() = remember { MutableInteractionSource() }

@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
) {
    Scaffold(
        containerColor = Color(0xFFF5F7F6),
        topBar = { AboutTopBar(onBackClick = onBackClick) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AppHeroSection()
            AboutCard()
            TeamCard()
            ContactCard()
            VersionCard()
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun AboutTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg)
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
                tint = PrimaryGreen,
            )
        }

        Text(
            text = "About AIExamEvaluator",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = PrimaryGreen,
        )

        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
private fun AppHeroSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.exameval_logo),
                contentDescription = "AIExamEvaluator logo",
                modifier = Modifier.size(120.dp),
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "AIExamEvaluator",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "AI Powered Examination Assessment System",
                fontSize = 13.sp,
                color = LightText,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun AboutCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionTitle("About")

            Text(
                text = "AIExamEvaluator is a modern Android application developed to simplify and improve the examination evaluation process. The system allows teachers to evaluate objective and subjective answer sheets efficiently using artificial intelligence assisted analysis.",
                color = DarkText,
                fontSize = 14.sp,
                lineHeight = 21.sp,
            )

            Text(
                text = "The application helps educators save time, improve consistency, generate detailed feedback, maintain evaluation history, and manage assessment records through a modern user friendly interface.",
                color = DarkText,
                fontSize = 14.sp,
                lineHeight = 21.sp,
            )

            Text(
                text = "Key Features:",
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FeatureRow("Objective Exam Evaluation")
                FeatureRow("Subjective Answer Assessment")
                FeatureRow("Detailed Feedback Generation")
                FeatureRow("PDF Report Generation")
                FeatureRow("Evaluation History")
                FeatureRow("Secure User Authentication")
                FeatureRow("Modern Android Interface")
                FeatureRow("AI Assisted Assessment Support")
            }
        }
    }
}

@Composable
private fun TeamCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionTitle("Development Team")

            TeamMemberRow("Lead Developer", "John Anderson")
            TeamMemberRow("Android Developer", "Sarah Williams")
            TeamMemberRow("UI/UX Designer", "Michael Clark")
        }
    }
}

@Composable
private fun ContactCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionTitle("Contact Information")

            ContactRow(Icons.Default.Email, "support@exameval.ai", "Email")
            ContactRow(Icons.Default.Phone, "+1 123 456 7890", "Phone")
            ContactRow(Icons.Default.Language, "www.exameval.ai", "Website")
        }
    }
}

@Composable
private fun VersionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SectionTitle("Version")
            Text(
                text = "Version 1.0.0",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkText,
            )
            Text(
                text = "Build: Release Candidate",
                fontSize = 13.sp,
                color = LightText,
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = PrimaryGreen,
    )
}

@Composable
private fun FeatureRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(PrimaryGreen, CircleShape),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, color = DarkText, fontSize = 14.sp)
    }
}

@Composable
private fun TeamMemberRow(role: String, name: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = role, color = LightText, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = name, color = DarkText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
    HorizontalDivider(color = BorderColor.copy(alpha = 0.6f))
}

@Composable
private fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(LightGreen),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryGreen)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = LightText, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = DarkText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}