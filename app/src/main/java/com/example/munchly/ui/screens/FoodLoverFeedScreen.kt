package com.example.munchly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.munchly.ui.components.EmptyState
import com.example.munchly.ui.components.ErrorState
import com.example.munchly.ui.components.LoadingState
import com.example.munchly.ui.components.RestaurantCard
import com.example.munchly.ui.components.SearchBar
import com.example.munchly.ui.theme.MunchlyColors
import com.example.munchly.ui.viewmodels.FoodLoverFeedViewModel

/**
 * Feed screen for food lovers showing all restaurants.
 * Includes search functionality and restaurant cards (without bookmark buttons).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLoverFeedScreen(
    viewModel: FoodLoverFeedViewModel,
    onRestaurantClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Discover",
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
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MunchlyColors.background)
        ) {
            when {
                state.isLoading -> {
                    LoadingState()
                }

                state.error != null -> {
                    ErrorState(
                        message = state.error ?: "An error occurred",
                        onRetry = { viewModel.loadRestaurants() }
                    )
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Search Bar
                        SearchBar(
                            query = state.searchQuery,
                            onQueryChange = viewModel::onSearchQueryChange,
                            modifier = Modifier.padding(16.dp)
                        )

                        // Restaurant List
                        if (state.filteredRestaurants.isEmpty()) {
                            EmptyState(
                                icon = Icons.Default.Restaurant,
                                title = if (state.searchQuery.isEmpty()) {
                                    "No Restaurants Yet"
                                } else {
                                    "No Results Found"
                                },
                                message = if (state.searchQuery.isEmpty()) {
                                    "Be the first to discover new restaurants"
                                } else {
                                    "Try searching with different keywords"
                                }
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = state.filteredRestaurants,
                                    key = { it.id }
                                ) { restaurant ->
                                    RestaurantCard(
                                        restaurant = restaurant,
                                        onCardClick = { onRestaurantClick(restaurant.id) }
                                        // REMOVED: isBookmarked and onBookmarkClick
                                    )
                                }

                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}