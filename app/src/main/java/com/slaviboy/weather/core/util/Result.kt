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
package com.slaviboy.weather.core.util

/**
 * Sealed class for the result response from the API/Local Db request
 * @param data the weather data object
 * @param message the message in case of error
 * @param isCachedData if we retrieved a cached data instead of fresh API data
 */
sealed class Result<T>(val data: T? = null, val message: Int? = null, val isCachedData: Boolean = true) {
    class Loading<T>(data: T? = null) : Result<T>(data)
    class Success<T>(data: T?, isCachedData: Boolean) : Result<T>(data = data, isCachedData = isCachedData)
    class Error<T>(message: Int, data: T? = null) : Result<T>(data, message)
}