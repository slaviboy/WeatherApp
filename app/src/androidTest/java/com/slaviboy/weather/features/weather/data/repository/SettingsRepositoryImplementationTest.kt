package com.slaviboy.weather.features.weather.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.slaviboy.weather.core.util.Language
import com.slaviboy.weather.core.util.TemperatureType
import com.slaviboy.weather.core.util.TimeFormat
import com.slaviboy.weather.core.util.UpdateIntervals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class SettingsRepositoryImplementationTest {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var context: Context
    private lateinit var repository: SettingsRepositoryImplementation

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        repository = SettingsRepositoryImplementation(sharedPreferences)
    }

    @Test
    fun set_getTemperatureType() = runBlockingTest {

        fun setAndCheck(expectedTemperatureType: TemperatureType) {
            repository.setTemperatureType(expectedTemperatureType)
            assertThat(repository.getTemperatureType()).isEqualTo(expectedTemperatureType)
        }

        setAndCheck(TemperatureType.Fahrenheit)
        setAndCheck(TemperatureType.Celsius)
    }

    @Test
    fun set_getTimeFormat() = runBlockingTest {

        fun setAndCheck(timeFormat: TimeFormat) {
            repository.setTimeFormat(timeFormat)
            assertThat(repository.getTimeFormat()).isEqualTo(timeFormat)
        }

        setAndCheck(TimeFormat.AmPm)
        setAndCheck(TimeFormat.Hours24)
    }

    @Test
    fun set_getUpdateIntervals() = runBlockingTest {

        fun setAndCheck(updateIntervals: UpdateIntervals) {
            repository.setUpdateIntervals(updateIntervals)
            assertThat(repository.getUpdateIntervals()).isEqualTo(updateIntervals)
        }

        setAndCheck(UpdateIntervals.Every6Hours)
        setAndCheck(UpdateIntervals.Every12Hours)
        setAndCheck(UpdateIntervals.Every24Hours)
    }

    @Test
    fun set_getUseCurrentLocation() = runBlockingTest {

        fun setAndCheck(useCurrentLocation: Boolean) {
            repository.setUseCurrentLocation(useCurrentLocation)
            assertThat(repository.getUseCurrentLocation()).isEqualTo(useCurrentLocation)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getSelectedCityId() = runBlockingTest {

        fun setAndCheck(cityId: Int) {
            repository.setSelectedCityId(cityId)
            assertThat(repository.getSelectedCityId()).isEqualTo(cityId)
        }

        setAndCheck(37938)
        setAndCheck(265672)
    }

    @Test
    fun set_getLanguage() = runBlockingTest {

        fun setAndCheck(language: Language) {
            repository.setLanguage(language)
            assertThat(repository.getLanguage()).isEqualTo(language)
        }

        setAndCheck(Language.English)
        setAndCheck(Language.Bulgarian)
    }

    @Test
    fun set_getShowWindBox() = runBlockingTest {

        fun setAndCheck(showWindBox: Boolean) {
            repository.setShowWindBox(showWindBox)
            assertThat(repository.getShowWindBox()).isEqualTo(showWindBox)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getSunriseAndSunsetBox() = runBlockingTest {

        fun setAndCheck(showSunriseAndSunsetBox: Boolean) {
            repository.setShowSunriseAndSunsetBox(showSunriseAndSunsetBox)
            assertThat(repository.getShowSunriseAndSunsetBox()).isEqualTo(showSunriseAndSunsetBox)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getShowComfortLevelBox() = runBlockingTest {

        fun setAndCheck(showComfortLevelBox: Boolean) {
            repository.setShowComfortLevelBox(showComfortLevelBox)
            assertThat(repository.getShowComfortLevelBox()).isEqualTo(showComfortLevelBox)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getShowNext24HoursForecastBox() = runBlockingTest {

        fun setAndCheck(showNext24HoursForecastBox: Boolean) {
            repository.setShowNext24HoursForecastBox(showNext24HoursForecastBox)
            assertThat(repository.getShowNext24HoursForecastBox()).isEqualTo(showNext24HoursForecastBox)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getShowNext4DaysForecastBox() = runBlockingTest {

        fun setAndCheck(showNext4DaysForecastBox: Boolean) {
            repository.setShowNext4DaysForecastBox(showNext4DaysForecastBox)
            assertThat(repository.getShowNext4DaysForecastBox()).isEqualTo(showNext4DaysForecastBox)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getEnableAnimation() = runBlockingTest {

        fun setAndCheck(enableAnimation: Boolean) {
            repository.setEnableAnimation(enableAnimation)
            assertThat(repository.getEnableAnimation()).isEqualTo(enableAnimation)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getCurrentLocationLat() = runBlockingTest {

        fun setAndCheck(currentLocationLat: Float) {
            repository.setCurrentLocationLat(currentLocationLat)
            assertThat(repository.getCurrentLocationLat()).isEqualTo(currentLocationLat)
        }

        setAndCheck(234.23f)
        setAndCheck(11.491f)
    }

    @Test
    fun set_getCurrentLocationLon() = runBlockingTest {

        fun setAndCheck(currentLocationLon: Float) {
            repository.setCurrentLocationLon(currentLocationLon)
            assertThat(repository.getCurrentLocationLon()).isEqualTo(currentLocationLon)
        }

        setAndCheck(234.23f)
        setAndCheck(11.491f)
    }

    @Test
    fun set_getLastUpdatedTime() = runBlockingTest {

        fun setAndCheck(lastUpdateTime: Long) {
            repository.setLastUpdatedTime(lastUpdateTime)
            assertThat(repository.getLastUpdatedTime()).isEqualTo(lastUpdateTime)
        }

        setAndCheck(3453242L)
        setAndCheck(121984456L)
    }

}