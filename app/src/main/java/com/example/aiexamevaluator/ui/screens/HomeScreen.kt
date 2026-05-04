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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings

@Composable
fun HomeScreen(
    onStartEvaluationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = Color(0xFFF5F7F6)
    val cardColor = Color.White
    val primaryGreen = Color(0xFF1B5E3C)
    val primaryText = primaryGreen
    val secondaryText = Color(0xFF7A7A7A)
    val accent = primaryGreen
    val borderColor = Color(0xFFE0E0E0)

    var selectedTab by rememberSaveable { mutableStateOf("Home") }

    Scaffold(
        topBar = { HomeTopBar(primaryText = primaryText) },
        bottomBar = {
            HomeBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                primaryText = primaryText,
                secondaryText = secondaryText,
            )
        },
        containerColor = backgroundColor,
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

            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
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
                            .background(Color(0xFFE6F4EA), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "Powered by AI",
                            color = primaryGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Evaluate Exams with AI",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Fast. Accurate. Effortless.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = secondaryText,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onStartEvaluationClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accent),
                    ) {
                        Text(
                            text = "Start Evaluating →",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "CORE CAPABILITIES",
                color = primaryGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CapabilityCard(
                    icon = "📄",
                    title = "Subjective Papers",
                    subtitle = "Handwritten evaluation",
                    cardColor = cardColor,
                    borderColor = borderColor,
                    primaryText = primaryText,
                    secondaryText = secondaryText,
                )

                CapabilityCard(
                    icon = "☑",
                    title = "MCQ Papers",
                    subtitle = "Auto grading",
                    cardColor = cardColor,
                    borderColor = borderColor,
                    primaryText = primaryText,
                    secondaryText = secondaryText,
                )

                CapabilityCard(
                    icon = "📄",
                    title = "Reports",
                    subtitle = "Detailed insights",
                    cardColor = cardColor,
                    borderColor = borderColor,
                    primaryText = primaryText,
                    secondaryText = secondaryText,
                )
            }
        }
    }
}

@Composable
private fun HomeTopBar(primaryText: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            tint = Color(0xFF1B5E3C),
        )

        Text(
            text = "ExamEval AI",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1B5E3C),
        )

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFE6F4EA), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF1B5E3C),
            )
        }
    }
}

@Composable
private fun HomeBottomBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    primaryText: Color,
    secondaryText: Color,
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = selectedTab == "Home",
            onClick = { onTabSelected("Home") },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1B5E3C),
                selectedTextColor = Color(0xFF1B5E3C),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
            )
        )
        NavigationBarItem(
            selected = selectedTab == "History",
            onClick = { onTabSelected("History") },
            icon = { Icon(Icons.Default.Menu, contentDescription = null) },
            label = { Text("History") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1B5E3C),
                selectedTextColor = Color(0xFF1B5E3C),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
            )
        )
        NavigationBarItem(
            selected = selectedTab == "Settings",
            onClick = { onTabSelected("Settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1B5E3C),
                selectedTextColor = Color(0xFF1B5E3C),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
            )
        )
    }
}

@Composable
private fun CapabilityCard(
    icon: String,
    title: String,
    subtitle: String,
    cardColor: Color,
    borderColor: Color,
    primaryText: Color,
    secondaryText: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .background(cardColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = icon,
            fontSize = 26.sp,
            modifier = Modifier.size(26.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                color = primaryText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = secondaryText,
                fontSize = 12.sp,
            )
        }
    }
}
