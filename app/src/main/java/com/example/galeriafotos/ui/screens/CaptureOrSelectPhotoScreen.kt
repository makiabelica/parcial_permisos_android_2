package com.example.galeriafotos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureOrSelectPhotoScreen(
    onCapturePhoto: () -> Unit,
    onSelectPhoto: () -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Captura o Selección") })
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it).padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onCapturePhoto,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Tomar Foto")
            }
            Button(
                onClick = onSelectPhoto,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Seleccionar de Galería")
            }
            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Cancelar")
            }
        }
    }
}