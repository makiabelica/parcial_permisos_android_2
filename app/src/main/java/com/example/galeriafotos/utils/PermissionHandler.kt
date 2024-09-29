package com.example.galeriafotos.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

class PermissionHandler(private val activity: ComponentActivity) {

    // URI de la foto capturada
    var photoUri: Uri? = null

    // Lanzadores de actividad para cámara, galería y permisos
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var selectPictureLauncher: ActivityResultLauncher<String>
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestLocationPermissionLauncher: ActivityResultLauncher<Array<String>>

    // Callbacks para devolver los resultados
    private var onCapturePhotoSuccess: ((Uri) -> Unit)? = null
    private var onSelectPhotoSuccess: ((Uri) -> Unit)? = null
    private var onLocationPermissionGranted: (() -> Unit)? = null

    // Inicializar los lanzadores de permisos y actividades
    fun initLaunchers(
        onCapturePhotoSuccess: (Uri) -> Unit,
        onSelectPhotoSuccess: (Uri) -> Unit,
        onLocationPermissionGranted: () -> Unit // Callback para ubicación
    ) {
        this.onCapturePhotoSuccess = onCapturePhotoSuccess
        this.onSelectPhotoSuccess = onSelectPhotoSuccess
        this.onLocationPermissionGranted = onLocationPermissionGranted

        takePictureLauncher = activity.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let { onCapturePhotoSuccess?.invoke(it) }
            }
        }

        selectPictureLauncher = activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onSelectPhotoSuccess?.invoke(it) }
        }

        // Lanzador para solicitar el permiso de cámara
        requestCameraPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                capturePhoto() // Si el permiso es otorgado, lanza la captura de foto
            } else {
                // Mostrar algún mensaje al usuario indicando que el permiso es necesario
            }
        }

        // Lanzador para solicitar los permisos de ubicación
        requestLocationPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                // Si se ha otorgado cualquiera de los permisos, se ejecuta el callback
                onLocationPermissionGranted?.invoke()
            } else {
                // Mostrar un mensaje indicando que se necesita al menos un permiso de ubicación
            }
        }
    }

    // Iniciar captura de foto
    fun capturePhoto() {
        if (checkPermission(Manifest.permission.CAMERA)) {
            val photoFile = createImageFile(activity)
            val tempPhotoUri = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                photoFile
            )
            photoUri = tempPhotoUri
            takePictureLauncher.launch(tempPhotoUri)
        } else {
            requestCameraPermission()
        }
    }

    // Iniciar selección de foto desde la galería
    fun selectPhoto() {
        selectPictureLauncher.launch("image/*")
    }

    // Solicitar permiso de cámara
    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Solicitar permisos de ubicación
    fun requestLocationPermission() {
        requestLocationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Verificar si un permiso está otorgado
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    // Crear archivo de imagen temporal
    private fun createImageFile(context: Context): File {
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }
}
