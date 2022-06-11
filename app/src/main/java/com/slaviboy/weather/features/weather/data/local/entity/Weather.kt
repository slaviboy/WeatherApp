package com.slaviboy.weather.features.weather.data.local.entity

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slaviboy.weather.R
import com.slaviboy.weather.core.util.StaticMethods
import com.slaviboy.weather.core.util.StaticMethods.mapTo

@Entity(tableName = "weather")
data class Weather(

    @PrimaryKey(autoGenerate = true) var id: Int? = null,

    var cityId: Int = 0,
    var maskId: Long = 0L,

    val descriptionId: Int? = null,
    val main: String? = null,
    val description: String? = null,
    val icon: String? = null
) {

    fun getWeatherIconResId(isDay: Boolean): Int {
        return if (descriptionId != null && icon != null) {
            getWeatherIconResId(descriptionId, getActualTimeWeatherIcon(isDay, icon))
        } else R.drawable.ic_none
    }

    fun getGradientBackgroundColorsHSV(isDay: Boolean): List<Color> {

        val hsv = if (descriptionId != null && icon != null) {
            getGradientBackgroundColorsHSV(descriptionId, getActualTimeWeatherIcon(isDay, icon))
        } else clearBackgroundColors

        val backgroundGradientTopColor = StaticMethods.hsl(hsv[0], hsv[1], hsv[2])
        val backgroundGradientBottomColor = StaticMethods.hsl(hsv[3], hsv[4], hsv[5])

        return listOf(backgroundGradientTopColor, backgroundGradientBottomColor)
    }

    /**
     * Get hte gradient baclground color depending on current time and its position between [sunrise, sunset]
     */
    fun getGradientBackgroundColorsHSV(sunrise: Long, sunset: Long, currentTime: Long): List<Color> {
        val hsv = if (descriptionId != null && icon != null) {
            getGradientBackgroundColorsHSV(descriptionId, icon)
        } else clearBackgroundColors

        return hsv.getGradientBackgroundColorsBySunriseSunsetToCurrentTime(sunrise, sunset, currentTime)
    }

    companion object {

        val clearBackgroundColors = floatArrayOf(
            225f, 0.90f, 0.19f, // top background color HSL
            212f, 0.50f, 0.45f  // bottom background color HSL
        )
        val cloudyBackgroundColor = floatArrayOf(
            222f, 0.85f, 0.10f,
            212f, 0.41f, 0.37f
        )
        val rainyBackgroundColor = floatArrayOf(
            190f, 0.43f, 0.11f,
            190f, 0.21f, 0.35f
        )
        val snowyBackgroundColor = floatArrayOf(
            205f, 0.60f, 0.15f,
            202f, 0.28f, 0.41f
        )

        val clearNightBackgroundColor = floatArrayOf(
            210f, 1f, 0.01f,
            211f, 1f, 0.15f
        )

        /**
         * Get the opposite icon, if the icon is for the 'day' then return 'night' icon and vice versa
         * @param isDay if it is currently day time
         */
        fun getActualTimeWeatherIcon(isDay: Boolean, icon: String): String {

            return when (icon) {

                // clear sky
                "01d" -> if (isDay) "01d" else "01n"
                "01n" -> if (!isDay) "01n" else "01d"

                // few clouds
                "02d" -> if (isDay) "02d" else "02n"
                "02n" -> if (!isDay) "02n" else "02d"

                // scattered clouds
                "03d" -> if (isDay) "03d" else "03n"
                "03n" -> if (!isDay) "03n" else "03d"

                // broken clouds
                "04d" -> if (isDay) "04d" else "04n"
                "04n" -> if (!isDay) "04n" else "04d"

                // shower rain
                "09d" -> if (isDay) "09d" else "09n"
                "09n" -> if (!isDay) "09n" else "09d"

                // rain
                "10d" -> if (isDay) "10d" else "10n"
                "10n" -> if (!isDay) "10n" else "10d"

                // thunderstorm
                "11d" -> if (isDay) "11d" else "11n"
                "11n" -> if (!isDay) "11n" else "11d"

                // snow
                "13d" -> if (isDay) "13d" else "13n"
                "13n" -> if (!isDay) "13n" else "13d"

                // mist
                "50d" -> if (isDay) "50d" else "50n"
                "50n" -> if (!isDay) "50n" else "50d"

                else -> if (isDay) "01d" else "01n"
            }
        }

        /**
         * Get the weather icon resource id
         * @param descriptionId the description id used if we want to use more specific weather icons
         * @param icon the icon type as string
         */
        fun getWeatherIconResId(descriptionId: Int, icon: String): Int {

            return when (icon) {

                // clear sky
                "01d" -> R.drawable.ic_weather8
                "01n" -> R.drawable.ic_weather11

                // few clouds
                "02d" -> R.drawable.ic_weather22
                "02n" -> R.drawable.ic_weather23

                // scattered clouds
                "03d" -> R.drawable.ic_weather21
                "03n" -> R.drawable.ic_weather21

                // broken clouds
                "04d" -> R.drawable.ic_weather24
                "04n" -> R.drawable.ic_weather24

                // shower rain
                "09d" -> R.drawable.ic_weather27
                "09n" -> R.drawable.ic_weather27

                // rain
                "10d" -> R.drawable.ic_weather25
                "10n" -> R.drawable.ic_weather26

                // thunderstorm
                "11d" -> R.drawable.ic_weather28
                "11n" -> R.drawable.ic_weather28

                // snow
                "13d" -> R.drawable.ic_weather6
                "13n" -> R.drawable.ic_weather6

                // mist
                "50d" -> R.drawable.ic_weather29
                "50n" -> R.drawable.ic_weather29

                else -> R.drawable.ic_weather8
            }
        }

        /**
         * Get the hsv values for the background gradient color depending on the weather
         * @param descriptionId the description id used if we want to use more specific weather icons
         * @param icon the icon type as string
         * @return array with two pairs of 3 float values for the top and bottom gradient background colors HSL(hue, saturation, lightness)
         */
        fun getGradientBackgroundColorsHSV(descriptionId: Int, icon: String): FloatArray {

            return when (icon) {

                // clear sky
                "01d" -> clearBackgroundColors
                "01n" -> clearNightBackgroundColor

                // few clouds
                "02d" -> clearBackgroundColors
                "02n" -> clearNightBackgroundColor

                // scattered clouds
                "03d" -> clearBackgroundColors
                "03n" -> clearNightBackgroundColor

                // broken clouds
                "04d" -> cloudyBackgroundColor
                "04n" -> clearNightBackgroundColor

                // shower rain
                "09d" -> rainyBackgroundColor
                "09n" -> clearNightBackgroundColor

                // rain
                "10d" -> clearBackgroundColors
                "10n" -> clearNightBackgroundColor

                // thunderstorm
                "11d" -> rainyBackgroundColor
                "11n" -> clearNightBackgroundColor

                // snow
                "13d" -> snowyBackgroundColor
                "13n" -> clearNightBackgroundColor

                // mist
                "50d" -> cloudyBackgroundColor
                "50n" -> clearNightBackgroundColor

                else -> clearBackgroundColors
            }
        }


        /**
         * Set the background color depending on the current time, this will back the background color
         * darker using the HSL(hus, saturation, lightness) color model
         */
        fun FloatArray.getGradientBackgroundColorsBySunriseSunsetToCurrentTime(sunrise: Long, sunset: Long, currentTime: Long): List<Color> {

            // the position of the sun [0 - night(sunrise), 0.5 - day, 1 - night(sunset)]
            val currentInRange = when {
                currentTime < sunrise -> sunrise
                currentTime > sunset -> sunset
                else -> currentTime
            }
            val sunFactor = ((currentInRange - sunrise) / (sunset - sunrise).toFloat())

            val minLightness = 0.2f
            val maxLightness = 1.0f
            val lightness = if (sunFactor < 0.5f) {
                sunFactor.mapTo(0f, 0.5f, minLightness, maxLightness) // from [0(sunrise), 0.5(day)]
            } else sunFactor.mapTo(0.5f, 1f, maxLightness, minLightness) // from [0.5(day), 1(sunset)]

            val hsv = this
            val backgroundGradientTopColor = StaticMethods.hsl(hsv[0], hsv[1], hsv[2] * lightness)
            val backgroundGradientBottomColor = StaticMethods.hsl(hsv[3], hsv[4], hsv[5] * lightness)

            return listOf(backgroundGradientTopColor, backgroundGradientBottomColor)
        }
    }
}