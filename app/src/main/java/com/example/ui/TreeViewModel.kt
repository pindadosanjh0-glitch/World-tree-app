package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.PlantedTree
import com.example.data.TreeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Species Preset definition
data class SpeciesPreset(
    val name: String,
    val scientificName: String,
    val bestSeason: String,
    val watering: String,
    val benefit: String,
    val icon: String // Emoji representation or simple label
)

// Tip definition
data class EnvironmentalTip(
    val id: Int,
    val text: String,
    val category: String,
    val pointsAwarded: Int = 20,
    val completed: Boolean = false
)

// Community Event definition
data class PlantingEvent(
    val id: Int,
    val title: String,
    val date: String,
    val location: String,
    val attendees: Int,
    val description: String,
    val joined: Boolean = false
)

class TreeViewModel(private val repository: TreeRepository) : ViewModel() {

    // 1. Planted trees flow from Room Database
    val plantedTrees: StateFlow<List<PlantedTree>> = repository.allTrees
        .catch { e ->
            e.printStackTrace()
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Preset trees species catalog
    val speciesPresets = listOf(
        SpeciesPreset("American White Oak", "Quercus alba", "Autumn to Early Spring", "Deep water once/week", "Filters 48 lbs of CO2/year and supports 500+ insect species", "🌳"),
        SpeciesPreset("Suga Maple", "Acer saccharum", "Late Autumn", "Twice per week", "Spectacular canopy shade and prevents soil wind erosion", "🍁"),
        SpeciesPreset("Douglas Fir", "Pseudotsuga menziesii", "Late Winter", "Every 10 days", "Thrives in various climates and provides robust bird shelter", "🌲"),
        SpeciesPreset("Yoshino Cherry Blossom", "Prunus yedoensis", "Early Spring", "Every 3 days", "Fosters native pollinators and beautifies urban walkways", "🌸"),
        SpeciesPreset("Honeycrisp Apple", "Malus domestica", "Spring", "Every 2 days (moist soil)", "Produces rich organic fruit yields and hosts garden bees", "🍎")
    )

    // 2. Mutable states for completing tips/activities to earn extra Green Points
    private val _completedTipIds = MutableStateFlow<Set<Int>>(emptySet())
    val completedTipIds: StateFlow<Set<Int>> = _completedTipIds.asStateFlow()

    // 3. Mutable states for environmental events
    private val _communityEvents = MutableStateFlow<List<PlantingEvent>>(
        listOf(
            PlantingEvent(1, "Greenwood Reforestation Drive", "Saturday, June 14, 2026 - 09:00 AM", "Greenwood Valley Park", 48, "Join local families in planting 500 cedar and fir wood sprouts along the eastern ridge.", false),
            PlantingEvent(2, "Riverbank Soil Strengthening", "Sunday, June 22, 2026 - 10:30 AM", "East Shore River Corridor", 32, "Reinforce erosion-prone banks with water-loving willow and birch saplings to stabilize the soil.", false),
            PlantingEvent(3, "Urban Shaded Pocket Parks", "Saturday, July 5, 2026 - 08:30 AM", "Metropolitan North Center", 75, "Help transplant beautiful mature trees into high-heat masonry streets to lower neighborhood temperatures.", false),
            PlantingEvent(4, "Oak Heritage Forest Revival", "Sunday, July 19, 2026 - 09:00 AM", "Sovereign Sanctuary Grounds", 19, "Plant legacy white oaks that will shelter local species for centuries to come.", false)
        )
    )
    val communityEvents: StateFlow<List<PlantingEvent>> = _communityEvents.asStateFlow()

    // 4. Daily Environmental Tips
    val environmentalTips = listOf(
        EnvironmentalTip(1, "Collect shower and kitchen water to irrigate young saplings naturally without wasting tap water.", "Water Saving"),
        EnvironmentalTip(2, "Add organic compost around the root collar to retain moisture and foster vital mycelium networks.", "Soil Health"),
        EnvironmentalTip(3, "Inspect leaves for pests weekly; remove them by hand or with soapy water instead of using chemical pesticides.", "Organic Care"),
        EnvironmentalTip(4, "Mulch in a donut shape around the tree trunk—do not create soil suffocating 'mulch volcanoes'.", "Mulching"),
        EnvironmentalTip(5, "Loosely stake young trees only if in extreme wind areas; free movement builds strong, flexible root-flares.", "Structural Growth")
    )

    // Gamification Points calculation reactive to database changes and completed activities
    val greenPoints: StateFlow<Int> = combine(
        plantedTrees,
        _completedTipIds,
        _communityEvents
    ) { trees, completedTips, events ->
        val pointsFromTrees = trees.size * 150
        val pointsFromTips = completedTips.size * 25
        val pointsFromEvents = events.filter { it.joined }.size * 100
        pointsFromTrees + pointsFromTips + pointsFromEvents
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Base community counts (1382 trees initialized, plus 18 per hour since epoch plus user additions)
    val communityPlantedCount: StateFlow<Int> = plantedTrees.map { userTrees ->
        // Generate a stable community count that increments realistically
        val baseCount = 2842
        baseCount + userTrees.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2842)

    // Database Actions
    fun plantTree(species: String, latitude: Double, longitude: Double, notes: String, imagePath: String? = null) {
        viewModelScope.launch {
            val newTree = PlantedTree(
                species = species,
                latitude = latitude,
                longitude = longitude,
                notes = notes,
                imagePath = imagePath
            )
            try {
                repository.insert(newTree)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeTree(tree: PlantedTree) {
        viewModelScope.launch {
            try {
                repository.delete(tree)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateTreeHealth(id: Int, status: String) {
        viewModelScope.launch {
            try {
                repository.updateCareStatus(id, status)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Toggle daily tip reading to earn points
    fun toggleTipCompleted(tipId: Int) {
        val current = _completedTipIds.value
        if (current.contains(tipId)) {
            _completedTipIds.value = current - tipId
        } else {
            _completedTipIds.value = current + tipId
        }
    }

    // Toggle event enrollment status
    fun toggleEventRegistration(eventId: Int) {
        _communityEvents.update { list ->
            list.map { event ->
                if (event.id == eventId) {
                    event.copy(
                        joined = !event.joined,
                        attendees = if (!event.joined) event.attendees + 1 else event.attendees - 1
                    )
                } else {
                    event
                }
            }
        }
    }

    // Badge configuration helper
    fun getBadgeInfo(points: Int): Pair<String, String> {
        return when {
            points < 150 -> Pair("Seedling", "You have started your green journey! Keep planting to sprout.")
            points < 450 -> Pair("Sapling Savior", "Your saplings are thriving. You are reliably carbon-negative!")
            points < 900 -> Pair("Canopy Caretaker", "You've grown a rich network of trees and local event support.")
            else -> Pair("Earth Guardian", "Legendary! Your contribution ensures a shaded, clean breath for posterity.")
        }
    }

    fun getNextLevelInfo(points: Int): Triple<String, Int, Float> {
        return when {
            points < 150 -> Triple("Sapling Savior", 150, points / 150f)
            points < 450 -> Triple("Canopy Caretaker", 450, (points - 150) / 300f)
            points < 900 -> Triple("Earth Guardian", 900, (points - 450) / 450f)
            else -> Triple("Max Level reached", 900, 1.0f)
        }
    }
}

class TreeViewModelFactory(private val repository: TreeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TreeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TreeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
