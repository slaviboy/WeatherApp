package com.slaviboy.weather.features.weather.data.local.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.slaviboy.weather.features.weather.data.local.entity.Current
import com.slaviboy.weather.features.weather.data.local.entity.Weather

data class CurrentWithRelations(
    @Embedded
    val current: Current,

    @Relation(
        parentColumn = "weatherId_",
        entityColumn = "maskId"
    )
    val weather: List<Weather>
)