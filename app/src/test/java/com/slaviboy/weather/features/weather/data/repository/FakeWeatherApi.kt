package com.slaviboy.weather.features.weather.data.repository

import com.google.gson.GsonBuilder
import com.slaviboy.weather.features.weather.data.remote.WeatherApi
import com.slaviboy.weather.features.weather.data.remote.dto.WeatherApiResponseDTO

class FakeWeatherApi : WeatherApi {

    private val fakeWeatherApiResponseString = """
    {
        "lat": 33.44,
        "lon": -94.04,
        "timezone": "America/Chicago",
        "timezone_offset": -21600,
        
        "current": {
            "dt": 1618317040,
            "sunrise": 1618282134,
            "sunset": 1618333901,
            "temp": 284.07,
            "feels_like": 282.84,
            "pressure": 1019,
            "humidity": 62,
            "dew_point": 277.08,
            "uvi": 0.89,
            "clouds": 0,
            "visibility": 10000,
            "wind_speed": 6,
            "wind_deg": 300,
            "weather": [
                {
                    "id": 500,
                    "main": "Rain",
                    "description": "light rain",
                    "icon": "10d"
                }
            ],
            "rain": {
                "1h": 0.21
            }
        }, 
        
        "hourly": [
            {
                "dt": 1618315200,
                "temp": 282.58,
                "feels_like": 280.4,
                "pressure": 1019,
                "humidity": 68,
                "dew_point": 276.98,
                "uvi": 1.4,
                "clouds": 19,
                "visibility": 306,
                "wind_speed": 4.12,
                "wind_deg": 296,
                "wind_gust": 7.33,
                "weather": [
                    {
                        "id": 801,
                        "main": "Clouds",
                        "description": "few clouds",
                        "icon": "02d"
                    }
                ],
                "pop": 0
            }
        ],
        
        "daily": [
            {
                "dt": 1618308000,
                "sunrise": 1618282134,
                "sunset": 1618333901,
                "moonrise": 1618284960,
                "moonset": 1618339740,
                "moon_phase": 0.04,
                "temp": {
                    "day": 279.79,
                    "min": 275.09,
                    "max": 284.07,
                    "night": 275.09,
                    "eve": 279.21,
                    "morn": 278.49
                },
                "feels_like": {
                    "day": 277.59,
                    "night": 276.27,
                    "eve": 276.49,
                    "morn": 276.27
                },
                "pressure": 1020,
                "humidity": 81,
                "dew_point": 276.77,
                "wind_speed": 3.06,
                "wind_deg": 294,
                "weather": [
                    {
                      "id": 500,
                      "main": "Rain",
                      "description": "light rain",
                      "icon": "10d"
                    }
                ],
                "clouds": 56,
                "pop": 0.2,
                "rain": 0.62,
                "uvi": 1.93
           }
        ]
    }
    """.trimIndent()

    private val gsonBuilder = GsonBuilder()
    private val gson = gsonBuilder.create()

    override suspend fun getWeatherApiResponseByGeographicCoordinates(lat: Float, lon: Float, language: String, units: String): WeatherApiResponseDTO {
        return gson.fromJson(fakeWeatherApiResponseString, WeatherApiResponseDTO::class.java)
    }
}