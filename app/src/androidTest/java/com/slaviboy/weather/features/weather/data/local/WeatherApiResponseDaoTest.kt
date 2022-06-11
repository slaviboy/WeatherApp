package com.slaviboy.weather.features.weather.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.slaviboy.weather.core.util.StaticMethodsTestHelpers.randomCity
import com.slaviboy.weather.core.util.StaticMethodsTestHelpers.randomCurrent
import com.slaviboy.weather.core.util.StaticMethodsTestHelpers.randomDaily
import com.slaviboy.weather.core.util.StaticMethodsTestHelpers.randomFeelsLike
import com.slaviboy.weather.core.util.StaticMethodsTestHelpers.randomHourly
import com.slaviboy.weather.core.util.StaticMethodsTestHelpers.randomTemp
import com.slaviboy.weather.core.util.StaticMethodsTestHelpers.randomWeather
import com.slaviboy.weather.core.util.StaticMethodsTestHelpers.randomWeatherApiResponse
import com.slaviboy.weather.features.weather.data.local.entity.*
import com.slaviboy.weather.features.weather.data.local.entity.relationship.CurrentWithRelations
import com.slaviboy.weather.features.weather.data.local.entity.relationship.DailyWithRelations
import com.slaviboy.weather.features.weather.data.local.entity.relationship.HourlyWithRelations
import com.slaviboy.weather.features.weather.data.local.entity.relationship.WeatherApiResponseWithRelations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherApiResponseDaoTest {

    private lateinit var database: WeatherApiResponseDatabase
    private lateinit var dao: WeatherApiResponseDao

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, WeatherApiResponseDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.daoWeather
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetCityByCityId() = runBlockingTest {

        val expectedCity = City(
            id = 5128638,
            name = "Sofia",
            country = "Bulgaria",
            lat = 2.325f,
            lon = 22.352f
        )
        dao.insertCity(expectedCity)
        val city = dao.getCityById(5128638)

        assertThat(city).isEqualTo(expectedCity)
    }

    @Test
    fun getAllCitiesByCityName() = runBlockingTest {

        // 15 cities starting with S (id is in range from 1 to 15)
        val sofia = randomCity(11, "Sofia")
        val sofa = randomCity(10, "Sofa")
        val softopia = randomCity(9, "Softopia")
        val solomon = randomCity(8, "Solomon")
        val shanghai = randomCity(7, "Shanghai")
        val shenzhen = randomCity(6, "Shenzhen")
        val seoul = randomCity(5, "Seoul")
        val saoPaulo = randomCity(4, "Sao Paulo")
        val shenyang = randomCity(3, "Shenyang")
        val suzhou = randomCity(2, "Suzhou")
        val shantou = randomCity(1, "Shantou")
        val shashouka = randomCity(12, "Shashouka")
        val shtiglec = randomCity(13, "Shtiglec")
        val smoky = randomCity(14, "Smoky")
        val sasy = randomCity(15, "Sasy")

        // 5 cities starting with L (id is in range from 16 to 20)
        val louisville = randomCity(16, "Louisville")
        val london = randomCity(18, "London")
        val lada = randomCity(17, "Lada")
        val love = randomCity(19, "Love")
        val lucky = randomCity(20, "Lucky")

        // insert all cities
        dao.insertCity(sofia, sofa, softopia, solomon, shanghai, shenzhen, seoul, saoPaulo, shenyang, suzhou, shantou, shashouka, shtiglec, smoky, sasy, louisville, london)

        val citiesStartingWithSof = dao.getAllCitiesByCityName("sof")
        assertThat(citiesStartingWithSof).isEqualTo(listOf(sofa, sofia, softopia).sortedBy {
            it.name
        })

        val citiesStartingWithSo = dao.getAllCitiesByCityName("so")
        assertThat(citiesStartingWithSo).isEqualTo(listOf(sofa, sofia, softopia, solomon).sortedBy {
            it.name
        })

        val citiesStartingWithLo = dao.getAllCitiesByCityName("lo")
        assertThat(citiesStartingWithLo).isEqualTo(listOf(london, louisville).sortedBy {
            it.name
        })

        // check for limit to max 10 returned cities
        val citiesStartingWithS = dao.getAllCitiesByCityName("s")
        assertThat(citiesStartingWithS.size).isEqualTo(10)

    }

    @Test
    fun weatherApiResponseWithRelations() = runBlockingTest {

        val cityId = 1473923
        val cityName = "Sofia"

        // current maskMultiplier [0]
        val currentMaskMultiplier = 0
        val currentWeather = randomWeather(cityId, currentMaskMultiplier)
        val current = randomCurrent(cityId, currentMaskMultiplier)
        dao.insertWeather(currentWeather)
        dao.insertCurrent(current)

        // daily maskMultiplier [1,4] for 4 days
        val dailyWeather = mutableListOf<Weather>()
        val dailyFeelsLike = mutableListOf<FeelsLike>()
        val dailyTemp = mutableListOf<Temp>()
        val dailyList = mutableListOf<Daily>()
        (1..4).forEach { i ->

            val weather = randomWeather(cityId, i)
            dailyWeather.add(weather)
            dao.insertWeather(weather)

            val temp = randomTemp(cityId, i)
            dailyTemp.add(temp)
            dao.insertTemperature(temp)

            val feelsLike = randomFeelsLike(cityId, i)
            dailyFeelsLike.add(feelsLike)
            dao.insertFeelLike(feelsLike)

            val daily = randomDaily(cityId, i)
            dailyList.add(daily)
            dao.insertDaily(daily)
        }

        // hourly maskMultiplier [5,30] for 24 hours
        val hourlyWeather = mutableListOf<Weather>()
        val hourlyList = mutableListOf<Hourly>()
        (5..30).forEach { i ->

            val weather = randomWeather(cityId, i)
            hourlyWeather.add(weather)
            dao.insertWeather(weather)

            val hourly = randomHourly(cityId, i)
            hourlyList.add(hourly)
            dao.insertHourly(hourly)
        }

        val weatherApiResponse = randomWeatherApiResponse(cityId, cityName)
        dao.insertWeatherApiResponse(weatherApiResponse)

        val currentWithRelations = CurrentWithRelations(current, listOf(currentWeather))
        val dailyWithRelations = dailyList.mapIndexed { i, daily ->
            DailyWithRelations(daily, dailyTemp[i], dailyFeelsLike[i], listOf(dailyWeather[i]))
        }
        val hourlyWithRelations = hourlyList.mapIndexed { i, hourly ->
            HourlyWithRelations(hourly, listOf(hourlyWeather[i]))
        }
        val expectedWeatherApiResponseWithRelations = WeatherApiResponseWithRelations(
            weatherApiResponse = weatherApiResponse,
            currentEntity = currentWithRelations,
            dailyEntity = dailyWithRelations,
            hourlyEntity = hourlyWithRelations
        )

        fun check(response: WeatherApiResponseWithRelations?) {
            response ?: return

            // make sure not null
            assertThat(response).isNotEqualTo(null)
            assertThat(response).isNotEqualTo(expectedWeatherApiResponseWithRelations)

            // check after reset id
            resetIds(response)
            assertThat(response).isEqualTo(expectedWeatherApiResponseWithRelations)
        }

        var weatherApiResponseByCityId = dao.getWeatherApiResponseByCityId(cityId + 1)
        assertThat(weatherApiResponseByCityId).isEqualTo(null)

        weatherApiResponseByCityId = dao.getWeatherApiResponseByCityName("$cityName-")
        assertThat(weatherApiResponseByCityId).isEqualTo(null)

        // check by city name
        weatherApiResponseByCityId = dao.getWeatherApiResponseByCityId(cityId)
        check(weatherApiResponseByCityId)

        // check by city id
        weatherApiResponseByCityId = dao.getWeatherApiResponseByCityName(cityName)
        check(weatherApiResponseByCityId)
    }


    /**
     * Reset the id for all Weather, FeelsLike, Temp, Daily, Hourly
     * @param weatherApiResponseWithRelations
     */
    fun resetIds(weatherApiResponseWithRelations: WeatherApiResponseWithRelations?) {
        weatherApiResponseWithRelations ?: return

        // we need to null-ify all ids before check, since they are automatically generated
        weatherApiResponseWithRelations.currentEntity?.let {
            it.weather.forEach {
                it.id = null
            }
        }
        weatherApiResponseWithRelations.dailyEntity.forEach {
            it.daily.id = null
            it.temp.id = null
            it.feelsLike.id = null
            it.weather.forEach {
                it.id = null
            }
        }
        weatherApiResponseWithRelations.hourlyEntity.forEach {
            it.hourly.id = null
            it.weather.forEach {
                it.id = null
            }
        }
    }
}