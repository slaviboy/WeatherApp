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
package com.slaviboy.weather.features.weather.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slaviboy.weather.R
import com.slaviboy.weather.core.util.*
import com.slaviboy.weather.features.weather.data.local.entity.City
import com.slaviboy.weather.features.weather.data.remote.WeatherApi
import com.slaviboy.weather.features.weather.domain.model.WeatherApiResponseInfo
import com.slaviboy.weather.features.weather.domain.repository.SettingsRepository
import com.slaviboy.weather.features.weather.domain.usecase.GetCityUseCase
import com.slaviboy.weather.features.weather.domain.usecase.GetWeatherUseCase
import com.slaviboy.weather.features.weather.presentation.state.CityState
import com.slaviboy.weather.features.weather.presentation.state.WeatherApiResponseInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * The view model for the HOME page
 */
@HiltViewModel
class CityWeatherViewModel @Inject constructor(

    private val getWeatherUseCase: GetWeatherUseCase,
    private val getCityUseCase: GetCityUseCase,

    private val settings: SettingsRepository
) : ViewModel() {

    private val _switchHomeToSetting = mutableStateOf(true)                         //
    private val _showDragDownToRefreshMsg = mutableStateOf(false)                   // show the drag down to refresh, when swipe refresh is used
    private val _swipeRefreshOffsetTop = mutableStateOf(0f)                         // the padding for the swipe refresh
    private val _isRefreshing = mutableStateOf(false)                               // if we are still waiting for new data
    private val _searchedCityName = mutableStateOf("")                              // the city the user have searched, it can be substring 'Bla' instead of 'Blagoevgrad'
    private val _eventFlow = MutableSharedFlow<UIEvent>()                                // hold the ui events, to show snackbar
    private var searchWeatherJob: Job? = null                                            // the job from the coroutines for requesting Weather from the API/DB (since we need to cancel it)
    private val _wasCityOrLocationUpdatedFromSettings = mutableStateOf(false)       // if the user choose different city, to indicate we need to update Home UI
    private val _weatherApiResponseInfo = mutableStateOf(WeatherApiResponseInfoState())  // the response with the weather info, either from local DB or the API
    private val _resultAllCities = mutableStateOf(CityState())                           // list with all cities that match a string 'Blag' -> Blagaj, Blagdon, Blagoevgrad... from the local DB
    private val _resultCityById = mutableStateOf<City?>(null)                       // the result city that matches the selected city id from the local DB
    private var searchCityJob: Job? = null                                               // the job from the coroutines for requesting City from  local DB
    private val _temperatureType = mutableStateOf(settings.getTemperatureType())
    private val _timeFormat = mutableStateOf(settings.getTimeFormat())
    private val _updateIntervals = mutableStateOf(settings.getUpdateIntervals())
    private val _useCurrentLocation = mutableStateOf(settings.getUseCurrentLocation())
    private val _currentLocationLat = mutableStateOf(settings.getCurrentLocationLat())
    private val _currentLocationLon = mutableStateOf(settings.getCurrentLocationLon())
    private val _showNext4DaysForecastBox = mutableStateOf(settings.getShowNext4DaysForecastBox())
    private val _showNext24HoursForecastBox = mutableStateOf(settings.getShowNext24HoursForecastBox())
    private val _enableAnimation = mutableStateOf(settings.getEnableAnimation())
    private val _lastUpdatedTime = mutableStateOf(settings.getLastUpdatedTime())
    private val _showSunriseAndSunsetBox = mutableStateOf(settings.getShowSunriseAndSunsetBox())
    private val _showComfortLevelBox = mutableStateOf(settings.getShowComfortLevelBox())
    private val _language = mutableStateOf(settings.getLanguage())
    private val _showWindBox = mutableStateOf(settings.getShowWindBox())
    private val _selectedCityId = mutableStateOf(settings.getSelectedCityId().also {
        getCityById(it)
        searchCityJob?.invokeOnCompletion {
            requestWeatherByCity(resultCityById.value ?: City(), true, 0L)
        }
    })


    // public GET properties
    val switchHomeToSetting: State<Boolean> = _switchHomeToSetting
    val showDragDownToRefreshMsg: State<Boolean> = _showDragDownToRefreshMsg
    val swipeRefreshOffsetTop: State<Float> = _swipeRefreshOffsetTop
    val isRefreshing: State<Boolean> = _isRefreshing
    val searchedCityName: State<String> = _searchedCityName
    val eventFlow = _eventFlow.asSharedFlow()
    val wasCityOrLocationUpdatedFromSettings: State<Boolean> = _wasCityOrLocationUpdatedFromSettings
    val weatherApiResponseApiResponseInfo: State<WeatherApiResponseInfoState> = _weatherApiResponseInfo
    val resultAllCities: State<CityState> = _resultAllCities
    val resultCityById: State<City?> = _resultCityById
    val showNext4DaysForecastBox: State<Boolean> = _showNext4DaysForecastBox
    val showNext24HoursForecastBox: State<Boolean> = _showNext24HoursForecastBox
    val enableAnimation: State<Boolean> = _enableAnimation
    val lastUpdatedTime: State<Long> = _lastUpdatedTime
    val showSunriseAndSunsetBox: State<Boolean> = _showSunriseAndSunsetBox
    val showComfortLevelBox: State<Boolean> = _showComfortLevelBox
    val language: State<Language> = _language
    val showWindBox: State<Boolean> = _showWindBox
    val selectedCityId: State<Int> = _selectedCityId
    val temperatureType: State<TemperatureType> = _temperatureType
    val timeFormat: State<TimeFormat> = _timeFormat
    val updateIntervals: State<UpdateIntervals> = _updateIntervals
    val useCurrentLocation: State<Boolean> = _useCurrentLocation
    val currentLocationLat: State<Float> = _currentLocationLat
    val currentLocationLon: State<Float> = _currentLocationLon


    /**
     * Request weather
     * @param id the id of the city
     * @param name the name of the city
     * @param lat the latitude of the city location
     * @param lon the longitude of the city location
     * @param requestCachedData if we want to get only the cached data, without making request to the API
     * @param delayTime the delay time between requests (to prevent to many request when user type in the textview, and the method is called)
     */
    private fun requestWeatherByCity(id: Int, name: String, lat: Float, lon: Float, requestCachedData: Boolean, delayTime: Long) {

        // cancel the previous call
        searchWeatherJob?.cancel()
        searchWeatherJob = viewModelScope.launch {

            // delay coroutine
            if (delayTime > 0) delay(delayTime)

            getWeatherUseCase(id, name, lat, lon, requestCachedData).onEach { result ->

                _weatherApiResponseInfo.value = _weatherApiResponseInfo.value.copy(
                    weatherApiResponseInfo = result.data ?: WeatherApiResponseInfo(),
                    isLoading = (result is Result.Loading)
                )

                if (result is Result.Error) {
                    showSnackbar(result.message ?: R.string.unknown_error)
                } else if (result is Result.Success) {
                    if (!result.isCachedData) {
                        setLastUpdatedTime(Date().time)
                    }
                    _wasCityOrLocationUpdatedFromSettings.value = false
                }

            }.launchIn(this)
        }
    }

    /**
     * Request the weather using City object
     */
    fun requestWeatherByCity(city: City = _resultCityById.value ?: City(), requestCachedData: Boolean = true, delayTime: Long = DELAY_BEFORE_REQUEST) {
        requestWeatherByCity(city.id, city.name, city.lat, city.lon, requestCachedData, delayTime)
    }

    /**
     * Request the weather using Geographic location
     */
    fun requestWeatherByGeographicLocation(lat: Float, lon: Float, requestCachedData: Boolean = true, delayTime: Long = DELAY_BEFORE_REQUEST) {

        // the city name when we use lat, lon location coordinates instead of city name
        val cityId = WeatherApi.GEOGRAPHIC_COORDINATES_CITY_ID
        val cityName = WeatherApi.GEOGRAPHIC_COORDINATES_CITY_NAME

        requestWeatherByCity(cityId, cityName, lat, lon, requestCachedData, delayTime)
    }

    /**
     * Request the local database for all the city that match a string. It can be partial string:
     * cityName=Blag -> result{ Blagaj, Blagdon, Blagoevgrad...}
     * @param cityName the name of the city, can be substring
     */
    fun getAllCitiesByName(cityName: String) {

        val queryLowerCase = cityName.lowercase(Locale.getDefault())
        _searchedCityName.value = cityName

        viewModelScope.launch {

            getCityUseCase(queryLowerCase).onEach { result ->
                _resultAllCities.value = _resultAllCities.value.copy(
                    allCities = result.data ?: listOf(),
                    isLoading = (result is Result.Loading)
                )
                if (result is Result.Error) {
                    showSnackbar(result.message ?: R.string.unknown_error)
                }
            }.launchIn(this)
        }
    }

    /**
     * Get a certain city by its id, if the id is -1, then null value is set to resultCityById
     * @param id the id of the city
     */
    fun getCityById(id: Int) {

        searchCityJob?.cancel()
        searchCityJob = viewModelScope.launch {

            getCityUseCase(id).onEach { result ->
                _resultCityById.value = result.data
                if (result is Result.Error) {
                    showSnackbar(result.message ?: R.string.unknown_error)
                }
            }.launchIn(this)
        }
    }

    /**
     * Clear the searched city, when the (x) close button for the search city is pressed
     */
    fun clearedSearchedCity() {
        _searchedCityName.value = ""
        _resultAllCities.value = CityState()
    }

    fun setTemperatureType(temperatureType: TemperatureType) {
        _temperatureType.value = temperatureType
        settings.setTemperatureType(temperatureType)
    }

    fun setTimeFormat(timeFormat: TimeFormat) {
        _timeFormat.value = timeFormat
        settings.setTimeFormat(timeFormat)
    }

    fun setUpdateIntervals(updateIntervals: UpdateIntervals) {
        _updateIntervals.value = updateIntervals
        settings.setUpdateIntervals(updateIntervals)
    }

    fun setUseCurrentLocation(useCurrentLocation: Boolean) {
        _useCurrentLocation.value = useCurrentLocation
        settings.setUseCurrentLocation(useCurrentLocation)
    }

    fun switchUseCurrentLocation() {
        setUseCurrentLocation(!_useCurrentLocation.value)
        _wasCityOrLocationUpdatedFromSettings.value = true
    }

    fun setCurrentLocationLat(currentLocationLat: Float) {
        _currentLocationLat.value = currentLocationLat
        settings.setCurrentLocationLat(currentLocationLat)
    }

    fun setCurrentLocationLon(currentLocationLon: Float) {
        _currentLocationLon.value = currentLocationLon
        settings.setCurrentLocationLon(currentLocationLon)
    }

    fun setSelectedCityId(cityId: Int) {
        _selectedCityId.value = cityId
        settings.setSelectedCityId(cityId)
        getCityById(cityId)

        _wasCityOrLocationUpdatedFromSettings.value = true
    }

    fun setLanguage(language: Language) {
        _language.value = language
        settings.setLanguage(language)
    }

    fun setShowWindBox(showWindBox: Boolean) {
        _showWindBox.value = showWindBox
        settings.setShowWindBox(showWindBox)
    }

    fun switchShowWindBox() {
        setShowWindBox(!_showWindBox.value)
    }

    fun setShowSunriseAndSunsetBox(showSunriseAndSunsetBox: Boolean) {
        _showSunriseAndSunsetBox.value = showSunriseAndSunsetBox
        settings.setShowSunriseAndSunsetBox(showSunriseAndSunsetBox)
    }

    fun switchShowSunriseAndSunsetBox() {
        setShowSunriseAndSunsetBox(!_showSunriseAndSunsetBox.value)
    }

    fun setShowComfortLevelBox(showHumidityBox: Boolean) {
        _showComfortLevelBox.value = showHumidityBox
        settings.setShowComfortLevelBox(showHumidityBox)
    }

    fun switchShowComfortLevelBox() {
        setShowComfortLevelBox(!_showComfortLevelBox.value)
    }

    fun setShowNext4DaysForecastBox(showNext4DaysForecastBox: Boolean) {
        _showNext4DaysForecastBox.value = showNext4DaysForecastBox
        settings.setShowNext4DaysForecastBox(showNext4DaysForecastBox)
    }

    fun switchShowNext4DaysForecastBox() {
        setShowNext4DaysForecastBox(!_showNext4DaysForecastBox.value)
    }

    fun setShowNext24HoursForecastBox(showNext24HoursForecastBox: Boolean) {
        _showNext24HoursForecastBox.value = showNext24HoursForecastBox
        settings.setShowNext24HoursForecastBox(showNext24HoursForecastBox)
    }

    fun switchShowNext24HoursForecastBox() {
        setShowNext24HoursForecastBox(!_showNext24HoursForecastBox.value)
    }

    fun setEnableAnimation(enableAnimation: Boolean) {
        _enableAnimation.value = enableAnimation
        settings.setEnableAnimation(enableAnimation)
    }

    fun switchEnableAnimation() {
        setEnableAnimation(!_enableAnimation.value)
    }

    fun setLastUpdatedTime(lastUpdatedTime: Long) {
        _lastUpdatedTime.value = lastUpdatedTime
        settings.setLastUpdatedTime(lastUpdatedTime)
    }

    fun switchHomeToSettings() {
        _switchHomeToSetting.value = !_switchHomeToSetting.value
    }

    fun setShowDragDownToRefreshMsg(value: Boolean) {
        _showDragDownToRefreshMsg.value = value
    }

    fun setSwipeRefreshTopPadding(padding: Float) {
        _swipeRefreshOffsetTop.value = padding
    }

    fun setIsRefreshing(isRefreshing: Boolean) {
        _isRefreshing.value = isRefreshing
    }

    fun setWasCityOrLocationUpdatedFromSettings(wasCityOrLocationUpdatedFromSettings: Boolean) {
        _wasCityOrLocationUpdatedFromSettings.value = wasCityOrLocationUpdatedFromSettings
    }

    suspend fun showSnackbar(stringResId: Int) {
        _eventFlow.emit(
            UIEvent.ShowSnackbar(stringResId)
        )
    }

    fun showSnackbarWithScope(stringResId: Int) {
        viewModelScope.launch {
            showSnackbar(stringResId)
        }
    }

    sealed class UIEvent {
        data class ShowSnackbar(val messageResId: Int) : UIEvent()
    }

    companion object {

        // time in ms, before we request new data
        const val DELAY_BEFORE_REQUEST = 1500L
    }
}