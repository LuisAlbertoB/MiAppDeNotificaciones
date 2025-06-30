package com.example.miappdenotificaciones

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.compose.runtime.mutableStateOf // <--- MOVIDO AQUÍ

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Este método se llama cuando se genera un nuevo token de registro de dispositivo
        // O cuando se actualiza un token existente.
        Log.d("FCM_TOKEN", "Nuevo token: $token")
        // Aquí puedes enviar el token a tu backend si lo necesitas para enviar notificaciones dirigidas.
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Este método se llama cuando la aplicación está en primer plano y recibe una notificación.
        Log.d("FCM_MESSAGE", "Mensaje recibido: ${remoteMessage.notification?.body}")

        // Define las variables de título y cuerpo para que sean accesibles
        val notificationTitle = remoteMessage.notification?.title ?: "Notificación"
        val notificationBody = remoteMessage.notification?.body ?: "Nuevo mensaje"

        // Procesa los datos de la notificación
        showNotification(applicationContext, notificationTitle, notificationBody)

        // Si la notificación contiene datos adicionales (data payload), puedes acceder a ellos aquí
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM_DATA", "Datos del mensaje: ${remoteMessage.data}")
            // Puedes usar estos datos para actualizar la UI en tiempo real o realizar otras acciones.
            // Usamos notificationBody como fallback si custom_message no existe
            updateNotificationMessage(remoteMessage.data["custom_message"] ?: notificationBody)
        }
    }
}

// Función para mostrar la notificación localmente si la app está en primer plano
fun showNotification(context: Context, title: String, message: String) {
    val channelId = "my_channel_id"
    val notificationId = 1

    // Crea un canal de notificación para Android 8.0 (API 26) y superior
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Mi Canal de Notificaciones", // Nombre visible para el usuario
            NotificationManager.IMPORTANCE_DEFAULT // Importancia de la notificación
        ).apply {
            description = "Canal para notificaciones de la aplicación."
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    // Construye la notificación
    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info) // Icono pequeño de la notificación
        .setContentTitle(title) // Título de la notificación
        .setContentText(message) // Texto principal de la notificación
        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Prioridad de la notificación
        .setAutoCancel(true) // Cierra la notificación al hacer clic

    // Muestra la notificación
    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, notificationBuilder.build())
    }
}

// Estado global para actualizar la UI con el mensaje de la notificación
val notificationMessage = mutableStateOf("")

fun updateNotificationMessage(message: String) {
    notificationMessage.value = message
}