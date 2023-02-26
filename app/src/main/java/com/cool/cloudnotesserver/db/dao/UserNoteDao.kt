package com.cool.cloudnotesserver.db.dao

import androidx.room.*
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.cool.cloudnotesserver.db.entity.Note
import com.cool.cloudnotesserver.db.entity.User
import com.cool.cloudnotesserver.db.entity.UserNote
import kotlinx.coroutines.flow.Flow

@Dao
interface UserNoteDao {
    @Query("select * from UserNote")
    fun list():List<UserNote>

    @Query("select * from UserNote")
    fun listAsFlow():Flow<List<UserNote>>

    @Query("select * from UserNote where userId=:userId")
    fun listByUserId(userId:Long):List<UserNote>

    @Insert
    fun insert(userNote : UserNote)

    @Delete
    fun delete(userNote: UserNote)

    @Update
    fun update(userNote: UserNote)
}