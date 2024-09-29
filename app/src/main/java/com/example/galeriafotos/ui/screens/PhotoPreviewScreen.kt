package com.example.galeriafotos.ui.screens


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPreviewScreen(
    photoUri: String,
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit,
    onRequestLocationPermission: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope() // Crear un CoroutineScope
    var hasLocationPermission by remember { mutableStateOf(false) }

    // Obtener la ubicación actual al iniciar la pantalla
    LaunchedEffect(Unit) {
        hasLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            getCurrentLocation(context, fusedLocationClient) { loc ->
                coroutineScope.launch { // Lanzar una corutina para ejecutar funciones de suspensión
                    val address = getAddressFromCoordinates(context, loc.latitude, loc.longitude)
                    location = address ?: "${loc.latitude}, ${loc.longitude}" // Muestra la dirección o las coordenadas si no se encuentra
                }
            }
        } else {
            // Llamar al callback para solicitar permisos si no están otorgados
            onRequestLocationPermission()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Previsualizar Foto") })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = photoUri),
                contentDescription = "Previsualización de la foto",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 16.dp)
            )
            Text("Descripción:")
            BasicTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Text("Ubicación:")
            BasicTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Button(
                onClick = { onSave(description, location) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Guardar")
            }
            TextButton(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Cancelar")
            }
        }
    }
}

// Función para obtener la ubicación actual
@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationObtained: (Location) -> Unit
) {
    try {
        // Verificar que los permisos de ubicación están otorgados antes de intentar obtener la ubicación
        val task = fusedLocationClient.lastLocation
        task.addOnSuccessListener { location ->
            location?.let {
                onLocationObtained(it)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// Función para convertir coordenadas en una dirección
private suspend fun getAddressFromCoordinates(
    context: Context,
    latitude: Double,
    longitude: Double
): String? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) { // Verifica que no sea nulo y no esté vacío
                val address = addresses[0] // Obtiene la primera dirección
                "${address.locality ?: "Localidad desconocida"}, ${address.adminArea ?: "Región desconocida"}, ${address.countryName ?: "País desconocido"}" // Devuelve ciudad, región y país con valores por defecto
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}