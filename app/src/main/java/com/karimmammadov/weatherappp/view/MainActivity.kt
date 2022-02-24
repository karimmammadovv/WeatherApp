package com.karimmammadov.weatherappp.view

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

    private lateinit var GET:SharedPreferences
    private lateinit var SET:SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

       searchCity.setOnClickListener {
           val cityName = editCity.text.toString()
           SET.putString("cityName",cityName)
           SET.apply()
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
              descriptionText.text =  data.weather.get(0).description
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