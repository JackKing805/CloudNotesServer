package com.cool.cloudnotesserver.ui.page

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.room.util.TableInfo
import com.blankj.utilcode.util.NetworkUtils
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.cool.cloudnotesserver.ui.page.viewmodel.MainViewModel
import com.cool.cloudnotesserver.ui.widget.StatusBar
import com.jerry.rt.interfaces.RtCoreListener
import com.jerry.rt.request.constants.Status
import java.text.SimpleDateFormat

data class MainScreenState(
    val statusColor: Color,
    val buttonText: String
)

@Composable
fun MainScreen(viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val collectAsState = viewModel.mainStatus.collectAsState()
    val mainScreenState = when (collectAsState.value.serverStatus) {
        Status.RUNNING -> {
            MainScreenState(
                Color(0xff2ecc71),
                "Stop"
            )
        }
        Status.STOPPED -> {
            MainScreenState(
                Color(0xffe74c3c),
                "Start"
            )
        }
    }


    val shake = rememberInfiniteTransition()
    val serverStatusAlpha = shake.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = InfiniteRepeatableSpec(
            animation = tween<Float>(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val activity = LocalContext.current as FragmentActivity
    Column(modifier = Modifier.fillMaxSize()) {
        StatusBar()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier
                .size(7.dp)
                .clip(RoundedCornerShape(10.dp))
                .graphicsLayer {
                    alpha = serverStatusAlpha.value
                }
                .background(mainScreenState.statusColor)
            )
            
            Text(
                text = NetworkUtils.getIpAddressByWifi().toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        AccessRecordList(viewModel = viewModel)

        Button(
            onClick = {
                viewModel.toggleServer(activity)
            }, modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
                .fillMaxWidth()
        ) {
            Text(text = mainScreenState.buttonText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun ColumnScope.AccessRecordList(viewModel: MainViewModel) {
    val accessRecordList = viewModel.mainStatus.collectAsState().value.accessRecordList
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .weight(1f), content = {
        items(accessRecordList.size, key = {
            it
        }) {
            AccessRecordItem(item = accessRecordList[it])
        }
    })
}

@SuppressLint("SimpleDateFormat")
@Composable
private fun AccessRecordItem(item: AccessRecord) {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth()
    ) {
        Text(text = "ID:${item.id}", maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "url:${item.url}", maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "time:${format.format(item.accessTime)}",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}