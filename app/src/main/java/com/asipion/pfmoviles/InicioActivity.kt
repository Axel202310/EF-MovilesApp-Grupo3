package com.asipion.pfmoviles

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.asipion.pfmoviles.databinding.ActivityInicioBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private val currentCalendar: Calendar = Calendar.getInstance()

    private val formatoFechaDiaMes = SimpleDateFormat("dd 'de' MMMM", Locale("es", "ES"))
    private val formatoNombreDia = SimpleDateFormat("EEE", Locale("es", "ES"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbarYMenu()
        configurarTabs()
        configurarNavegacionFechas()
        mostrarFechaActual()
        obtenerDatosUsuarioDesdeFirestore()

        binding.fabAdd.setOnClickListener {
            Toast.makeText(this, "Agregar transacción", Toast.LENGTH_SHORT).show()
        }

        binding.tabLayoutType.getTabAt(0)?.select()
        binding.tabLayoutPeriod.getTabAt(0)?.select()
    }

    private fun configurarToolbarYMenu() {
        setSupportActionBar(binding.topAppBar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, binding.topAppBar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
        }
    }

    private fun obtenerDatosUsuarioDesdeFirestore() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val correo = FirebaseAuth.getInstance().currentUser?.email ?: "Usuario desconocido"

        if (uid == null) {
            Log.e("Firestore", "Usuario no autenticado.")
            return
        }

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { documento ->
                if (documento.exists()) {
                    val saldo = documento.getDouble("saldo") ?: 0.0
                    val divisa = documento.getString("divisa") ?: "PEN"

                    val formato = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("es", "PE")).apply {
                        groupingSeparator = '.'
                        decimalSeparator = ','
                    })

                    val saldoFormateado = "${formato.format(saldo)} ${divisa.takeLast(3)}"

                    // Actualiza el saldo en la pantalla principal
                    binding.textViewTotalAmount.text = saldoFormateado

                    // Actualiza el saldo y correo en el header del menú lateral
                    val headerView: View = binding.navegacionLateral.getHeaderView(0)
                    val textoSaldo = headerView.findViewById<TextView>(R.id.textViewSaldoMenu)
                    val textoCorreo = headerView.findViewById<TextView>(R.id.textViewCorreoUsuario)

                    textoSaldo.text = "Balance: $saldoFormateado"
                    textoCorreo.text = correo
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al obtener datos del usuario", it)
            }
    }

    private fun configurarTabs() {
        binding.tabLayoutType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val texto = if (tab?.position == 0) "gastos" else "ingresos"
                binding.textViewDonutCenterText.text = "No hubo\n$texto ${obtenerTextoPeriodoActual()}"
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.tabLayoutPeriod.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentCalendar.time = Date()
                mostrarFechaActual()
                actualizarTextoDonut()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun configurarNavegacionFechas() {
        binding.buttonPreviousDate.setOnClickListener { cambiarFecha(-1) }
        binding.buttonNextDate.setOnClickListener { cambiarFecha(1) }
    }

    private fun cambiarFecha(cantidad: Int) {
        when (binding.tabLayoutPeriod.selectedTabPosition) {
            0 -> currentCalendar.add(Calendar.DAY_OF_YEAR, cantidad)
            1 -> currentCalendar.add(Calendar.WEEK_OF_YEAR, cantidad)
            2 -> currentCalendar.add(Calendar.MONTH, cantidad)
            3 -> currentCalendar.add(Calendar.YEAR, cantidad)
        }
        mostrarFechaActual()
        actualizarTextoDonut()
    }

    private fun mostrarFechaActual() {
        val hoy = Calendar.getInstance()
        val posicion = binding.tabLayoutPeriod.selectedTabPosition
        val diaNombre = formatoNombreDia.format(currentCalendar.time).replaceFirstChar { it.uppercase() }
        val fecha = formatoFechaDiaMes.format(currentCalendar.time)

        val textoFecha = when (posicion) {
            0 -> if (esMismoDia(currentCalendar, hoy)) "Hoy, ${fecha.substringAfter("de ")}"
            else "$diaNombre, $fecha"

            1 -> {
                val inicio = (currentCalendar.clone() as Calendar).apply {
                    set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                }
                val fin = (inicio.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, 6)
                }
                val formato = SimpleDateFormat("dd MMM", Locale("es", "ES"))
                "Semana: ${formato.format(inicio.time)} - ${formato.format(fin.time)}"
            }

            2 -> SimpleDateFormat("MMMM 'de' yyyy", Locale("es", "ES"))
                .format(currentCalendar.time).replaceFirstChar { it.uppercase() }

            3 -> SimpleDateFormat("yyyy", Locale("es", "ES")).format(currentCalendar.time)
            else -> ""
        }

        binding.textViewCurrentDate.text = textoFecha
    }

    private fun esMismoDia(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun obtenerTextoPeriodoActual(): String {
        return when (binding.tabLayoutPeriod.selectedTabPosition) {
            0 -> "hoy"
            1 -> "esta semana"
            2 -> "este mes"
            3 -> "este año"
            else -> ""
        }
    }

    private fun actualizarTextoDonut() {
        val tipo = if (binding.tabLayoutType.selectedTabPosition == 0) "gastos" else "ingresos"
        binding.textViewDonutCenterText.text = "No hubo\n$tipo ${obtenerTextoPeriodoActual()}"
    }
}
