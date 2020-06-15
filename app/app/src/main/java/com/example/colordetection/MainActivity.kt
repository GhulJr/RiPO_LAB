package com.example.colordetection

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream

// Aktywność startowa, która pozwala na wybranie zdjęcia z galerii na telefonie.
class MainActivity : AppCompatActivity() {

    // Stałe pomocnicze.
    companion object {
        private val REQUEST_PERMISSION = 300
        private val INTENT_CAMERA = 1
        val INTENT_IMAGE_URI = "INTENT_IMAGE_URI"
    }

    // Metoda tworząca aktywność.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askForPermissions()
        btnTakePicture.setOnClickListener { takePicture() }             // Ustawienie przyciskowi funkcjonalności wyboru zdjęcia.
    }

    // Metoda wywoływana po wybraniu zdjęcia z galerii zdjęć.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == INTENT_CAMERA) {        // Sprawdzienie czy zdjęcie zostało wybrane poprawnie.
            val intent = Intent(this, DetectActivity::class.java)       // Tworzenie obiektu przesyłającego ścieżkę do zdjęcia (sama klasa jest właśnie nośnikiem danych).
            intent.putExtra(INTENT_IMAGE_URI, data?.dataString)                       // Dodanie ścieżki do zdjęcia.
            startActivity(intent)                                                     // Rozpoczęcie aktywności detektującej kolory.
          }
    }

    /** Utils. */

    // Uruchamia systemowe wybieranie zdjęć.
    private fun takePicture() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply { type = "image/*" }
        startActivityForResult(intent, INTENT_CAMERA)
    }

    // Sprawdza, czy posiada pozwolenia zadane w argumencie.
    private fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    // Pyta o pozwolenia dotyczące wybierania zdjęcia z plików.
    private fun askForPermissions() {
        val permissions = Array<String>(3) {
            Manifest.permission.READ_EXTERNAL_STORAGE
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            Manifest.permission.CAMERA}
        if(!hasPermissions(this, *permissions)) {                                     // Sprawdza czy ma pozwolenie.
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION)  // Jeżeli nie to prosi o nie.
        }
    }
}
