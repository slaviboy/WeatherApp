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
package com.slaviboy.weather.core.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.PointF
import android.view.WindowManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.slaviboy.weather.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object StaticMethods {

    const val MILLIS_PER_DAY = 86_400_000L

    /**
     * Hide the system UI bottom action bar and top status bar
     */
    fun Activity.hideSystemBars() {

        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    /**
     * Get the sunrise/sunset time with added offset
     * @param timeZoneOffset the offset for the location for example Bulgaria (+2.0).hoursToMilliseconds()
     */
    fun Long.getSunriseAndSunsetWithOffset(timeZoneOffset: Long): Long {

        // !!this offset is added, because the API gives wrong UTC sunrise, sunset times (2 hours) offset
        val additionalOffset = 2f.hoursToMilliseconds()

        return this.getCurrentWithOffset(timeZoneOffset) - additionalOffset
    }

    /**
     * Get the current time with added offset
     * @param timeZoneOffset the offset for the location for example Bulgaria (+2.0).hoursToMilliseconds()
     */
    fun Long.getCurrentWithOffset(timeZoneOffset: Long): Long {
        return this + timeZoneOffset
    }

    /**
     * Get the sunrise, sunset and current times with the added offset
     * @param sunrise the time in ms for the sunrise
     * @param sunset the time in ms for the sunset
     * @param currentTime the current time in ms
     * @param timeZoneOffset the offset for the location for example Bulgaria (+2.0).hoursToMilliseconds()
     */
    fun getTimeWithOffset(sunrise: Long, sunset: Long, currentTime: Long, timeZoneOffset: Long): LongArray {

        val sunriseWithOffset = sunrise.getSunriseAndSunsetWithOffset(timeZoneOffset)
        val sunsetWithOffset = sunset.getSunriseAndSunsetWithOffset(timeZoneOffset)
        val currentWithOffset = currentTime.getCurrentWithOffset(timeZoneOffset)

        return longArrayOf(sunriseWithOffset, sunsetWithOffset, currentWithOffset)
    }

    /**
     * Get the time state for current time : Day, Night, Sunrise, Sunset
     * @param sunrise the time in ms for the sunrise
     * @param sunset the time in ms for the sunset
     * @param currentTime the time in ms of which we want to get the TimeState
     * @param timeZoneOffset the offset for the location for example Bulgaria (+2.0).hoursToMilliseconds()
     */
    fun getTimeState(sunrise: Long, sunset: Long, currentTime: Long, timeZoneOffset: Long): TimeState {

        val (sunrise24Hour, sunset24Hour, current24Hour) =
            getTimeWithOffset(sunrise, sunset, currentTime, timeZoneOffset).map { it.get24HourFormat() }

        return if (current24Hour == sunrise24Hour) TimeState.Sunrise
        else if (current24Hour == sunset24Hour) TimeState.Sunset
        else if (current24Hour > sunrise24Hour && current24Hour < sunset24Hour) TimeState.Day
        else TimeState.Night
    }

    /**
     * Get the hours from a Long date to 24 hour format [0,24]
     */
    fun Long.get24HourFormat(): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    /**
     * Check if the day matches fot two time
     */
    fun daysMatch(time1: Long, time2: Long): Boolean {
        return ((time1 / MILLIS_PER_DAY) == (time2 / MILLIS_PER_DAY))
    }

    /**
     * Find the activity
     */
    fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    /**
     * Mask(store) two Int values in a single Long value, that way we can store two id values
     * as a single value
     * @param a the first number
     * @param b the second number
     * @return the masked Long value containing the two integers
     */
    fun maskTwoIntsToLong(a: Int, b: Int): Long {
        return a.toLong() shl 32 or (b.toLong() and 0xffffffffL)
    }

    fun Int.maskToLong(secondInt: Int): Long {
        return maskTwoIntsToLong(this, secondInt)
    }

    /**
     * Unmask(restore) two Int values from a single Long value, that way we can retrieve two id values
     * from one long value
     * @param c the long masked number
     * @return list with the two numbers
     */
    fun unmaskLongToTwoInts(c: Long): List<Int> {
        val a = (c shr 32).toInt()
        val b = c.toInt()
        return listOf(a, b)
    }

    fun Long.unmaskToTwoInts(): List<Int> {
        return unmaskLongToTwoInts(this)
    }

    /**
     * Get the current time as string from a Long value retrieved from Date().time
     * @param format the format AM/PM or 24 Hour Format
     */
    fun Long?.getHourAsString(format: TimeFormat): String {

        // set the format
        val formatPattern = if (format == TimeFormat.AmPm) {
            "hh:mm aa"
        } else "HH:mm"

        // force english locale to get AM/PM instead of
        val locale = Locale.ENGLISH
        return if (this != null) {
            SimpleDateFormat(formatPattern, locale).format(Date(this))
        } else "n/a"
    }

    /**
     * Get the current day as short abbreviation string from a Long value retrieved from Date().time
     * @return Mon, Tue, Wen,..
     */
    fun Long?.getDayAsShortString(): String {
        return if (this != null) {
            SimpleDateFormat("EE", Locale.getDefault()).format(Date(this))
        } else "n/a"
    }

    /**
     * Get the current day as string from a Long value retrieved from Date().time
     * @return Monday, Tuesday, Wednesday,..
     */
    fun Long?.getDayAsString(): String {
        return if (this != null) {
            SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(this))
        } else "n/a"
    }

    /**
     * Converts the degree value from Celsius to Fahrenheit, for float value
     */
    fun Float.toFahrenheit(): Int {
        return ((this * 1.8f) + 32f).roundToInt()
    }

    /**
     * Converts the degree value from Celsius to Fahrenheit, for integer value
     */
    fun Int.toFahrenheit(): Int {
        return ((this * 1.8f) + 32f).roundToInt()
    }

    /**
     * Map value from one range to another lets say we have the value (this)0.5 is in range [0,1]
     * and we want to map the value to new range [10,20]. Then the returned value will be 15
     */
    fun Float.mapTo(inMin: Float, inMax: Float, outMin: Float, outMax: Float): Float {
        return (this - inMin) * (outMax - outMin) / (inMax - inMin) + outMin
    }

    /**
     * Convert hours to ms
     * 2.0 => 7200000
     */
    fun Float.hoursToMilliseconds(): Long {
        return (this * 3_600_000L).toLong()
    }

    /**
     * Convert color values set as HSV(hue,saturation,value) to Composable Color
     * @param hue the hue range is [0,360]
     * @param saturation the saturation range is [0,1]
     * @param value the value range is [0,1]
     * @param alpha the alpha [0,1]
     */
    fun hsv(hue: Float, saturation: Float, value: Float, alpha: Float = 1f): Color {

        fun hsvToRgbComponent(n: Int, h: Float, s: Float, v: Float): Float {
            val k = (n.toFloat() + h / 60f) % 6f
            return v - (v * s * max(0f, minOf(k, 4 - k, 1f)))
        }

        val red = hsvToRgbComponent(5, hue, saturation, value)
        val green = hsvToRgbComponent(3, hue, saturation, value)
        val blue = hsvToRgbComponent(1, hue, saturation, value)
        return Color(red, green, blue, alpha, ColorSpaces.Srgb)
    }

    /**
     * Convert color values set as HSL(hue,saturation,lightness) to Composable Color
     * @param hue the hue range is [0,360]
     * @param saturation the saturation range is [0,1]
     * @param lightness the lightness range is [0,1]
     * @param alpha the alpha [0,1]
     */
    fun hsl(hue: Float, saturation: Float, lightness: Float, alpha: Float = 1f): Color {

        fun hslToRgbComponent(n: Int, h: Float, s: Float, l: Float): Float {
            val k = (n.toFloat() + h / 30f) % 12f
            val a = s * min(l, 1f - l)
            return l - a * max(-1f, minOf(k - 3, 9 - k, 1f))
        }

        val red = hslToRgbComponent(0, hue, saturation, lightness)
        val green = hslToRgbComponent(8, hue, saturation, lightness)
        val blue = hslToRgbComponent(4, hue, saturation, lightness)
        return Color(red, green, blue, alpha, ColorSpaces.Srgb)
    }

    /**
     * Rotate a point around a center with given angle
     * @param cx rotary center point x coordinate
     * @param cy rotary center point y coordinate
     * @param x x coordinate of the point that will be rotated
     * @param y y coordinate of the point that will be rotated
     * @param angle angle of rotation in degrees
     * @param antiClockwise rotate clockwise or anti-clockwise
     * @param resultPoint object where the result rotational point will be stored
     */
    fun rotate(cx: Float, cy: Float, x: Float, y: Float, angle: Float, antiClockwise: Boolean = false, resultPoint: PointF = PointF()): PointF {

        if (angle == 0f) {
            resultPoint.x = x
            resultPoint.y = y
            return resultPoint
        }

        val radians = if (antiClockwise) {
            (Math.PI / 180) * angle
        } else {
            (Math.PI / -180) * angle
        }

        val cos = Math.cos(radians)
        val sin = Math.sin(radians)
        val nx = (cos * (x - cx)) + (sin * (y - cy)) + cx
        val ny = (cos * (y - cy)) - (sin * (x - cx)) + cy

        resultPoint.x = nx.toFloat()
        resultPoint.y = ny.toFloat()
        return resultPoint
    }

    /**
     * Get the float value as string, if the value is null use 'n/a' as returned value
     */
    fun Float?.toStringSafe(): String {
        return this?.toString() ?: "n/a"
    }

    /**
     * Get the icon res id, if value is null return the default none icon res id
     */
    fun Int?.getSafeIconResId(): Int {
        return this ?: R.drawable.ic_none
    }

    fun getLastUpdateMessage(context: Context, lastUpdatedTime: Long): String {

        val data = Date()
        val timeMillis = (data.time - lastUpdatedTime)

        val days = (timeMillis / (60 * 60 * 24 * 1000))
        val hours = (timeMillis / (60 * 60 * 1000))
        val minutes = (timeMillis / (60 * 1000))
        val seconds = (timeMillis / (1000))

        fun Resources.withSuffixPrefix(value: Long, resId: Int): String {
            return getString(R.string.updated) + " $value " + getString(resId) + " " + getString(R.string.ago)
        }
        return context.resources.let {
            when {
                (days > 100) -> it.getString(R.string.no_update)
                (days > 0) -> it.withSuffixPrefix(days, R.string.days)
                (hours > 0) -> it.withSuffixPrefix(hours, R.string.hours)
                (minutes > 0) -> it.withSuffixPrefix(minutes, R.string.minutes)
                (seconds > 0) -> it.withSuffixPrefix(seconds, R.string.seconds)
                else -> it.getString(R.string.just_now)
            }
        }
    }

    fun getLastUpdateMessageWithDate(context: Context, lastUpdatedTime: Long, timeFormat: TimeFormat): String {

        val data = Date()
        val timeMillis = (data.time - lastUpdatedTime)

        val days = (timeMillis / (60 * 60 * 24 * 1000))

        return if (days > 100) {
            context.resources.getString(R.string.no_update)
        } else {
            context.resources.getString(R.string.last_updated) + " : " +
                    lastUpdatedTime.getHourAsString(timeFormat) + " (" +
                    SimpleDateFormat("dd.mm.yyyy", Locale.ENGLISH).format(Date(lastUpdatedTime)) + ")"
        }
    }
}