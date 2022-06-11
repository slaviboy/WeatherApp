package com.slaviboy.weather.features.weather.data.local.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.slaviboy.weather.features.weather.data.local.entity.Daily
import com.slaviboy.weather.features.weather.data.local.entity.FeelsLike
import com.slaviboy.weather.features.weather.data.local.entity.Temp
import com.slaviboy.weather.features.weather.data.local.entity.Weather

data class DailyWithRelations(
    @Embedded
    val daily: Daily,

    @Relation(
        parentColumn = "tempId_",
        entityColumn = "maskId"
    )
    val temp: Temp,

    @Relation(
        parentColumn = "feelsLikeId_",
        entityColumn = "maskId"
    )
    val feelsLike: FeelsLike,

    @Relation(
        parentColumn = "weatherId_",
        entityColumn = "maskId"
    )
    val weather: List<Weather>
)