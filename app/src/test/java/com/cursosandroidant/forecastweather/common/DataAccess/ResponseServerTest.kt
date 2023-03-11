package com.cursosandroidant.forecastweather.common.DataAccess

import com.cursosandroidant.forecastweather.entities.WeatherForecastEntity
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.http.HTTP
import java.net.HttpURLConnection

@RunWith(MockitoJUnitRunner::class)
class ResponseServerTest {

    private lateinit var mockWebServer : MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test response server`() {
        val jsonFileLoader = JSONFileLoader()
        val json = jsonFileLoader.loadJSONFromAsset("weather_forecast_response_sucess")
        assertThat(json, `is`(notNullValue()))
        assertThat(json , containsString("America/Mexico_City"))
    }

    //Text with mockWebServer and load JSON from asset
    @Test
    fun `get wheather forecast Check TimeZone Exist`() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(JSONFileLoader().loadJSONFromAsset("weather_forecast_response_sucess")?:"{errorCode: 404, message: 'Not Found'}")
        mockWebServer.enqueue(response)

        assertThat(response.getBody()?.readUtf8(), containsString("\"timezone\""))
    }

    @Test
    fun `get wheather forecast and check timezone fail response`() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(JSONFileLoader().loadJSONFromAsset("weather")?:"{errorCode: 404, message: 'Not Found'}")
        mockWebServer.enqueue(response)

        assertThat(response.getBody()?.readUtf8(), containsString("Invalid API key"))
    }


    @Test
    fun `get wheather forecast and check constantly hourly list not empty`() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(JSONFileLoader().loadJSONFromAsset("weather_forecast_response_sucess")
                ?:"{errorCode: 404, message: 'Not Found'}")
        mockWebServer.enqueue(response)

        assertThat(response.getBody()?.readUtf8(), containsString("\"hourly\""))

        val json = Gson().fromJson(response.getBody()?.readUtf8()?:"", WeatherForecastEntity::class.java)
        assertThat(json.hourly, `is`(notNullValue()))
    }

}