package com.cool.cloudnotesserver.db.dao

import androidx.room.*
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.cool.cloudnotesserver.db.entity.RolePermission
import com.cool.cloudnotesserver.db.entity.User
import com.cool.cloudnotesserver.db.entity.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface RolePermissionDao {
    @Query("select * from RolePermission")
    fun list():List<RolePermission>

    @Query("select * from RolePermission")
    fun listAsFlow():Flow<List<RolePermission>>

    @Query("select * from RolePermission where roleId=:roleId")
    fun findRolePermissionsByRoleId(roleId:Long):List<RolePermission>

    @Query("select * from RolePermission where permissionId=:permissionId")
    fun findRolePermissionsByPermissionId(permissionId:Long):List<RolePermission>


    @Insert
    fun insert(rolePermission : RolePermission)

    @Delete
    fun delete(rolePermission: RolePermission)

    @Update
    fun update(rolePermission: RolePermission)
}