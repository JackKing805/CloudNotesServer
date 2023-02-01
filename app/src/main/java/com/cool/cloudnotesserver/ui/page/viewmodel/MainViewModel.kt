package com.cool.cloudnotesserver.ui.page.viewmodel

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.jerry.request_core.Core
import com.jerry.request_core.constants.Status
import com.jerry.request_core.interfaces.IRequestListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(application: Application):AndroidViewModel(application) {
    private val _mainUIStatus = MutableStateFlow(MainUiState())

    val mainStatus = _mainUIStatus

    init {
        Core.listen(object : IRequestListener {
            override fun onStatusChange(status: Status) {
                _mainUIStatus.value = _mainUIStatus.value.copy(serverStatus = status)
            }

            override fun onRequest(url: String) {
                ServerRoom.instance.getAccessRecordDao().insert(AccessRecord(url = url))
            }
        })
        viewModelScope.launch(Dispatchers.IO) {
            val accessRecordDao = ServerRoom.instance.getAccessRecordDao()
            accessRecordDao.listAsFlow().onEach {
                _mainUIStatus.value = _mainUIStatus.value.copy(accessRecordList = it.sortedBy { -it.accessTime }.toMutableList())
            }.collect()
        }
    }


    override fun onCleared() {

    }

    private var isToggle = false
    fun toggleServer(activity: FragmentActivity){
        if (isToggle){
            return
        }
        isToggle = true
        XXPermissions.with(activity)
            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
            .request(object :OnPermissionCallback{
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    isToggle = false
                    if (all){
                        when (mainStatus.value.serverStatus){
                            Status.STOPPED -> {
                                Core.startServer()
                            }
                            Status.RUNNING -> {
                                Core.stopServer()
                            }
                        }
                    }
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    super.onDenied(permissions, never)
                    isToggle =false
                }
            })
    }
}


data class MainUiState(
    val serverStatus: Status = Status.STOPPED,
    val accessRecordList:MutableList<AccessRecord> = mutableListOf()
)