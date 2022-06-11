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
package com.slaviboy.weather.features.weather.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.slaviboy.weather.features.weather.data.local.entity.*

@Database(
    entities = [
        WeatherApiResponse::class, City::class, Current::class,
        Daily::class, FeelsLike::class, Hourly::class,
        Temp::class, Weather::class
    ],
    version = 1
)
abstract class WeatherApiResponseDatabase : RoomDatabase() {

    abstract val daoWeather: WeatherApiResponseDao

    companion object {

        @Volatile
        private var INSTANCE: WeatherApiResponseDatabase? = null

        /**
         * We do not use it in this case, since we are using Hilt for dependencies injection, instead of
         * creating a instance of the database in our application class
         */
        fun getInstance(context: Context): WeatherApiResponseDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WeatherApiResponseDatabase::class.java,
                    "weather_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}