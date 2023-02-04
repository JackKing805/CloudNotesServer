package com.cool.cloudnotesserver.db.service

import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.db.entity.*
import com.cool.cloudnotesserver.requset.exceptions.DatabaseException

object ServiceRoomService {
    fun createUser(user: User){
        val userDao = ServerRoom.instance.getUserDao()
        userDao.insert(user)
        val findByUserName = userDao.findByUserName(user.username)
        if (findByUserName!=null){

            //设置Role
            val userRoleDao = ServerRoom.instance.getUserRoleDao()
            val roleDao = ServerRoom.instance.getRoleDao()
            if (user.username=="18582672979"){
                //超级管理员
                val superRole = roleDao.getRoleByRoleName("super")?:throw DatabaseException("super role is not expires")
                userRoleDao.insert(UserRole(userId = findByUserName.id, roleId = superRole.id))
            }else{
                //普通用户
                val userRole = roleDao.getRoleByRoleName("user")?:throw DatabaseException("user role is not expires")
                userRoleDao.insert(UserRole(userId = findByUserName.id, roleId = userRole.id))
            }
        }
    }

    fun createPermission(roleName:String,permissionName:String,permissionDesc: String){
        val permissionDao = ServerRoom.instance.getPermissionDao()
        val roleDao = ServerRoom.instance.getRoleDao()
        val rolePermissionDao = ServerRoom.instance.getRolePermissionDao()


        val role = roleDao.getRoleByRoleName(roleName)?:throw DatabaseException("$roleName is not expires")
        permissionDao.insert(Permission(permissionName = permissionName, permissionDesc = permissionDesc))
        val permission =permissionDao.getPermissionByPermissionName(permissionName = permissionName)?:throw DatabaseException("$permissionName insert error")


        val superRole = roleDao.getRoleByRoleName("super")?:throw DatabaseException("super role is not expires")


        rolePermissionDao.insert(RolePermission(roleId = role.id, permissionId = permission.id))
        rolePermissionDao.insert(RolePermission(roleId = superRole.id, permissionId = permission.id))
    }

    fun createRole(roleName:String,roleDesc:String){
        val roleDao = ServerRoom.instance.getRoleDao()
        val role = roleDao.getRoleByRoleName(roleName)
        if (role==null){
            roleDao.insert(Role(roleName = roleName, roleDesc = roleDesc))
        }
    }
}