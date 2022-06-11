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
package com.slaviboy.weather.features.weather.data.remote.dto

import retrofit2.http.Field

/**
 * DTO (Data Transfer Object) matching expected JSON from Weather API
 * @param v1h Snow volume for the last 1 hour, mm
 * @param v3h Snow volume for the last 3 hours, mm
 */
class SnowDTO(
    @Field("1h")
    val v1h: Double = 0.0,
    @Field("3h")
    val v3h: Double = 0.0
)