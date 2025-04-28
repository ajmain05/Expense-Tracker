package com.example.expensetracker

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
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

class DetailedActivity : AppCompatActivity() {
    private lateinit var transaction : Transaction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detailed)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val amountInput = findViewById<EditText>(R.id.amountInput)
        amountInput.requestFocus()

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(amountInput, InputMethodManager.SHOW_IMPLICIT)

        val updateBtn = findViewById<Button>(R.id.updateBtn)
        val labelInput = findViewById<EditText>(R.id.labelInput)

        val labelLayout = findViewById<TextInputLayout>(R.id.labelLayout)
        val amountLayout = findViewById<TextInputLayout>(R.id.amountLayout)
        val descriptionInput = findViewById<EditText>(R.id.descriptionInput)
        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)
        transaction = intent.getSerializableExtra("transaction") as Transaction

        labelInput.setText(transaction.label)
        amountInput.setText(transaction.amount.toString())
        descriptionInput.setText(transaction.description)

        val rootView = findViewById<View>(R.id.rootView)
        rootView.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }


        labelInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if (it!!.count()>0)
                labelLayout.error = null
        }
        amountInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if (it!!.count()>0)
                amountLayout.error = null
        }
        descriptionInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
        }


        updateBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()

            if(label.isEmpty())
                labelLayout.error="Please enter a valid label"
            else if (amount== null)
                amountLayout.error="Please enter a valid number"
            else {
                val transaction  =Transaction(transaction.id, label, amount, description)
                update(transaction)
            }
        }
        closeBtn.setOnClickListener {
            finish()
        }


    }

    private fun update(transaction: Transaction){
        val db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }
    }
}
