package com.cursosandroidant.forecastweather.common.DataAccess

import com.cursosandroidant.forecastweather.entities.WeatherForecastEntity
import com.google.gson.Gson
import java.io.InputStreamReader

class JSONFileLoader {
    private  var jsonStr: String? = null

    fun loadJSONFromAsset(fileName: String): String? {
        try {
            val inputStream = javaClass.classLoader?.getResourceAsStream(fileName)
            val loader = InputStreamReader(inputStream)
            jsonStr = loader.readText()
            loader.close()
            return jsonStr
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
    }

    fun loadWeatherForecastEntity(file:String): WeatherForecastEntity? {
        val inputStream = javaClass.classLoader?.getResourceAsStream(file)
        val loader = InputStreamReader(inputStream)
        jsonStr = loader.readText()
        loader.close()
        return Gson().fromJson(jsonStr, WeatherForecastEntity::class.java)
    }
}