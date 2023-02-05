package com.cool.cloudnotesserver.db.dao

import androidx.room.*
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.cool.cloudnotesserver.db.entity.UrlRole
import com.cool.cloudnotesserver.db.entity.User
import com.cool.cloudnotesserver.db.entity.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UrlRoleDao {
    @Query("select * from UrlRole")
    fun list():List<UrlRole>

    @Query("select * from UrlRole")
    fun listAsFlow():Flow<List<UrlRole>>

    @Query("select * from UrlRole where urlPrefix=:url")
    fun findUrlRolesByUrl(url:String):List<UrlRole>



    @Insert
    fun insert(urlRole : UrlRole)

    @Delete
    fun delete(urlRole: UrlRole)

    @Update
    fun update(urlRole: UrlRole)
}