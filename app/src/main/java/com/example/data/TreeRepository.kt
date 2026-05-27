package com.example.data

import kotlinx.coroutines.flow.Flow

class TreeRepository(private val treeDao: TreeDao) {
    val allTrees: Flow<List<PlantedTree>> = treeDao.getAllTrees()

    suspend fun insert(tree: PlantedTree) {
        treeDao.insertTree(tree)
    }

    suspend fun delete(tree: PlantedTree) {
        treeDao.deleteTree(tree)
    }

    suspend fun updateCareStatus(id: Int, status: String) {
        treeDao.updateCareStatus(id, status)
    }
}
