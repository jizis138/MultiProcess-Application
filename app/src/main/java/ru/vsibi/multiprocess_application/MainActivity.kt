package ru.vsibi.multiprocess_application

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import ru.vsibi.multiprocess_application.model.MainCounter
import ru.vsibi.multiprocess_application.ui.theme.MultiProcessApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiProcessApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
    val notificationPermissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS
    )
    if (!notificationPermissionState.status.isGranted) {
        LaunchedEffect(notificationPermissionState.status) {
            notificationPermissionState.launchPermissionRequest()
        }
    }
    val textFromServiceProcess = remember {
        mutableStateOf("")
    }
    val mainCounter = remember {
        mutableStateOf<MainCounter?>(null)
    }
    val counter = remember {
        mutableIntStateOf(0)
    }
    val serviceStatus = remember {
        mutableStateOf(false)
    }
    val buttonValue = remember {
        mutableStateOf("Start Service")
    }
    val context = LocalContext.current

    val serviceController = remember {
        mutableStateOf(
            ServiceController(
                context = context,
                onServiceConnected = {
                    mainCounter.value = it
                },
                onServiceDisconnected = {
                    mainCounter.value = null
                }
            ))
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            if (serviceStatus.value) {
                serviceStatus.value = !serviceStatus.value
                buttonValue.value = "Bind Service"
                textFromServiceProcess.value = ""
                serviceController.value.unbindMainService()
            } else {
                serviceStatus.value = !serviceStatus.value
                buttonValue.value = "Unbind Service"

                serviceController.value.bindMainService()
            }
        }) {
            Text(text = buttonValue.value)
        }

        if (serviceStatus.value) {
            Button(
                onClick = {
                    counter.intValue++
                    mainCounter.value?.sendNumber(counter.intValue)
                    mainCounter.value?.message?.let {
                        textFromServiceProcess.value = it
                    }
                }) {
                Text(text = counter.intValue.toString())
            }
            Button(
                onClick = {
                    counter.intValue = 0
                    mainCounter.value?.clearNumber()
                    mainCounter.value?.message?.let {
                        textFromServiceProcess.value = it
                    }
                }) {
                Text(text = "Clear")
            }
        }
        if (textFromServiceProcess.value != "") {
            Text(text = textFromServiceProcess.value)
        }
    }

}

