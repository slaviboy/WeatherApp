package com.slaviboy.weather.features.weather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temperature")
data class Temp(

    @PrimaryKey(autoGenerate = true) var id: Int? = null,

    var cityId: Int = 0,
    var maskId: Long = 0L,

    val morn: Double? = null,
    val day: Double? = null,
    val eve: Double? = null,
    val night: Double? = null,
    val min: Double? = null,
    val max: Double? = null
)