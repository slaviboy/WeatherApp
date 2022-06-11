package com.slaviboy.weather.features.weather.data.repository

import org.junit.Before
import org.junit.Test

class WeatherRepositoryImplementationTest {

    private lateinit var repository: WeatherRepositoryImplementation
    private lateinit var fakeWeatherApiResponseDao: FakeWeatherApiResponseDao
    private lateinit var fakeWeatherApi: FakeWeatherApi

    @Before
    fun setup() {
        fakeWeatherApiResponseDao = FakeWeatherApiResponseDao()
        fakeWeatherApi = FakeWeatherApi()
        repository = WeatherRepositoryImplementation(fakeWeatherApi, fakeWeatherApiResponseDao)
    }

    @Test
    fun set_getTemperatureType()   {
    }
}