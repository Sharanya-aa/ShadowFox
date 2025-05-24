package com.example.shadowfox

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var sessionManager: SessionManager
    private lateinit var textViewWelcome: TextView
    private lateinit var buttonLogout: Button

    // Calculator UI Elements
    private lateinit var textViewCalculatorDisplay: TextView
    private lateinit var button0: Button
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button7: Button
    private lateinit var button8: Button
    private lateinit var button9: Button
    private lateinit var buttonAdd: Button
    private lateinit var buttonSubtract: Button
    private lateinit var buttonMultiply: Button
    private lateinit var buttonDivide: Button
    private lateinit var buttonDecimal: Button
    private lateinit var buttonClear: Button
    private lateinit var buttonEquals: Button
    private lateinit var buttonBackspace: Button

    // Calculator Logic Variables
    private var currentInput = StringBuilder()
    private var operand1: Double? = null
    private var pendingOperation: String? = null
    private var justCalculated = false // To handle behavior after pressing equals

    private val decimalFormat = DecimalFormat("#.##########") // To format output nicely

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)
        textViewWelcome = findViewById(R.id.textViewWelcome)
        buttonLogout = findViewById(R.id.buttonLogout)

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        // Get user details
        val userDetails = sessionManager.getUserDetails()
        val email = userDetails[SessionManager.KEY_EMAIL]
        textViewWelcome.text = "Welcome, $email!"

        buttonLogout.setOnClickListener {
            sessionManager.logoutUser()
            navigateToLogin()
        }

        initializeCalculator()
    }

    private fun initializeCalculator() {
        textViewCalculatorDisplay = findViewById(R.id.textViewCalculatorDisplay)

        button0 = findViewById(R.id.button0)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        button5 = findViewById(R.id.button5)
        button6 = findViewById(R.id.button6)
        button7 = findViewById(R.id.button7)
        button8 = findViewById(R.id.button8)
        button9 = findViewById(R.id.button9)

        buttonAdd = findViewById(R.id.buttonAdd)
        buttonSubtract = findViewById(R.id.buttonSubtract)
        buttonMultiply = findViewById(R.id.buttonMultiply)
        buttonDivide = findViewById(R.id.buttonDivide)

        buttonDecimal = findViewById(R.id.buttonDecimal)
        buttonClear = findViewById(R.id.buttonClear)
        buttonEquals = findViewById(R.id.buttonEquals)
        buttonBackspace = findViewById(R.id.buttonBackspace)

        val buttons = listOf(
            button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
            buttonAdd, buttonSubtract, buttonMultiply, buttonDivide,
            buttonDecimal, buttonClear, buttonEquals, buttonBackspace
        )

        buttons.forEach { it.setOnClickListener(this) }
        updateDisplay()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
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
        if (justCalculated) { // If a result was just shown and user types a digit
            clearCalculator() // Start fresh for the new calculation
            // justCalculated is reset in clearCalculator()
        }
        if (currentInput.toString() == "0" && digit != ".") {
            currentInput.clear() // Avoid leading zero unless it's for a decimal
        }
        currentInput.append(digit)
        updateDisplay()
    }

    private fun appendDecimal() {
        if (justCalculated) {
            currentInput.clear()
            currentInput.append("0.")
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
            // This means operand1 holds the result of the last calculation,
            // and currentInput was updated to show it. We are now choosing a new operator.
            // We want to use operand1 (the result) as the first operand for the new operation.
            currentInput.clear() // Clear display for next number
            pendingOperation = operator
            justCalculated = false
            return
        }

        if (currentInput.isEmpty()) {
            if (operand1 != null) { // Already have an operand1, user is changing operator
                pendingOperation = operator
            }
            // else if operand1 is null and currentInput is empty, do nothing
            return
        }

        // If there's an existing operand1 and new input, calculate the pending operation
        if (operand1 != null) {
            if (currentInput.isNotEmpty()) { // Ensure there's a second operand entered
                calculateResult()
                // After calculateResult, operand1 is updated with the result,
                // and currentInput is cleared (or shows result if justCalculated was set)
                // We need to ensure operand1 is correctly set from the display if a calculation just happened.
                operand1 = textViewCalculatorDisplay.text.toString().toDoubleOrNull()
            }
            // If currentInput is empty here, it means user did op1, operator, op2, operator (chaining)
            // or op1, operator, operator (changing operator)
        } else {
            // This is the first operand
            operand1 = currentInput.toString().toDoubleOrNull()
        }

        pendingOperation = operator
        currentInput.clear()
        justCalculated = false // Reset for next number input
    }

    private fun calculateResult() {
        if (operand1 == null || pendingOperation == null || currentInput.isEmpty()) {
            // Not enough info to calculate, or user pressed equals without second operand
            if (operand1 != null && pendingOperation != null && currentInput.isEmpty()) {
                // If user presses op1, operator, then equals, treat currentInput as op1
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
            operand1 = result // Store result for chained operations
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
        currentInput.append("0") // Reset display to 0
        updateDisplay()
    }

    private fun backspace() {
        if (justCalculated) {
            clearCalculator() // If backspacing after equals, clear all
            return
        }
        if (currentInput.isNotEmpty()) {
            currentInput.deleteCharAt(currentInput.length - 1)
            if (currentInput.isEmpty()) {
                currentInput.append("0") // If all cleared, show 0
            }
        } else if (operand1 != null && pendingOperation != null) {
            // If backspacing after an operator is chosen, allow changing the operator or operand1
            // For simplicity, let's just clear the pending op if no current input
            pendingOperation = null
            // Or, one could revert to operand1 in display:
            // currentInput.append(decimalFormat.format(operand1))
            // operand1 = null
        }
        updateDisplay()
    }


    private fun updateDisplay() {
        if (currentInput.isEmpty()) {
            textViewCalculatorDisplay.text = "0"
        } else {
            textViewCalculatorDisplay.text = currentInput.toString()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}