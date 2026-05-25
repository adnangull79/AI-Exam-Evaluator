package com.example.aiexamevaluator.ui.screens

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun rememberSafeInteractionSource() = remember { MutableInteractionSource() }

// ── Shared colours (internal so HistoryScreen can use them) ───────────────────
internal val AppGreen      = Color(0xFF1B5E3C)
internal val AppGreenLight = Color(0xFFE8F5E9)
internal val AppGray       = Color(0xFF757575)
internal val AppFabColor   = Color(0xFF1B5E3C)
internal val AppCardBg     = Color.White
internal val AppBorder     = Color(0xFFE0E0E0)

// ── Which tab is active ────────────────────────────────────────────────────────
enum class BottomTab { HISTORY, SETTINGS, NONE }

// ──────────────────────────────────────────────────────────────────────────────
// UnifiedBottomBar — used by BOTH HomeScreen and HistoryScreen
//
// Layout:  [ History ]  [ + FAB (elevated) ]  [ Settings ]
// FAB floats above the bar via offset(y = -20.dp)
// ──────────────────────────────────────────────────────────────────────────────
@Composable
fun UnifiedBottomBar(
    activeTab      : BottomTab,
    onHistoryClick : () -> Unit,
    onFabClick     : () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val fabInteractionSource = rememberSafeInteractionSource()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)                     // total height including FAB overhang
    ) {
        // White bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(MaterialTheme.colorScheme.surface)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // History
            NavTabItem(
                label   = "History",
                active  = activeTab == BottomTab.HISTORY,
                onClick = onHistoryClick
            ) {
                Icon(
                    imageVector        = Icons.Default.Menu,
                    contentDescription = null,
                    tint               = if (activeTab == BottomTab.HISTORY) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(22.dp)
                )
            }

            // Empty space for FAB
            Spacer(modifier = Modifier.width(64.dp))

            // Settings
            NavTabItem(
                label   = "Settings",
                active  = activeTab == BottomTab.SETTINGS,
                onClick = onSettingsClick
            ) {
                Icon(
                    imageVector        = Icons.Default.Settings,
                    contentDescription = null,
                    tint               = if (activeTab == BottomTab.SETTINGS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(22.dp)
                )
            }
        }

        // FAB — elevated above bar
        Box(
            modifier = Modifier
                .size(62.dp)
                .offset(y = (-14).dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .clickable(
                    interactionSource = fabInteractionSource,
                    indication = null
                ) {
                    onFabClick()
                }
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.Add,
                contentDescription = "Start Evaluation",
                tint               = Color.White,
                modifier           = Modifier.size(30.dp)
            )
        }

        // "Start Evaluation" label under FAB
        Text(
            text      = "Start Evaluation",
            fontSize  = 10.sp,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier  = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

// ── Single tab item ────────────────────────────────────────────────────────────
@Composable
private fun NavTabItem(
    label  : String,
    active : Boolean,
    onClick: () -> Unit,
    icon   : @Composable () -> Unit,
) {
    val tabInteractionSource = rememberSafeInteractionSource()

    Column(
        modifier            = Modifier
            .clickable(
                interactionSource = tabInteractionSource,
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text       = label,
            fontSize   = 11.sp,
            color      = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// HomeScreen
// ──────────────────────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    onStartEvaluationClick: () -> Unit,
    onHistoryClick        : () -> Unit,
    onSettingsClick       : () -> Unit,
    modifier              : Modifier = Modifier,
) {
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        topBar         = { HomeTopBar() },
        bottomBar      = {
            UnifiedBottomBar(
                activeTab      = BottomTab.NONE,       // no tab active on home
                onHistoryClick = onHistoryClick,
                onFabClick     = onStartEvaluationClick,
                onSettingsClick= onSettingsClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color(0x112196F3), CircleShape)
                        .align(Alignment.TopEnd),
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color(0x112196F3), CircleShape)
                        .align(Alignment.BottomStart),
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text       = "Powered by AI",
                            color      = MaterialTheme.colorScheme.primary,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text       = "Evaluate Exams with AI",
                        modifier   = Modifier.fillMaxWidth(),
                        textAlign  = TextAlign.Center,
                        fontSize   = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text      = "Fast. Accurate. Effortless.",
                        modifier  = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize  = 14.sp,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick  = onStartEvaluationClick,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape    = RoundedCornerShape(50.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Text(
                            text       = "Start Evaluating →",
                            color      = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text       = "CORE CAPABILITIES",
                color      = MaterialTheme.colorScheme.primary,
                fontSize   = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Column(
                modifier            = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CapabilityCard("📄", "Subjective Papers", "Handwritten evaluation", secondaryText)
                CapabilityCard("☑",  "MCQ Papers",        "Auto grading",          secondaryText)
                CapabilityCard("📄", "Reports",            "Detailed insights",     secondaryText)
            }
        }
    }
}

// ── Top bar ────────────────────────────────────────────────────────────────────
@Composable
private fun HomeTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {


        Text(
            text = "ExamEval AI",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

// ── Capability card ────────────────────────────────────────────────────────────
@Composable
private fun CapabilityCard(
    icon         : String,
    title        : String,
    subtitle     : String,
    secondaryText: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = icon, fontSize = 26.sp, modifier = Modifier.size(26.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title,    color = MaterialTheme.colorScheme.onSurface,       fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant,  fontSize = 12.sp)
        }
    }
}