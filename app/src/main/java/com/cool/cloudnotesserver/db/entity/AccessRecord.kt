package com.cool.cloudnotesserver.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccessRecord(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id:Long = 0,
    @ColumnInfo
    val url:String,
    @ColumnInfo
    val accessTime:Long = System.currentTimeMillis()
)
