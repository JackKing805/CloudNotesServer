package com.cool.cloudnotesserver.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0,
    @ColumnInfo
    val title:String,
    @ColumnInfo
    val content: String,
    @ColumnInfo
    val type:String,
    @ColumnInfo
    val lock:Boolean = false,
    @ColumnInfo
    val lastModifyTime:Long = System.currentTimeMillis(),
    @ColumnInfo
    val createTime: Long = System.currentTimeMillis()
)
