package com.karimmammadov.weatherappp.view

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.VerifiedInputEvent
import android.view.View
import androidx.core.os.trace
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.karimmammadov.weatherappp.R
import com.karimmammadov.weatherappp.model.WeatherModel
import com.karimmammadov.weatherappp.service.WeatherAPIService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val weatherAPIService = WeatherAPIService()
    private val disposable = CompositeDisposable()

    val weatherData = MutableLiveData<WeatherModel>()
    val weatherError = MutableLiveData<Boolean>()
    val weatherLoad = MutableLiveData<Boolean>()

    private lateinit var degreePrefs:SharedPreferences
    var storedDegree : String? = null

    private lateinit var codePrefs :SharedPreferences
    var storedCode : String? = null

    private lateinit var cityPrefs :SharedPreferences
    var storedCity : String? = null

    private lateinit var humidityPrefs :SharedPreferences
    var storedHumidity: String? = null

    private lateinit var speedPrefs :SharedPreferences
    var storedSpeed : String? = null

    private lateinit var feelsPrefs :SharedPreferences
    var storedFeels : String? = null

    private lateinit var descriptionPrefs :SharedPreferences
    var storedDescription : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        degreePrefs = this.getSharedPreferences("com.karimmammadov.weatherappp", MODE_PRIVATE)
        storedDegree = degreePrefs.getString("degree", "DEFAULT")
        degreeText.text = storedDegree

        cityPrefs = this.getSharedPreferences("com.karimmammadov.weatherappp", MODE_PRIVATE)
        storedCity = cityPrefs.getString("city","DEFAULT")
        cityNameText.text = storedCity

        codePrefs = this.getSharedPreferences("com.karimmammadov.weatherappp", MODE_PRIVATE)
        storedCode = codePrefs.getString("code","DEFAULT")
        cityCodeText.text = storedCode

        humidityPrefs = this.getSharedPreferences("com.karimmammadov.weatherappp", MODE_PRIVATE)
        storedHumidity = humidityPrefs.getString("humidity","DEFAULT")
        humidityText.text = storedHumidity

        speedPrefs = this.getSharedPreferences("com.karimmammadov.weatherappp", MODE_PRIVATE)
        storedSpeed = speedPrefs.getString("speed","DEFAULT")
        speedText.text = storedSpeed

        feelsPrefs = this.getSharedPreferences("com.karimmammadov.weatherappp", MODE_PRIVATE)
        storedFeels = feelsPrefs.getString("feels","DEFAULT")
        feelsLikeText.text = storedFeels

        descriptionPrefs = this.getSharedPreferences("com.karimmammadov.weatherappp", MODE_PRIVATE)
        storedDescription = descriptionPrefs.getString("description","DEFAULT")
        descriptionText.text = storedDescription

       searchCity.setOnClickListener {
           val cityName = editCity.text.toString()
           getDataFromAPI(cityName!!)
           getLiveData()
       }
    }

    private fun getLiveData() {
        weatherData.observe(this, Observer { data ->
            data?.let {
                llData.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                degreeText.text = data.main.temp.toString() + "°C"
                cityCodeText.text = data.sys.country.toString()
                cityNameText.text = data.name.toString()
                humidityText.text = data.main.humidity.toString()
                speedText.text = data.wind.speed.toString()
                feelsLikeText.text = data.main.feels_like.toString() + "°C"
                descriptionText.text =  data.weather.get(0).description.toString()

                degreePrefs.edit().putString("degree", data.main.temp.toString() + "°C").apply()
                codePrefs.edit().putString("code",data.sys.country.toString()).apply()
                cityPrefs.edit().putString("city",data.name.toString()).apply()
                humidityPrefs.edit().putString("humidity",data.main.humidity.toString()).apply()
                speedPrefs.edit().putString("speed",data.wind.speed.toString()).apply()
                feelsPrefs.edit().putString("feels",data.main.feels_like.toString() + "°C").apply()
                descriptionPrefs.edit().putString("description",data.weather.get(0).description.toString()).apply()
            }
        })
    }


    fun getDataFromAPI(cityName:String){
        weatherLoad.value = true
        disposable.add(
            weatherAPIService.getDataFromService(cityName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherModel>() {
                    override fun onSuccess(t: WeatherModel) {
                        weatherData.value = t
                        weatherError.value = false
                        weatherLoad.value = false
                    }

                    override fun onError(e: Throwable) {
                        weatherError.value = true
                        weatherLoad.value = false
                        errorTextView.text = "Error, Try Again!"
                    }

                })
        )
    }
}