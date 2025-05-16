package com.example.weatherapp

import android.health.connect.datatypes.units.Pressure
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.cardview.widget.CardView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.widget.ContentLoadingProgressBar
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log

class MainActivity : ComponentActivity() {
    private lateinit var pbLoading: ProgressBar
    private lateinit var rlMainLayout: RelativeLayout
    private lateinit var etCityName: EditText
    private lateinit var tvData: TextView
    private lateinit var tvDayMaxTemp:TextView
//   ** private lateinit var tvDayMinTemp:TextView
    private lateinit var tvTemp:TextView
    private lateinit var tvFeelsLike:TextView
    private lateinit var tvWeatherType:TextView
    private lateinit var tvPressure: TextView
    private lateinit var tvHumidity:TextView
    private lateinit var tvWindSpeed:TextView
    private lateinit var tvSunrise:TextView
    private lateinit var tvSunset:TextView
    private lateinit var tvTempFahrenheit:TextView
    private lateinit var ivWeather:ImageView
    private lateinit var weatherIcon:ImageView
    private lateinit var cvToolbar: CardView
    private lateinit var tvClothingAdvice: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

        initViews()
        setupSearch()
        loadWeatherDate("Amman")
    }
    private fun getClothingAdvice(temp: Double): String {
        return when {
            temp < 5 -> "It's very cold 🥶. Wear a heavy coat 🧥, gloves 🧤, and a scarf 🧣."
            temp in 5.0..15.0 -> "It's cool 😌. Wear a jacket 🧥 or sweater."
            temp in 15.0..25.0 -> "The weather is mild 🙂. A t-shirt 👕 or light jacket is fine."
            temp > 25 -> "It's hot 🥵. Wear light clothing like shorts 🩳 and a t-shirt 👕."
            else -> "Check the weather again for accurate clothing advice."
        }
    }



    private fun initViews(){
        pbLoading=findViewById(R.id.pb_loading)
        rlMainLayout=findViewById(R.id.rl_main_layout)
        etCityName=findViewById(R.id.et_get_city_name)
        tvData=findViewById(R.id.tv_date)
        tvDayMaxTemp=findViewById(R.id.tv_day_max_temp)
//      **  tvDayMinTemp=findViewById(R.id.tv_day_min_temp)
        tvTemp=findViewById(R.id.tv_temp)
        tvFeelsLike=findViewById(R.id.tv_feels_like)
        tvWeatherType=findViewById(R.id.tv_weather_type)
        tvPressure=findViewById(R.id.tv_pressure)
        tvHumidity=findViewById(R.id.tv_humidity)
        tvWindSpeed=findViewById(R.id.tv_wind_speed)
        tvSunrise=findViewById(R.id.tv_sunrise)
        tvSunset=findViewById(R.id.tv_sunset)
        tvTempFahrenheit=findViewById(R.id.tv_temp_fahrenhaite)
        ivWeather=findViewById(R.id.iv_weather)
        weatherIcon=findViewById(R.id.weather_icon)
        cvToolbar=findViewById(R.id.cv_toolbar)
        tvClothingAdvice = findViewById(R.id.tv_clothing_advice)



    }

    private fun setupSearch() {
        etCityName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val city = etCityName.text.toString().trim()
                if (city.isNotEmpty()) {
                    loadWeatherDate(city)
                } else {
                    Toast.makeText(this, "Please enter a city name", Toast.LENGTH_LONG).show()
                }
                true
            } else {
                false
            }
        }
    }

    private fun loadWeatherDate(city:String){
        pbLoading.visibility= View.VISIBLE
        rlMainLayout.visibility=View.GONE

        mockWeatherApiCall(city)
    }

    private fun mockWeatherApiCall(city: String) {
        val apiKey = "79f2165fed10286ad71c14d4dff6da0d" // Replace with your OpenWeatherMap API key
        RetrofitInstance.api.getWeatherByCityName(city, apiKey).enqueue(object : retrofit2.Callback<WeatherResponse> {
            override fun onResponse(call: retrofit2.Call<WeatherResponse>, response: retrofit2.Response<WeatherResponse>) {
                Log.d("MainActivity", "Response code: ${response.code()}")
                Log.d("MainActivity", "Response: ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    val weatherData = response.body()
                    val jsonResponse = JSONObject().apply {
                        put("temp", weatherData?.main?.temp)
                        put("feels_like", weatherData?.main?.feels_like)
//                      **  put("temp_min", weatherData?.main?.temp_min)
                        put("temp_max", weatherData?.main?.temp_max)
                        put("pressure", weatherData?.main?.pressure)
                        put("humidity", weatherData?.main?.humidity)
                        put("wind_speed", weatherData?.wind?.speed)
                        put("sunrise", weatherData?.sys?.sunrise)
                        put("sunset", weatherData?.sys?.sunset)
                        put("weather", weatherData?.weather?.get(0)?.main)
                        put("weather_icon", weatherData?.weather?.get(0)?.icon)
                    }
                    processWeatherData(jsonResponse)
                } else {
                    showError()
                }
            }

            override fun onFailure(call: retrofit2.Call<WeatherResponse>, t: Throwable) {
                t.printStackTrace()
                showError()
            }
        })
    }

    private fun processWeatherData(data: JSONObject) {
        runOnUiThread {
            try {
                val currentDate = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
                tvData.text = currentDate
                tvTemp.text = "%.0f °C".format(data.getDouble("temp"))
                tvFeelsLike.text = "%.0f °C".format(data.getDouble("feels_like"))
                tvDayMaxTemp.text = "%.0f °C".format(data.getDouble("temp_max"))
//               ** tvDayMinTemp.text = "%.0f °C".format(data.getDouble("temp_min"))
                tvWeatherType.text = data.getString("weather")
                tvPressure.text = "${data.getInt("pressure")} hPa"
                tvHumidity.text = "${data.getInt("humidity")} %"
                tvWindSpeed.text = "%.1f km/h".format(data.getDouble("wind_speed"))
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                tvSunrise.text = timeFormat.format(Date(data.getLong("sunrise") * 1000L))
                tvSunset.text = timeFormat.format(Date(data.getLong("sunset") * 1000L))
                val tempFahrenheit = (data.getDouble("temp") * 9 / 5) + 32
                tvTempFahrenheit.text = "%.0f °F".format(tempFahrenheit)
                val iconCode = data.getString("weather_icon")
                val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
//                Picasso.get().load(iconUrl).into(weatherIcon)
//                updateBackgroundImage(data.getString("weather"))
                pbLoading.visibility = View.GONE
                rlMainLayout.visibility = View.VISIBLE
                tvTemp.text = "%.0f °C".format(data.getDouble("temp"))
                val temp = data.getDouble("temp")
                val advice = getClothingAdvice(temp)
                tvClothingAdvice.text = advice


            } catch (e: JSONException) {
                e.printStackTrace()
                showError()
            }
        }
    }

