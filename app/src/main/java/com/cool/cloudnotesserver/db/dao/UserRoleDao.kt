package com.cool.cloudnotesserver.db.dao

import androidx.room.*
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.cool.cloudnotesserver.db.entity.User
import com.cool.cloudnotesserver.db.entity.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserRoleDao {
    @Query("select * from UserRole")
    fun list():List<UserRole>

    @Query("select * from UserRole")
    fun listAsFlow():Flow<List<UserRole>>

    @Query("select * from UserRole where userId=:userId")
    fun findUserRolesByUserId(userId:Long):List<UserRole>

    @Query("select * from UserRole where roleId=:roleId")
    fun findUserRolesByRoleId(roleId:Long):List<UserRole>


    @Insert
    fun insert(userRole : UserRole)

    @Delete
    fun delete(userRole: UserRole)

    @Update
    fun update(userRole: UserRole)
}