package com.cool.cloudnotesserver.db.dao

import androidx.room.*
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.cool.cloudnotesserver.db.entity.Permission
import com.cool.cloudnotesserver.db.entity.Role
import com.cool.cloudnotesserver.db.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface PermissionDao {
    @Query("select * from Permission")
    fun list():List<Permission>

    @Query("select * from Permission")
    fun listAsFlow():Flow<List<Permission>>

    @Query("select * from Permission where id=:id")
    fun getPermissionById(id:Long):Permission?

    @Query("select * from Permission where permissionName=:permissionName")
    fun getPermissionByPermissionName(permissionName:String):Permission?

    @Insert
    fun insert(permission : Permission)

    @Delete
    fun delete(permission: Permission)

    @Update
    fun update(permission: Permission)
}