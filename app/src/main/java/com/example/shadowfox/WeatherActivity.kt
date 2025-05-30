package com.example.shadowfox

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shadowfox.data.WeatherResponse
import com.example.shadowfox.databinding.ActivityWeatherBinding
import com.example.shadowfox.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
// Import Glide or Picasso for image loading if you want to load icons from URL
// import com.bumptech.glide.Glide

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding
    private val TAG = "WeatherActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add a Toolbar if you don't have one in the layout, or use an existing one
        // For simplicity, assuming no explicit Toolbar in activity_weather.xml for now,
        // but you'd typically add one and set it up:
        // setSupportActionBar(binding.toolbarWeather) // Assuming binding.toolbarWeather exists
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Weather Forecast"


        binding.buttonFetchWeather.setOnClickListener {
            val location = binding.editTextLocation.text.toString().trim()
            if (location.isNotEmpty()) {
                fetchWeatherData(location)
            } else {
                Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeatherData(location: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.textViewCityName.visibility = View.GONE // Hide previous data
        // Hide other views as well if they show old data

        val preferredUnit = if (SettingsActivity.isCelsiusPreferred(this)) "metric" else "imperial"

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getCurrentWeather(location, RetrofitInstance.API_KEY, preferredUnit)
                if (response.isSuccessful) {
                    response.body()?.let { weatherResponse ->
                        updateUI(weatherResponse, preferredUnit)
                    } ?: run {
                        showError("No data received")
                        Log.e(TAG, "Response body is null")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    showError("Error: ${response.code()} - ${errorBody ?: "Unknown error"}")
                    Log.e(TAG, "API Error: ${response.code()} - $errorBody")
                    if (response.code() == 401) {
                        Toast.makeText(this@WeatherActivity, "Invalid API Key. Please check your API key in RetrofitInstance.kt", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
                Log.e(TAG, "Network Exception: ${e.message}", e)
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateUI(weatherData: WeatherResponse, unit: String) {
        val tempUnitSuffix = if (unit == "metric") "°C" else "°F"
        val windSpeedSuffix = if (unit == "metric") " m/s" else " mph" // API provides m/s for metric, mph for imperial

        binding.textViewCityName.visibility = View.VISIBLE
        binding.textViewCityName.text = weatherData.cityName
        binding.textViewTemperature.text = "${weatherData.mainStats.temperature}$tempUnitSuffix"
        binding.textViewWeatherCondition.text = weatherData.weather.firstOrNull()?.main ?: "N/A"
        binding.textViewWeatherDescription.text = weatherData.weather.firstOrNull()?.description?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        } ?: "N/A"

        binding.textViewFeelsLike.text = "${weatherData.mainStats.feelsLike}$tempUnitSuffix"
        binding.textViewMinMaxTemp.text = "${weatherData.mainStats.tempMin}$tempUnitSuffix / ${weatherData.mainStats.tempMax}$tempUnitSuffix"
        binding.textViewHumidity.text = "${weatherData.mainStats.humidity}%"
        binding.textViewWindSpeed.text = "${weatherData.wind.speed}$windSpeedSuffix"
        binding.textViewPressure.text = "${weatherData.mainStats.pressure} hPa"

        binding.textViewSunrise.text = formatUnixTimestamp(weatherData.systemInfo.sunrise, weatherData.timezone)
        binding.textViewSunset.text = formatUnixTimestamp(weatherData.systemInfo.sunset, weatherData.timezone)

        // Load weather icon using Glide or Picasso
        // val iconCode = weatherData.weather.firstOrNull()?.icon
        // if (iconCode != null) {
        //     val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
        //     Glide.with(this).load(iconUrl).into(binding.imageViewWeatherIcon)
        // } else {
        //     binding.imageViewWeatherIcon.setImageResource(R.drawable.ic_app_logo_placeholder) // A default placeholder
        // }
        // For now, using a placeholder as Glide/Picasso is not added
        binding.imageViewWeatherIcon.setImageResource(R.drawable.ic_app_logo_placeholder)


        // Make other views visible if they were hidden
    }

    private fun formatUnixTimestamp(unixTime: Long, timezoneOffsetSeconds: Int): String {
        try {
            val date = Date((unixTime + timezoneOffsetSeconds) * 1000L) // Apply timezone offset
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC") // Important: Date object was created with offset, so format as UTC
            return sdf.format(date)
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting timestamp: $unixTime", e)
            return "N/A"
        }
    }


    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        // Optionally, update UI to show error message
        binding.textViewCityName.text = "Error"
        binding.textViewTemperature.text = message
        // Clear other fields or show relevant error info
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // Go back to the previous activity (HomeActivity)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}