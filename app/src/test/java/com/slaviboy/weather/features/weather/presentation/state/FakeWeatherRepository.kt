package com.slaviboy.weather.features.weather.presentation.state

import com.slaviboy.weather.core.util.Result
import com.slaviboy.weather.features.weather.data.local.entity.City
import com.slaviboy.weather.features.weather.domain.repository.WeatherRepository
import com.slaviboy.weather.features.weather.domain.model.WeatherApiResponseInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWeatherRepository : WeatherRepository {

    fun randomWeatherApiResponseInfo(): WeatherApiResponseInfo {
        return WeatherApiResponseInfo()
    }

    val sofiaLatLon = floatArrayOf(92.24f, 2161.42f)
    val newYorkLatLon = floatArrayOf(912.422f, 26.42f)
    val londonLatLon = floatArrayOf(32.24f, 63.499f)
    val louisvilleLatLon = floatArrayOf(66.64f, 61.32f)
    val lexingtonLatLon = floatArrayOf(922.4f, 161.92f)

    val allCitiesList = mutableListOf(
        City(id = 0, name = "Sofia", lat = sofiaLatLon[0], lon = sofiaLatLon[1]),
        City(id = 1, name = "New York", lat = newYorkLatLon[0], lon = newYorkLatLon[1]),
        City(id = 2, name = "London", lat = londonLatLon[0], lon = londonLatLon[1]),
        City(id = 3, name = "Louisville", lat = louisvilleLatLon[0], lon = louisvilleLatLon[1]),
        City(id = 4, name = "Lexington", lat = lexingtonLatLon[0], lon = lexingtonLatLon[1])
    )
    val weatherApiResponse: HashMap<FloatArray, WeatherApiResponseInfo> = hashMapOf(
        sofiaLatLon to randomWeatherApiResponseInfo(),
        newYorkLatLon to randomWeatherApiResponseInfo(),
        londonLatLon to randomWeatherApiResponseInfo(),
        louisvilleLatLon to randomWeatherApiResponseInfo(),
        lexingtonLatLon to randomWeatherApiResponseInfo(),
    )

    var shouldReturnNetworkError = false

    override fun getWeatherApiResponse(id: Int, name: String, lat: Float, lon: Float, requestCachedData: Boolean): Flow<Result<WeatherApiResponseInfo>> {
        return flow {
            val matchCities = weatherApiResponse[floatArrayOf(lat, lon)]
            Result.Success(data = matchCities, isCachedData = requestCachedData)
        }
    }

    override fun getAllCitiesByCityName(cityName: String): Flow<Result<List<City>>> {
        return flow {
            val matchCities = allCitiesList.filter {
                (it.name.uppercase()).contains(cityName.uppercase())
            }
            Result.Success(data = matchCities, isCachedData = true)
        }
    }

    override fun getCityById(cityId: Int): Flow<Result<City>> {
        return flow {
            val city = allCitiesList.find {
                it.id == cityId
            }
            Result.Success(data = city, isCachedData = true)
        }
    }

}