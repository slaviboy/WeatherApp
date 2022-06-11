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

import java.util.*

enum class TemperatureType(val value: Int) {

    Celsius(0),
    Fahrenheit(1);

    companion object {
        fun fromInt(value: Int) = TemperatureType.values().first { it.value == value }
    }
}

enum class TimeFormat(val value: Int) {

    AmPm(0),
    Hours24(1);

    companion object {
        fun fromInt(value: Int) = TimeFormat.values().first { it.value == value }
    }
}

enum class UpdateIntervals(val value: Int) {

    Every6Hours(0),
    Every12Hours(1),
    Every24Hours(2);

    companion object {
        fun fromInt(value: Int) = UpdateIntervals.values().first { it.value == value }
    }
}

enum class Language(val value: Int) {

    English(0),
    Bulgarian(1);

    companion object {

        fun fromInt(value: Int) = Language.values().first { it.value == value }

        fun getAsString(language: Language): String {
            return when (language) {
                English -> "en_US"
                Bulgarian -> "bg_BG"
                else -> ""
            }
        }

        fun getAsLocale(language: Language): Locale {
            return when (language) {
                English -> Locale("en", "US")
                Bulgarian -> Locale("bg", "BG")


                else -> Locale("en", "US")
            }
        }
    }
}
