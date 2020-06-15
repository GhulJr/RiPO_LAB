package com.example.colordetection

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_detect.*
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.Exception


// Klasa odpowiedzialna za przetworzenie oraz wyświetlenie informacji ze zdjęcie.
class DetectActivity : AppCompatActivity() {

    private var bitmap: Bitmap? = null          // Przetrzymuje załadowane zdjęcie.
    private lateinit var palette: Palette       // Instancja klasy, wyłuskującej informacje na temat
                                                // kolorów zdjęcia.

    // Metoda odpowiedzialna za tworzenie aktywności Androida.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)                                          // Metody androidowe, mało ważne.
        setContentView(R.layout.activity_detect)
        val stringUri = intent.extras?.getString(MainActivity.INTENT_IMAGE_URI)     // Wyłuskanie wysłąnego zdjęcia.
        palette = generatePalette(getBitmap(stringUri!!)!!)                         // Stworzenie instancji klasy Palette.
        setupRecyclerView(palette)
        takenPicture.setImageBitmap(bitmap)
    }

    /** Utils. */

    // Metoda ładująca listę kolorów.
    private fun setupRecyclerView(palette: Palette) {
        val colorsAdapter = ColorsAdapter(getColorsModels(palette))        // Stworzenia adaptera, zawierającego listę kolorów.
        colorsRecyclerView.apply {                                         // Dodanie do widoku adaptera i managera.
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    // Metoda generująca palatę barw z zadanego zdjęcia.
    private fun generatePalette(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()

    // Metoda wyłuskująca modele kolorów, otrzymanych z palety.
    private fun getColorsModels(palette: Palette): ArrayList<ColorModel> {
        val colors = ArrayList<ColorModel>()                   // Stworzenie listy.
        return colors.let {                                    // Cały ten blok zostanie wykonany na rzecz listy.
            for(swatch in palette.swatches) {                  // Pętla wyłuskuje kolory z palety.
                it.add(ColorModel(swatch))                     // Tworzy modele i dodaje do listy (it jest tym samym co colors).
            }
            it                                                 // Całość jest wyrażeniem lambda, dlatego ostatni element bloku kodu jest zwracany.
        }
    }

    // Funkcja wyłuskująca bitmapę z plików z zadanej ścieżki.
    private fun getBitmap(stringUri: String): Bitmap? {
        val contentURI = Uri.parse(stringUri)                                   // Wyłuskuje instancje obiektu Uri z ścieżki.
        val input: InputStream? = contentResolver.openInputStream(contentURI)   // Tworzenie strumienia danych.
        val options = BitmapFactory.Options().apply { inSampleSize = 2 }        // Przeskalowanie zdjęcia (zmniejszenie o połowę, inSampleSize oznacza dzielnik).

        bitmap = BitmapFactory.decodeStream(input, null, options)   // stworzenie bitmapy.
        return bitmap
    }
}
