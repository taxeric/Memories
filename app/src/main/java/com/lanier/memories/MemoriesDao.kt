package com.lanier.memories

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * Author: Turtledove
 * Date  : on 2023/5/8
 * Desc  :
 */
@Dao
interface MemoriesDao {

    @Query("select * from our_memories")
    fun getAllMemories(): List<MemoriesData>

    @Query("select * from our_memories where favourite = 1")
    suspend fun getMemoriesByFavourite(): List<MemoriesData>

    @Query("select * from our_memories where show_in_glance = 1")
    suspend fun getMemoriesByShowInGlance(): List<MemoriesData>

    @Insert
    suspend fun insertMemories(vararg memoriesData: MemoriesData)

    @Delete
    suspend fun deleteMemories(memoriesData: MemoriesData)
}