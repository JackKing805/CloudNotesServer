package com.cool.cloudnotesserver.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.cool.cloudnotesserver.db.entity.AccessRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessRecordDao {
    @Query("select * from AccessRecord")
    fun list():List<AccessRecord>

    @Query("select * from AccessRecord")
    fun listAsFlow():Flow<List<AccessRecord>>

    @Insert
    fun insert(bookRule : AccessRecord)

    @Delete
    fun delete(bookRule: AccessRecord)
}