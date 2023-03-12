package com.cool.cloudnotesserver.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.cool.cloudnotesserver.db.entity.ServerFileRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerFileRecordDao {
    @Query("select * from ServerFileRecord")
    fun list():List<ServerFileRecord>

    @Query("select * from ServerFileRecord")
    fun listAsFlow():Flow<List<ServerFileRecord>>

    @Insert
    fun insert(serverFileRecord : ServerFileRecord)

    @Delete
    fun delete(serverFileRecord: ServerFileRecord)
}