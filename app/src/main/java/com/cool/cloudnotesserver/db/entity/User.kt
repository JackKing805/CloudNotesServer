package com.cool.cloudnotesserver.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id", unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo
    val username: String,
    @ColumnInfo
    val salt: String,//密码盐
    @ColumnInfo
    val hashedCredential:String,//密码加盐后的字符串
    @ColumnInfo
    val nickName:String,
    @ColumnInfo
    val createTime: Long = System.currentTimeMillis()
)
