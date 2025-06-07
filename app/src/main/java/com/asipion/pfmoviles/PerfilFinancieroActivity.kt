package com.asipion.pfmoviles


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asipion.pfmoviles.databinding.ActividadPerfilFinancieroBinding
import android.view.MenuItem

class PerfilFinancieroActivity : AppCompatActivity() {

    private lateinit var binding: ActividadPerfilFinancieroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadPerfilFinancieroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        // Habilita el icono para que sea clickable
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // El título se establece en el XML, pero puedes cambiarlo aquí
        supportActionBar?.title = "Perfil Financiero"
    }

    // ESTE ES EL MÉTODO CORRECTO PARA MANEJAR CLICS EN ICONOS DE LA TOOLBAR
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // El icono de navegación (menú o flecha) tiene un ID especial: android.R.id.home
        if (item.itemId == android.R.id.home) {
            // Acción para el icono del menú. Por ejemplo, mostrar un mensaje.
            // Aquí iría la lógica para abrir un NavigationDrawer.
            Toast.makeText(this, "Botón de menú presionado", Toast.LENGTH_SHORT).show()
            return true // Indica que el evento ha sido manejado
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupListeners() {
        binding.buttonSiguiente.setOnClickListener {
            Toast.makeText(this, "Navegando a la siguiente sección...", Toast.LENGTH_SHORT).show()
        }
    }
}