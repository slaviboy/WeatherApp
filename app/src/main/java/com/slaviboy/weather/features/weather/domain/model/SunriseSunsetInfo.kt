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
package com.slaviboy.weather.features.weather.domain.model

import com.slaviboy.weather.R
import com.slaviboy.weather.core.util.StaticMethods.get24HourFormat
import com.slaviboy.weather.core.util.StaticMethods.getHourAsString
import com.slaviboy.weather.core.util.TimeFormat

/**
 * Class for the sunrise and sunset box
 * @param sunrise the time of the sunrise as Long
 * @param sunset the time of the sunset as Long
 * @param currentTime current time as Long
 * @param moonPhase 0 and 1 are 'new moon', 0.25 is 'first quarter moon', 0.5 is 'full moon' and 0.75 is 'last quarter moon'
 */
data class SunriseSunsetInfo(
    var sunrise: Long? = null,
    var sunset: Long? = null,
    var currentTime: Long? = null,
    var moonPhase: Float? = null
) {

    var moonPhaseStrResId: Int    // the res id for the moon image
    var moonIconSrc: Int          // the res id for the moon icon

    init {

        // fit the current time in range [sunrise, sunset]
        moonPhaseStrResId = R.string.na
        moonIconSrc = R.drawable.ic_none
        setMoonPhaseIconAndTextResId()
    }

    fun getSunriseHourAsString(format: TimeFormat) = sunrise.getHourAsString(format)
    fun getSunsetHourAsString(format: TimeFormat) = sunset.getHourAsString(format)

    /**
     * Method that returns the sun angle [0,180]
     */
    fun sunAngle(): Float {
        val sunrise = (sunrise ?: 0L).get24HourFormat()
        val sunset = (sunset ?: 0L).get24HourFormat()
        val currentTime = (currentTime ?: 0L).get24HourFormat()
        val currentInRange = when {
            currentTime < sunrise -> sunrise
            currentTime > sunset -> sunset
            else -> currentTime
        }
        return ((currentInRange - sunrise) / (sunset - sunrise).toFloat()) * 180f
    }

    /**
     * Method that sets the res id for the moon phase icon and text
     */
    fun setMoonPhaseIconAndTextResId() {

        findMoonPhaseIndex(moonPhase)?.let {
            moonPhaseStrResId = getMoonPhaseStringResId(it)
            moonIconSrc = getMoonPhaseIconResId(it)
        }
    }

    companion object {

        /**
         * Get the moon phase index in range [-1,7]
         */
        fun findMoonPhaseIndex(moonPhase: Float?): Int? {
            moonPhase ?: return null

            // check from [-1 to 7]
            for (i in -1..7) {
                val fragment = 1f / 8f
                val start = fragment / 2f

                val totalStart = start + fragment * (i)
                if (moonPhase > totalStart && moonPhase <= totalStart + fragment) {
                    return i
                }
            }
            return null
        }

        /**
         * Get the moon phase icon resource id
         */
        fun getMoonPhaseIconResId(moonIndex: Int): Int {

            return when (moonIndex) {
                0 -> R.drawable.ic_moon2
                1 -> R.drawable.ic_moon3
                2 -> R.drawable.ic_moon4
                3 -> R.drawable.ic_moon5
                4 -> R.drawable.ic_moon6
                5 -> R.drawable.ic_moon7
                6 -> R.drawable.ic_moon8
                7, -1 -> R.drawable.ic_moon1
                else -> R.drawable.ic_none
            }
        }

        /**
         * Get the moon phase string resource id
         */
        fun getMoonPhaseStringResId(moonIndex: Int): Int {

            return when (moonIndex) {
                0 -> R.string.waxing_crescent
                1 -> R.string.first_quarter
                2 -> R.string.waxing_gibbous
                3 -> R.string.full_moon
                4 -> R.string.waning_gibbous
                5 -> R.string.third_quarter
                6 -> R.string.waning_crescent
                7, -1 -> R.string.new_moon
                else -> R.string.na
            }
        }
    }

}