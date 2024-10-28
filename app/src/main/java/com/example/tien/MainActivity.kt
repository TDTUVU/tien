package com.example.tien

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import java.util.Locale
import com.example.tien.R

class MainActivity : AppCompatActivity() {
    private lateinit var edtAmount1: EditText
    private lateinit var edtAmount2: EditText
    private lateinit var spinnerCurrency1: Spinner
    private lateinit var spinnerCurrency2: Spinner

    private val exchangeRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.91,
        "JPY" to 151.68,
        "VND" to 24585.0,
        "CNY" to 7.23
    )

    // Cờ để ngăn vòng lặp cập nhật
    private var isUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)

        // Khởi tạo views
        edtAmount1 = findViewById(R.id.edtAmount1)
        edtAmount2 = findViewById(R.id.edtAmount2)
        spinnerCurrency1 = findViewById(R.id.spinnerCurrency1)
        spinnerCurrency2 = findViewById(R.id.spinnerCurrency2)

        // Thiết lập adapter cho Spinner
        val currencies = arrayOf("USD", "EUR", "JPY", "VND", "CNY")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            currencies
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency1.adapter = adapter
        spinnerCurrency2.adapter = adapter

        // Thiết lập listener cho EditText và Spinner
        setupListeners()
    }

    private fun setupListeners() {
        // TextWatcher để theo dõi thay đổi văn bản
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating || s?.isEmpty() == true) return

                isUpdating = true
                try {
                    if (edtAmount1.hasFocus()) {
                        val amount = edtAmount1.text.toString().toDoubleOrNull() ?: 0.0
                        convertCurrency(amount, true)
                    } else if (edtAmount2.hasFocus()) {
                        val amount = edtAmount2.text.toString().toDoubleOrNull() ?: 0.0
                        convertCurrency(amount, false)
                    }
                } finally {
                    isUpdating = false
                }
            }
        }

        edtAmount1.addTextChangedListener(textWatcher)
        edtAmount2.addTextChangedListener(textWatcher)

        // Listener cho Spinner
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                // Gọi chuyển đổi nếu có giá trị nhập
                if (edtAmount1.text.isNotEmpty()) {
                    val amount = edtAmount1.text.toString().toDoubleOrNull() ?: 0.0
                    convertCurrency(amount, true)
                } else if (edtAmount2.text.isNotEmpty()) {
                    val amount = edtAmount2.text.toString().toDoubleOrNull() ?: 0.0
                    convertCurrency(amount, false)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerCurrency1.onItemSelectedListener = spinnerListener
        spinnerCurrency2.onItemSelectedListener = spinnerListener
    }

    private fun convertCurrency(amount: Double, isFromAmount1: Boolean) {
        val currency1 = spinnerCurrency1.selectedItem.toString()
        val currency2 = spinnerCurrency2.selectedItem.toString()

        val amountInUSD = if (isFromAmount1) {
            amount / exchangeRates[currency1]!!
        } else {
            amount / exchangeRates[currency2]!!
        }

        val result = if (isFromAmount1) {
            amountInUSD * exchangeRates[currency2]!!
        } else {
            amountInUSD * exchangeRates[currency1]!!
        }

        // Cập nhật EditText tương ứng
        if (isFromAmount1) {
            edtAmount2.setText(String.format(Locale.getDefault(), "%.2f", result))
        } else {
            edtAmount1.setText(String.format(Locale.getDefault(), "%.2f", result))
        }
    }
}
