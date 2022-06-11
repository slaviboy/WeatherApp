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
package com.slaviboy.weather.features.weather.domain.usecase

import com.slaviboy.weather.core.util.Result
import com.slaviboy.weather.features.weather.domain.model.WeatherApiResponseInfo
import kotlinx.coroutines.flow.Flow

interface GetWeatherUseCase {

    /**
     * Return the city weather as a flow by latitude and longitude (AKA geographic coordinates)
     * @param id the city id
     * @param name the city name
     * @param lat the latitude of the city location
     * @param lon the longitude of the city location
     * @param requestCachedData if we explicitly want to get a cached data instead of triggering new API request
     */
    operator fun invoke(id: Int, name: String, lat: Float, lon: Float, requestCachedData: Boolean): Flow<Result<WeatherApiResponseInfo>>

}