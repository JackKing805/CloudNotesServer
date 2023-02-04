package com.cool.cloudnotesserver.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id", unique = true)])
data class Note(
    @PrimaryKey(autoGenerate = true)
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
