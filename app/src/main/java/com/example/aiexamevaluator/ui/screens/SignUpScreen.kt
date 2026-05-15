package com.example.aiexamevaluator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiexamevaluator.R

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onLoginClick: () -> Unit
) {

    val primaryGreen = Color(0xFF0D6B3C)
    val lightGreen = Color(0xFFEAF6EE)
    val darkText = Color(0xFF1F2937)
    val subText = Color(0xFF0D6B3C)

    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Decorative background
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .offset(x = (-120).dp, y = (-40).dp)
                    .clip(CircleShape)
                    .background(lightGreen.copy(alpha = 0.35f))
            )

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .offset(x = 260.dp, y = 620.dp)
                    .clip(CircleShape)
                    .background(lightGreen.copy(alpha = 0.25f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(18.dp))

                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = primaryGreen
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "Sign Up",
                            color = primaryGreen,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Icon(
                    painter = painterResource(id = R.drawable.exameval_logo),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(180.dp)
                )


                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Create your account",
                    color = darkText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Join ExamEval AI and start evaluating smarter",
                    color = subText,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        // Full Name
                        Text(
                            text = "Full Name",
                            color = primaryGreen,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Enter your full name")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = primaryGreen,
                                unfocusedTextColor = primaryGreen,
                                cursorColor = primaryGreen,
                                focusedBorderColor = primaryGreen,
                                unfocusedBorderColor = primaryGreen.copy(alpha = 0.6f),
                                focusedLeadingIconColor = primaryGreen,
                                unfocusedLeadingIconColor = primaryGreen.copy(alpha = 0.6f),
                                focusedPlaceholderColor = primaryGreen.copy(alpha = 0.5f),
                                unfocusedPlaceholderColor = primaryGreen.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(18.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Email
                        Text(
                            text = "Email Address",
                            color = primaryGreen,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Enter your email address")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = primaryGreen,
                                unfocusedTextColor = primaryGreen,
                                cursorColor = primaryGreen,
                                focusedBorderColor = primaryGreen,
                                unfocusedBorderColor = primaryGreen.copy(alpha = 0.6f),
                                focusedLeadingIconColor = primaryGreen,
                                unfocusedLeadingIconColor = primaryGreen.copy(alpha = 0.6f),
                                focusedPlaceholderColor = primaryGreen.copy(alpha = 0.5f),
                                unfocusedPlaceholderColor = primaryGreen.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(18.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Password
                        Text(
                            text = "Password",
                            color = primaryGreen,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Create a password")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {

                                IconButton(
                                    onClick = {
                                        passwordVisible = !passwordVisible
                                    }
                                ) {

                                    Icon(
                                        imageVector = if (passwordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = primaryGreen,
                                unfocusedTextColor = primaryGreen,
                                cursorColor = primaryGreen,
                                focusedBorderColor = primaryGreen,
                                unfocusedBorderColor = primaryGreen.copy(alpha = 0.6f),
                                focusedLeadingIconColor = primaryGreen,
                                unfocusedLeadingIconColor = primaryGreen.copy(alpha = 0.6f),
                                focusedTrailingIconColor = primaryGreen,
                                unfocusedTrailingIconColor = primaryGreen.copy(alpha = 0.6f),
                                focusedPlaceholderColor = primaryGreen.copy(alpha = 0.5f),
                                unfocusedPlaceholderColor = primaryGreen.copy(alpha = 0.4f)
                            ),
                            visualTransformation =
                            if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            shape = RoundedCornerShape(18.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Confirm Password
                        Text(
                            text = "Confirm Password",
                            color = primaryGreen,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Confirm your password")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {

                                IconButton(
                                    onClick = {
                                        confirmPasswordVisible =
                                            !confirmPasswordVisible
                                    }
                                ) {

                                    Icon(
                                        imageVector = if (confirmPasswordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = primaryGreen,
                                unfocusedTextColor = primaryGreen,
                                cursorColor = primaryGreen,
                                focusedBorderColor = primaryGreen,
                                unfocusedBorderColor = primaryGreen.copy(alpha = 0.6f),
                                focusedLeadingIconColor = primaryGreen,
                                unfocusedLeadingIconColor = primaryGreen.copy(alpha = 0.6f),
                                focusedTrailingIconColor = primaryGreen,
                                unfocusedTrailingIconColor = primaryGreen.copy(alpha = 0.6f),
                                focusedPlaceholderColor = primaryGreen.copy(alpha = 0.5f),
                                unfocusedPlaceholderColor = primaryGreen.copy(alpha = 0.4f)
                            ),
                            visualTransformation =
                            if (confirmPasswordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            shape = RoundedCornerShape(18.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Password rules
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                            Arrangement.SpaceBetween
                        ) {

                            PasswordRule("✓ 8+ chars", primaryGreen)
                            PasswordRule("✓ Number", primaryGreen)
                            PasswordRule("✓ Special", primaryGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Create Button
                Button(
                    onClick = onCreateAccountClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp),
                    shape = RoundedCornerShape(22.dp),
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
                            text = "Create Account",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Already have an account? ",
                        color = subText,
                        fontSize = 16.sp
                    )

                    TextButton(
                        onClick = onLoginClick
                    ) {

                        Text(
                            text = "Log In",
                            color = primaryGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun PasswordRule(
    text: String,
    color: Color
) {

    Text(
        text = text,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
}