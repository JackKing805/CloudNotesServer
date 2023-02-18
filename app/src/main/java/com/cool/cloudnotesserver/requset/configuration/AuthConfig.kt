package com.cool.cloudnotesserver.requset.configuration

import android.content.Context
import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.db.entity.User
import com.jerry.request_base.annotations.Bean
import com.jerry.request_base.annotations.Configuration
import com.jerry.request_core.additation.DefaultAuthConfigRegister
import com.jerry.request_shiro.shiro.ShiroUtils
import com.jerry.request_shiro.shiro.bean.ShiroLogic
import com.jerry.request_shiro.shiro.interfaces.IShiroAuth
import com.jerry.request_shiro.shiro.model.AuthToken
import com.jerry.request_shiro.shiro.model.AuthenticationInfo
import com.jerry.request_shiro.shiro.model.AuthorizationInfo
import com.jerry.rt.core.http.pojo.Request
import com.jerry.rt.core.http.pojo.Response
import java.util.UUID

//注解拦截处理器
@Configuration
class AuthConfig {
    @Bean
    fun shiroAuth() = object : IShiroAuth {
        override fun onAuthentication(authToken: AuthToken): AuthenticationInfo? {
            val findByUserName =
                ServerRoom.instance.getUserDao().findByUserName(authToken.getUserName())
                    ?: return null
            return AuthenticationInfo(
                findByUserName,
                authToken.getPassword(),
                UUID.randomUUID().toString()
            )
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
                roleDao.getRoleById(it.roleId)?.let { r ->
                    authzation.setRole(r.roleName)
                    rolePermissionDao.findRolePermissionsByRoleId(r.id).forEach { rp ->
                        permissionDao.getPermissionById(rp.permissionId)?.let { p->
                            authzation.setPermission(p.permissionName)
                        }
                    }
                }
            }
            return authzation
        }

        override fun getAccessToken(request: Request, shiroTokenName: String): String {
            var token = request.getPackage().getHeader().getHeaderValue("Token", "")
            if(token.isEmpty()){
                token = request.getPackage().getHeader().getCookie(shiroTokenName)?:""
            }
            return token
        }
    }

    @Bean
    fun authInter(): DefaultAuthConfigRegister.RequestInterceptor {
        val aa = DefaultAuthConfigRegister.RequestInterceptor()
        aa.interceptor("/")
        aa.build(object : DefaultAuthConfigRegister.IRequestHandler {
            override fun handle(context: Context, request: Request, response: Response): Boolean {
                val path = request.getPackage().getRequestURI().path

                val urlRoleDao = ServerRoom.instance.getUrlRoleDao()

                val findUrlRolesByUrl = urlRoleDao.list().filter { it.urlPrefix==path || path.startsWith(it.urlPrefix) }.sortedBy { it.urlPrefix.length }
                if (findUrlRolesByUrl.isNotEmpty()){
                    val pathRoles = mutableListOf<String>()
                    val pathPermissions = mutableListOf<String>()

                    val roleDao = ServerRoom.instance.getRoleDao()
                    val rolePermissionDao = ServerRoom.instance.getRolePermissionDao()
                    val permissionDao = ServerRoom.instance.getPermissionDao()

                    findUrlRolesByUrl.forEach {
                        roleDao.getRoleById(it.roleId)?.let {
                            pathRoles.add(it.roleName)
                            rolePermissionDao.findRolePermissionsByRoleId(it.id).forEach {
                                permissionDao.getPermissionById(it.permissionId)?.let {
                                    pathPermissions.add(it.permissionName)
                                }
                            }
                        }
                    }

                    ShiroUtils.verify(
                        request,
                        pathRoles.toList(),
                        pathPermissions.toList(),
                        roleLogic = ShiroLogic.OR,
                        permissionLogic = ShiroLogic.OR
                    )
                }

                return true
            }
        })
        return aa
    }
}