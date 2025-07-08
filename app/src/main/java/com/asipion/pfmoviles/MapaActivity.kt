package com.asipion.pfmoviles

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    // Variables para almacenar los datos recibidos del Intent.
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var titulo: String = ""
    private var idIcono: Int = 0 // ID del recurso drawable para el ícono personalizado.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        // Se recuperan los datos pasados desde la actividad anterior.
        recuperarDatosDelIntent()

        // Se obtiene el fragmento del mapa del layout y se inicia la carga del mapa de forma asíncrona.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun recuperarDatosDelIntent() {
        latitud = intent.getDoubleExtra("EXTRA_LATITUD", 0.0)
        longitud = intent.getDoubleExtra("EXTRA_LONGITUD", 0.0)
        titulo = intent.getStringExtra("EXTRA_TITULO").orEmpty()
        idIcono = intent.getIntExtra("EXTRA_ID_ICONO", 0) // 0 significa sin ícono personalizado.
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Habilita los botones de zoom (+/-) en el mapa para una mejor navegación.
        googleMap.uiSettings.isZoomControlsEnabled = true

        // Crea un objeto LatLng con las coordenadas recibidas.
        val ubicacion = LatLng(latitud, longitud)

        // Configura las opciones del marcador (posición y título).
        val marcadorOptions = MarkerOptions()
            .position(ubicacion)
            .title(titulo)

        // Si se proporcionó un ID de ícono válido, se crea y asigna el ícono personalizado.
        if (idIcono != 0) {
            val iconoPersonalizado = crearBitmapDescriptor(this, idIcono, 120, 120)
            marcadorOptions.icon(iconoPersonalizado)
        }
        // Si idIcono es 0, no se llama a .icon() y Google Maps usará el marcador rojo por defecto.

        // Añade el marcador ya configurado al mapa.
        googleMap.addMarker(marcadorOptions)

        // Anima la cámara para centrarse en la ubicación del marcador con un nivel de zoom de 16.
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 16f))
    }

    private fun crearBitmapDescriptor(context: Context, resourceId: Int, width: Int, height: Int): BitmapDescriptor? {
        // Obtiene el drawable desde los recursos del proyecto.
        return ContextCompat.getDrawable(context, resourceId)?.run {
            // Define el tamaño del ícono.
            setBounds(0, 0, width, height)
            // Crea un bitmap (un lienzo) con las dimensiones especificadas.
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            // Dibuja el drawable en el lienzo.
            draw(Canvas(bitmap))
            // Convierte el bitmap al formato que el mapa necesita y lo devuelve.
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}