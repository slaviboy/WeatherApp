package com.slaviboy.weather.features.weather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(

    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,

    val name: String = "None",
    val state: String? = null,
    val country: String = "None",
    val lat: Float = 0f,
    val lon: Float = 0f,
    @ColumnInfo(name = "timezone") val timeZoneOffset: Float? = 0f

) {

}