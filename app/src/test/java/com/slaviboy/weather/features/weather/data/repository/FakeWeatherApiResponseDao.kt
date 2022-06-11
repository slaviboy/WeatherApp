package com.slaviboy.weather.features.weather.data.repository

import com.slaviboy.weather.features.weather.data.local.WeatherApiResponseDao
import com.slaviboy.weather.features.weather.data.local.entity.*
import com.slaviboy.weather.features.weather.data.local.entity.relationship.WeatherApiResponseWithRelations

class FakeWeatherApiResponseDao : WeatherApiResponseDao {

    val citiesList = mutableListOf(
        City()
    )

    override suspend fun deleteWeatherApiResponseByCityId(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeatherApiResponseByCityName(cityName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeatherById(cityId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTemperatureById(cityId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFeelsLikeById(cityId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCurrentById(cityId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteHourlyById(cityId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDailyById(cityId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun insertWeatherApiResponse(vararg weatherApiResponse: WeatherApiResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun insertCurrent(current: Current) {
        TODO("Not yet implemented")
    }

    override suspend fun insertDaily(daily: Daily) {
        TODO("Not yet implemented")
    }

    override suspend fun insertFeelLike(feelsLike: FeelsLike) {
        TODO("Not yet implemented")
    }

    override suspend fun insertHourly(hourly: Hourly) {
        TODO("Not yet implemented")
    }

    override suspend fun insertTemperature(temp: Temp) {
        TODO("Not yet implemented")
    }

    override suspend fun insertWeather(weather: Weather) {
        TODO("Not yet implemented")
    }

    override suspend fun insertCity(vararg city: City) {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherApiResponseByCityId(id: Int): WeatherApiResponseWithRelations? {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherApiResponseByCityName(cityName: String): WeatherApiResponseWithRelations? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllCitiesByCityName(name: String): List<City> {
        TODO("Not yet implemented")
    }

    override suspend fun getCityById(id: Int): City? {
        TODO("Not yet implemented")
    }
}