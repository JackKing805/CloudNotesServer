package com.cool.cloudnotesserver.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ServerFileRecord(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    @ColumnInfo
    val name:String,
    @ColumnInfo
    val path:String,
    @ColumnInfo
    val size:Long,
    @ColumnInfo
    val createTime:Long = System.currentTimeMillis()
)