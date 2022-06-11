package com.slaviboy.weather.features.weather.presentation.state

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.slaviboy.weather.core.util.Language
import com.slaviboy.weather.core.util.TemperatureType
import com.slaviboy.weather.core.util.TimeFormat
import com.slaviboy.weather.core.util.UpdateIntervals
import com.slaviboy.weather.features.weather.data.local.entity.City
import com.slaviboy.weather.features.weather.domain.model.WeatherApiResponseInfo
import com.slaviboy.weather.features.weather.presentation.CityWeatherViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.coroutines.ContinuationInterceptor

/**
 * Allows launching suspend function in Main coroutine
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainCoroutineRule : TestWatcher(), TestCoroutineScope by TestCoroutineScope() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(this.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
class CityWeatherViewModelTest {

    private lateinit var viewModel: CityWeatherViewModel
    private lateinit var fakeGetWeatherUseCase: FakeGetWeatherUseCase
    private lateinit var fakeGetCityUseCase: FakeGetCityUseCase
    private lateinit var fakeSettings: FakeSettingRepository

    private lateinit var correctCity: City
    private lateinit var incorrectCity: City

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {

        correctCity = FakeGetWeatherUseCase.fakeCity.copy()
        incorrectCity = City()

        fakeGetWeatherUseCase = FakeGetWeatherUseCase()
        fakeGetCityUseCase = FakeGetCityUseCase()
        fakeSettings = FakeSettingRepository()
        viewModel = CityWeatherViewModel(fakeGetWeatherUseCase, fakeGetCityUseCase, fakeSettings)
    }

    @Test
    fun getAllCitiesByName_Success() = runBlockingTest {

        viewModel.getAllCitiesByName(FakeGetCityUseCase.fakeCityName)

        assertThat(viewModel.resultAllCities.value).isEqualTo(
            CityState(allCities = fakeGetCityUseCase.fakeCities)
        )
    }

    @Test
    fun getAllCitiesByName_Fail() = runBlockingTest {

        viewModel.getAllCitiesByName("")

        assertThat(viewModel.resultAllCities.value).isNotEqualTo(
            CityState(allCities = fakeGetCityUseCase.fakeCities)
        )
        assertThat(viewModel.resultAllCities.value).isEqualTo(
            CityState(allCities = listOf())
        )
    }

    @Test
    fun getCityById_Success() = runBlockingTest {

        viewModel.getCityById(FakeGetCityUseCase.fakeCityId)

        assertThat(viewModel.resultCityById.value).isEqualTo(fakeGetCityUseCase.fakeCity)
    }

    @Test
    fun getCityById_Fail() = runBlockingTest {

        viewModel.getCityById(-1)

        assertThat(viewModel.resultCityById.value).isNotEqualTo(fakeGetCityUseCase.fakeCity)
        assertThat(viewModel.resultCityById.value).isEqualTo(null)
    }

    @Test
    fun requestCityWeatherByCity_Success() = runBlockingTest {

        // once we request the expected city we should receive the emitted fakeWeatherApiResponseInfo to the view model
        viewModel.setWasCityOrLocationUpdatedFromSettings(true)
        viewModel.requestWeatherByCity(city = correctCity, requestCachedData = true, delayTime = 0L)

        val state = viewModel.weatherApiResponseApiResponseInfo.value
        val info = state.weatherApiResponseInfo
        assertThat(info).isEqualTo(fakeGetWeatherUseCase.fakeWeatherApiResponseInfo)
        assertThat(info).isNotEqualTo(WeatherApiResponseInfo())
    }

    @Test
    fun requestCityWeatherByCity_Fail() = runBlockingTest {

        // once we request the NOT expected city we should receive the default WeatherApiResponseInfo() object
        viewModel.requestWeatherByCity(city = incorrectCity, requestCachedData = true, delayTime = 0L)

        val state = viewModel.weatherApiResponseApiResponseInfo.value
        val info = state.weatherApiResponseInfo
        assertThat(info).isEqualTo(WeatherApiResponseInfo())
        assertThat(info).isNotEqualTo(fakeGetWeatherUseCase.fakeWeatherApiResponseInfo)

    }

    @Test
    fun requestCityWeatherByCity_ShowErrorSnackbar() = runBlockingTest {

        viewModel.eventFlow.test {

            // the call must be made inside the test scope
            viewModel.requestWeatherByCity(city = incorrectCity, requestCachedData = true, delayTime = 0L)

            val it = awaitItem()
            assertThat(it).isEqualTo(CityWeatherViewModel.UIEvent.ShowSnackbar(FakeGetWeatherUseCase.ERROR_MSG_NO_DATA))
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun requestCityWeatherByCity_WasCityOrLocationUpdatedFromSettings() = runBlockingTest {

        // once we request the expected city we should receive the emitted fakeWeatherApiResponseInfo to the view model
        viewModel.setWasCityOrLocationUpdatedFromSettings(true)
        viewModel.requestWeatherByCity(city = correctCity, requestCachedData = true, delayTime = 0L)

        val state = viewModel.weatherApiResponseApiResponseInfo.value
        val info = state.weatherApiResponseInfo
        assertThat(info).isEqualTo(fakeGetWeatherUseCase.fakeWeatherApiResponseInfo)
        assertThat(info).isNotEqualTo(WeatherApiResponseInfo())
        assertThat(viewModel.wasCityOrLocationUpdatedFromSettings.value).isEqualTo(false)
        assertThat(viewModel.lastUpdatedTime.value).isEqualTo(fakeSettings.getLastUpdatedTime())
    }

    @Test
    fun requestCityWeatherByCity_LastUpdatedTime() = runBlockingTest {

        // we DO NOT update the last updated time
        var requestCachedData = true
        val lastUpdatedTime = fakeSettings.getLastUpdatedTime()
        viewModel.requestWeatherByCity(city = correctCity, requestCachedData = requestCachedData, delayTime = 0L)
        assertThat(viewModel.lastUpdatedTime.value).isEqualTo(lastUpdatedTime)
        assertThat(fakeSettings.getLastUpdatedTime()).isEqualTo(lastUpdatedTime)

        // we update the last updated time
        requestCachedData = false
        viewModel.requestWeatherByCity(city = correctCity, requestCachedData = requestCachedData, delayTime = 0L)
        assertThat(viewModel.lastUpdatedTime.value).isNotEqualTo(lastUpdatedTime)
        assertThat(fakeSettings.getLastUpdatedTime()).isNotEqualTo(lastUpdatedTime)
        assertThat(viewModel.lastUpdatedTime.value).isEqualTo(fakeSettings.getLastUpdatedTime())

    }

    @Test
    fun showSnackbar() = runBlockingTest {

        viewModel.eventFlow.test {

            // this must be called inside the test scope
            val stringResId = 34143
            viewModel.showSnackbar(stringResId)

            val it = awaitItem()
            assertThat(it).isEqualTo(CityWeatherViewModel.UIEvent.ShowSnackbar(stringResId))
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun set_getShowDragDownToRefreshMsg() = runBlockingTest {

        fun setAndCheck(rotateSwipeRefreshArrow: Boolean) {
            viewModel.setShowDragDownToRefreshMsg(rotateSwipeRefreshArrow)
            assertThat(viewModel.showDragDownToRefreshMsg.value).isEqualTo(rotateSwipeRefreshArrow)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getSwipeRefreshPaddingTop() = runBlockingTest {

        fun setAndCheck(swipeRefreshTopPadding: Float) {
            viewModel.setSwipeRefreshTopPadding(swipeRefreshTopPadding)
            assertThat(viewModel.swipeRefreshOffsetTop.value).isEqualTo(swipeRefreshTopPadding)
        }

        setAndCheck(3234.32f)
        setAndCheck(231.64f)
    }

    @Test
    fun set_getTemperatureType() = runBlockingTest {

        fun setAndCheck(expectedTemperatureType: TemperatureType) {
            viewModel.setTemperatureType(expectedTemperatureType)
            assertThat(viewModel.temperatureType.value).isEqualTo(expectedTemperatureType)
            assertThat(fakeSettings.getTemperatureType()).isEqualTo(expectedTemperatureType)
        }

        setAndCheck(TemperatureType.Celsius)
        setAndCheck(TemperatureType.Fahrenheit)
    }

    @Test
    fun set_getTimeFormat() = runBlockingTest {

        fun setAndCheck(timeFormat: TimeFormat) {
            viewModel.setTimeFormat(timeFormat)
            assertThat(viewModel.timeFormat.value).isEqualTo(timeFormat)
            assertThat(fakeSettings.getTimeFormat()).isEqualTo(timeFormat)
        }

        setAndCheck(TimeFormat.AmPm)
        setAndCheck(TimeFormat.Hours24)
    }

    @Test
    fun set_getUpdateIntervals() = runBlockingTest {

        fun setAndCheck(updateIntervals: UpdateIntervals) {
            viewModel.setUpdateIntervals(updateIntervals)
            assertThat(viewModel.updateIntervals.value).isEqualTo(updateIntervals)
            assertThat(fakeSettings.getUpdateIntervals()).isEqualTo(updateIntervals)
        }

        setAndCheck(UpdateIntervals.Every6Hours)
        setAndCheck(UpdateIntervals.Every12Hours)
        setAndCheck(UpdateIntervals.Every24Hours)
    }

    @Test
    fun set_getUseCurrentLocation() = runBlockingTest {

        fun setAndCheck(useCurrentLocation: Boolean) {
            viewModel.setUseCurrentLocation(useCurrentLocation)
            assertThat(viewModel.useCurrentLocation.value).isEqualTo(useCurrentLocation)
            assertThat(fakeSettings.getUseCurrentLocation()).isEqualTo(useCurrentLocation)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getSelectedCityId() = runBlockingTest {

        fun setAndCheck(cityId: Int) {
            viewModel.setSelectedCityId(cityId)
            assertThat(viewModel.selectedCityId.value).isEqualTo(cityId)
            assertThat(fakeSettings.getSelectedCityId()).isEqualTo(cityId)
        }

        setAndCheck(37938)
        setAndCheck(265672)
    }

    @Test
    fun set_getLanguage() = runBlockingTest {

        fun setAndCheck(language: Language) {
            viewModel.setLanguage(language)
            assertThat(viewModel.language.value).isEqualTo(language)
            assertThat(fakeSettings.getLanguage()).isEqualTo(language)
        }

        setAndCheck(Language.English)
        setAndCheck(Language.Bulgarian)
    }

    @Test
    fun set_getShowWindBox() = runBlockingTest {

        fun setAndCheck(showWindBox: Boolean) {
            viewModel.setShowWindBox(showWindBox)
            assertThat(viewModel.showWindBox.value).isEqualTo(showWindBox)
            assertThat(fakeSettings.getShowWindBox()).isEqualTo(showWindBox)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getSunriseAndSunsetBox() = runBlockingTest {

        fun setAndCheck(showSunriseAndSunsetBox: Boolean) {
            viewModel.setShowSunriseAndSunsetBox(showSunriseAndSunsetBox)
            assertThat(viewModel.showSunriseAndSunsetBox.value).isEqualTo(showSunriseAndSunsetBox)
            assertThat(fakeSettings.getShowSunriseAndSunsetBox()).isEqualTo(showSunriseAndSunsetBox)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getShowComfortLevelBox() = runBlockingTest {

        fun setAndCheck(showComfortLevelBox: Boolean) {
            viewModel.setShowComfortLevelBox(showComfortLevelBox)
            assertThat(viewModel.showComfortLevelBox.value).isEqualTo(showComfortLevelBox)
            assertThat(fakeSettings.getShowComfortLevelBox()).isEqualTo(showComfortLevelBox)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getShowNext24HoursForecastBox() = runBlockingTest {

        fun setAndCheck(showNext24HoursForecastBox: Boolean) {
            viewModel.setShowNext24HoursForecastBox(showNext24HoursForecastBox)
            assertThat(viewModel.showNext24HoursForecastBox.value).isEqualTo(showNext24HoursForecastBox)
            assertThat(fakeSettings.getShowNext24HoursForecastBox()).isEqualTo(showNext24HoursForecastBox)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getShowNext4DaysForecastBox() = runBlockingTest {

        fun setAndCheck(showNext4DaysForecastBox: Boolean) {
            viewModel.setShowNext4DaysForecastBox(showNext4DaysForecastBox)
            assertThat(viewModel.showNext4DaysForecastBox.value).isEqualTo(showNext4DaysForecastBox)
            assertThat(fakeSettings.getShowNext4DaysForecastBox()).isEqualTo(showNext4DaysForecastBox)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getEnableAnimation() = runBlockingTest {

        fun setAndCheck(enableAnimation: Boolean) {
            viewModel.setEnableAnimation(enableAnimation)
            assertThat(viewModel.enableAnimation.value).isEqualTo(enableAnimation)
            assertThat(fakeSettings.getEnableAnimation()).isEqualTo(enableAnimation)
        }

        setAndCheck(true)
        setAndCheck(false)
    }

    @Test
    fun set_getCurrentLocationLat() = runBlockingTest {

        fun setAndCheck(currentLocationLat: Float) {
            viewModel.setCurrentLocationLat(currentLocationLat)
            assertThat(viewModel.currentLocationLat.value).isEqualTo(currentLocationLat)
            assertThat(fakeSettings.getCurrentLocationLat()).isEqualTo(currentLocationLat)
        }

        setAndCheck(234.23f)
        setAndCheck(11.491f)
    }

    @Test
    fun set_getCurrentLocationLon() = runBlockingTest {

        fun setAndCheck(currentLocationLon: Float) {
            viewModel.setCurrentLocationLon(currentLocationLon)
            assertThat(viewModel.currentLocationLon.value).isEqualTo(currentLocationLon)
            assertThat(fakeSettings.getCurrentLocationLon()).isEqualTo(currentLocationLon)
        }

        setAndCheck(234.23f)
        setAndCheck(11.491f)
    }

    @Test
    fun set_getLastUpdatedTime() = runBlockingTest {

        fun setAndCheck(lastUpdateTime: Long) {
            viewModel.setLastUpdatedTime(lastUpdateTime)
            assertThat(viewModel.lastUpdatedTime.value).isEqualTo(lastUpdateTime)
            assertThat(fakeSettings.getLastUpdatedTime()).isEqualTo(lastUpdateTime)
        }

        setAndCheck(3453242L)
        setAndCheck(121984456L)
    }

}
