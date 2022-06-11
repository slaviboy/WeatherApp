package com.slaviboy.weather.features.weather.presentation.state

import androidx.compose.ui.graphics.Color
import com.slaviboy.weather.R
import com.slaviboy.weather.core.util.Result
import com.slaviboy.weather.features.weather.data.local.entity.City
import com.slaviboy.weather.features.weather.domain.model.CurrentInfo
import com.slaviboy.weather.features.weather.domain.model.WeatherApiResponseInfo
import com.slaviboy.weather.features.weather.domain.usecase.GetWeatherUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGetWeatherUseCase : GetWeatherUseCase {

    // fake object that will be returned with the flow to the view model
    val fakeWeatherApiResponseInfo = WeatherApiResponseInfo(
        topBackgroundColor = Color.Red,
        bottomBackgroundColor = Color.Green,
        currentInfo = CurrentInfo(0f, 12f, 2f, R.drawable.ic_weather1, "Rainy", "mostly rainy", 3L)
    )

    override fun invoke(id: Int, name: String, lat: Float, lon: Float, requestCachedData: Boolean): Flow<Result<WeatherApiResponseInfo>> = flow {

        if (id == fakeCity.id && name == fakeCity.name && lat == fakeCity.lat && lon == fakeCity.lon) {
            emit(Result.Success(data = fakeWeatherApiResponseInfo, isCachedData = requestCachedData))
        } else {
            emit(Result.Error<WeatherApiResponseInfo>(data = null, message = ERROR_MSG_NO_DATA))
        }
    }

    companion object {
        const val ERROR_MSG_NO_DATA = 0

        val fakeCity = City(id = 33255, name = "Fake City", lat = 345.2f, lon = 124.21f)
    }
}