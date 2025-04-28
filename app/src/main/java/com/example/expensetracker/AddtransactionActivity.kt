package com.example.expensetracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.view.inputmethod.InputMethodManager

class AddtransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_transaction)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val amountInput = findViewById<EditText>(R.id.amountInput)
        amountInput.requestFocus()

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(amountInput, InputMethodManager.SHOW_IMPLICIT)

        val addTransactionBtn = findViewById<Button>(R.id.addTransactionBtn)
        val labelInput = findViewById<EditText>(R.id.labelInput)

        val labelLayout = findViewById<TextInputLayout>(R.id.labelLayout)
        val amountLayout = findViewById<TextInputLayout>(R.id.amountLayout)
        val descriptionInput = findViewById<EditText>(R.id.descriptionInput)
        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)
        labelInput.addTextChangedListener {
            if (it!!.count()>0)
                labelLayout.error = null
        }
        amountInput.addTextChangedListener {
            if (it!!.count()>0)
                amountLayout.error = null
        }

        addTransactionBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()

            if(label.isEmpty())
                labelLayout.error="Please enter a valid label"
            else if (amount== null)
                amountLayout.error="Please enter a valid number"
            else {
                val transaction  =Transaction(0, label, amount, description)
                insert(transaction)
            }
        }
        closeBtn.setOnClickListener {
            finish()
        }


    }

    private fun insert(transaction: Transaction){
        val db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }

}