package com.cool.cloudnotesserver.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id", unique = true)])
data class Role(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo
    val roleName:String,
    @ColumnInfo
    val roleDesc:String = "",
    @ColumnInfo
    val createTime: Long = System.currentTimeMillis()
)
