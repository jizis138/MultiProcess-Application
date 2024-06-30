/**
 * Created by Dmitry Popov on 30.06.2024.
 */
package ru.vsibi.multiprocess_application

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import ru.vsibi.multiprocess_application.model.MainCounter

class ServiceController(
    private val context: Context,
    onServiceConnected: (MainCounter) -> Unit,
    onServiceDisconnected: () -> Unit
) {

    private val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                onServiceConnected(MainCounter.Stub.asInterface(service))
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                onServiceDisconnected()
            }
        }


    fun bindMainService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotificationChannel(context)
        }
        val notification = NotificationUtil.buildNotification(
            context,
            context.getString(R.string.notification)
        )
        NotificationUtil.notificationManager(context).notify(3, notification)

        context.bindService(Intent(context, MainService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindMainService() {
        context.unbindService(serviceConnection)

        NotificationUtil.notificationManager(context).cancel(3)
    }
}