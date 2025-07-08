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
import com.google.android.gms.maps.model.*

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    private var puntos: ArrayList<PuntoInteres> = arrayListOf()
    private var tituloGeneral: String = ""
    private var idIcono: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        recuperarDatosDelIntent()

        supportActionBar?.title = tituloGeneral

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun recuperarDatosDelIntent() {
        puntos = intent.getSerializableExtra("EXTRA_PUNTOS") as? ArrayList<PuntoInteres> ?: arrayListOf()
        tituloGeneral = intent.getStringExtra("EXTRA_TITULO_GENERAL").orEmpty()
        idIcono = intent.getIntExtra("EXTRA_ID_ICONO", 0)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.uiSettings.isZoomControlsEnabled = true

        if (puntos.isEmpty()) return

        val boundsBuilder = LatLngBounds.builder()

        val iconoPersonalizado = if (idIcono != 0) {
            crearBitmapDescriptor(this, idIcono, 120, 120)
        } else {
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        }

        for (punto in puntos) {
            val ubicacion = LatLng(punto.latitud, punto.longitud)
            val marcadorOptions = MarkerOptions()
                .position(ubicacion)
                .title(punto.titulo)
                .icon(iconoPersonalizado)

            googleMap.addMarker(marcadorOptions)
            boundsBuilder.include(ubicacion)
        }

        val bounds = boundsBuilder.build()
        val padding = 150
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        googleMap.animateCamera(cameraUpdate)
    }

    private fun crearBitmapDescriptor(context: Context, resourceId: Int, width: Int, height: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, resourceId)?.run {
            setBounds(0, 0, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}