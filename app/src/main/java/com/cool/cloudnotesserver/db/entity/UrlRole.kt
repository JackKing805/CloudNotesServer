package com.cool.cloudnotesserver.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id", unique = true)])
data class UrlRole(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo
    val urlPrefix:String,
    @ColumnInfo
    val roleId:Long,
    @ColumnInfo
    val createTime: Long = System.currentTimeMillis()
)
