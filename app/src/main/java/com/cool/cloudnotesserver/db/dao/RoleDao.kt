package com.cool.cloudnotesserver.db.dao

import androidx.room.*
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.cool.cloudnotesserver.db.entity.Role
import com.cool.cloudnotesserver.db.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface RoleDao {
    @Query("select * from Role")
    fun list():List<Role>

    @Query("select * from Role")
    fun listAsFlow():Flow<List<Role>>

    @Query("select * from Role where id=:id")
    fun getRoleById(id:Long):Role?

    @Query("select * from Role where roleName=:roleName")
    fun getRoleByRoleName(roleName:String):Role?

    @Insert
    fun insert(role : Role)

    @Delete
    fun delete(role: Role)

    @Update
    fun update(role: Role)
}