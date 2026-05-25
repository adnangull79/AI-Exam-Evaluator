package com.example.aiexamevaluator.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aiexamevaluator.theme.ThemeMode
import com.example.aiexamevaluator.viewmodel.EvaluationViewModel
import com.example.aiexamevaluator.viewmodel.EvaluationViewModelFactory
import com.example.aiexamevaluator.viewmodel.ThemeViewModel
import com.example.aiexamevaluator.viewmodel.ThemeViewModelFactory
import com.example.aiexamevaluator.viewmodel.UserViewModel
import com.example.aiexamevaluator.viewmodel.UserViewModelFactory

// ── FIX: Added helper to avoid PlatformRipple crash on newer Compose ──────────
@Composable
private fun rememberSafeInteractionSource() = remember { MutableInteractionSource() }

private val PrimaryGreen = Color(0xFF0B6E4F)
private val LightGreen   = Color(0xFFEAF6F0)
private val DarkText     = Color(0xFF1B1B1B)
private val LightText    = Color(0xFF7A7A7A)
private val LogoutRed    = Color(0xFFD32F2F)

private data class SettingsOption(
    val title   : String,
    val subtitle: String,
    val icon    : ImageVector,
)

@Composable
fun SettingsScreen(
    onHistoryClick        : () -> Unit,
    onStartEvaluationClick: () -> Unit,
    onSettingsClick       : () -> Unit,
    onAboutClick          : () -> Unit,
    onFaqClick            : () -> Unit,
    onEditProfileClick    : () -> Unit,
    onLogout              : () -> Unit,
) {
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory()
    )
    val context = LocalContext.current
    val evaluationViewModel: EvaluationViewModel = viewModel(
        factory = EvaluationViewModelFactory(context)
    )
    val themeViewModel: ThemeViewModel = viewModel(
        factory = ThemeViewModelFactory(context)
    )
    val uiState by userViewModel.uiState.collectAsState()
    val themeMode by themeViewModel.themeMode.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showDeleteHistoryDialog by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(ThemeMode.SYSTEM) }

    val options = listOf(
        SettingsOption("Edit Profile",       "Update your personal information",  Icons.Default.Edit),
        SettingsOption("Delete History",     "Clear your evaluated exam records", Icons.Default.DeleteSweep),
        SettingsOption("About ExamEval AI",  "Learn more about this app",         Icons.Default.Info),
        SettingsOption("FAQs",               "Frequently asked questions",        Icons.AutoMirrored.Filled.HelpOutline),
        )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            UnifiedBottomBar(
                activeTab      = BottomTab.SETTINGS,
                onHistoryClick = onHistoryClick,
                onFabClick     = onStartEvaluationClick,
                onSettingsClick= onSettingsClick,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = "Settings",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.isDataLoaded -> {
                        ProfileSection(fullName = uiState.fullName, email = uiState.email)
                    }
                }
            }

            item {
                AppearanceCard(
                    themeMode = themeMode,
                    onClick = {
                        selectedTheme = themeMode
                        showThemeDialog = true
                    }
                )
            }

            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(22.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        options.forEachIndexed { index, option ->
                            val optionClick: () -> Unit = when (option.title) {
                                "Edit Profile" -> onEditProfileClick
                                "Delete History" -> { { showDeleteHistoryDialog = true } }
                                "About ExamEval AI" -> onAboutClick
                                "FAQs" -> onFaqClick
                                else -> { { } }
                            }
                            SettingsOptionItem(
                                title       = option.title,
                                subtitle    = option.subtitle,
                                icon        = option.icon,
                                onClick     = optionClick,
                                showDivider = index != options.lastIndex,
                            )
                        }
                    }
                }
            }

            item { LogoutCard(onClick = { showLogoutDialog = true }) }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
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

    if (showThemeDialog) {
        ThemeSelectionDialog(
            selectedTheme = selectedTheme,
            onThemeSelected = { selectedTheme = it },
            onDismiss = { showThemeDialog = false },
            onApply = {
                themeViewModel.updateTheme(selectedTheme)
                showThemeDialog = false
            }
        )
    }

    if (showDeleteHistoryDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteHistoryDialog = false
            },

            title = {
                Text("Delete History")
            },

            text = {
                Text(
                    "This will permanently remove all evaluation history records from this device. This action cannot be undone."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        evaluationViewModel.deleteAllHistory()

                        showDeleteHistoryDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDeleteHistoryDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileSection(
    fullName: String,
    email: String,
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(22.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
        ) {
            Text(
                text = "Full Name",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = fullName,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Email",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = email,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
fun AppearanceCard(
    themeMode: ThemeMode,
    onClick: () -> Unit,
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(22.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = rememberSafeInteractionSource(),
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier         = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Default.Palette,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "Appearance",
                    color      = MaterialTheme.colorScheme.onSurface,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text     = "Current Theme: ${themeMode.name}",
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ThemeSelectionDialog(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Theme") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeOptionRow(
                    label = "System Default",
                    selected = selectedTheme == ThemeMode.SYSTEM,
                    onClick = { onThemeSelected(ThemeMode.SYSTEM) }
                )
                ThemeOptionRow(
                    label = "Light",
                    selected = selectedTheme == ThemeMode.LIGHT,
                    onClick = { onThemeSelected(ThemeMode.LIGHT) }
                )
                ThemeOptionRow(
                    label = "Dark",
                    selected = selectedTheme == ThemeMode.DARK,
                    onClick = { onThemeSelected(ThemeMode.DARK) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onApply) { Text(text = "Apply") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "Cancel") }
        }
    )
}

@Composable
private fun ThemeOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = rememberSafeInteractionSource(),
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}

@Composable
fun SettingsOptionItem(
    title      : String,
    subtitle   : String,
    icon       : ImageVector,
    onClick    : () -> Unit,
    showDivider: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // ── FIX 3: explicit interactionSource + indication = null ──────────
            .clickable(
                interactionSource = rememberSafeInteractionSource(),
                indication        = null,
                onClick           = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier         = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(20.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = title,
                    color      = MaterialTheme.colorScheme.onSurface,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = subtitle,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                )
            }

            Icon(
                imageVector        = Icons.Default.ChevronRight,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (showDivider) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        }
    }
}

@Composable
fun LogoutCard(onClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(22.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = rememberSafeInteractionSource(),
                    indication        = null,
                ) { onClick() }
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier         = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LogoutRed.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint               = LogoutRed,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "Logout",
                    color      = LogoutRed,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = "Sign out from your account",
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
            }

            Icon(
                imageVector        = Icons.Default.ChevronRight,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(
            onHistoryClick         = { },
            onStartEvaluationClick = { },
            onSettingsClick        = { },
            onAboutClick           = { },
            onFaqClick             = { },
            onEditProfileClick     = { },
            onLogout               = { },
        )
    }
}