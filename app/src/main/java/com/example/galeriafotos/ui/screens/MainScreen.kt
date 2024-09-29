package com.example.galeriafotos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.galeriafotos.data.model.PhotoItem
import com.example.galeriafotos.data.repository.PhotoRepository
import com.example.galeriafotos.ui.components.PhotoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    photoRepository: PhotoRepository,
    onCapturePhoto: () -> Unit,
    onSelectPhoto: () -> Unit
) {
    val photos by remember { mutableStateOf(photoRepository.getAllPhotos()) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Galería de Fotos 📸") })
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(onClick = onCapturePhoto, modifier = Modifier.padding(bottom = 16.dp)) {
                    Text("Capturar 📷")
                }
                FloatingActionButton(onClick = onSelectPhoto) {
                    Text("Seleccionar 👆")
                }
            }
        }
    ) { contentPadding -> // Aquí recibimos el padding que Scaffold pasa al contenido
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding) // Aplicar el padding proporcionado por Scaffold
                .padding(16.dp) // Padding adicional que deseas
        ) {
            items(photos) { photo ->
                PhotoCard(photo)
            }
        }
    }
}