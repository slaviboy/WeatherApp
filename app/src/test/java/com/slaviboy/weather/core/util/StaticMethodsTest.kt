package com.slaviboy.weather.core.util

import android.graphics.PointF
import com.google.common.truth.Truth.assertThat
import com.slaviboy.weather.core.util.StaticMethods.getHourAsString
import com.slaviboy.weather.core.util.StaticMethods.mapTo
import com.slaviboy.weather.core.util.StaticMethods.maskToLong
import com.slaviboy.weather.core.util.StaticMethods.toFahrenheit
import com.slaviboy.weather.core.util.StaticMethods.unmaskToTwoInts
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Use Robolectric since we want to use the PointF class
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class StaticMethodsTest {

    @Before
    fun setUp() {
    }

    @Test
    fun maskTwoIntsToLong() {

        val int1 = 896739
        val int2 = 38320
        val long = 3851464678086064L

        val resultLong = StaticMethods.maskTwoIntsToLong(int1, int2)
        assertThat(resultLong).isEqualTo(long)
        assertThat(int1.maskToLong(int2)).isEqualTo(long)
    }

    @Test
    fun unmaskLongToTwoInts() {

        val int1 = 896739
        val int2 = 38320
        val long = 3851464678086064L

        val resultInts = StaticMethods.unmaskLongToTwoInts(long)
        assertThat(resultInts.size).isEqualTo(2)
        assertThat(resultInts).isEqualTo(listOf(int1, int2))
        assertThat(long.unmaskToTwoInts()).isEqualTo(listOf(int1, int2))
    }

    @Test
    fun getTimeAsFormattedString() {

        val dateTime = 1645270252000L

        val resultAmPm = dateTime.getHourAsString(TimeFormat.AmPm)
        val result24Hours = dateTime.getHourAsString(TimeFormat.Hours24)
        assertThat(resultAmPm).isEqualTo("01:30 PM")
        assertThat(result24Hours).isEqualTo("13:30")
    }

    @Test
    fun celsiusToFahrenheit() {
        val celsiusFloat = 35.3f
        assertThat(celsiusFloat.toFahrenheit()).isEqualTo(96)

        val celsiusInt = 35
        assertThat(celsiusInt.toFahrenheit()).isEqualTo(95)
    }

    @Test
    fun mapFloat() {
        val floatValue = 50f

        // from [0,100] -> [25,75]
        val result1 = floatValue.mapTo(0f, 100f, 25f, 75f)
        assertThat(result1).isEqualTo(50f)

        // from [0,100] -> [-10,0]
        val result2 = floatValue.mapTo(0f, 100f, -10f, 0f)
        assertThat(result2).isEqualTo(-5f)

        // from [0,100] -> [10,20]
        val result3 = floatValue.mapTo(0f, 100f, 10f, 20f)
        assertThat(result3).isEqualTo(15f)

        // from [50,100] -> [0,1]
        val result4 = floatValue.mapTo(50f, 100f, 0f, 1f)
        assertThat(result4).isEqualTo(0f)

        // from [50,100] -> [0,1]
        val result5 = floatValue.mapTo(50f, 100f, 0f, 1f)
        assertThat(result5).isEqualTo(0f)

        // from [25,125] -> [0,1]
        val result6 = floatValue.mapTo(25f, 125f, 0f, 1f)
        assertThat(result6).isEqualTo(0.25f)
    }

    @Test
    fun rotatePointAroundPivot() {

        val (x, y) = listOf(50f, 100f)
        val (cx, cy) = listOf(100f, 100f)

        // use point object thanks to Robolectric
        val resultPoint = PointF()
        StaticMethods.rotate(cx, cy, x, y, 90f, true, resultPoint)
        assertThat(resultPoint).isEqualTo(PointF(100f, 150f))

        StaticMethods.rotate(cx, cy, x, y, 90f, false, resultPoint)
        assertThat(resultPoint).isEqualTo(PointF(100f, 50f))

        StaticMethods.rotate(cx, cy, x, y, 180f, true, resultPoint)
        assertThat(resultPoint).isEqualTo(PointF(150f, 100f))

        StaticMethods.rotate(cx, cy, x, y, 180f, false, resultPoint)
        assertThat(resultPoint).isEqualTo(PointF(150f, 100f))

        StaticMethods.rotate(cx, cy, x, y, 270f, false, resultPoint)
        assertThat(resultPoint).isEqualTo(PointF(100f, 150f))
    }
}