package com.example.spurthi_2k25

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class IntroActivity : AppCompatActivity() {

    private lateinit var edtCode: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Moved this here
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro) // Ensure the layout is set before accessing views

        edtCode = findViewById(R.id.logincode)
        loginButton = findViewById(R.id.btnlogin)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loginButton.setOnClickListener {
            val code = edtCode.text.toString()
            if (code == "2025") {
                val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }
        }
    }
}
