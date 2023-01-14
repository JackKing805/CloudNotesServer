package com.cool.cloudnotesserver.db.dao

import androidx.room.*
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.cool.cloudnotesserver.db.entity.Note
import com.cool.cloudnotesserver.db.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("select * from Note")
    fun list():List<Note>

    @Query("select * from Note")
    fun listAsFlow():Flow<List<Note>>

    @Query("select * from Note limit :start,:size")
    fun list(start:Int=0,size:Int = 10):List<Note>
    @Insert
    fun insert(note : Note)

    @Delete
    fun delete(note: Note)

    @Update
    fun update(note: Note)
}