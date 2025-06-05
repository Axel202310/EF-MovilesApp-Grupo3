package com.asipion.pfmoviles

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar


class AgregarTransaccionActividad(val listener: (day: Int, month: Int, year: Int) -> Unit) : DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener(dayOfMonth, month, year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val picker = DatePickerDialog(requireContext(), this, year, month, day)

        picker.setOnShowListener {
            val headerId = resources.getIdentifier("date_picker_header", "id", "android")
            val headerView = picker.findViewById<View>(headerId)
            headerView?.visibility = View.GONE
        }
        return picker
    }
}