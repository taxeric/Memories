package com.lanier.memories

/**
 * Author: Turtledove
 * Date  : on 2023/5/8
 * Desc  :
 */
object MemoriesRoomHelper {

    val dao = MemoriesDatabase.db.memoriesDao()

    fun getAllMemories(): List<MemoriesData> {
        return dao.getAllMemories()
    }

    suspend fun getMemoriesById(id: Int): MemoriesData {
        return dao.getMemoriesById(id)
    }

    suspend fun insertMemories(vararg memoriesData: MemoriesData) {
        dao.insertMemories(*memoriesData)
    }

    suspend fun deleteMemories(memoriesData: MemoriesData) {
        dao.deleteMemories(memoriesData)
    }
}