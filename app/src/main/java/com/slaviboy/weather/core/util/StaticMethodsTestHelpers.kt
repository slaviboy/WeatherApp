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

import com.slaviboy.weather.core.util.StaticMethods.maskToLong
import com.slaviboy.weather.features.weather.data.local.entity.*

object StaticMethodsTestHelpers {

    fun randomFloat(rangeMin: Float = -100f, rangeMax: Float = 100f): Float {
        return (rangeMin + (rangeMax - rangeMin) * Math.random()).toFloat()
    }

    fun randomInt(rangeMin: Int = -100, rangeMax: Int = 100): Int {
        return (rangeMin + (rangeMax - rangeMin) * Math.random()).toInt()
    }

    fun randomLong(rangeMin: Long = -100L, rangeMax: Long = 100L): Long {
        return (rangeMin + (rangeMax - rangeMin) * Math.random()).toLong()
    }

    fun randomDouble(rangeMin: Double = -100.0, rangeMax: Double = 100.0): Double {
        return rangeMin + (rangeMax - rangeMin) * Math.random()
    }

    fun randomString(stringCharacterSize: Int = 5): String {

        // character to generate random string
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return (1..stringCharacterSize)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    /**
     * Generate random City, only set the id and the name
     * @param id city id
     * @param name city name
     */
    fun randomCity(id: Int, name: String): City {
        return City(
            id = id,
            name = name,
            state = null,
            country = randomString(),
            lat = randomFloat(),
            lon = randomFloat()
        )
    }

    /**
     * Generate random Current, only set the id and the maskMultiplier
     * @param id city id
     * @param maskMultiplier multiplier used to determine the Weather id
     */
    fun randomCurrent(id: Int, maskMultiplier: Int): Current {
        return Current(
            cityId = id,
            maskMultiplier = maskMultiplier,
            dt = randomLong(),
            sunrise = randomLong(),
            sunset = randomLong(),
            temp = randomDouble(),
            feelsLike = randomDouble(),
            pressure = randomInt(),
            humidity = randomInt(),
            dewPoint = randomDouble(),
            clouds = randomInt(),
            uvi = randomDouble(),
            visibility = randomInt(),
            windSpeed = randomDouble(),
            windDeg = randomInt()
        )
    }

    /**
     * Generate random Daily, only set the id and the maskMultiplier
     * @param id city id
     * @param maskMultiplier multiplier used to determine the Weather, Temp and FeelsLike id
     */
    fun randomDaily(id: Int, maskMultiplier: Int): Daily {
        return Daily(
            cityId = id,
            maskMultiplier = maskMultiplier,
            dt = randomLong(),
            sunrise = randomLong(),
            sunset = randomLong(),
            moonrise = randomLong(),
            moonset = randomLong(),
            moonPhase = randomDouble(),
            pressure = randomInt(),
            humidity = randomInt(),
            dewPoint = randomDouble(),
            windSpeed = randomDouble(),
            windGust = randomDouble(),
            windDeg = randomInt(),
            clouds = randomInt(),
            uvi = randomDouble(),
            pop = randomDouble(),
            rain = randomDouble(),
            snow = randomDouble()
        )
    }

    /**
     * Generate random Hourly, only set the id and the maskMultiplier
     * @param id city id
     * @param maskMultiplier multiplier used to determine the Weather id
     */
    fun randomHourly(id: Int, maskMultiplier: Int): Hourly {
        return Hourly(
            cityId = id,
            maskMultiplier = maskMultiplier,
            dt = randomLong(),
            temp = randomDouble(),
            feelsLike = randomDouble(),
            pressure = randomInt(),
            humidity = randomInt(),
            dewPoint = randomDouble(),
            uvi = randomDouble(),
            clouds = randomInt(),
            visibility = randomInt(),
            windSpeed = randomDouble(),
            windGust = randomDouble(),
            windDeg = randomInt(),
            pop = randomDouble()
        )
    }

    fun randomWeatherApiResponse(id: Int, name: String): WeatherApiResponse {
        return WeatherApiResponse(
            cityId = id,
            cityName = name,
            lat = randomDouble(),
            lon = randomDouble(),
            timezone = randomString(),
            timezoneOffset = randomInt()
        )
    }

    fun randomTemp(id: Int, maskMultiplier: Int): Temp {
        return Temp(
            cityId = id,
            maskId = id.maskToLong(maskMultiplier),
            morn = randomDouble(),
            day = randomDouble(),
            eve = randomDouble(),
            night = randomDouble(),
            min = randomDouble(),
            max = randomDouble()
        )
    }

    fun randomFeelsLike(id: Int, maskMultiplier: Int): FeelsLike {
        return FeelsLike(
            cityId = id,
            maskId = id.maskToLong(maskMultiplier),
            morn = randomDouble(),
            day = randomDouble(),
            eve = randomDouble(),
            night = randomDouble()
        )
    }

    fun randomWeather(id: Int, maskMultiplier: Int): Weather {
        return Weather(
            cityId = id,
            maskId = id.maskToLong(maskMultiplier),
            descriptionId = randomInt(),
            main = randomString(),
            description = randomString(),
            icon = randomString()
        )
    }

}