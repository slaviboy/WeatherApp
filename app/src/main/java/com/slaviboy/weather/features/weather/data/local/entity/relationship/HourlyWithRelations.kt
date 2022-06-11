package com.slaviboy.weather.features.weather.data.local.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.slaviboy.weather.features.weather.data.local.entity.Hourly
import com.slaviboy.weather.features.weather.data.local.entity.Weather

data class HourlyWithRelations(
    @Embedded
    val hourly: Hourly,

    @Relation(
        parentColumn = "weatherId_",
        entityColumn = "maskId"
    )
    val weather: List<Weather>
)