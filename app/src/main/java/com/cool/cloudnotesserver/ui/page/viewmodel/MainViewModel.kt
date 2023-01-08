package com.cool.cloudnotesserver.ui.page.viewmodel

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.UiMessageUtils
import com.cool.cloudnotesserver.ServerApp
import com.cool.cloudnotesserver.background.ServerService
import com.cool.cloudnotesserver.db.ServerRoom
import com.cool.cloudnotesserver.db.dao.AccessRecordDao
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.jerry.rt.interfaces.RtCoreListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(application: Application):AndroidViewModel(application) {
    private val _mainUIStatus = MutableStateFlow(MainUiState())

    val mainStatus = _mainUIStatus

    private val uiMessage = UiMessageUtils.UiMessageCallback {
        if(it.id==1){
            val newStatus = it.`object` as RtCoreListener.Status
            _mainUIStatus.value = _mainUIStatus.value.copy(serverStatus = newStatus)
        }else if (it.id==2){
            _mainUIStatus.value = _mainUIStatus.value.copy(serverStatus = null)
        }
    }

    init {
        UiMessageUtils.getInstance().addListener(uiMessage)
        viewModelScope.launch(Dispatchers.IO) {
            val accessRecordDao = ServerRoom.instance.getAccessRecordDao()
            accessRecordDao.listAsFlow().onEach {
                _mainUIStatus.value = _mainUIStatus.value.copy(accessRecordList = it.sortedBy { -it.accessTime }.toMutableList())
            }.collect()
        }
    }


    override fun onCleared() {
        UiMessageUtils.getInstance().removeListener(uiMessage)
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
                        mainStatus.value.serverStatus?.let {
                            ServerService.run(getApplication(),false)
                        }?: kotlin.run {
                            ServerService.run(getApplication(),true)
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
    val serverStatus:RtCoreListener.Status?=null,
    val accessRecordList:MutableList<AccessRecord> = mutableListOf()
)