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

    fun insertMemories(vararg memoriesData: MemoriesData) {
        dao.insertMemories(*memoriesData)
    }

    fun deleteMemories(memoriesData: MemoriesData) {
        dao.deleteMemories(memoriesData)
    }
}