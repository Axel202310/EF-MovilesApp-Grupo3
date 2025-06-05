package com.asipion.pfmoviles

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class TransferenciaActividad : AppCompatActivity() {

    private lateinit var backArrow: ImageView
    private lateinit var fromAccountLayout: LinearLayout
    private lateinit var toAccountLayout: LinearLayout
    private lateinit var fromAccountValue: TextView
    private lateinit var toAccountValue: TextView
    private lateinit var etMonto: EditText
    private lateinit var tvMoneda: TextView
    private lateinit var ivCalculator: ImageView
    private lateinit var tvDateValue: TextView
    private lateinit var etComentario: EditText
    private lateinit var tvCommentCounter: TextView
    private lateinit var btnAnadir: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_transferencia)

        inicializarVista()
        configurarEventos()
    }

    private fun inicializarVista() {
        backArrow = findViewById(R.id.iv_back)
        fromAccountLayout = findViewById(R.id.layout_from_account)
        toAccountLayout = findViewById(R.id.layout_to_account)
        fromAccountValue = findViewById(R.id.tv_from_account_value)
        toAccountValue = findViewById(R.id.tv_to_account_value)
        etMonto = findViewById(R.id.et_monto)
        tvMoneda = findViewById(R.id.tv_moneda)
        ivCalculator = findViewById(R.id.iv_calculator)
        tvDateValue = findViewById(R.id.tv_date_value)
        etComentario = findViewById(R.id.et_comentario)
        tvCommentCounter = findViewById(R.id.tv_comment_counter)
        btnAnadir = findViewById(R.id.btn_anadir)
    }

    private fun configurarEventos() {
        backArrow.setOnClickListener {
            finish()
        }

        etComentario.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                tvCommentCounter.text = "$length/4096"
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        fromAccountLayout.setOnClickListener {
            Toast.makeText(this, "Seleccionar cuenta origen", Toast.LENGTH_SHORT).show()
        }

        toAccountLayout.setOnClickListener {
            Toast.makeText(this, "Seleccionar cuenta destino", Toast.LENGTH_SHORT).show()
        }

        ivCalculator.setOnClickListener {
            Toast.makeText(this, "Elegir fecha", Toast.LENGTH_SHORT).show()
        }

        btnAnadir.setOnClickListener {
            Toast.makeText(this, "Transferencia registrada (demo)", Toast.LENGTH_SHORT).show()
        }
    }
}
