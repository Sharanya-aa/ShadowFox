package com.example.shadowfox

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shadowfox.databinding.ActivityMainBinding
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    // Calculator Logic Variables
    private var currentInput = StringBuilder()
    private var operand1: Double? = null
    private var pendingOperation: String? = null
    private var justCalculated = false

    private val decimalFormat = DecimalFormat("#.##########")
    private val tempDecimalFormat = DecimalFormat("#.#") // For temperature display

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show back button

        initializeCalculator()
        initializeTempConverter()
    }

    private fun initializeCalculator() {
        val calculatorButtons = listOf(
            binding.button0, binding.button1, binding.button2, binding.button3, binding.button4,
            binding.button5, binding.button6, binding.button7, binding.button8, binding.button9,
            binding.buttonAdd, binding.buttonSubtract, binding.buttonMultiply, binding.buttonDivide,
            binding.buttonDecimal, binding.buttonClear, binding.buttonEquals, binding.buttonBackspace
        )
        calculatorButtons.forEach { it.setOnClickListener(this) }
        updateDisplay()
    }

    private fun initializeTempConverter() {
        binding.buttonConvertTemp.setOnClickListener {
            convertTemperature()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // Calculator buttons
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9 -> {
                val digit = (v as Button).text.toString()
                appendDigit(digit)
            }
            R.id.buttonDecimal -> appendDecimal()
            R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply, R.id.buttonDivide -> {
                val operator = (v as Button).text.toString()
                chooseOperation(operator)
            }
            R.id.buttonEquals -> calculateResult()
            R.id.buttonClear -> clearCalculator()
            R.id.buttonBackspace -> backspace()
        }
    }

    private fun appendDigit(digit: String) {
        if (justCalculated) {
            clearCalculator()
        }
        if (currentInput.toString() == "0" && digit != ".") {
            currentInput.clear()
        }
        currentInput.append(digit)
        updateDisplay()
    }

    private fun appendDecimal() {
        if (justCalculated) {
            clearCalculator()
            currentInput.append("0.") // Start new number with 0.
            justCalculated = false
        } else if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) {
                currentInput.append("0")
            }
            currentInput.append(".")
        }
        updateDisplay()
    }

    private fun chooseOperation(operator: String) {
        if (justCalculated && currentInput.toString() == decimalFormat.format(operand1)) {
            currentInput.clear()
            pendingOperation = operator
            justCalculated = false
            return
        }
        if (currentInput.isEmpty()) {
            if (operand1 != null) {
                pendingOperation = operator
            }
            return
        }
        if (operand1 != null) {
            if (currentInput.isNotEmpty()) {
                calculateResult()
                operand1 = binding.textViewCalculatorDisplay.text.toString().toDoubleOrNull()
            }
        } else {
            operand1 = currentInput.toString().toDoubleOrNull()
        }
        pendingOperation = operator
        currentInput.clear()
        justCalculated = false
    }

    private fun calculateResult() {
        if (operand1 == null || pendingOperation == null || currentInput.isEmpty()) {
            if (operand1 != null && pendingOperation != null && currentInput.isEmpty()) {
                currentInput.append(decimalFormat.format(operand1))
            } else {
                return
            }
        }
        val operand2 = currentInput.toString().toDoubleOrNull()
        if (operand2 == null) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            return
        }
        var result = 0.0
        try {
            when (pendingOperation) {
                "+" -> result = operand1!! + operand2
                "-" -> result = operand1!! - operand2
                "*" -> result = operand1!! * operand2
                "/" -> {
                    if (operand2 == 0.0) {
                        Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show()
                        clearCalculator()
                        return
                    }
                    result = operand1!! / operand2
                }
            }
            currentInput.clear()
            currentInput.append(decimalFormat.format(result))
            operand1 = result
            pendingOperation = null
            justCalculated = true
            updateDisplay()
        } catch (e: Exception) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            clearCalculator()
        }
    }

    private fun clearCalculator() {
        currentInput.clear()
        operand1 = null
        pendingOperation = null
        justCalculated = false
        currentInput.append("0")
        updateDisplay()
    }

    private fun backspace() {
        if (justCalculated) {
            clearCalculator()
            return
        }
        if (currentInput.isNotEmpty()) {
            currentInput.deleteCharAt(currentInput.length - 1)
            if (currentInput.isEmpty()) {
                currentInput.append("0")
            }
        } else if (operand1 != null && pendingOperation != null) {
            pendingOperation = null
        }
        updateDisplay()
    }

    private fun updateDisplay() {
        binding.textViewCalculatorDisplay.text = if (currentInput.isEmpty()) "0" else currentInput.toString()
    }

    private fun convertTemperature() {
        val tempValueString = binding.editTextTempValue.text.toString()
        if (tempValueString.isEmpty()) {
            Toast.makeText(this, "Please enter a temperature value", Toast.LENGTH_SHORT).show()
            return
        }
        val tempValue = tempValueString.toDoubleOrNull()
        if (tempValue == null) {
            Toast.makeText(this, "Invalid temperature value", Toast.LENGTH_SHORT).show()
            return
        }

        val fromCelsius = binding.radioConvertFromCelsius.isChecked
        val toCelsius = binding.radioConvertToCelsius.isChecked

        var result: Double
        var resultUnit: String

        if (fromCelsius && !toCelsius) { // C to F
            result = (tempValue * 9/5) + 32
            resultUnit = "°F"
        } else if (!fromCelsius && toCelsius) { // F to C
            result = (tempValue - 32) * 5/9
            resultUnit = "°C"
        } else { // Same unit conversion or invalid selection
            Toast.makeText(this, "Select different units to convert", Toast.LENGTH_SHORT).show()
            binding.textViewConvertedTempResult.text = "Result: -"
            return
        }
        binding.textViewConvertedTempResult.text = "Result: ${tempDecimalFormat.format(result)} $resultUnit"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // Go back to HomeActivity
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}