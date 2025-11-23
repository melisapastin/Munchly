package com.example.munchly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.munchly.ui.components.BottomNavigationBar
import com.example.munchly.ui.components.RestaurantCard
import com.example.munchly.ui.components.SearchBar
import com.example.munchly.ui.viewmodels.FoodLoverFeedUiState
import com.example.munchly.ui.viewmodels.FoodLoverFeedViewModel
import com.example.munchly.ui.viewmodels.FoodLoverFeedViewModelFactory
import com.example.munchly.ui.viewmodels.RestaurantWithBookmark // ADD THIS IMPORT

@Composable
fun FoodLoverFeedScreen(
    navController: NavController
) {
    // Clean ViewModel acquisition - factory handles dependencies
    val viewModel: FoodLoverFeedViewModel = viewModel(factory = FoodLoverFeedViewModelFactory())
    val uiState by viewModel.uiState.collectAsState()

    // Pure UI composition
    FoodLoverFeedScreenContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onBookmarkClick = viewModel::onBookmarkClick,
        onHomeClick = { /* Already on home */ },
        onProfileClick = { navController.navigate("profile") }
    )
}

@Composable
private fun FoodLoverFeedScreenContent(
    uiState: FoodLoverFeedUiState,
    onSearchQueryChange: (String) -> Unit,
    onBookmarkClick: (String) -> Unit,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        topBar = {
            FeedTopBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = onSearchQueryChange
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = "home",
                onHomeClick = onHomeClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        FeedContent(
            uiState = uiState,
            onBookmarkClick = onBookmarkClick,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun FeedTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Discover Restaurants",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B4513),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SearchBar(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = "Search restaurants or tags..."
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FeedContent(
    uiState: FoodLoverFeedUiState,
    onBookmarkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            LoadingState(modifier = modifier)
        }

        uiState.error != null -> {
            ErrorState(
                error = uiState.error,
                modifier = modifier
            )
        }

        else -> {
            RestaurantList(
                restaurants = uiState.restaurants,
                onBookmarkClick = onBookmarkClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFFD2691E))
    }
}

@Composable
private fun ErrorState(
    error: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = error,
            color = Color.Red
        )
    }
}

@Composable
private fun RestaurantList(
    restaurants: List<RestaurantWithBookmark>,
    onBookmarkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (restaurants.isEmpty()) {
        EmptyState(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(restaurants) { restaurantWithBookmark ->
                RestaurantCard(
                    name = restaurantWithBookmark.restaurant.name,
                    openingHours = formatOpeningHours(restaurantWithBookmark.restaurant.openingHours),
                    tags = restaurantWithBookmark.restaurant.tags,
                    isBookmarked = restaurantWithBookmark.isBookmarked,
                    onBookmarkClick = { onBookmarkClick(restaurantWithBookmark.restaurant.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No restaurants found",
            color = Color.Gray
        )
    }
}

// Helper function to format opening hours
private fun formatOpeningHours(openingHours: com.example.munchly.data.models.OpeningHours): String {
    // Simple formatting - you can enhance this later with actual logic
    return "Check hours for details"
}