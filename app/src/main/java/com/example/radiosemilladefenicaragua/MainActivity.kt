package com.example.radiosemilladefenicaragua

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
// Importaciones específicas para evitar errores de referencia
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.radiosemilladefenicaragua.ui.theme.RadioSemillaDeFeNicaraguaTheme
import com.google.common.util.concurrent.MoreExecutors

class MainActivity : ComponentActivity() {

    private var controller: MediaController? = null
    // Usamos un estado que Compose pueda rastrear correctamente
    private var isPlayingState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()

        controllerFuture.addListener({
            try {
                val connectedController = controllerFuture.get()
                controller = connectedController

                // Sincronizar el estado inicial
                isPlayingState.value = connectedController.isPlaying

                connectedController.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        isPlayingState.value = isPlaying
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())

        enableEdgeToEdge()

        setContent {
            RadioSemillaDeFeNicaraguaTheme {
                // Observamos el estado reactivo
                val isPlaying by remember { isPlayingState }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RadioControlScreen(
                        modifier = Modifier.padding(innerPadding),
                        isPlaying = isPlaying,
                        onTogglePlay = {
                            if (isPlaying) controller?.pause() else controller?.play()
                        },
                        onStop = {
                            controller?.stop()
                            isPlayingState.value = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RadioControlScreen(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onStop: () -> Unit
) {
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A237E), Color(0xFF000000))
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Logo de la Radio
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_radio),
                    contentDescription = "Logo de la Radio",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Radio Semilla de Fe",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Nicaragua para Cristo",
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Botón Principal de Reproducción
            Button(
                onClick = onTogglePlay,
                modifier = Modifier.size(90.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) Color(0xFFE91E63) else Color(0xFF4CAF50)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Secundario de Detener
            TextButton(onClick = onStop) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = "Detener transmisión",
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DETENER TRANSMISIÓN",
                        color = Color.Red.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}