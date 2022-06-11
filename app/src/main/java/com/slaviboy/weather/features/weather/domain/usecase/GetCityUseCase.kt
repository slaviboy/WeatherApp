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
import kotlinx.coroutines.flow.Flow

interface GetCityUseCase {

    /**
     * Get list of all cities that match the substring
     * @param cityName the name of the city, that can be substring: 'sa' -> San Francisco, Sacramento, Santa Ana...
     */
    operator fun invoke(cityName: String): Flow<Result<List<City>>>

    /**
     * Get city by its id
     * @param cityId the id of the city for example 5128638(the id of Ney York)
     */
    operator fun invoke(cityId: Int): Flow<Result<City>>
}