package com.cool.cloudnotesserver.requset.configuration

import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.db.entity.User
import com.jerry.request_base.annotations.Bean
import com.jerry.request_base.annotations.Configuration
import com.jerry.request_shiro.shiro.interfaces.IShiroAuth
import com.jerry.request_shiro.shiro.model.AuthToken
import com.jerry.request_shiro.shiro.model.AuthenticationInfo
import com.jerry.request_shiro.shiro.model.AuthorizationInfo
import com.jerry.rt.core.http.pojo.Request
import java.util.UUID

//注解拦截处理器
@Configuration
class AuthConfig {
    @Bean
    fun shiroAuth()=object :IShiroAuth{
        override fun onAuthentication(authToken: AuthToken): AuthenticationInfo? {
            val findByUserName = ServerRoom.instance.getUserDao().findByUserName(authToken.getUserName())?:return null
            return AuthenticationInfo(findByUserName,authToken.getPassword(),UUID.randomUUID().toString())
        }

        override fun onAuthorization(authorization: AuthenticationInfo): AuthorizationInfo {
            val authzation = AuthorizationInfo()

            val user = authorization.main as User
            val userRoleDao = ServerRoom.instance.getUserRoleDao()
            val roleDao = ServerRoom.instance.getRoleDao()
            val permissionDao = ServerRoom.instance.getPermissionDao()
            val rolePermissionDao = ServerRoom.instance.getRolePermissionDao()
            //查找role
            userRoleDao.findUserRolesByUserId(user.id).forEach {
                roleDao.getRoleById(it.roleId)?.let { r->
                    authzation.setRole(r.roleName)
                    rolePermissionDao.findRolePermissionsByRoleId(r.id).forEach { rp->
                        permissionDao.getPermissionById(rp.permissionId)?.let {
                            authzation.setPermission(it.permissionName)
                        }
                    }
                }
            }
            return authzation
        }

        override fun getAccessToken(request: Request, shiroTokenName: String): String {
            return request.getPackage().getHeader().getHeaderValue("Token","")
        }
    }
}