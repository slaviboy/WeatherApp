package com.slaviboy.weather.features.weather.data.local.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.slaviboy.weather.R
import com.slaviboy.weather.core.util.StaticMethods
import com.slaviboy.weather.core.util.StaticMethods.daysMatch
import com.slaviboy.weather.core.util.StaticMethods.get24HourFormat
import com.slaviboy.weather.core.util.StaticMethods.getCurrentWithOffset
import com.slaviboy.weather.core.util.StaticMethods.getSunriseAndSunsetWithOffset
import com.slaviboy.weather.core.util.TimeState
import com.slaviboy.weather.features.weather.data.local.entity.Current
import com.slaviboy.weather.features.weather.data.local.entity.Daily
import com.slaviboy.weather.features.weather.data.local.entity.Hourly
import com.slaviboy.weather.features.weather.data.local.entity.WeatherApiResponse
import com.slaviboy.weather.features.weather.domain.model.CurrentInfo
import com.slaviboy.weather.features.weather.domain.model.WeatherApiResponseInfo

data class WeatherApiResponseWithRelations(

    @Embedded
    val weatherApiResponse: WeatherApiResponse?,

    @Relation(
        entity = Current::class,
        parentColumn = "currentId_",
        entityColumn = "cityId"
    )
    val currentEntity: CurrentWithRelations?,

    @Relation(
        entity = Daily::class,
        parentColumn = "dailyId_",
        entityColumn = "cityId"
    )
    val dailyEntity: List<DailyWithRelations>,

    @Relation(
        entity = Hourly::class,
        parentColumn = "hourlyId_",
        entityColumn = "cityId"
    )
    val hourlyEntity: List<HourlyWithRelations>

) {

    /**
     * @param timeZoneOffset the time zone offset in ms
     * @param currentTime the current time in UTC in ms
     */
    fun WeatherApiResponseInfo.init(timeZoneOffset: Long, currentTime: Long, dailyWithRelations: DailyWithRelations) {

        val tempEntity = dailyWithRelations.temp
        val weatherEntity = dailyWithRelations.weather
        val dailyEntity = dailyWithRelations.daily
        val feelsLikeEntity = dailyWithRelations.feelsLike

        val (sunriseWithOffset, sunsetWithOffset, currentWithOffset) =
            StaticMethods.getTimeWithOffset(
                dailyEntity.sunrise ?: 0L,
                dailyEntity.sunset ?: 0L,
                currentTime,
                timeZoneOffset
            )

        val timeState = dailyEntity.getTimeState(currentTime, timeZoneOffset)
        val isDay = (timeState != TimeState.Night)

        tempEntity.let { temperature ->
            currentInfo.apply {
                minTemperature = temperature.min?.toFloat()
                maxTemperature = temperature.max?.toFloat()
                currentTemperature = temperature.day?.toFloat()
            }
        }

        weatherEntity.firstOrNull()?.let { weather ->

            currentInfo.apply {
                weatherIconResId = weather.getWeatherIconResId(isDay)
                weatherTitle = weather.main ?: CurrentInfo.DEFAULT_WEATHER_TITLE
                weatherSubtitle = weather.description ?: CurrentInfo.DEFAULT_WEATHER_SUBTITLE
                time = dailyEntity.dt
            }

            /*weather.getGradientBackgroundColorsHSV(
                dailyEntity.sunrise ?: 0L,
                dailyEntity.sunset ?: 0L,
                currentTime
            ).let {
                topBackgroundColor = it[0]
                bottomBackgroundColor = it[1]
            }*/
            weather.getGradientBackgroundColorsHSV(isDay).let {
                topBackgroundColor = it[0]
                bottomBackgroundColor = it[1]
            }
        }

        // COMFORT LEVEL
        comfortLevelInfo.apply {
            humidity = dailyEntity.humidity?.toFloat() ?: 0f
            feelsLike = feelsLikeEntity.day?.toFloat()
            uvIndex = dailyEntity.uvi?.toFloat()
        }

        // WIND
        windInfo.apply {
            degree = dailyEntity.windDeg?.toFloat()
            speed = dailyEntity.windSpeed?.toFloat()
        }

        // SUNRISE AND SUNSET
        sunriseSunsetInfo.apply {
            sunrise = sunriseWithOffset
            sunset = sunsetWithOffset
            this.currentTime = currentWithOffset
            moonPhase = dailyEntity.moonPhase?.toFloat()
            setMoonPhaseIconAndTextResId()
        }

    }

    /**
     * Convert to Weather Info from the API response
     * @param londonTime the current London UTC time using the user system clock to remove the offset
     */
    fun toWeatherApiResponseInfo(londonTime: Long): WeatherApiResponseInfo {

        val weatherApiResponseInfo = WeatherApiResponseInfo()
        val timeZoneOffset = (weatherApiResponse?.timezoneOffset ?: 0) * 1000L

        // get if it is currently day, use the first daily for sunrise and sunset times
        val firstDaily = dailyEntity.firstOrNull()?.daily
        val timeState = firstDaily?.getTimeState(londonTime, timeZoneOffset) ?: true

        // 24 Hour Forecast
        val current24Format = londonTime
            .getCurrentWithOffset(timeZoneOffset)
            .get24HourFormat()
        var hourlyStart = -1
        for (i in hourlyEntity.indices) {

            val hourly24Format = (hourlyEntity[i].hourly.dt ?: 0L)
                .getCurrentWithOffset(timeZoneOffset)
                .get24HourFormat()
            if (hourly24Format >= current24Format) {
                hourlyStart = i
                break
            }
        }
        val hourlyEnd = Math.min(hourlyEntity.size, weatherApiResponseInfo.next24HoursForecastInfo.size)
        if (hourlyStart > -1 && hourlyStart < hourlyEnd) {
            for (i in 0 until hourlyEnd) {
                val hourly = hourlyEntity[i + hourlyStart]

                val hourlyTimeState = firstDaily?.getTimeState((hourly.hourly.dt ?: 0L), timeZoneOffset) ?: true
                val resId = when (hourlyTimeState) {
                    TimeState.Day -> hourly.weather.first().getWeatherIconResId(true)
                    TimeState.Night -> hourly.weather.first().getWeatherIconResId(false)
                    TimeState.Sunrise -> R.drawable.ic_sunrise
                    TimeState.Sunset -> R.drawable.ic_sunset
                    else -> R.drawable.ic_none
                }
                weatherApiResponseInfo.next24HoursForecastInfo[i].apply {
                    temperature = hourly.hourly.temp?.toFloat()
                    weatherIconResId = resId
                    time = (hourly.hourly.dt ?: 0L) + timeZoneOffset
                }
            }
        }

        // 4 Days Forecast
        var dailyStart = -1
        for (i in dailyEntity.indices) {

            val current = londonTime
                .getCurrentWithOffset(timeZoneOffset)
            val sunrise = (dailyEntity[i].daily.sunrise ?: 0L)
                .getSunriseAndSunsetWithOffset(timeZoneOffset)
            val sunset = (dailyEntity[i].daily.sunset ?: 0L)
                .getSunriseAndSunsetWithOffset(timeZoneOffset)
            val peek = sunrise + (sunset - sunrise) / 2

            if (daysMatch(current, peek)) {

                // get the next day +1
                dailyStart = i + 1
                break
            }
        }
        val dailyEnd = Math.min(dailyEntity.size, weatherApiResponseInfo.next4DaysForecastInfo.size)
        if (dailyStart > -1 && dailyStart < dailyEnd) {
            for (i in 0 until dailyEnd) {
                val daily = dailyEntity[i + dailyStart]
                val isDay = (timeState != TimeState.Night)
                weatherApiResponseInfo.next4DaysForecastInfo[i].apply {
                    minTemperature = daily.temp.min?.toFloat()
                    maxTemperature = daily.temp.max?.toFloat()
                    weatherIconResId = daily.weather.first().getWeatherIconResId(isDay)
                    time = daily.daily.dt
                }
            }
        }

        if (dailyStart > -1) {
            weatherApiResponseInfo.init(timeZoneOffset, londonTime, dailyEntity[dailyStart])
        }

        /*dailyEntity.let {
            if (it.isNotEmpty()) {

                val tempEntity = it.first().temp
                val weatherEntity = it.first().weather
                val dailyEntity = it.first().daily
                val feelsLikeEntity = it.first().feelsLike

                tempEntity.let { temperature ->
                    weatherApiResponseInfo.currentInfo.apply {
                        minTemperature = temperature.min?.toFloat()
                        maxTemperature = temperature.max?.toFloat()
                        currentTemperature = temperature.day?.toFloat()
                    }
                }

                weatherEntity.let { list ->
                    if (list.isNotEmpty()) {
                        list.first().let { weather ->

                            weatherApiResponseInfo.currentInfo.apply {
                                weatherIconResId = weather.getWeatherIconResId()
                                weatherTitle = weather.main ?: CurrentInfo.DEFAULT_WEATHER_TITLE
                                weatherSubtitle = weather.description ?: CurrentInfo.DEFAULT_WEATHER_SUBTITLE
                                time = dailyEntity.dt
                            }

                            val currentWeatherBackgroundColors = weather.getGradientBackgroundColorsHSV(
                                dailyEntity.sunrise ?: 0L,
                                dailyEntity.sunset ?: 0L,
                                timeAtRequest
                            )
                            weatherApiResponseInfo.topBackgroundColor = currentWeatherBackgroundColors[0]
                            weatherApiResponseInfo.bottomBackgroundColor = currentWeatherBackgroundColors[1]
                        }
                    }
                }

                weatherApiResponseInfo.comfortLevelInfo.apply {
                    humidity = dailyEntity.humidity?.toFloat() ?: 0f
                    feelsLike = feelsLikeEntity.day?.toFloat()
                    uvIndex = dailyEntity.uvi?.toFloat()
                }

                weatherApiResponseInfo.windInfo.apply {
                    degree = dailyEntity.windDeg?.toFloat()
                    speed = dailyEntity.windSpeed?.toFloat()
                }

                weatherApiResponseInfo.sunriseSunsetInfo.apply {
                    sunrise = dailyEntity.sunrise
                    sunset = dailyEntity.sunset
                    currentTime = timeAtRequest
                    moonPhase = dailyEntity.moonPhase?.toFloat()
                    setMoonPhaseIconAndTextResId()
                }

            }

            if (it.size >= 4) {
                weatherApiResponseInfo.next4DaysForecastInfo.forEachIndexed { i, dailyInfo ->
                    if (it[i].weather.isNotEmpty()) {
                        dailyInfo.apply {
                            minTemperature = it[i].temp.min?.toFloat()
                            maxTemperature = it[i].temp.max?.toFloat()
                            weatherIconResId = it[i].weather.first().getWeatherIconResId()
                            this.time = it[i].daily.dt
                        }
                    }
                }
            }
        }

        hourlyEntity.let {

            weatherApiResponseInfo.next24HoursForecastInfo.forEachIndexed { i, hourlyInfo ->
                if (it[i].weather.isNotEmpty()) {
                    hourlyInfo.apply {
                        temperature = it[i].hourly.temp?.toFloat()
                        weatherIconResId = it[i].weather.first().getWeatherIconResId()
                        time = it[i].hourly.dt
                    }
                }
            }
        }*/

        return weatherApiResponseInfo
    }

    companion object {


    }
}