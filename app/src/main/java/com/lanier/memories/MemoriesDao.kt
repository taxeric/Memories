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

    @Insert
    fun insertMemories(vararg memoriesData: MemoriesData)

    @Delete
    fun deleteMemories(memoriesData: MemoriesData)
}