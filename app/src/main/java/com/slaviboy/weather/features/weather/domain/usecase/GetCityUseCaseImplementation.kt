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
import com.slaviboy.weather.features.weather.data.local.entity.City
import com.slaviboy.weather.features.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCityUseCaseImplementation @Inject constructor(
    private val repository: WeatherRepository
) : GetCityUseCase {

    /**
     * Get city by its name
     * @param cityName the name of the city for example 'New York'
     */
    override operator fun invoke(cityName: String): Flow<Result<List<City>>> {
        if (cityName.isBlank()) {
            return flow { }
        }
        return repository.getAllCitiesByCityName(cityName)
    }

    /**
     * Get city by its id
     * @param cityId the id of the city for example 5128638(the id of Ney York)
     */
    override operator fun invoke(cityId: Int): Flow<Result<City>> {
        if (cityId == -1) {
            return flow { }
        }
        return repository.getCityById(cityId)
    }
}