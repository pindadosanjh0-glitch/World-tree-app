package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planted_trees")
data class PlantedTree(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val species: String,
    val datePlanted: Long = System.currentTimeMillis(),
    val latitude: Double,
    val longitude: Double,
    val notes: String = "",
    val careStatus: String = "Healthy", // "Healthy", "Needs Water", "Sprouting", "Growing"
    val imagePath: String? = null // Can hold a local Uri or a preset illustration catalog name
)
