package com.example.aiexamevaluator.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aiexamevaluator.viewmodel.UserViewModel
import com.example.aiexamevaluator.viewmodel.UserViewModelFactory
import android.widget.Toast

private val PrimaryGreen = Color(0xFF0B6E4F)
private val LightGreen = Color(0xFFEAF6F0)
private val DarkText = Color(0xFF1B1B1B)
private val LightText = Color(0xFF7A7A7A)
private val CardBg = Color.White
private val BorderColor = Color(0xFFE0E0E0)
private val DangerRed = Color(0xFFD32F2F)
private val DangerBg = Color(0xFFFFEBEE)

@Composable
private fun rememberSafeInteractionSource() = remember { MutableInteractionSource() }

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory()
    )
    val uiState by userViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<String?>(null) }
    var fullNameInput by remember { mutableStateOf(uiState.fullName) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    BackHandler(onBack = onBackClick)

    LaunchedEffect(uiState.updateSuccess, pendingAction) {
        if (uiState.updateSuccess && pendingAction != null) {
            when (pendingAction) {
                "edit_name" -> {
                    Toast.makeText(context, "Full name updated.", Toast.LENGTH_SHORT).show()
                    showEditNameDialog = false
                }
                "change_password" -> {
                    Toast.makeText(context, "Password updated.", Toast.LENGTH_SHORT).show()
                    showPasswordDialog = false
                }
                "delete_account" -> {
                    onDeleteAccount()
                }
            }
            pendingAction = null
            userViewModel.clearUpdateSuccess()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7F6),
        topBar = { EditProfileTopBar(onBackClick = onBackClick) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                item {
                    ProfileHeader(
                        fullName = uiState.fullName,
                        email = uiState.email
                    )
                }

                item {
                    PersonalInformationCard(
                        title = "Personal Information",
                        subtitle = "Update your personal details",
                        fullName = uiState.fullName,
                        email = uiState.email,
                        onFullNameClick = {
                            fullNameInput = uiState.fullName
                            showEditNameDialog = true
                        },
                        items = listOf(
                            InfoRowItem("Full Name", uiState.fullName),
                            InfoRowItem("Email Address", uiState.email)
                        )
                    )
                }
            }
            item {
                ActionCard(
                    icon = Icons.Default.Lock,
                    title = "Change Password",
                    subtitle = "Update your account password",
                    accentColor = LightGreen,
                    contentColor = DarkText,
                    onClick = {
                        currentPassword = ""
                        newPassword = ""
                        confirmPassword = ""
                        showPasswordDialog = true
                    },
                )
            }
            item {
                ActionCard(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Logout",
                    subtitle = "Sign out from your account",
                    accentColor = DangerRed,
                    contentColor = DangerRed,
                    onClick = { showLogoutDialog = true },
                )
            }
            item {
                ActionCard(
                    icon = Icons.Default.Delete,
                    title = "Delete Account",
                    subtitle = "Permanently delete your account and all data",
                    accentColor = DangerRed,
                    contentColor = DangerRed,
                    onClick = { showDeleteDialog = true },
                )
            }
            item {
                WarningCard()
            }
            item { Spacer(modifier = Modifier.height(4.dp)) }
        }
    }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text(text = "Edit Full Name") },
            text = {
                OutlinedTextField(
                    value = fullNameInput,
                    onValueChange = { fullNameInput = it },
                    label = { Text(text = "Full Name") },
                    singleLine = true,
                    enabled = !uiState.isUpdating,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingAction = "edit_name"
                        userViewModel.updateFullName(fullNameInput)
                    },
                    enabled = !uiState.isUpdating,
                ) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text(text = "Change Password") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text(text = "Current Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !uiState.isUpdating,
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text(text = "New Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !uiState.isUpdating,
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text(text = "Confirm New Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !uiState.isUpdating,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPassword != confirmPassword) {
                            Toast.makeText(context, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        pendingAction = "change_password"
                        userViewModel.changePassword(currentPassword, newPassword)
                    },
                    enabled = !uiState.isUpdating,
                ) {
                    Text(text = "Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Logout?") },
            text = { Text(text = "Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onLogout()
                        showLogoutDialog = false
                    }
                ) {
                    Text(text = "Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Delete Account?") },
            text = { Text(text = "This will permanently delete your account and all associated data.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingAction = "delete_account"
                        userViewModel.deleteAccount()
                    }
                ) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
private fun EditProfileTopBar(onBackClick: () -> Unit) {
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
                text = "Edit Profile",
                color = PrimaryGreen,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
        )

            Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
private fun ProfileHeader(
    fullName: String,
    email: String,
) {
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Edit Profile",
                color = PrimaryGreen,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = fullName,
                color = DarkText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = email,
                color = LightText,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun PersonalInformationCard(
    title: String,
    subtitle: String,
    fullName: String,
    email: String,
    onFullNameClick: () -> Unit,
    items: List<InfoRowItem>,
) {
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    color = PrimaryGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    color = LightText,
                    fontSize = 13.sp,
                )
            }

            items.forEachIndexed { index, item ->
                InfoRow(
                    item = item,
                    onClick = if (item.label == "Full Name") onFullNameClick else null,
                )
                if (index != items.lastIndex) {
                    HorizontalDivider(color = BorderColor)
                }
            }
        }
    }
}

private data class InfoRowItem(
    val label: String,
    val value: String,
)

@Composable
private fun InfoRow(
    item: InfoRowItem,
    onClick: (() -> Unit)? = null,
) {
    val rowInteractionSource = rememberSafeInteractionSource()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = rowInteractionSource,
                indication = null,
                enabled = onClick != null,
            ) { onClick?.invoke() }
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.label,
                color = LightText,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.value,
                color = DarkText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = LightText,
        )
    }
}

@Composable
private fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    contentColor: Color,
    onClick: () -> Unit = { },
) {
    val cardInteractionSource = rememberSafeInteractionSource()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = cardInteractionSource,
                    indication = null,
                ) { onClick() }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = contentColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = LightText,
                    fontSize = 13.sp,
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = LightText,
            )
        }
    }
}

@Composable
private fun WarningCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DangerBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = DangerRed,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Heads up!",
                    color = DangerRed,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Deleting your account is permanent and cannot be undone. All your data will be permanently removed.",
                    color = DarkText,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                )
            }
        }
    }
}