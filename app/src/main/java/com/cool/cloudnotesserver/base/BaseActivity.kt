package com.cool.cloudnotesserver.base

import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity

abstract class BaseActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        onContent()
        onComposeCreated()
    }

    override fun onResume() {
        super.onResume()
        onComposeResume()
    }

    override fun onPause() {
        super.onPause()
        onComposePause()
    }

    override fun onDestroy() {
        onComposeDestroy()
        super.onDestroy()
    }

    abstract fun onContent()

    open fun onComposeCreated(){}
    open fun onComposeResume(){}
    open fun onComposePause(){}
    open fun onComposeDestroy(){}
}