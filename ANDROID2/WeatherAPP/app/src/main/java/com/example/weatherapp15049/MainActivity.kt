package com.example.weatherapp15049

import android.content.res.Configuration
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var editLat: EditText
    private lateinit var editLong: EditText
    private lateinit var txtPressure: TextView
    private lateinit var txtWindDir: TextView
    private lateinit var txtWindSpeed: TextView
    private lateinit var txtTemp: TextView
    private lateinit var txtTime: TextView
    private lateinit var btnUpdate: Button
    private lateinit var imgWeather: ImageView
    private var day: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (day) setTheme(R.style.Theme_Day)
            else setTheme(R.style.Theme_Night)
        } else {
            if (day) setTheme(R.style.Theme_Day_Land)
            else setTheme(R.style.Theme_Night_Land)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editLat = findViewById(R.id.editLat)
        editLong = findViewById(R.id.editLong)
        txtPressure = findViewById(R.id.PressureValue)
        txtWindDir = findViewById(R.id.WindDirections)
        txtWindSpeed = findViewById(R.id.WindSpeeds)
        txtTemp = findViewById(R.id.txtTemperature)
        txtTime = findViewById(R.id.TimeValue)
        btnUpdate = findViewById(R.id.btnUpdate)
        imgWeather = findViewById(R.id.imgWeather)
        btnUpdate.setOnClickListener {
            val lat = editLat.text.toString().toFloatOrNull() ?: 38.7f
            val lon = editLong.text.toString().toFloatOrNull() ?: -9.1f
            fetchWeatherData(lat, lon)
            Toast.makeText(this, "A atualizar dados...", Toast.LENGTH_SHORT).show()
        }
    }
    private fun WeatherAPI_Call(lat: Float, long: Float): WeatherData? {
        return try {
            val reqString = buildString {
                append("https://api.open-meteo.com/v1/forecast?")
                append("latitude=${lat}&longitude=${long}&")
                append("current_weather=true&")
                append("timezone=auto&")
                append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m")
            }
            val url = URL(reqString)
            url.openStream().use {
                Gson().fromJson(InputStreamReader(it, "UTF-8"), WeatherData::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun fetchWeatherData(lat: Float, long: Float) {
        Thread {
            val weather = WeatherAPI_Call(lat, long)
            if (weather != null) {
                runOnUiThread {
                    updateUI(weather)
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Erro ao obter dados da API", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun updateUI(request: WeatherData) {
        txtTemp.text = "${request.current_weather.temperature} ºC"
        txtWindSpeed.text = "${request.current_weather.windspeed} km/h"
        txtWindDir.text = "${request.current_weather.winddirection} º"
        txtTime.text = request.current_weather.time
        if (request.hourly.pressure_msl.isNotEmpty()) {
            txtPressure.text = "${request.hourly.pressure_msl[12]} hPa"
        }
        val mapt = getWeatherCodeMap()
        val wCode = mapt[request.current_weather.weathercode]

        val wImage = when (wCode) {
            WMO_WeatherCode.CLEAR_SKY,
            WMO_WeatherCode.MAINLY_CLEAR,
            WMO_WeatherCode.PARTLY_CLOUDY -> {
                if (day) wCode.image + "day" else wCode.image + "night"
            }
            else -> wCode?.image ?: "clear_day"
        }
        val resID = resources.getIdentifier(wImage, "drawable", packageName)
        if (resID != 0) {
            imgWeather.setImageResource(resID)
        } else {
            imgWeather.setImageResource(R.drawable.clear_day) // Fallback caso não encontre a imagem
        }
    }
}