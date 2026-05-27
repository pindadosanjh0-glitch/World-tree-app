package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TreeDao {
    @Query("SELECT * FROM planted_trees ORDER BY datePlanted DESC")
    fun getAllTrees(): Flow<List<PlantedTree>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTree(tree: PlantedTree)

    @Delete
    suspend fun deleteTree(tree: PlantedTree)

    @Query("UPDATE planted_trees SET careStatus = :status WHERE id = :id")
    suspend fun updateCareStatus(id: Int, status: String)
}
