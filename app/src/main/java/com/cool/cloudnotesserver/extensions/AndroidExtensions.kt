package com.cool.cloudnotesserver.extensions

import android.media.MediaParser.InputReader
import androidx.compose.ui.focus.FocusDirection.Companion.In
import com.blankj.utilcode.util.GsonUtils
import com.cool.cloudnotesserver.ServerApp
import java.io.BufferedReader
import java.io.InputStreamReader

fun String.fromAssets(): String {
    val stringBuilder = StringBuilder()
    BufferedReader(InputStreamReader(ServerApp.app.assets.open(this))).use {
        var line = ""
        while (it.readLine().also { r->
                line = r
            } != null) {
            stringBuilder.append(line)
        }
    }
    return stringBuilder.toString()
}