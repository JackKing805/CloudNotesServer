package com.cool.cloudnotesserver.db.dao

import androidx.room.*
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.cool.cloudnotesserver.db.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("select * from User")
    fun list():List<User>

    @Query("select * from User")
    fun listAsFlow():Flow<List<User>>

    @Query("select * from User where username=:username")
    fun findByUserName(username:String):User?

    @Insert
    fun insert(user : User)

    @Delete
    fun delete(user: User)

    @Update
    fun update(user: User)
}