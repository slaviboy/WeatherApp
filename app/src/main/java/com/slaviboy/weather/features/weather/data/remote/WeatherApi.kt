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
package com.slaviboy.weather.features.weather.data.remote

import com.slaviboy.weather.BuildConfig
import com.slaviboy.weather.core.util.Language
import com.slaviboy.weather.features.weather.data.remote.dto.WeatherApiResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * The API to the site that is using Retrofit to retrieve the json response
 * https://openweathermap.org/api
 */
interface WeatherApi {

    /**
     * You can make an API call by geographic coordinates
     * Free API quota 60 calls/minute (1,000,000 calls/month)
     * @param lat the latitude
     * @param lon the longitude
     * @param language the language of the response
     * @param units the units of temperature Fahrenheit(imperial), Celsius(metric), Kelvin is used by default
     */
    @GET("/data/2.5/onecall?appid=${BuildConfig.WEATHER_APP_KEY}&exclude=minutely,alerts")
    suspend fun getWeatherApiResponseByGeographicCoordinates(
        @Query("lat") lat: Float = 0f,
        @Query("lon") lon: Float = 0f,
        @Query("lang") language: String = SELECTED_LANGUAGE_API,
        @Query("units") units: String = "metric"
    ): WeatherApiResponseDTO

    companion object {

        const val BASE_URL = "https://api.openweathermap.org/"

        // when the user request weather from his location, rather than particular city those variables are used for city name and id
        const val GEOGRAPHIC_COORDINATES_CITY_ID = 0
        const val GEOGRAPHIC_COORDINATES_CITY_NAME = "gps"

        // languages that are supported by the API
        const val AF = "af" // Afrikaans
        const val AL = "al" // Albanian
        const val AR = "ar" // Arabic
        const val AZ = "az" // Azerbaijani
        const val BG = "bg" // Bulgarian
        const val CA = "ca" // Catalan
        const val CZ = "cz" // Czech
        const val DA = "da" // Danish
        const val DE = "de" // German
        const val EL = "el" // Greek
        const val EN = "en" // English
        const val EU = "eu" // Basque
        const val FA = "fa" // Persian (Farsi)
        const val FI = "fi" // Finnish
        const val FR = "fr" // French
        const val GL = "gl" // Galician
        const val HE = "he" // Hebrew
        const val HI = "hi" // Hindi
        const val HR = "hr" // Croatian
        const val HU = "hu" // Hungarian
        const val Id = "id" // Indonesian
        const val IT = "it" // Italian
        const val JA = "ja" // Japanese
        const val KR = "kr" // Korean
        const val LA = "la" // Latvian
        const val LT = "lt" // Lithuanian
        const val MK = "mk" // Macedonian
        const val NO = "no" // Norwegian
        const val NL = "nl" // Dutch
        const val PL = "pl" // Polish
        const val PT = "pt" // Portuguese
        const val PT_BR = "pt_br" // Portuguese Brazil
        const val RO = "ro" // Romanian
        const val RU = "ru" // Russian
        const val SE = "sv, se" // Swedish
        const val SL = "sl" // Slovak
        const val SP = "sp, es" // Spanish
        const val SR = "sr" // Serbian
        const val TH = "th" // Thai
        const val TR = "tr" // Turkish
        const val UA = "ua, uk" // Ukrainian
        const val VI = "sr" // Vietnamese
        const val ZH_CH = "zh_cn" // Chinese Simplified
        const val ZH_TW = "zh_tw" // Chinese Traditional
        const val ZU = "zu" // Zulu

        var SELECTED_LANGUAGE_API = EN

        /**
         * Set the language for the API request, to set the language to matches the language from the settings
         */
        fun setSupportedLanguageAPI(language: Language) {
            SELECTED_LANGUAGE_API = when (language) {
                Language.English -> EN
                Language.Bulgarian -> BG
                else -> EN
            }
        }
    }
}