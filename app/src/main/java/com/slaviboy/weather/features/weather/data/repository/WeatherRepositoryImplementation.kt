/*
* Copyright (C) 2022 Stanislav Georgiev
* https://github.com/slaviboy
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.slaviboy.weather.features.weather.data.repository

import com.slaviboy.weather.R
import com.slaviboy.weather.core.util.Result
import com.slaviboy.weather.features.weather.data.local.WeatherApiResponseDao
import com.slaviboy.weather.features.weather.data.local.entity.City
import com.slaviboy.weather.features.weather.data.local.entity.relationship.WeatherApiResponseWithRelations
import com.slaviboy.weather.features.weather.data.remote.WeatherApi
import com.slaviboy.weather.features.weather.domain.model.WeatherApiResponseInfo
import com.slaviboy.weather.features.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import javax.inject.Inject

class WeatherRepositoryImplementation @Inject constructor(
    private val api: WeatherApi,
    private val dao: WeatherApiResponseDao
) : WeatherRepository {

    /**
     * Store the API weather data to local database
     * @param weatherApiResponseWithRelations the object that holds all the different class object with weather information
     */
    suspend fun insertApiDataToLocalDB(weatherApiResponseWithRelations: WeatherApiResponseWithRelations) {

        // insert new data
        weatherApiResponseWithRelations.weatherApiResponse?.let {
            dao.insertWeatherApiResponse(it)
        }
        weatherApiResponseWithRelations.currentEntity?.apply {
            dao.insertCurrent(current)
            weather.forEach {
                dao.insertWeather(it)
            }
        }
        weatherApiResponseWithRelations.dailyEntity.forEach {
            dao.insertDaily(it.daily)
            dao.insertTemperature(it.temp)
            dao.insertFeelLike(it.feelsLike)
            it.weather.forEach {
                dao.insertWeather(it)
            }
        }
        weatherApiResponseWithRelations.hourlyEntity.forEach {
            dao.insertHourly(it.hourly)
            it.weather.forEach {
                dao.insertWeather(it)
            }
        }
    }

    /**
     * Delete the previous cached data, for particular city by given Id, this is done since
     * there can be different Weather object for the current, hourly and daily tables under
     * the same cityId
     * @param cityId the id of the city
     */
    suspend fun deleteOldCacheData(cityId: Int) {

        // delete Current, Hourly, Daily fields from db
        dao.deleteCurrentById(cityId)
        dao.deleteHourlyById(cityId)
        dao.deleteDailyById(cityId)

        // delete all Weather, FeelsLike, Temp fields from db
        dao.deleteWeatherById(cityId)
        dao.deleteFeelsLikeById(cityId)
        dao.deleteTemperatureById(cityId)
    }

    override fun getWeatherApiResponse(id: Int, name: String, lat: Float, lon: Float, requestCachedData: Boolean): Flow<Result<WeatherApiResponseInfo>> = flow {

        val calendar = GregorianCalendar(Locale.getDefault())

        // the phone(device) time zone offset for current city in ms, for example Sofia -> (+2.0).hoursToMilliseconds()
        val phoneOffsetMilliseconds = calendar.timeZone.rawOffset.toLong()
        val londonTime = calendar.timeInMillis - phoneOffsetMilliseconds // UTC time

        // indicate we are loading weather data
        emit(Result.Loading())

        // get cached data
        var cityWeatherWithRelations = dao.getWeatherApiResponseByCityId(id)

        if (!requestCachedData || cityWeatherWithRelations == null) {

            // emit loading, so we can display the cached data while the new API data is loading
            emit(Result.Loading(data = cityWeatherWithRelations?.toWeatherApiResponseInfo(londonTime)))

            // request new API weather data
            try {

                // request new API data
                val remoteCityWeather = api.getWeatherApiResponseByGeographicCoordinates(lat, lon)
                val remoteCityWeatherWithRelations = remoteCityWeather.toCityWeatherWithRelations(id, name)

                // insert new API data to local database
                deleteOldCacheData(id)
                insertApiDataToLocalDB(remoteCityWeatherWithRelations)

                cityWeatherWithRelations = remoteCityWeatherWithRelations
                emit(Result.Success(data = cityWeatherWithRelations.toWeatherApiResponseInfo(londonTime), isCachedData = false))

            } catch (e: Exception) {

                val message = when (e) {
                    is HttpException -> R.string.oops
                    is IOException -> R.string.could_not_reach_server
                    else -> R.string.unknown_error
                }

                // emit error and return cached data
                val error = Result.Error(
                    message = message,
                    data = cityWeatherWithRelations?.toWeatherApiResponseInfo(londonTime)
                )
                emit(error)
            }
        } else {

            // force use the cached weather data, for example when the user opens the app for, we do not want
            // to show errors for network connection, instead show him the last stored cached data
            emit(Result.Success(data = cityWeatherWithRelations.toWeatherApiResponseInfo(londonTime), isCachedData = true))
        }
    }


    override fun getAllCitiesByCityName(cityName: String): Flow<Result<List<City>>> = flow {

        // get cached data
        val allCities = dao.getAllCitiesByCityName(cityName).map {
            it
        }
        if (allCities.isNotEmpty()) {
            emit(Result.Success(data = allCities, isCachedData = true))
        } else {
            emit(Result.Error(R.string.no_records_found, allCities))
        }
    }

    override fun getCityById(cityId: Int): Flow<Result<City>> = flow {
        val city = dao.getCityById(cityId)
        if (city != null) {
            emit(Result.Success(data = city, isCachedData = true))
        } else {
            emit(Result.Error(R.string.no_records_found, City()))
        }
    }
}