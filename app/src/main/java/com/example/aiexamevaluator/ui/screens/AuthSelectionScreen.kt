package com.example.aiexamevaluator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiexamevaluator.R

@Composable
fun AuthSelectionScreen(
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit
) {

    val primaryGreen = Color(0xFF0D6B3C)
    val lightGreen = Color(0xFFEAF6EE)
    val darkText = Color(0xFF1F2937)
    val subText = Color(0xFF667085)

    Scaffold(
        containerColor = Color.White
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Background circles
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .offset(x = (-120).dp, y = (-40).dp)
                    .clip(CircleShape)
                    .background(lightGreen.copy(alpha = 0.4f))
            )

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .offset(x = 260.dp, y = 420.dp)
                    .clip(CircleShape)
                    .background(lightGreen.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(70.dp))

                Icon(
                    painter = painterResource(id = R.drawable.exameval_logo),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(240.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Smart evaluation for a smarter tomorrow.",
                    fontSize = 22.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = darkText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Evaluate MCQ and subjective answer sheets accurately, quickly and effortlessly with the power of AI.",
                    fontSize = 18.sp,
                    lineHeight = 30.sp,
                    textAlign = TextAlign.Center,
                    color = subText
                )

                Spacer(modifier = Modifier.weight(1f))

                // Sign Up Button
                Button(
                    onClick = onSignUpClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(24.dp),
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
                            )
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "👤",
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.width(18.dp))

                            Column {

                                Text(
                                    text = "Sign Up",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Create a new account",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Login Button
                OutlinedButton(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            listOf(primaryGreen, primaryGreen)
                        )
                    )
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "➜",
                            fontSize = 18.sp,
                            color = primaryGreen
                        )

                        Spacer(modifier = Modifier.width(18.dp))

                        Column {

                            Text(
                                text = "Log In",
                                color = primaryGreen,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Welcome back!",
                                color = subText,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}
