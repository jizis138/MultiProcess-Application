/**
 * Created by Dmitry Popov on 30.06.2024.
 */
package ru.vsibi.multiprocess_application

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ru.vsibi.multiprocess_application.model.MainCounter

class MainService : Service() {

    private var message = ""

    private val binder: IBinder = object : MainCounter.Stub() {

        override fun sendNumber(number: Int) {
            updateNotification(number)
        }

        override fun clearNumber() {
            updateNotification(0)
        }

        override fun getMessage(): String {
            return this@MainService.message
        }

    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private fun updateNotification(number: Int) {
        message = "$number - привет из другого процесса"
        val notification = NotificationUtil.buildNotification(
            applicationContext,
            message
        )
        NotificationUtil.notificationManager(applicationContext).notify(3, notification)
    }

}