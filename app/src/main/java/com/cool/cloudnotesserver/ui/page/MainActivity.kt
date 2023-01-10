package com.cool.cloudnotesserver.ui.page

import com.cool.cloudnotesserver.base.BaseActivity
import com.cool.cloudnotesserver.extensions.serverContent

//ui：https://www.zcool.com.cn/work/ZNDM1MjQyMDA=.html
class MainActivity : BaseActivity() {
    override fun onContent() {
        serverContent { 
           MainScreen()
        }
    }
}
