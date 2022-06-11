package com.slaviboy.weather.features.weather.presentation.state

import com.slaviboy.weather.core.util.Language
import com.slaviboy.weather.core.util.TemperatureType
import com.slaviboy.weather.core.util.TimeFormat
import com.slaviboy.weather.core.util.UpdateIntervals
import com.slaviboy.weather.features.weather.domain.repository.SettingsRepository

class FakeSettingRepository : SettingsRepository {

    private var _temperatureType = TemperatureType.Fahrenheit
    private var _timeFormat = TimeFormat.AmPm
    private var _updateIntervals = UpdateIntervals.Every12Hours
    private var _useCurrentLocation = false
    private var _selectedCityId = SettingsRepository.NEW_YORK_CITY_ID
    private var _language = Language.English
    private var _showWindBox = true
    private var _showSunriseAndSunsetBox = true
    private var _showComfortLevelBox = true
    private var _showNext24HoursForecastBox = true
    private var _showNext4DaysForecastBox = true
    private var _enableAnimation = true
    private var _currentLocationLat = 210.42f
    private var _currentLocationLon = 51.61f
    private var _lastUpdatedTime = 320L

    override fun getTemperatureType(): TemperatureType {
        return _temperatureType
    }

    override fun setTemperatureType(temperatureType: TemperatureType) {
        _temperatureType = temperatureType
    }

    override fun getTimeFormat(): TimeFormat {
        return _timeFormat
    }

    override fun setTimeFormat(timeFormat: TimeFormat) {
        _timeFormat = timeFormat
    }

    override fun getUpdateIntervals(): UpdateIntervals {
        return _updateIntervals
    }

    override fun setUpdateIntervals(updateIntervals: UpdateIntervals) {
        _updateIntervals = updateIntervals
    }

    override fun getUseCurrentLocation(): Boolean {
        return _useCurrentLocation
    }

    override fun setUseCurrentLocation(useCurrentLocation: Boolean) {
        _useCurrentLocation = useCurrentLocation
    }

    override fun getSelectedCityId(): Int {
        return _selectedCityId
    }

    override fun setSelectedCityId(cityId: Int) {
        _selectedCityId = cityId
    }

    override fun getLanguage(): Language {
        return _language
    }

    override fun setLanguage(language: Language) {
        _language = language
    }

    override fun getShowWindBox(): Boolean {
        return _showWindBox
    }

    override fun setShowWindBox(showWindBox: Boolean) {
        _showWindBox = showWindBox
    }

    override fun getShowSunriseAndSunsetBox(): Boolean {
        return _showSunriseAndSunsetBox
    }

    override fun setShowSunriseAndSunsetBox(showSunriseSunsetBox: Boolean) {
        _showSunriseAndSunsetBox = showSunriseSunsetBox
    }

    override fun getShowComfortLevelBox(): Boolean {
        return _showComfortLevelBox
    }

    override fun setShowComfortLevelBox(showComfortLevelBox: Boolean) {
        _showComfortLevelBox = showComfortLevelBox
    }

    override fun getShowNext24HoursForecastBox(): Boolean {
        return _showNext24HoursForecastBox
    }

    override fun setShowNext24HoursForecastBox(showNext24HoursForecastBox: Boolean) {
        _showNext24HoursForecastBox = showNext24HoursForecastBox
    }

    override fun getShowNext4DaysForecastBox(): Boolean {
        return _showNext4DaysForecastBox
    }

    override fun setShowNext4DaysForecastBox(showNext4DaysForecastBox: Boolean) {
        _showNext4DaysForecastBox = showNext4DaysForecastBox
    }

    override fun getEnableAnimation(): Boolean {
        return _enableAnimation
    }

    override fun setEnableAnimation(enableAnimation: Boolean) {
        _enableAnimation = enableAnimation
    }

    override fun getCurrentLocationLat(): Float {
        return _currentLocationLat
    }

    override fun setCurrentLocationLat(currentLocationLat: Float) {
        _currentLocationLat = currentLocationLat
    }

    override fun getCurrentLocationLon(): Float {
        return _currentLocationLon
    }

    override fun setCurrentLocationLon(currentLocationLon: Float) {
        _currentLocationLon = currentLocationLon
    }

    override fun getLastUpdatedTime(): Long {
        return _lastUpdatedTime
    }

    override fun setLastUpdatedTime(lastUpdatedTime: Long) {
        _lastUpdatedTime = lastUpdatedTime
    }

}