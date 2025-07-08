package com.asipion.pfmoviles

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActivityPuntosInteresBinding
import java.io.Serializable

data class PuntoInteres(
    val latitud: Double,
    val longitud: Double,
    val titulo: String
) : Serializable

class PuntosInteresActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPuntosInteresBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuntosInteresBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configurarListenersDeBotones()
    }

    private fun configurarListenersDeBotones() {
        binding.btnVerAtmsBcp.setOnClickListener {
            val puntosBcp = arrayListOf(
                PuntoInteres(-12.070993985364549, -77.06063210964244, "BCP Bolivar"),
                PuntoInteres(-12.058811634172764, -77.0588672161197, "BCP Av. Tingo María 1194"),
                PuntoInteres(-12.056821382165793, -77.04701175014709, "BCP Jr. Jorge Chávez 847")
            )
            abrirMapa(puntosBcp, "Cajeros BCP", R.drawable.logo_bcp)
        }

        binding.btnVerSucursalesInterbank.setOnClickListener {
            val puntosInterbank = arrayListOf(
                PuntoInteres(-12.066038, -77.048203, "ATM Interbank - Breña"),
                PuntoInteres(-12.055814, -77.050111, "Interbank - La Rambla Brasil"),
                PuntoInteres(-12.059430, -77.034540, "Interbank - Centro Cívico")
            )
            abrirMapa(puntosInterbank, "Cajeros Interbank", R.drawable.logo_interbank)
        }

        binding.btnVerAgentesBBVA.setOnClickListener {
            val puntosBBVA = arrayListOf(
                PuntoInteres(-12.058554577879416, -77.06362411379443, "Agente BBVA - Av. Bélisario Sosa Peláez 1100"),
                PuntoInteres(-12.064967288748079, -77.04956569035615, "Cajero BBVA - Jirón Huaraz 1600"),
                PuntoInteres(-12.054685079582251, -77.05194749195843, "Cajero BBVA- Av. Venezuela 1254")
            )
            abrirMapa(puntosBBVA, "Cajeros BBVA", R.drawable.logo_bbva)
        }
    }

    private fun abrirMapa(puntos: ArrayList<PuntoInteres>, tituloGeneral: String, idIcono: Int) {
        val intent = Intent(this, MapaActivity::class.java).apply {
            putExtra("EXTRA_PUNTOS", puntos)
            putExtra("EXTRA_TITULO_GENERAL", tituloGeneral)
            putExtra("EXTRA_ID_ICONO", idIcono)
        }
        startActivity(intent)
    }
}