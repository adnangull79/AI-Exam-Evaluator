package com.example.aiexamevaluator.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
// removed rememberScrollState for non-scrolling layout
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
// removed verticalScroll for non-scrolling layout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.aiexamevaluator.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {

    val primaryGreen = MaterialTheme.colorScheme.primary
    val lightGreen = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
    val darkText = MaterialTheme.colorScheme.onSurface
    val subText = MaterialTheme.colorScheme.onSurfaceVariant

    // Logo is loaded from drawable resources: R.drawable.exameval_logo

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.exameval_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(260.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(lightGreen)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "✦",
                    color = lightGreen,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(lightGreen)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Smart evaluation for a smarter tomorrow.\nEvaluate MCQ and subjective answer sheets\naccurately and effortlessly.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                FeatureItem(
                    title = "Accurate\nEvaluations",
                    subtitle = "AI-powered accuracy",
                    primaryGreen = primaryGreen,
                    lightGreen = lightGreen
                )
                FeatureItem(
                    title = "Save Time\nEffortlessly",
                    subtitle = "Evaluate more in",
                    primaryGreen = primaryGreen,
                    lightGreen = lightGreen
                )

                Box(
                    modifier = Modifier
                        .height(90.dp)
                        .width(1.dp)
                        .background(Color(0xFFE5E7EB))
                )

                FeatureItem(
                    title = "Secure &\nReliable",
                    subtitle = "Your data is safe",
                    primaryGreen = primaryGreen,
                    lightGreen = lightGreen
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF0B5D33),
                                    Color(0xFF0D6B3C),
                                    Color(0xFF128248)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Get Started   →",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FeatureItem(
    title: String,
    subtitle: String,
    primaryGreen: Color,
    lightGreen: Color
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {

        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(lightGreen),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "✓",
                color = primaryGreen,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = title,
            color = primaryGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
