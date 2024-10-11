package com.example.projecmanage.fcm


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.example.projecmanage.Activities.MainActivity
import com.example.projecmanage.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.nio.channels.Channel
import java.util.Random

class MyFirebaseMessagingService(private val applicationContext : Context) : FirebaseMessagingService(){

    private val channelId = "projecManage_notification_channel_id"
    private val channelName = "Channel projemanag title"
    val intent = Intent(applicationContext,MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(applicationContext,
        0,intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)


    private val notificationManager : NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createNotificationChannel()
        }
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            applicationContext,channelId)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            with(NotificationManagerCompat.from(applicationContext)){
                if(ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED){
                    return
                }
                notify(Random().nextInt(3000),notificationBuilder.build())
            }
        } else{
            NotificationManagerCompat.from(applicationContext).notify(Random().nextInt(3000),notificationBuilder.build())
        }

      /*  Log.d("test","from : ${message.from}")
        message.data.isNotEmpty().let {
            Log.d("test","Message data payloads : ${message.data}")
        }
        val notification = message.notification?.let {
            Log.d("test","Message Notification Body : ${it.body}")
            val title = it.title
            val message = it.body
        }*/
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("test","refreshed token : $token")
        sendRegistration(token)
    }

    private fun sendRegistration(token:String?){
        //ToDO implemengtation
    }

 /*    fun sendNotification(context : Context,title:String,messageBody:String){
        val intent = Intent(context,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context,
            0,intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
     notificationManager.createNotificationChannel(channel)
 }

}