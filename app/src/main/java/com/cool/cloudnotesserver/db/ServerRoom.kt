package com.cool.cloudnotesserver.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cool.cloudnotesserver.ServerApp
import com.cool.cloudnotesserver.db.dao.*
import com.cool.cloudnotesserver.db.entity.*

@Database(entities = [AccessRecord::class, User::class, Note::class,Role::class,Permission::class,UserRole::class,RolePermission::class], version = 3, exportSchema = false)
abstract class ServerRoom : RoomDatabase() {
    companion object {
        val instance by lazy {
            Room.databaseBuilder(ServerApp.app, ServerRoom::class.java, "server.db")
                .allowMainThreadQueries()
                .build()
        }


        fun onCreate(){
            val roleDao = instance.getRoleDao()
            if (roleDao.getRoleByRoleName("super")==null){
                roleDao.insert(Role(roleName = "super", roleDesc = "超级管理员"))
            }
            if (roleDao.getRoleByRoleName("user")==null){
                roleDao.insert(Role(roleName = "user", roleDesc = "普通用户"))
            }
        }
    }

    abstract fun getAccessRecordDao():AccessRecordDao
    abstract fun getUserDao():UserDao
    abstract fun getNoteDao():NoteDao
    abstract fun getRoleDao():RoleDao
    abstract fun getPermissionDao():PermissionDao
    abstract fun getRolePermissionDao():RolePermissionDao
    abstract fun getUserRoleDao():UserRoleDao
}