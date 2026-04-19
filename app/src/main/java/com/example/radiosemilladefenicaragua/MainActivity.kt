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
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.radiosemilladefenicaragua.ui.theme.RadioSemillaDeFeNicaraguaTheme
import com.google.common.util.concurrent.MoreExecutors

class MainActivity : ComponentActivity() {

    private var controller: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Conexión con el PlaybackService
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()

        controllerFuture.addListener({
            controller = controllerFuture.get()
        }, MoreExecutors.directExecutor())

        enableEdgeToEdge()

        setContent {
            RadioSemillaDeFeNicaraguaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RadioControlScreen(
                        modifier = Modifier.padding(innerPadding),
                        onPlay = { controller?.play() },
                        onPause = { controller?.pause() }
                    )
                }
            }
        }
    }
}

@Composable
fun RadioControlScreen(
    modifier: Modifier = Modifier,
    onPlay: () -> Unit,
    onPause: () -> Unit
) {
    // Gradiente de fondo (bg-gradient-to-b)
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
            // Contenedor del Logo con el archivo WebP
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
                    contentScale = ContentScale.Crop // Equivalente a object-cover
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

            // Botón de Play Circular
            Button(
                onClick = onPlay,
                modifier = Modifier.size(90.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "▶",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Detener
            TextButton(onClick = onPause) {
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