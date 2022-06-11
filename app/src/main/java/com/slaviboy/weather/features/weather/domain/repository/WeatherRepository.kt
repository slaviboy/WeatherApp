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
package com.slaviboy.weather.features.weather.domain.repository

import com.slaviboy.weather.core.util.Result
import com.slaviboy.weather.features.weather.data.local.entity.City
import com.slaviboy.weather.features.weather.domain.model.WeatherApiResponseInfo
import kotlinx.coroutines.flow.Flow

/**
 * We are using interface since, that way we can implement it in our test cases, for unit testing
 */
interface WeatherRepository {

    // get city weather from API or local DB
    fun getWeatherApiResponse(id: Int, name: String, lat: Float, lon: Float, requestCachedData: Boolean): Flow<Result<WeatherApiResponseInfo>>

    // get city from local DB
    fun getAllCitiesByCityName(cityName: String): Flow<Result<List<City>>>
    fun getCityById(cityId: Int): Flow<Result<City>>

}