package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.PlantedTree
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

// Screen destinations
sealed class Screen(val route: String, val title: String, val icon: ImageVector, val outlinedIcon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Tracker : Screen("tracker", "Tracker", Icons.Filled.AddLocationAlt, Icons.Outlined.AddLocationAlt)
    object Community : Screen("community", "Community", Icons.Filled.Groups, Icons.Outlined.Groups)
    object Profile : Screen("profile", "Profile", Icons.Filled.Eco, Icons.Outlined.Eco)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreeApp(viewModel: TreeViewModel, modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
            ) {
                val screens = listOf(Screen.Home, Screen.Tracker, Screen.Community, Screen.Profile)
                screens.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        label = { Text(screen.title) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.icon else screen.outlinedIcon,
                                contentDescription = screen.title
                            )
                        },
                        modifier = Modifier.testTag("bottom_nav_${screen.route}"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    Screen.Home -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToTracker = { currentScreen = Screen.Tracker }
                    )
                    Screen.Tracker -> TrackerScreen(viewModel = viewModel)
                    Screen.Community -> CommunityScreen(viewModel = viewModel)
                    Screen.Profile -> ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// 1. HOME SCREEN & EDUCATION TIPS
// --------------------------------------------------------------------------------------
@Composable
fun HomeScreen(
    viewModel: TreeViewModel,
    onNavigateToTracker: () -> Unit
) {
    val userTrees by viewModel.plantedTrees.collectAsStateWithLifecycle()
    val communityCount by viewModel.communityPlantedCount.collectAsStateWithLifecycle()
    val activeTips by viewModel.completedTipIds.collectAsStateWithLifecycle()

    val quoteList = listOf(
        Pair("“The true meaning of life is to plant trees, under whose shade you do not expect to sit.”", "— Nelson Henderson"),
        Pair("“A nation that destroys its soils destroys itself. Forests are the lungs of our land...”", "— Franklin D. Roosevelt"),
        Pair("“Ancient woodlands are not just timber, they are the architectural heritage of natural systems.”", "— Dame Fiona Reynolds"),
        Pair("“Someone is sitting in the shade today because someone planted a tree a long time ago.”", "— Warren Buffett")
    )
    val randomQuote = remember { quoteList[Random.nextInt(quoteList.size)] }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🌱",
                            fontSize = 32.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Green Canopy",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = randomQuote.first,
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    
                    Text(
                        text = randomQuote.second,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp, end = 8.dp)
                    )
                }
            }
        }

        // Stats Dashboard
        item {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Conservation Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Community Stats Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🌎 Community",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$communityCount",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Trees Planted",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Personal Stats Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🏡 Your Contribution",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${userTrees.size}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (userTrees.size == 1) "Tree Logged" else "Trees Logged",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Quick Call to Action Button
        item {
            Button(
                onClick = onNavigateToTracker,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("home_plant_tree_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CompassCalibration,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Plant a Tree Now",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Education Tip Section Header
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(
                    text = "Daily Environmental Tips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Complete daily ecological tips to earn Green Points (+25 pts each!)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Checklist of tips
        items(viewModel.environmentalTips) { tip ->
            val isCompleted = activeTips.contains(tip.id)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { viewModel.toggleTipCompleted(tip.id) },
                        modifier = Modifier.testTag("tip_checkbox_${tip.id}"),
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tip.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tip.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (isCompleted) {
                        Text(
                            text = "+25 pts",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }

        // Seasonal Tree Plantation Guide
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Help,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "How to Plant & Care",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Dig a wide, shallow hole (3x the width of root ball).\n" +
                               "2. Gently tease root bounds to stimulate radial extension.\n" +
                               "3. Backfill with compost modified site-soil—do not add sand.\n" +
                               "4. Water thoroughly immediately, then mulch 3 inches deep.\n" +
                               "5. Build a water containment dike of soil around the root zone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// 2. TREE TRACKER SCREEN (FORM AND LIST)
// --------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(viewModel: TreeViewModel) {
    val trees by viewModel.plantedTrees.collectAsStateWithLifecycle()
    
    // Form Inputs
    var selectedSpecies by remember { mutableStateOf(viewModel.speciesPresets[0].name) }
    var noteText by remember { mutableStateOf("") }
    var latitudeText by remember { mutableStateOf("34.0522") }
    var longitudeText by remember { mutableStateOf("-118.2437") }
    
    // Status selectors
    var dropdownExpanded by remember { mutableStateOf(false) }
    
    // Photos simulation
    var isPhotoAttached by remember { mutableStateOf(false) }
    var simulatedPhotoLabel by remember { mutableStateOf<String?>(null) }
    
    // Feedback Snackbar setup
    val scope = rememberCoroutineScope()
    var successMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Title
        item {
            Column {
                Text(
                    text = "Tree Logger Form",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Record tree saplings you plant with geographical coordinates for growth audit.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Form Section Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    
                    // 1. Species Selector Dropdown
                    Column {
                        Text(
                            text = "Tree Species",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedSpecies,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(
                                        onClick = { dropdownExpanded = true },
                                        modifier = Modifier.testTag("new_tree_species_select")
                                    ) {
                                        Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { dropdownExpanded = true },
                                shape = RoundedCornerShape(12.dp)
                            )
                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                viewModel.speciesPresets.forEach { preset ->
                                    DropdownMenuItem(
                                        text = { Text("${preset.icon} ${preset.name} (${preset.scientificName})") },
                                        onClick = {
                                            selectedSpecies = preset.name
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 2. Geolocation Section
                    Column {
                        Text(
                            text = "Plantation Location",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = latitudeText,
                                onValueChange = { latitudeText = it },
                                label = { Text("Latitude") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("new_tree_latitude_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = longitudeText,
                                onValueChange = { longitudeText = it },
                                label = { Text("Longitude") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("new_tree_longitude_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = {
                                // Simulate random beautiful coordinates coordinates
                                val randomLat = 30.0 + Random.nextDouble() * 15.0
                                val randomLon = -120.0 + Random.nextDouble() * 40.0
                                latitudeText = String.format(Locale.US, "%.5f", randomLat)
                                longitudeText = String.format(Locale.US, "%.5f", randomLon)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_tree_gps_autofill_btn")
                        ) {
                            Icon(imageVector = Icons.Filled.MyLocation, contentDescription = "GPS Simulator")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Auto-Detect Location (GPS Sim)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    // 3. Photo Upload Simulator
                    Column {
                        Text(
                            text = "Tree Photo",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (isPhotoAttached) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📸", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = simulatedPhotoLabel ?: "image_sapling_01.jpg",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = {
                                    isPhotoAttached = false
                                    simulatedPhotoLabel = null
                                }) {
                                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    isPhotoAttached = true
                                    simulatedPhotoLabel = "nature_tree_${Random.nextInt(100, 999)}.jpg"
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("new_tree_photo_picker_btn")
                            ) {
                                Icon(imageVector = Icons.Filled.AddAPhoto, contentDescription = "Add Photo")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Simulate Photo Upload (Camera/Gallery)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 4. Notes & Care Comments
                    Column {
                        Text(
                            text = "Additional Bio-Notes",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            placeholder = { Text("e.g. Planted with my grandchildren. Rich loamy soil, added natural forest mulch.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .testTag("new_tree_notes_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // 5. Submit Form Button
                    Button(
                        onClick = {
                            val latVal = latitudeText.toDoubleOrNull() ?: 34.0522
                            val lonVal = longitudeText.toDoubleOrNull() ?: -118.2437
                            viewModel.plantTree(
                                species = selectedSpecies,
                                latitude = latVal,
                                longitude = lonVal,
                                notes = noteText,
                                imagePath = if (isPhotoAttached) simulatedPhotoLabel else null
                            )
                            // Clear form
                            noteText = ""
                            isPhotoAttached = false
                            simulatedPhotoLabel = null
                            successMessage = "Awesome! Logged $selectedSpecies. You earned +150 Points!"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .testTag("new_tree_submit_btn")
                    ) {
                        Text(text = "Save and Register Tree", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Success Popup Banner / Alert
        if (successMessage != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🌟", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = successMessage ?: "",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { successMessage = null }) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // Log Title
        item {
            Text(
                text = "Your Planted Forest Log (${trees.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Condition check for empty state
        if (trees.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🌾", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Trees Registered Yet",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Be the change—plant your first tree, fill out the form above, and watch it thrive!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(trees) { tree ->
                TreeRowItem(tree = tree, onRemove = { viewModel.removeTree(tree) }, onInspectCare = { nextStatus ->
                    viewModel.updateTreeHealth(tree.id, nextStatus)
                })
            }
        }
    }
}

@Composable
fun TreeRowItem(
    tree: PlantedTree,
    onRemove: () -> Unit,
    onInspectCare: (String) -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }

    val formatter = remember { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()) }
    val readableDate = formatter.format(Date(tree.datePlanted))

    // Icon matching
    val plantEmoji = when {
        tree.species.contains("Oak", ignoreCase = true) -> "🌳"
        tree.species.contains("Maple", ignoreCase = true) -> "🍁"
        tree.species.contains("Fir", ignoreCase = true) -> "🌲"
        tree.species.contains("Cherry", ignoreCase = true) -> "🌸"
        else -> "🍎"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_card_${tree.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = borderStrokeLight()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leaf decoration circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = plantEmoji, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tree.species,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Planted on $readableDate",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("💧 Mark as Watered") },
                            onClick = {
                                onInspectCare("Growing strong (Watered today)")
                                expandedMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🌿 Mark as Sprouting") },
                            onClick = {
                                onInspectCare("Sprouting Sproutling")
                                expandedMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🗑️ Delete Record") },
                            onClick = {
                                onRemove()
                                expandedMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Extra metrics in a nice chips-like container
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Coord chip
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = String.format(Locale.US, "📍 %.4f, %.4f", tree.latitude, tree.longitude),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )

                // Health chip
                val statusBg = when {
                    tree.careStatus.contains("Watered", true) -> MaterialTheme.colorScheme.primaryContainer
                    tree.careStatus.contains("Sprout", true) -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.secondaryContainer
                }
                val statusText = when {
                    tree.careStatus.contains("Watered", true) -> MaterialTheme.colorScheme.onPrimaryContainer
                    tree.careStatus.contains("Sprout", true) -> MaterialTheme.colorScheme.onTertiaryContainer
                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = tree.careStatus,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusText
                    )
                }
            }

            if (tree.notes.isNotBlank() || tree.imagePath != null) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(8.dp))
                
                if (tree.imagePath != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🖼️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Photo attached: ${tree.imagePath}",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (tree.notes.isNotBlank()) {
                    Text(
                        text = tree.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// 3. COMMUNITY EVENTS SCREEN
// --------------------------------------------------------------------------------------
@Composable
fun CommunityScreen(viewModel: TreeViewModel) {
    val events by viewModel.communityEvents.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Plantation Campaigns",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Form groups, register with local environmental activists, and join physical restoration drives.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(events) { event ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("event_card_${event.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (event.joined) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                border = borderStrokeLight()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.Event, contentDescription = "Date", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = event.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.Place, contentDescription = "Place", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = event.location, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👥 ${event.attendees} Eco-Guardians Registered",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Button(
                            onClick = { viewModel.toggleEventRegistration(event.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (event.joined) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary,
                                contentColor = if (event.joined) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("event_join_btn_${event.id}")
                        ) {
                            Text(
                                text = if (event.joined) "Leave Campaign" else "Join Drive (+100 pts)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// 4. ACHIEVEMENTS & HEALTHIER PROFILE SCREEN
// --------------------------------------------------------------------------------------
@Composable
fun ProfileScreen(viewModel: TreeViewModel) {
    val trees by viewModel.plantedTrees.collectAsStateWithLifecycle()
    val points by viewModel.greenPoints.collectAsStateWithLifecycle()

    val badgeTuple = viewModel.getBadgeInfo(points)
    val nextLevelTuple = viewModel.getNextLevelInfo(points)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper card: Profile overview
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when {
                                points < 150 -> "🌱"
                                points < 450 -> "🌿"
                                points < 900 -> "🌳"
                                else -> "👑"
                            },
                            fontSize = 44.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = badgeTuple.first,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "Current Earth Advocate Rank",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$points",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Green Points",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }

                        VerticalDivider(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                            modifier = Modifier.height(30.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${trees.size}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "My Trees",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        // Mid Card: Next Achievement Progress Meter
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = borderStrokeLight()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next Tier Goal: ${nextLevelTuple.first}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${(nextLevelTuple.third * 100).toInt()}% Done",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { nextLevelTuple.third },
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Earn points by planting trees (+150), completing daily tips (+25), and joining events (+100). Need ${nextLevelTuple.second} cumulative points to level up.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Bottom section: Gamification Badges Gallery
        item {
            Text(
                text = "Environmental Achievement Badges",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Hardcoded beautifully stylized badges representing levels
        item {
            val badges = listOf(
                Triple("Seedling Activist", "Awarded immediately upon downloading the app to kickstart ecosystem support.", points >= 0),
                Triple("Sprout Initiator", "Awarded upon planting and registering your very first forest tree.", trees.isNotEmpty()),
                Triple("Sapling Savior", "Fulfill environmental tips and log dynamic saplings to reach 150 points.", points >= 150),
                Triple("Canopy Caretaker", "Fulfill environmental drives and expand canopy nodes up to 450 points.", points >= 450),
                Triple("Earth Guardian", "Ultimate honor. Lead the community and maintain rich forest records above 900 points.", points >= 900)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                badges.forEach { badge ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (badge.third) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (badge.third) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (badge.third) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (badge.third) "🎖️" else "🔒",
                                    fontSize = 18.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = badge.first,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (badge.third) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = badge.second,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (badge.third) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun borderStrokeLight() = androidx.compose.foundation.BorderStroke(
    width = 1.dp,
    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
)
