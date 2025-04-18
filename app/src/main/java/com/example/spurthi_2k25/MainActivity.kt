package com.example.spurthi_2k25

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.privacysandbox.tools.core.model.Method
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.androidgamesdk.gametextinput.Listener
import okhttp3.*
import java.io.IOException
import kotlin.text.Typography.section
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnSubmit: Button
    private lateinit var etName: EditText
    private lateinit var etRegNo: EditText
    private lateinit var etEmail: EditText
    private lateinit var etYear: EditText
    private lateinit var etBranch: EditText
    private lateinit var etSection: EditText
    private lateinit var etMobile: EditText
    private lateinit var tvQRResult: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var cbCategory1: CheckBox
    private lateinit var cbCategory2: CheckBox
    private lateinit var cbCategory3: CheckBox
    private lateinit var cbCategory4: CheckBox
    private lateinit var talkathon: CheckBox
    private lateinit var aidojo: CheckBox
    private lateinit var codewars: CheckBox
    private lateinit var ideapresentation: CheckBox
    private lateinit var webverse: CheckBox
    private lateinit var acegot: CheckBox
    private lateinit var esports: CheckBox
    private lateinit var astroarcade: CheckBox
    private lateinit var boxoffice: CheckBox
    private lateinit var cricketauction: CheckBox
    private lateinit var vr: CheckBox
    private lateinit var offline: CheckBox
    private lateinit var online: CheckBox
    private lateinit var btnQR: Button
    private lateinit var payMode: TextView

    private val CAMERA_REQUEST_CODE = 100
    private lateinit var barcodeScanner: BarcodeScanner

    // prices map

    private val comboPrices = mapOf(
        R.id.cbCategory1 to 100,
        R.id.cbCategory2 to 150,
        R.id.cbCategory3 to 150,
        R.id.cbCategory4 to 250
    )

    private val techEvents = setOf(
        R.id.Ideapresentation, R.id.Aidojo, R.id.webverse, R.id.Talkathon, R.id.Codewars
    )

    private val nonTechEvents = setOf(
        R.id.Astroarcade, R.id.Boxoffice, R.id.Esports, R.id.Acegot, R.id.Cricketauction
    )

    private val specialEvent = R.id.Vr

    private val eventPrices = mapOf(
        R.id.Talkathon to 30,
        R.id.Aidojo to 30,
        R.id.Codewars to 30,
        R.id.Ideapresentation to 30,
        R.id.webverse to 30,
        R.id.Acegot to 0,
        R.id.Esports to 50,
        R.id.Astroarcade to 50,
        R.id.Boxoffice to 50,
        R.id.Cricketauction to 50,
        R.id.Vr to 80  // VR should be counted separately
    )

    //    private var totalAmount = 0
    private var selectedTechCount = 0
    private var selectedNonTechCount = 0
    //    private var selectedCombo = 0
    private val selectedEventIds = mutableSetOf<Int>() // Track selected individual events


    private val WEB_APP_URL = "https://script.google.com/macros/s/AKfycbztbokuPCceW8ZUcpl0xCTPt2EseQzS7usoKDXNaXiM3Ff5jv2U8NG79LP5aXHkcdcpHA/exec"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        btnSubmit = findViewById(R.id.btnSubmit)
        etName = findViewById(R.id.etName)
        etRegNo = findViewById(R.id.etRegNo)
        etEmail = findViewById(R.id.etEmail)
        etYear = findViewById(R.id.etYear)
        etBranch = findViewById(R.id.etBranch)
        etSection = findViewById(R.id.etSection)
        etMobile = findViewById(R.id.numberSection)
        tvQRResult = findViewById(R.id.tvQRResult)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        cbCategory1 = findViewById(R.id.cbCategory1)
        cbCategory2 = findViewById(R.id.cbCategory2)
        cbCategory3 = findViewById(R.id.cbCategory3)
        cbCategory4 = findViewById(R.id.cbCategory4)
        talkathon = findViewById(R.id.Talkathon)
        aidojo = findViewById(R.id.Aidojo)
        codewars = findViewById(R.id.Codewars)
        ideapresentation = findViewById(R.id.Ideapresentation)
        webverse = findViewById(R.id.webverse)
        acegot = findViewById(R.id.Acegot)
        esports = findViewById(R.id.Esports)
        astroarcade = findViewById(R.id.Astroarcade)
        boxoffice = findViewById(R.id.Boxoffice)
        cricketauction = findViewById(R.id.Cricketauction)
        vr = findViewById(R.id.Vr)
        payMode = findViewById(R.id.payment_mode)
        offline = findViewById(R.id.Offline)
        online = findViewById(R.id.Online)

        btnQR = findViewById(R.id.btnScanQR)

        barcodeScanner = BarcodeScanning.getClient()

        btnQR.setOnClickListener {
            requestCameraPermission()

        }

        btnSubmit.setOnClickListener {
            sendDataToGoogleSheet()
        }

        setCheckBoxListeners()

    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            openCamera() // Permission already granted, open the camera
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private var selectedCombo: Int? = null
    private val selectedTechEvents = mutableSetOf<Int>()
    private val selectedNonTechEvents = mutableSetOf<Int>()
    private var totalAmount = 0

    private fun setCheckBoxListeners() {
        val checkBoxes = listOf(
            cbCategory1, cbCategory2, cbCategory3, cbCategory4,
            talkathon, aidojo, codewars, ideapresentation,
            webverse, acegot, esports, astroarcade,
            boxoffice, cricketauction, vr, offline, online
        )

        for (checkBox in checkBoxes) {
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                handleSelection(buttonView.id, isChecked)
            }
        }
    }

    private fun handleSelection(eventId: Int, isChecked: Boolean) {
        if (comboPrices.containsKey(eventId)) {
            if (isChecked) {
                applyCombo(eventId) // Select combo
            } else {
                removeCombo() // Unselect combo
            }
        } else {
            if (!isValidEventSelection(eventId, isChecked)) {
                (findViewById<CheckBox>(eventId)).isChecked = false
                return
            }

            if (isChecked) {
                if (techEvents.contains(eventId)) selectedTechEvents.add(eventId)
                if (nonTechEvents.contains(eventId)) selectedNonTechEvents.add(eventId)
            } else {
                selectedTechEvents.remove(eventId)
                selectedNonTechEvents.remove(eventId)
            }
        }
        if (eventId == R.id.Online || eventId == R.id.Offline) {
            if (isChecked) {
                if (eventId == R.id.Online) offline.isChecked = false
                if (eventId == R.id.Offline) online.isChecked = false
            }
        }
        updatePaymentMode()
        updateTotalPrice()

    }


    private fun applyCombo(comboId: Int) {
        if (selectedCombo == comboId) return // Ignore if already selected

        // Uncheck previous combo
        selectedCombo?.let { (findViewById<CheckBox>(it)).isChecked = false }

        selectedCombo = comboId
        selectedTechEvents.clear()
        selectedNonTechEvents.clear()
        totalAmount = if (comboId == R.id.cbCategory4) 250 else comboPrices[comboId] ?: 0 // Set ₹250 for Combo 4

        when (comboId) {
            R.id.cbCategory1 -> { /* 2 Tech + 2 Non-Tech */ }
            R.id.cbCategory2 -> { /* 3 Tech + 3 Non-Tech */ }
            R.id.cbCategory3 -> (findViewById<CheckBox>(R.id.Vr)).isChecked = true // VR included
            R.id.cbCategory4 -> { // 5 Tech + 5 Non-Tech (₹250 Only)
                selectedTechEvents.addAll(techEvents)
                selectedNonTechEvents.addAll(nonTechEvents)

                for (event in techEvents + nonTechEvents) {
                    val checkBox = findViewById<CheckBox>(event)
                    checkBox.setOnCheckedChangeListener(null) // Temporarily remove listener
                    checkBox.isChecked = true
                    checkBox.setOnCheckedChangeListener { buttonView, isChecked -> handleSelection(buttonView.id, isChecked) }
                }
            }
        }

        tvTotalAmount.text = "Total Amount: ₹$totalAmount"
    }



    private fun removeCombo() {
        selectedCombo = null
        selectedTechEvents.clear()
        selectedNonTechEvents.clear()
        totalAmount = 0

        for (event in eventPrices.keys) {
            (findViewById<CheckBox>(event)).isChecked = false
        }

        tvTotalAmount.text = "Total Amount: ₹$totalAmount"
    }

    private fun isValidEventSelection(eventId: Int, isChecked: Boolean): Boolean {
        if (selectedCombo == null) return true // Allow individual selection

        when (selectedCombo) {
            R.id.cbCategory1 -> { // 2 Tech + 2 Non-Tech
                if (isChecked && techEvents.contains(eventId) && selectedTechEvents.size >= 2) return false
                if (isChecked && nonTechEvents.contains(eventId) && selectedNonTechEvents.size >= 2) return false
            }
            R.id.cbCategory2 -> { // 3 Tech + 3 Non-Tech
                if (isChecked && techEvents.contains(eventId) && selectedTechEvents.size >= 3) return false
                if (isChecked && nonTechEvents.contains(eventId) && selectedNonTechEvents.size >= 3) return false
            }
            R.id.cbCategory3 -> { // Any 3 Events + VR (Prices should NOT be added)
                if (isChecked && selectedTechEvents.size + selectedNonTechEvents.size >= 3) return false
            }
            R.id.cbCategory4 -> { // All 5 Tech + 5 Non-Tech (No VR allowed)
//                if (isChecked && eventId == R.id.Vr) return false
            }
        }
        return true
    }

    private fun updateTotalPrice() {
        totalAmount = 0

        if (selectedCombo != null) {
            when (selectedCombo) {
                R.id.cbCategory1 -> {
                    totalAmount = comboPrices[R.id.cbCategory1] ?: 0
                    if (findViewById<CheckBox>(R.id.Vr).isChecked) {
                        totalAmount += eventPrices[R.id.Vr] ?: 80 // Add VR price for Combo 1
                    }
                }
                R.id.cbCategory2 -> {
                    totalAmount = comboPrices[R.id.cbCategory2] ?: 0
                    if (findViewById<CheckBox>(R.id.Vr).isChecked) {
                        totalAmount += eventPrices[R.id.Vr] ?: 80 // Add VR price for Combo 2
                    }
                }
                R.id.cbCategory3 -> {
                    totalAmount = comboPrices[R.id.cbCategory3] ?: 0
                    // VR is included in Combo 3, so do not add extra price
                }
                R.id.cbCategory4 -> {
                    totalAmount = comboPrices[R.id.cbCategory4] ?: 0
                    if (findViewById<CheckBox>(R.id.Vr).isChecked) {
                        totalAmount += eventPrices[R.id.Vr] ?: 80 // Add VR price for Combo 1
                    }
                }
            }
        } else {
            // Individual event selection
            for (eventId in selectedTechEvents + selectedNonTechEvents) {
                totalAmount += eventPrices[eventId] ?: 0
            }

            if (findViewById<CheckBox>(R.id.Vr).isChecked) {
                totalAmount += eventPrices[R.id.Vr] ?: 0 // Add VR price for individual selection
            }
        }


        tvTotalAmount.text = "Total Amount: ₹$totalAmount"
    }
    private fun updatePaymentMode() {
        val mode = when {
            online.isChecked -> "Online"
            offline.isChecked -> "Offline"
            else -> "Not Selected"
        }
        payMode.text = mode
    }




    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val photo = data?.extras?.get("data") as? android.graphics.Bitmap
            if (photo != null) {
                scanBarcode(photo)
            }
        }
    }

    private fun scanBarcode(bitmap: android.graphics.Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val barcode = barcodes[0]
                    tvQRResult.text = barcode.displayValue ?: "No barcode detected"
                } else {
                    Toast.makeText(this, "No barcode found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to scan barcode", Toast.LENGTH_SHORT).show()
            }
    }




    private fun sendDataToGoogleSheet() {



        val url = WEB_APP_URL

        val requestQueue = Volley.newRequestQueue(this)

        val postRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(applicationContext, "Response: $response", Toast.LENGTH_SHORT).show()
                clearForm()
            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["action"] = "addItem"
                params["name"] = etName.text.toString()
                params["regdno"] = etRegNo.text.toString()
                params["email"] = etEmail.text.toString()
                params["year"] = etYear.text.toString()
                params["branch"] = etBranch.text.toString()
                params["section"] = etSection.text.toString()
                params["mobile"] = etMobile.text.toString()
                params["barcodeno"] = tvQRResult.text.toString()
                params["combo1"] = if (cbCategory1.isChecked) "Yes" else "No"
                params["combo2"] = if (cbCategory2.isChecked) "Yes" else "No"
                params["combo3"] = if (cbCategory3.isChecked) "Yes" else "No"
                params["combo4"] = if (cbCategory4.isChecked) "Yes" else "No"
                params["talk"] = if (talkathon.isChecked) "Yes" else "No"
                params["ai"] = if (aidojo.isChecked) "Yes" else "No"
                params["code"] = if (codewars.isChecked) "Yes" else "No"
                params["idea"] = if (ideapresentation.isChecked) "Yes" else "No"
                params["web"] = if (webverse.isChecked) "Yes" else "No"
                params["ace"] = if (acegot.isChecked) "Yes" else "No"
                params["esports"] = if (esports.isChecked) "Yes" else "No"
                params["arcade"] = if (astroarcade.isChecked) "Yes" else "No"
                params["box"] = if (boxoffice.isChecked) "Yes" else "No"
                params["cricket"] = if (cricketauction.isChecked) "Yes" else "No"
                params["vr"] = if (vr.isChecked) "Yes" else "No"
                params["tamount"] = tvTotalAmount.text.toString()
                params["paymode"] = payMode.text.toString()

                return params
            }
        }

        requestQueue.add(postRequest)
    }

    private fun clearForm() {
        etName.text.clear()
        etRegNo.text.clear()
        etEmail.text.clear()
        etYear.text.clear()
        etBranch.text.clear()
        etSection.text.clear()
        etMobile.text.clear()
        tvQRResult.text = ""

        // Uncheck all checkboxes
        cbCategory1.isChecked = false
        cbCategory2.isChecked = false
        cbCategory3.isChecked = false
        cbCategory4.isChecked = false
        talkathon.isChecked = false
        aidojo.isChecked = false
        codewars.isChecked = false
        ideapresentation.isChecked = false
        webverse.isChecked = false
        acegot.isChecked = false
        esports.isChecked = false
        astroarcade.isChecked = false
        boxoffice.isChecked = false
        cricketauction.isChecked = false
        vr.isChecked = false
        payMode.text = ""
        offline.isChecked = false
        online.isChecked = false
    }


}