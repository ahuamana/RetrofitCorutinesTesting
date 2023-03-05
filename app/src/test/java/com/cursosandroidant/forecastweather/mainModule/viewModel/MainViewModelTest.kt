package com.cursosandroidant.forecastweather.mainModule.viewModel

import com.cursosandroidant.forecastweather.common.dataAccess.WeatherForecastService
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var service : WeatherForecastService

    companion object{
        private lateinit var retrofit: Retrofit

        @BeforeClass
        @JvmStatic
        fun setup(){
            retrofit = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    @Before
    fun setUp(){
        viewModel = MainViewModel()
        service = retrofit.create(WeatherForecastService::class.java)
    }

    @Test
    fun checkCurrentWeatherIsNotNullTest(){
        runBlocking {
            val result = service.getWeatherForecastByCoordinates(
                40.416775,
                -3.703790,
                "fdbcfef5d14487a138b55af24d1cf470",
                "metric", "es")
            assertThat(result.current, `is` (notNullValue()))
        }
    }

    @Test
    fun checkTimezoneReturnsMexicoCityTest(){
        runBlocking {
            val result = service.getWeatherForecastByCoordinates(
                19.432608,
                -99.133209,
                "fdbcfef5d14487a138b55af24d1cf470",
                "metric", "es")
            assertThat(result.timezone, `is` ("America/Mexico_City"))
        }
    }

    // This test will fail because the API Key is not valid
    @Test
    fun checkErrorResponseWithOnlyCoordinates(){
        runBlocking {
            try {
                service.getWeatherForecastByCoordinates(
                    40.416775,
                    -3.703790,
                    "",
                    "", "")
            }catch (e: Exception){
                assertThat(e.message, `is` ("HTTP 401 Unauthorized"))
            }

        }
    }
}