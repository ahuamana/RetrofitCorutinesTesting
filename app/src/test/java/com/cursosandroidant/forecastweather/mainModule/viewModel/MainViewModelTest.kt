package com.cursosandroidant.forecastweather.mainModule.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cursosandroidant.forecastweather.common.dataAccess.WeatherForecastService
import com.cursosandroidant.historicalweatherref.getOrAwaitValue
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")


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
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun checkHourlySizeTest(){
        runTest   {
            launch(Dispatchers.Unconfined){
                viewModel.getWeatherAndForecast(
                    -19.432608,
                    -99.1962,
                    "fdbcfef5d14487a138b55af24d1cf470",
                    "metric", "en")

                val result = viewModel.getResult().getOrAwaitValue()
                assertThat(result?.hourly?.size, `is` (48))
            }
        }
    }
}