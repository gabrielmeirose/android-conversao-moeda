package com.example.aula01

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private lateinit var result: TextView
    private var api_key = BuildConfig.API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Referencia ao layout

        result = findViewById<TextView>(R.id.txt_result)
        val buttonConverter = findViewById<TextView>(R.id.btn_converter)

        buttonConverter.setOnClickListener {
            converter()
        }

    }

    private fun round(value: Double): Double {
        // Envia 2 casas para frente, elimina o decimal, e volta 2 para tras
        return ( (value * 100).toInt() ).toDouble() / 100
    }

    private fun converter() {
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val selected = radioGroup.checkedRadioButtonId

        var currency: String
        var symbol: String

        when(selected) {
            R.id.radio_usd -> {
                currency = "USD"
                symbol = "$"
            }
            R.id.radio_eur -> {
                currency = "EUR"
                symbol = "€"
            }
            else -> {
                currency = "JPY"
                symbol = "¥"
            }
        }

        val editField = findViewById<EditText>(R.id.edit_field)
        val value = editField.text.toString()

        if(value.isEmpty())
            return

        Thread {
            // codigo acontece em paralelo

            val url = URL("https://api.freecurrencyapi.com/v1/latest?apikey=${api_key}&currencies=${currency}&base_currency=BRL")

            val connection = url.openConnection() as HttpsURLConnection

            try {

                val data = connection.inputStream.bufferedReader().readText()

                val json = JSONObject(data)

                /* Isso não poderia, pois está dentro de uma thread
                   Não pode atribuir

                result.text = data
                result.visibility = View.VISIBLE

                */

                // Usa a thread da UI
                runOnUiThread() {
                    val obj = json.getJSONObject("data")
                    val res = obj.getDouble(currency)

                    // Deixar 2 casas decimais
                    val resultValue = symbol + (round(value.toDouble() * res)).toString()


                    result.text = resultValue
                    result.visibility = View.VISIBLE
                }

            } finally {
                connection.disconnect()
            }

        }.start()

    }
}