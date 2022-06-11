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
package com.slaviboy.weather.features.weather.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.slaviboy.weather.features.weather.data.local.WeatherApiResponseDatabase
import com.slaviboy.weather.features.weather.data.remote.WeatherApi
import com.slaviboy.weather.features.weather.data.repository.SettingsRepositoryImplementation
import com.slaviboy.weather.features.weather.data.repository.WeatherRepositoryImplementation
import com.slaviboy.weather.features.weather.domain.repository.SettingsRepository
import com.slaviboy.weather.features.weather.domain.repository.WeatherRepository
import com.slaviboy.weather.features.weather.domain.usecase.GetCityUseCase
import com.slaviboy.weather.features.weather.domain.usecase.GetCityUseCaseImplementation
import com.slaviboy.weather.features.weather.domain.usecase.GetWeatherUseCase
import com.slaviboy.weather.features.weather.domain.usecase.GetWeatherUseCaseImplementation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CityWeatherModule {

    @Provides
    @Singleton
    fun provideGetCityWeatherUseCase(repository: WeatherRepository): GetWeatherUseCase {
        return GetWeatherUseCaseImplementation(repository)
    }

    @Provides
    @Singleton
    fun provideGetCityUseCase(repository: WeatherRepository): GetCityUseCase {
        return GetCityUseCaseImplementation(repository)
    }

    @Provides
    @Singleton
    fun provideCityWeatherRepository(
        api: WeatherApi,
        database: WeatherApiResponseDatabase
    ): WeatherRepository {
        return WeatherRepositoryImplementation(api, database.daoWeather)
    }

    @Provides
    @Singleton
    fun provideCityWeatherDatabase(application: Application): WeatherApiResponseDatabase {
        return Room.databaseBuilder(
            application, WeatherApiResponseDatabase::class.java, "weather"
        ).createFromAsset("database/weather.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        sharedPreferences: SharedPreferences
    ): SettingsRepository {
        return SettingsRepositoryImplementation(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(WeatherApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}