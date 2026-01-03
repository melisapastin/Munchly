package com.example.munchly.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.munchly.ui.components.AddTagDialog
import com.example.munchly.ui.theme.MunchlyColors
import com.example.munchly.ui.viewmodels.OwnerProfileViewModel

/**
 * Main restaurant owner profile screen.
 * Handles routing between display, edit, and loading states.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerProfileScreen(
    viewModel: OwnerProfileViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // File pickers for PDF and images
    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onMenuPdfSelected(it) }
    }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onImageSelected(it) }
    }

    // Show success message
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSuccessMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                LoadingScreen()
            }

            state.isEditing || !state.hasRestaurant -> {
                EditScreen(
                    state = state,
                    viewModel = viewModel,
                    onPdfUploadClick = { pdfLauncher.launch("application/pdf") },
                    onImageUploadClick = { imageLauncher.launch("image/*") }
                )
            }

            else -> {
                DisplayScreen(
                    state = state,
                    viewModel = viewModel,
                    onLogout = onLogout
                )
            }
        }

        // Tag dialog
        if (state.showTagDialog) {
            AddTagDialog(
                onDismiss = viewModel::hideTagDialog,
                onConfirm = viewModel::addTag,
                existingTags = state.tags
            )
        }

        // Snackbar for success messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = MunchlyColors.primary
            )
        }
    }
}

/**
 * Loading state screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MunchlyColors.surface,
                    titleContentColor = MunchlyColors.textPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MunchlyColors.primary)
        }
    }
}

/**
 * Edit mode screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditScreen(
    state: com.example.munchly.ui.viewmodels.OwnerProfileState,
    viewModel: OwnerProfileViewModel,
    onPdfUploadClick: () -> Unit,
    onImageUploadClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.hasRestaurant) "Edit Restaurant" else "Create Restaurant",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (state.hasRestaurant) {
                        IconButton(onClick = viewModel::cancelEditing) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Cancel",
                                tint = MunchlyColors.textPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MunchlyColors.surface,
                    titleContentColor = MunchlyColors.textPrimary
                )
            )
        }
    ) { paddingValues ->
        OwnerProfileEditForm(
            state = state,
            viewModel = viewModel,
            onPdfUploadClick = onPdfUploadClick,
            onImageUploadClick = onImageUploadClick,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

/**
 * Display mode screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisplayScreen(
    state: com.example.munchly.ui.viewmodels.OwnerProfileState,
    viewModel: OwnerProfileViewModel,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = viewModel::startEditing) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MunchlyColors.primary
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = MunchlyColors.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MunchlyColors.surface,
                    titleContentColor = MunchlyColors.textPrimary
                )
            )
        }
    ) { paddingValues ->
        OwnerProfileDisplay(
            state = state,
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}