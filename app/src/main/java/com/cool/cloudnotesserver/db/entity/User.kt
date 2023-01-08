package com.cool.cloudnotesserver.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0,
    @ColumnInfo
    val username: String,
    @ColumnInfo
    val password: String,
    @ColumnInfo
    val nickName:String,
    @ColumnInfo
    val createTime: Long = System.currentTimeMillis()
)
