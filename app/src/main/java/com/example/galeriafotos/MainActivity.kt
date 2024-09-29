package com.example.galeriafotos

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.galeriafotos.data.model.PhotoItem
import com.example.galeriafotos.data.repository.PhotoRepository
import com.example.galeriafotos.ui.screens.CaptureOrSelectPhotoScreen
import com.example.galeriafotos.ui.screens.MainScreen
import com.example.galeriafotos.ui.screens.PhotoPreviewScreen
import com.example.galeriafotos.ui.theme.GaleriaFotosTheme
import com.example.galeriafotos.utils.PermissionHandler

class MainActivity : ComponentActivity() {
    private val photoRepository = PhotoRepository()
    private lateinit var navController: NavHostController
    private lateinit var permissionHandler: PermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inicializar PermissionHandler
        permissionHandler = PermissionHandler(this)

        // Configurar los lanzadores con callbacks
        permissionHandler.initLaunchers(
            onCapturePhotoSuccess = { uri ->
                val encodedUri = Uri.encode(uri.toString())
                navController.navigate("photo_preview_screen/$encodedUri")
            },
            onSelectPhotoSuccess = { uri ->
                val encodedUri = Uri.encode(uri.toString())
                navController.navigate("photo_preview_screen/$encodedUri")
            },
            onLocationPermissionGranted = {
                val currentScreen = navController.currentBackStackEntry?.destination?.route
                if (currentScreen == "photo_preview_screen/{uri}") {
                    // Si ya estamos en la pantalla de previsualización, puedes obtener la ubicación
                    // o hacer alguna acción adicional aquí.
                }
            }
        )

        setContent {
            GaleriaFotosTheme {
                navController = rememberNavController()
                // Configurar NavHost con las rutas de las pantallas
                NavHost(
                    navController = navController,
                    startDestination = "main_screen"
                ) {
                    composable("main_screen") {
                        MainScreen(
                            photoRepository = photoRepository,
                            onCapturePhoto = {
                                permissionHandler.capturePhoto()
                            },
                            onSelectPhoto = {
                                permissionHandler.selectPhoto()
                            }
                        )
                    }
                    composable("capture_or_select_screen") {
                        CaptureOrSelectPhotoScreen(
                            onCapturePhoto = {
                                permissionHandler.capturePhoto()
                            },
                            onSelectPhoto = {
                                permissionHandler.selectPhoto()
                            },
                            onCancel = {
                                navController.popBackStack() // Vuelve a la pantalla anterior
                            }
                        )
                    }
                    composable("photo_preview_screen/{uri}") { backStackEntry ->
                        val uri = backStackEntry.arguments?.getString("uri") ?: ""
                        PhotoPreviewScreen(
                            photoUri = uri,
                            onSave = { description, location ->
                                val newPhoto = PhotoItem(
                                    id = System.currentTimeMillis().toString(),
                                    uri = uri,
                                    description = description,
                                    location = location
                                )
                                photoRepository.addPhoto(newPhoto)
                                navController.popBackStack() // Vuelve a la pantalla principal
                            },
                            onCancel = {
                                navController.popBackStack()
                            },
                            onRequestLocationPermission = {
                                // Solicitar permisos de ubicación desde MainActivity
                                permissionHandler.requestLocationPermission()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenSetup() {
    val navController = rememberNavController()

}
