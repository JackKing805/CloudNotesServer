package com.cool.cloudnotesserver.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cool.cloudnotesserver.ServerApp
import com.cool.cloudnotesserver.db.dao.*
import com.cool.cloudnotesserver.db.entity.*

@Database(entities = [AccessRecord::class, User::class, Note::class,Role::class,Permission::class,UserRole::class,RolePermission::class,UrlRole::class,UserNote::class], version = 4, exportSchema = false)
abstract class ServerRoom : RoomDatabase() {
    companion object {
        val instance by lazy {
            Room.databaseBuilder(ServerApp.app, ServerRoom::class.java, "server.db")
                .allowMainThreadQueries()
                .build()
        }


        fun onCreate(){
            val roleDao = instance.getRoleDao()
            val urlRoleDao = instance.getUrlRoleDao()
            if (roleDao.getRoleByRoleName("super")==null){
                roleDao.insert(Role(roleName = "super", roleDesc = "超级管理员"))
                val superRole = roleDao.getRoleByRoleName("super")!!
                urlRoleDao.insert(UrlRole(urlPrefix = "/auth", roleId = superRole.id))
            }
            if (roleDao.getRoleByRoleName("user")==null){
                roleDao.insert(Role(roleName = "user", roleDesc = "普通用户"))
                val userRole = roleDao.getRoleByRoleName("user")!!
                urlRoleDao.insert(UrlRole(urlPrefix = "/auth", roleId = userRole.id))
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
    abstract fun getUrlRoleDao():UrlRoleDao
    abstract fun getUserNoteDao():UserNoteDao
}