//    private fun mockWeatherApiCall(city: String){
//        android.os.Handler().postDelayed({
//            try {
//                val mockResponse = JSONObject().apply {
//                    put("temp", 25)
//                    put("feels_like", 27)
//                    put("temp_min", 22)
//                    put("temp_max", 30)
//                    put("pressure", 1012)
//                    put("humidity", 65)
//                    put("wind_speed", 5.5)
//                    put("sunrise", 1643688800)
//                    put("sunset", 1643728800)
//                    put("weather", "clear")
//                    put("weather_icon", "01d")
//                }
//                processWeatherData(mockResponse)
//            }
//            catch (e:JSONException){
//                e.printStackTrace()
//
//            }
//        },1500)
//    }

//    private fun processWeatherData(data: JSONObject){
//        runOnUiThread{
//            try {
//                val currentDate =
//                    SimpleDateFormat("EEEE,MMMM d", Locale.getDefault()).format(Date())
//                tvData.text = currentDate
//                tvTemp.text = ".0 f".format(data.getDouble("temp"))
//                tvFeelsLike.text = ".0 f".format(data.getDouble("feels_like"))
//                tvDayMaxTemp.text = ".0 f".format(data.getDouble("temp_max"))
//                tvDayMinTemp.text = ".0 f".format(data.getDouble("temp_min"))
//                tvWeatherType.text = data.getString("weather")
//                tvPressure.text = "${data.getInt("pressure")}hPa"
//                tvHumidity.text = "${data.getInt("humidity")}%"
//                tvWindSpeed.text = "0.1f km/h".format(data.getDouble("wind_speed"))
//                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
//                tvSunrise.text = timeFormat.format(Date(data.getLong("sunrise") * 1000L))
//                tvSunset.text = timeFormat.format(Date(data.getLong("sunset") * 1000L))
//                val tempFahrenheit = (data.getDouble("temp") * 9 / 5) + 32
//                tvTempFahrenheit.text = ".0f f".format(tempFahrenheit)
//                val iconCode = data.getString("weather_icon")
//                val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
//                Picasso.get().load(iconUrl).into(weatherIcon)
//                updateBackgroundImage(data.getString("weather"))
//                pbLoading.visibility = View.GONE
//                rlMainLayout.visibility = View.VISIBLE
//            }
//            catch(e:JSONException){
//                e.printStackTrace()
//                showError()
//            }
//        }
//    }
//
    private fun updateBackgroundImage(weatherCondition:String){
        val backgroundResId=when(weatherCondition.toLowerCase()){
            "clear"->R.drawable.clear
            "rain"-> R.drawable.rain
            "cloud"->R.drawable.cloud
            "snow"->R.drawable.snowflake
            else->R.drawable.clear

        }
        ivWeather.setImageResource(backgroundResId)
    }
    private fun showError() {
        runOnUiThread {
            pbLoading.visibility = View.GONE
            rlMainLayout.visibility = View.VISIBLE
            Toast.makeText(this, "Failed to load weather data", Toast.LENGTH_SHORT).show()
        }
        }
    }



