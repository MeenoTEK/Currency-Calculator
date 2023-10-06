package com.meenotek.myapplication

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.meenotek.myapplication.databinding.ActivityMainBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Define conversion rates
    private val ngnToUsd = 0.0013
    private val ngnToPound = 0.0011
    private val ngnToEuro = 0.0012
    private val ngnToBitcoin = 0.00000045

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.convertButton.setOnClickListener { convertCurrency() }

        // Set a key listener for the EditText
        binding.amountToConvertText.setOnKeyListener { _, keyCode, event ->
            handleKeyEvent(event, keyCode)
        }

        // Set the initial value of conversionResult to 0.00 naira
        val initialAmount = NumberFormat.getCurrencyInstance().format(0.00)
        binding.conversionResult.text = getString(R.string.amount, initialAmount)
    }

    private fun convertCurrency() {
        // To check for null input
        val userInput = binding.amountToConvertText.text.toString().toDoubleOrNull()
        if (userInput == null) {
            binding.conversionResult.text = ""
            return
        }

        // To make conversion
        val conversion = when (binding.conversionOptions.checkedRadioButtonId) {
            R.id.dollar -> ngnToUsd
            R.id.pounds -> ngnToPound
            R.id.euros -> ngnToEuro
            else -> ngnToBitcoin
        }

        // To calculate conversion
        var calculateConversion = conversion * userInput

        // To do the roundup
        val roundUp = binding.roundUpSwitch.isChecked
        // We're rounding up, not down
        if (roundUp) {
            calculateConversion = ceil(calculateConversion)
        }

        val formattedResult = when (binding.conversionOptions.checkedRadioButtonId) {
            R.id.dollar, R.id.pounds, R.id.euros -> formatCurrency(calculateConversion)
            R.id.bitcoin -> formatBitcoin(calculateConversion)
            else -> ""
        }

        // Set the formatted result text
        binding.conversionResult.text = getString(R.string.amount, formattedResult)
    }

    private fun formatCurrency(amount: Double): String {
        val formatter = DecimalFormat("#,##0.00") // Adjust the pattern for your needs
        return formatter.format(amount)
    }

    private fun formatBitcoin(amount: Double): String {
        val formatter = DecimalFormat("#,##0.00000000") // Adjust the pattern for Bitcoin
        return formatter.format(amount)
    }

    private fun handleKeyEvent(event: KeyEvent, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            return true
        }
        return false
    }
}
