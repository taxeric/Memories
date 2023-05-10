package com.lanier.memories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Author: Turtledove
 * Date  : on 2023/5/8
 * Desc  :
 */
@Entity(tableName = "our_memories")
@Serializable
data class MemoriesData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "pic_path") val path: String,
    @ColumnInfo(name = "pic_name") val name: String,
    @ColumnInfo(name = "pic_time") val time: Long = 0L,
    @ColumnInfo(name = "desc") val desc: String = "",
    @ColumnInfo(name = "favourite") val favourite: Boolean = false,
    @ColumnInfo(name = "show_in_glance") val showInGlance: Boolean = false,
)
