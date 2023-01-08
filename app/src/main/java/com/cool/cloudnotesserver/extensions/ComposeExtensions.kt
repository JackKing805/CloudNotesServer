package com.cool.cloudnotesserver.extensions

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cool.cloudnotesserver.base.compose.CloudNotesServerTheme

fun ComponentActivity.serverContent(content:@Composable ()->Unit){
    setContent {
        CloudNotesServerTheme {
           content()
        }
    }
}