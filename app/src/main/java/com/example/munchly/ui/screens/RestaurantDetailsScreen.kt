package com.example.munchly.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.munchly.domain.models.RestaurantDomain
import com.example.munchly.ui.components.ErrorState
import com.example.munchly.ui.components.LoadingState
import com.example.munchly.ui.components.OpeningHoursDisplay
import com.example.munchly.ui.components.WriteReviewDialog
import com.example.munchly.ui.theme.MunchlyColors
import com.example.munchly.ui.viewmodels.RestaurantDetailsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * UPDATED: Added image carousel and menu PDF viewer
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun RestaurantDetailsScreen(
    viewModel: RestaurantDetailsViewModel,
    onBackClick: () -> Unit,
    onViewAllReviews: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.restaurant) {
        state.restaurant?.let { restaurant ->
            if (state.latitude == 0.0) { // Only search if we haven't found them yet
                viewModel.fetchMapCoordinates(restaurant.address, context)
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurant Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MunchlyColors.textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleBookmark() }
                    ) {
                        Icon(
                            imageVector = if (state.isBookmarked) {
                                Icons.Filled.Bookmark
                            } else {
                                Icons.Outlined.BookmarkBorder
                            },
                            contentDescription = if (state.isBookmarked) {
                                "Remove bookmark"
                            } else {
                                "Add bookmark"
                            },
                            tint = if (state.isBookmarked) {
                                MunchlyColors.primary
                            } else {
                                MunchlyColors.textPrimary
                            }
                        )



                    }
                    IconButton(onClick = {
                        // We use the Elvis operator to provide a fallback "" if the restaurant is null
                        shareRestaurant(
                            context = context,
                            name = state.restaurant?.name ?: "Restaurant",
                            address = state.restaurant?.address ?: "Unknown Address"
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MunchlyColors.primary
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
                        onRetry = { viewModel.loadRestaurantDetails() }
                    )
                }

                state.restaurant != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // UPDATED: Image Carousel
                        item {
                            if (state.restaurant!!.images.isNotEmpty()) {
                                ImageCarousel(
                                    images = state.restaurant!!.images,
                                    restaurantName = state.restaurant!!.name
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .background(MunchlyColors.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "🍽️",
                                        fontSize = 64.sp
                                    )
                                }
                            }
                        }

                        // Restaurant Header
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = state.restaurant!!.name,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MunchlyColors.textPrimary,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Text(
                                        text = state.restaurant!!.priceRange.symbol,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MunchlyColors.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Rating
                                if (state.totalReviews > 0) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFA726),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = String.format("%.1f", state.averageRating),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MunchlyColors.textPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "(${state.totalReviews} reviews)",
                                            fontSize = 14.sp,
                                            color = MunchlyColors.textSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Tags
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    state.restaurant!!.tags.forEach { tag ->
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = MunchlyColors.primaryLight
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = tag,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MunchlyColors.primary,
                                                modifier = Modifier.padding(
                                                    horizontal = 12.dp,
                                                    vertical = 6.dp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Description
                        item {
                            InfoSection(title = "About") {
                                Text(
                                    text = state.restaurant!!.description,
                                    fontSize = 14.sp,
                                    color = MunchlyColors.textSecondary,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        // ADDED: Menu PDF Button
                        if (!state.restaurant!!.menuPdfUrl.isNullOrEmpty()) {
                            item {
                                InfoSection(title = "Menu") {
                                    OutlinedButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                data = Uri.parse(state.restaurant!!.menuPdfUrl)
                                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            }
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = Color.White,
                                            contentColor = MunchlyColors.primary
                                        ),
                                        border = BorderStroke(2.dp, MunchlyColors.primary),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            tint = MunchlyColors.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "View Menu (PDF)",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        // Contact Information
                        item {
                            InfoSection(title = "Contact") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MunchlyColors.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = state.restaurant!!.address,
                                        fontSize = 14.sp,
                                        color = MunchlyColors.textPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = MunchlyColors.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = state.restaurant!!.phone,
                                        fontSize = 14.sp,
                                        color = MunchlyColors.textPrimary
                                    )
                                }
                            }
                        }
                        item {
                            InfoSection(title = "Location") {
                                // 1. Check the coordinates in the UI state, NOT the restaurant object
                                if (state.latitude != 0.0 && state.longitude != 0.0) {

                                    // 2. Create a temporary copy that combines the restaurant info
                                    // with the coordinates the ViewModel found
                                    val restaurantWithCoordinates = state.restaurant!!.copy(
                                        latitude = state.latitude,
                                        longitude = state.longitude
                                    )

                                    // 3. Pass this "complete" object to your Map function
                                    RestaurantLocationMap(restaurantWithCoordinates)

                                } else {
                                    // 4. While the coordinates are 0.0, show this
                                    Column {
                                        Text(
                                            text = "Map location not available",
                                            fontSize = 14.sp,
                                            color = MunchlyColors.textSecondary
                                        )
                                        // Helpful for debugging:
                                        Text(
                                            text = "Debug Info: Lat=${state.latitude}, Lng=${state.longitude}",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }

                        // Opening Hours
                        item {
                            InfoSection(title = "Opening Hours") {
                                OpeningHoursDisplay(
                                    schedule = state.restaurant!!.openingHours
                                )
                            }
                        }

                        // Action Buttons
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.showWriteReviewDialog() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MunchlyColors.primary,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Write a Review",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                OutlinedButton(
                                    onClick = onViewAllReviews,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.White,
                                        contentColor = MunchlyColors.primary
                                    ),
                                    border = BorderStroke(2.dp, MunchlyColors.primary)
                                ) {
                                    Text(
                                        text = "View All Reviews (${state.totalReviews})",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }

    // Write Review Dialog
    if (state.showWriteReviewDialog && state.restaurant != null) {
        WriteReviewDialog(
            restaurantName = state.restaurant!!.name,
            onDismiss = { viewModel.hideWriteReviewDialog() },
            onSubmit = { rating, comment, onError ->
                viewModel.submitReview(rating, comment, onError)
            }
        )
    }
}

/**
 * ADDED: Image carousel component with page indicators
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageCarousel(
    images: List<String>,
    restaurantName: String,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(modifier = modifier.fillMaxWidth()) {
        // Image Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) { page ->
            Image(
                painter = rememberAsyncImagePainter(images[page]),
                contentDescription = "$restaurantName image ${page + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop
            )
        }

        // Page Indicators (only show if more than 1 image)
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(images.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage) {
                                    Color.White
                                } else {
                                    Color.White.copy(alpha = 0.5f)
                                }
                            )
                    )
                }
            }

            // Image Counter
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${images.size}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MunchlyColors.textPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}
@Composable
private fun RestaurantLocationMap(restaurant: RestaurantDomain) {
    val context = LocalContext.current
    val restaurantLocation = LatLng(restaurant.latitude, restaurant.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(restaurantLocation, 15f)
    }

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                // Disable map gestures so the user doesn't accidentally scroll away
                // while trying to scroll down the list
                properties = MapProperties(isMyLocationEnabled = false),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, scrollGesturesEnabled = false)
            ) {
                Marker(state = MarkerState(position = restaurantLocation), title = restaurant.name)
            }
        }

        // ADD THIS: A floating button on the map to "Get Directions"
        Button(
            onClick = {
                openGoogleMaps(context, restaurant.latitude, restaurant.longitude, restaurant.name)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MunchlyColors.primary),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("Directions", fontSize = 12.sp)
        }
    }
}

fun openGoogleMaps(context: android.content.Context, lat: Double, lng: Double, label: String) {
    // This creates a "Uri" that the Google Maps app understands
    val uri = "geo:$lat,$lng?q=$lat,$lng($label)"
    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri))

    // Tell the intent to specifically look for the Maps app
    intent.setPackage("com.google.android.apps.maps")

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // If they don't have the Maps app, open in a browser instead
        val browserUri = "https://www.google.com/maps/search/?api=1&query=$lat,$lng"
        val browserIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(browserUri))
        context.startActivity(browserIntent)
    }
}


fun shareRestaurant(context: android.content.Context, name: String, address: String) {
    val shareBody = """
        🍴 Check out this place I found on Munchly!
        
        🏠 Restaurant: $name
        📍 Location: $address
        
        Sent from Munchly App
    """.trimIndent()

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareBody)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share this restaurant via")
    context.startActivity(shareIntent)
}