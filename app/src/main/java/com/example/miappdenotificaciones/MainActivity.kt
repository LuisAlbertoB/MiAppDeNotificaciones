package com.example.miappdenotificaciones

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.messaging.FirebaseMessaging // Importar FirebaseMessaging

class MainActivity : ComponentActivity() {

    // Registra el lanzador de resultados para solicitar permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("Permissions", "Permiso de notificación concedido")
        } else {
            Log.d("Permissions", "Permiso de notificación denegado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicita el permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Obtener el token de registro de FCM cuando la actividad se crea
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Obtener el nuevo token de registro de FCM
            val token = task.result
            Log.d("FCM_TOKEN_MAIN", "Token de FCM en MainActivity: $token")
            // Puedes mostrar este token en la UI para pruebas
        }

        setContent {
            MyNotificationsApp()
        }
    }
}

@Composable
fun MyNotificationsApp() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Aplicación de Notificaciones Push",
            modifier = Modifier.padding(bottom = 16.dp))

        // Mostrar el último mensaje de notificación recibido en la UI
        if (notificationMessage.value.isNotEmpty()) {
            Text(
                text = "Última Notificación: ${notificationMessage.value}",
                modifier = Modifier.padding(top = 16.dp) // <--- Aquí se corrige la coma y la sintaxis
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Ejemplo de cómo puedes obtener el token en cualquier momento (para depuración)
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM_TOKEN_BUTTON", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d("FCM_TOKEN_BUTTON", "Token actual: $token")
                // Puedes mostrar un Toast o un Snackbar con el token si quieres para pruebas.
            }
        }) {
            Text("Obtener Token FCM (Logcat)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Puedes enviar una notificación de prueba local para verificar el canal
            showNotification(context, "Notificación Local", "¡Esta es una notificación de prueba local!")
        }) {
            Text("Enviar Notificación Local de Prueba")
        }
    }
}