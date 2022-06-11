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
package com.slaviboy.weather.features.weather.data.local

import androidx.room.*
import com.slaviboy.weather.features.weather.data.local.entity.*
import com.slaviboy.weather.features.weather.data.local.entity.relationship.WeatherApiResponseWithRelations

@Dao
interface WeatherApiResponseDao {

    @Query("DELETE FROM api_response WHERE cityId=:id")
    suspend fun deleteWeatherApiResponseByCityId(id: Int)

    @Query("DELETE FROM api_response WHERE cityName=:cityName")
    suspend fun deleteWeatherApiResponseByCityName(cityName: String)

    @Query("DELETE FROM weather WHERE cityId =:cityId")
    suspend fun deleteWeatherById(cityId: Int)

    @Query("DELETE FROM temperature WHERE cityId =:cityId")
    suspend fun deleteTemperatureById(cityId: Int)

    @Query("DELETE FROM feels_like WHERE cityId =:cityId")
    suspend fun deleteFeelsLikeById(cityId: Int)

    @Query("DELETE FROM current WHERE cityId =:cityId")
    suspend fun deleteCurrentById(cityId: Int)

    @Query("DELETE FROM hourly WHERE cityId =:cityId")
    suspend fun deleteHourlyById(cityId: Int)

    @Query("DELETE FROM daily WHERE cityId =:cityId")
    suspend fun deleteDailyById(cityId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherApiResponse(vararg weatherApiResponse: WeatherApiResponse)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrent(current: Current)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDaily(daily: Daily)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeelLike(feelsLike: FeelsLike)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourly(hourly: Hourly)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemperature(temp: Temp)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: Weather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(vararg city: City)

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRain(rainEntity: RainEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnow(snowEntity: SnowEntity)*/

    @Transaction
    @Query("SELECT * FROM api_response WHERE cityId=:id")
    suspend fun getWeatherApiResponseByCityId(id: Int): WeatherApiResponseWithRelations?

    @Transaction
    @Query("SELECT * FROM api_response WHERE cityName=:cityName")
    suspend fun getWeatherApiResponseByCityName(cityName: String): WeatherApiResponseWithRelations?

    @Query("SELECT * FROM city WHERE lower(name) GLOB :name || '*' ORDER BY name ASC LIMIT 10")
    suspend fun getAllCitiesByCityName(name: String): List<City>

    @Query("SELECT * FROM city WHERE id=:id")
    suspend fun getCityById(id: Int): City?


}