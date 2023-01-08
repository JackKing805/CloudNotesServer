package com.cool.cloudnotesserver.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cool.cloudnotesserver.background.ServerService
import com.cool.cloudnotesserver.base.BaseActivity
import com.cool.cloudnotesserver.extensions.serverContent
import com.cool.cloudnotesserver.ui.widget.StatusBar

//ui：https://www.zcool.com.cn/work/ZNDM1MjQyMDA=.html
class MainActivity : BaseActivity() {
    override fun onContent() {
        serverContent { 
           MainScreen()
        }
    }
}
