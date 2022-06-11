package com.slaviboy.weather.features.weather.presentation.state

import com.slaviboy.weather.core.util.Result
import com.slaviboy.weather.core.util.StaticMethodsTestHelpers
import com.slaviboy.weather.features.weather.data.local.entity.City
import com.slaviboy.weather.features.weather.domain.usecase.GetCityUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class FakeGetCityUseCase : GetCityUseCase {

    val fakeCities = listOf(
        StaticMethodsTestHelpers.randomCity(0, "Town1"),
        StaticMethodsTestHelpers.randomCity(1, "Town2"),
        StaticMethodsTestHelpers.randomCity(2, "Town3"),
        StaticMethodsTestHelpers.randomCity(3, "Town4"),
    )

    val fakeCity = StaticMethodsTestHelpers.randomCity(fakeCityId, "Fake city")

    override fun invoke(cityName: String): Flow<Result<List<City>>> = flow {

        if (cityName == fakeCityName.lowercase(Locale.getDefault())) {
            emit(Result.Success(data = fakeCities, true))
        } else {
            emit(Result.Error<List<City>>(data = null, message = NO_MATCHING_CITIES_FOUND))
        }
    }

    override fun invoke(cityId: Int): Flow<Result<City>> = flow {
        if (cityId == fakeCityId) {
            emit(Result.Success(data = fakeCity, true))
        } else {
            emit(Result.Error<City>(data = null, message = NO_SUCH_CITY_BY_ID))
        }
    }

    companion object {
        const val NO_MATCHING_CITIES_FOUND = 0
        const val NO_SUCH_CITY_BY_ID = 1

        const val fakeCityName = "My Fake City"
        const val fakeCityId = 34729
    }
}