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
package com.slaviboy.weather.ui.composable.screens

import android.graphics.Rect
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.graphics.ColorUtils
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.slaviboy.composeunits.adh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.weather.R
import com.slaviboy.weather.core.util.StaticMethods
import com.slaviboy.weather.core.util.StaticMethods.getLastUpdateMessage
import com.slaviboy.weather.core.util.StaticMethods.getLastUpdateMessageWithDate
import com.slaviboy.weather.core.util.StaticMethods.toFahrenheit
import com.slaviboy.weather.core.util.TemperatureType
import com.slaviboy.weather.core.util.TimeFormat
import com.slaviboy.weather.features.weather.domain.model.DailyInfo
import com.slaviboy.weather.features.weather.domain.model.HourlyInfo
import com.slaviboy.weather.features.weather.domain.model.SunriseSunsetInfo
import com.slaviboy.weather.features.weather.presentation.CityWeatherViewModel
import com.slaviboy.weather.ui.composable.main.Clouds
import com.slaviboy.weather.ui.theme.RobotoFont
import com.slaviboy.weather.ui.theme.TextColor
import com.slaviboy.weather.ui.theme.TextSemiTransparentColor
import kotlin.math.roundToInt

@Composable
fun HomeScreen(viewModel: CityWeatherViewModel) {

    val scrollState = rememberScrollState()

    val isRefreshing by viewModel.isRefreshing
    val weatherApiResponseInfo = viewModel.weatherApiResponseApiResponseInfo.value.weatherApiResponseInfo

    val topColor by animateColorAsState(
        targetValue = weatherApiResponseInfo.topBackgroundColor,
        animationSpec = tween(durationMillis = 500)
    )
    val bottomColor by animateColorAsState(
        targetValue = weatherApiResponseInfo.bottomBackgroundColor,
        animationSpec = tween(durationMillis = 500)
    )
    val gradientColors = arrayListOf(topColor, bottomColor)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    gradientColors
                )
            )
    ) {}

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = {

            val selectedCityId = viewModel.selectedCityId.value
            val lat = viewModel.currentLocationLat.value
            val lon = viewModel.currentLocationLon.value
            val useCurrentLocation = viewModel.useCurrentLocation.value
            if (useCurrentLocation) {
                if (lat == -1f && lon == -1f) {
                    // request refresh of the GPS coordinates
                } else {
                    viewModel.requestWeatherByGeographicLocation(lat, lon, false, 0L)
                }
            } else if (!useCurrentLocation && selectedCityId != -1) {
                viewModel.requestWeatherByCity(requestCachedData = false, delayTime = 0L)
            }
        },
        indicator = { state, triggerDp ->

            if (state.isRefreshing) {
                // display loading progress bar
            } else {
                val triggerPx = with(LocalDensity.current) { triggerDp.toPx() }
                val progress = (state.indicatorOffset / triggerPx).coerceIn(0f, 1f)
                viewModel.apply {
                    setShowDragDownToRefreshMsg(progress >= 0.9)
                    setSwipeRefreshTopPadding(progress * with(LocalDensity.current) { 0.3.dw.toPx() })
                }
            }
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState, enabled = true)
                .offset { IntOffset(0, viewModel.swipeRefreshOffsetTop.value.roundToInt()) }
        ) {
            HiddenSwipeRefreshBox(viewModel)
            CurrentTemperatureBox(viewModel)

            if (viewModel.showNext4DaysForecastBox.value) {
                ApiLinkLogo()
                NexDaysBox(viewModel)
            }

            if (viewModel.showNext24HoursForecastBox.value) {
                Next24HourForecastBox(viewModel)
            }

            if (viewModel.showComfortLevelBox.value) {
                ComfortLevelBox(viewModel)
            }

            if (viewModel.showWindBox.value) {
                WindBox(viewModel)
            }

            if (viewModel.showSunriseAndSunsetBox.value) {
                SunriseAndSunset(viewModel)
            }
        }
    }
    TopHomeBar(viewModel = viewModel, gradientColors = gradientColors,
        onOpenSettingsPage = {
            viewModel.switchHomeToSettings()
        })

    if (viewModel.enableAnimation.value) {
        //SnowFall()
        Clouds()
    }
}

@Composable
fun HiddenSwipeRefreshBox(viewModel: CityWeatherViewModel) {

    val angle: Float by animateFloatAsState(
        targetValue = if (viewModel.showDragDownToRefreshMsg.value) 0f else 180f,
        animationSpec = tween(
            durationMillis = 500
        )
    )

    val context = LocalContext.current
    var lastUpdateMessage by remember { mutableStateOf("") }
    LaunchedEffect(viewModel.lastUpdatedTime.value) {
        lastUpdateMessage = getLastUpdateMessageWithDate(context, viewModel.lastUpdatedTime.value, viewModel.timeFormat.value)
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .height(0.2.dw)
            .fillMaxWidth()
    ) {
        val fontSize = 0.035.sw

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .matchParentSize()
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(end = 0.1.dw)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow),
                    contentDescription = null,
                    modifier = Modifier
                        .width(0.1.dw)
                        .rotate(angle)
                )
                Text(
                    fontFamily = RobotoFont,
                    text = stringResource(id = if (!viewModel.showDragDownToRefreshMsg.value) R.string.drag_down_to_refresh else R.string.release_to_refresh),
                    color = TextColor,
                    textAlign = TextAlign.Center,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Light
                )
            }
            Text(
                fontFamily = RobotoFont,
                text = lastUpdateMessage,
                color = TextColor,
                textAlign = TextAlign.Center,
                fontSize = fontSize.times(0.9f),
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun TopHomeBar(viewModel: CityWeatherViewModel, gradientColors: ArrayList<Color>, onOpenSettingsPage: () -> Unit) {

    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    var lastUpdateMessage by remember { mutableStateOf("") }
    LaunchedEffect(viewModel.lastUpdatedTime.value) {
        lastUpdateMessage = getLastUpdateMessage(context, viewModel.lastUpdatedTime.value)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.2.dw)
            .background(
                Brush.verticalGradient(
                    gradientColors,
                    startY = 0f,
                    endY = with(LocalDensity.current) { 1.adh.toPx() }
                )
            )
    ) {


        Image(
            painterResource(R.drawable.ic_settings), null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .offset(x = -(0.02.dw))
                .padding(0.03.dw)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    onOpenSettingsPage.invoke()
                }
                .padding(0.033.dw)
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center)
                .padding(bottom = 0.04.dw)
        ) {

            if (viewModel.useCurrentLocation.value) {
                Image(
                    painter = painterResource(id = R.drawable.ic_current_location),
                    contentDescription = null,
                    modifier = Modifier.size(0.04.dw)
                )
                Spacer(modifier = Modifier.width(0.02.dw))
            }
            Text(
                text = (viewModel.resultCityById.value?.name) ?: stringResource(id = R.string.none),
                fontFamily = RobotoFont,
                textAlign = TextAlign.Center,
                fontSize = 0.041.sw,
                color = TextColor
            )
        }

        Text(
            fontFamily = RobotoFont,
            text = lastUpdateMessage,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 0.03.dw),
            textAlign = TextAlign.Center,
            fontSize = 0.028.sw,
            color = TextSemiTransparentColor
        )
    }
}

@Composable
fun CurrentTemperatureBox(viewModel: CityWeatherViewModel, width: Dp = 0.7.dw) {

    val fontSize = 0.039.sw
    val currentInfo = viewModel.weatherApiResponseApiResponseInfo.value.weatherApiResponseInfo.currentInfo

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxWidth()
            .height(1.18.dw)
    ) {

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.10.dw)
        ) {
            TemperatureCanvas(
                width = width,
                viewModel = viewModel,
                minTemperature = currentInfo.minTemperature ?: 0f,
                maxTemperature = currentInfo.maxTemperature ?: 0f,
                temperature = currentInfo.currentTemperature ?: 0f
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.34.dw)
        ) {
            Image(
                painter = painterResource(id = currentInfo.weatherIconResId),
                contentDescription = null,
                modifier = Modifier.size(0.14.dw)
            )
            Text(
                fontFamily = RobotoFont,
                text = currentInfo.dayFormatted,
                color = TextColor,
                textAlign = TextAlign.Center,
                fontSize = fontSize,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(top = 0.24.dw)
            )
            Text(
                fontFamily = RobotoFont,
                text = currentInfo.weatherTitle,
                color = TextColor,
                textAlign = TextAlign.Center,
                fontSize = fontSize.times(1.25),
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(top = 0.45.dw)
            )
            Text(
                fontFamily = RobotoFont,
                text = "(${currentInfo.weatherSubtitle})",
                color = TextColor,
                textAlign = TextAlign.Center,
                fontSize = fontSize.times(0.8),
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(top = 0.56.dw)
            )
        }

    }
}

@Composable
fun ApiLinkLogo() {

    val fontSize = 0.039.sw
    val uriHandler = LocalUriHandler.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .wrapContentSize()
            .fillMaxWidth()
            .padding(bottom = 0.01.dw, end = 0.08.dw)
            .clickable {
                uriHandler.openUri("https://openweathermap.org/")
            }
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_api_logo_small),
            contentDescription = null,
            modifier = Modifier
                .size(0.07.dw)
                .padding(0.003.dw)
        )
        Spacer(modifier = Modifier.width(0.01.dw))
        Text(
            fontFamily = RobotoFont,
            text = "OpenWeather",
            color = TextColor,
            textAlign = TextAlign.Center,
            fontSize = fontSize.times(0.84),
            fontWeight = FontWeight.Light
        )
    }

    Divider(
        color = TextSemiTransparentColor, thickness = 1.dp, modifier = Modifier
            .padding(horizontal = 0.08.dw)
            .fillMaxWidth(0.9f)
    )

    Spacer(modifier = Modifier.height(0.01.dw))
}

@Composable
fun TemperatureCanvas(
    modifier: Modifier = Modifier,
    viewModel: CityWeatherViewModel,
    minTemperature: Float,
    maxTemperature: Float,
    temperature: Float,
    width: Dp = 0.7.dw,
    totalBars: Int = 90,
    temperatureAnimationSwitch: Any = viewModel.weatherApiResponseApiResponseInfo.value,
    temperatureSymbol: Char = '°',
    separateTextComponents: Boolean = true,
    minColoredBarIndexDefault: Int? = null,
    maxColoredBarIndexDefault: Int? = null,
    numBarsFor0Degrees: Int = totalBars / 5,
    startAngle: Float = -180f + 30f,
    endAngle: Float = 180f - 30f,
    coloredBarIndex: ColoredBarIndex = ColoredBarIndex.MAX,
    currentTemperatureCircleScale: Float = 1f,
    minMaxTemperatureTextMargin: Float = 1.3f,
    minMaxTemperatureTextScale: Float = 1f,
    isUsedFotTemeprature: Boolean = true,
    onColorGenerated: (temperature: Float) -> Int = { temp ->
        when {
            temp < 0f -> 0xFF4286FB
            temp < 15f -> 0xFF86FF21
            else -> 0xFFFB822C
        }.toInt()
    }
) {

    val numBars by remember { mutableStateOf(totalBars - 1) }
    val anglePerBar = (endAngle - startAngle) / numBars

    var temperatureDiff = maxTemperature - minTemperature
    val indexAt0Degrees = numBarsFor0Degrees * 2
    val minColoredBarIndex = minColoredBarIndexDefault ?: if (minTemperature < 0) {

        val diff = indexAt0Degrees + minTemperature.toInt()
        if (diff < 0) {

            val diff2 = indexAt0Degrees + maxTemperature.toInt()
            temperatureDiff = Math.max((diff2 - numBarsFor0Degrees), 0).toFloat()
            0
        } else {
            diff
        }
    } else {
        Math.min(indexAt0Degrees + minTemperature.toInt(), totalBars - numBarsFor0Degrees)
    }
    val maxColoredBarIndex = maxColoredBarIndexDefault ?: minColoredBarIndex + numBarsFor0Degrees + temperatureDiff.toInt()

    val color1 = onColorGenerated.invoke(minTemperature)
    val color2 = onColorGenerated.invoke(maxTemperature)

    var temperatureForText by remember { mutableStateOf(0) }
    LaunchedEffect(temperatureAnimationSwitch) {

        val animation = TargetBasedAnimation(
            animationSpec = tween(1500),
            typeConverter = Int.VectorConverter,
            initialValue = minTemperature.roundToInt(),
            targetValue = temperature.roundToInt()
        )

        val startTime = withFrameNanos { it }
        do {
            val playTime = withFrameNanos { it } - startTime
            temperatureForText = animation.getValueFromNanos(playTime)
        } while (temperatureForText < temperature)
    }

    var animationValueColoredBarIndex by remember { mutableStateOf(0) }
    LaunchedEffect(temperatureAnimationSwitch) {

        val animationColoredBarIndex = TargetBasedAnimation(
            animationSpec = tween(1500),
            typeConverter = Int.VectorConverter,
            initialValue = minColoredBarIndex,
            targetValue = maxColoredBarIndex
        )

        val startTime = withFrameNanos { it }
        do {
            val playTime = withFrameNanos { it } - startTime
            animationValueColoredBarIndex = animationColoredBarIndex.getValueFromNanos(playTime)
        } while (animationValueColoredBarIndex < maxColoredBarIndex)
    }

    var currentColoredBarIndex by remember { mutableStateOf(0) }
    var animationValueCircleIndex by remember { mutableStateOf(0) }
    LaunchedEffect(temperatureAnimationSwitch) {

        val targetValue = minColoredBarIndex + ((temperature - minTemperature) / (maxTemperature - minTemperature)) * (maxColoredBarIndex - minColoredBarIndex)
        currentColoredBarIndex = targetValue.toInt()
        val animationCircleIndex = TargetBasedAnimation(
            animationSpec = tween(1500),
            typeConverter = Int.VectorConverter,
            initialValue = minColoredBarIndex,
            targetValue = currentColoredBarIndex.toInt()
        )

        val startTime = withFrameNanos { it }
        do {
            val playTime = withFrameNanos { it } - startTime
            animationValueCircleIndex = animationCircleIndex.getValueFromNanos(playTime)
        } while (animationValueCircleIndex <= currentColoredBarIndex.toInt())
    }

    val onDraw: DrawScope.() -> Unit = {

        val barsWidth = width.times(0.005f).toPx()
        val barsHeight = width.times(0.08f).toPx()

        val halfWidth = width.times(0.5f).toPx()
        val offset = Offset(halfWidth, halfWidth)

        val circleRadius = width.times(0.015f).toPx()

        fun getBarColorAtIndex(i: Int): Color {

            val endIndex = if (coloredBarIndex == ColoredBarIndex.MAX) {
                animationValueColoredBarIndex
            } else animationValueCircleIndex

            if (minTemperature == maxTemperature) {
                return TextColor
            }
            return if (i >= minColoredBarIndex && i <= endIndex) {
                val ratio = i.toFloat() / totalBars
                Color(ColorUtils.blendARGB(color1, color2, ratio))
            } else TextColor
        }

        fun getBarWidthAtIndex(i: Int): Float {

            val endIndex = if (coloredBarIndex == ColoredBarIndex.MAX) {
                animationValueColoredBarIndex
            } else animationValueCircleIndex

            if (minTemperature == maxTemperature) {
                return barsWidth
            }
            return if (i >= minColoredBarIndex && i <= endIndex) {
                barsWidth * 1.7f
            } else barsWidth
        }

        var i = 0
        var totalDegree = startAngle
        while (i <= numBars) {

            val actualBarsHeight = if (i == 0 || i == numBars) {
                barsHeight.times(1.4).toFloat()
            } else barsHeight

            val barColor = getBarColorAtIndex(i)
            val barWidth = getBarWidthAtIndex(i)

            rotate(totalDegree, offset) {
                drawRect(
                    color = barColor,
                    topLeft = Offset(halfWidth, -(actualBarsHeight - barsHeight)),
                    size = Size(barWidth, actualBarsHeight)
                )
            }

            totalDegree += anglePerBar
            i++
        }

        val degree = startAngle + animationValueCircleIndex * anglePerBar
        val alpha = (animationValueCircleIndex - minColoredBarIndex) / (currentColoredBarIndex - minColoredBarIndex).toFloat()
        rotate(degree, offset) {
            drawCircle(
                color = getBarColorAtIndex(animationValueCircleIndex),
                radius = circleRadius.times(currentTemperatureCircleScale),
                center = Offset(halfWidth, barsHeight.times(1.6).toFloat()),
                //alpha = if (alpha > 1f) 1f else if (alpha < 0f) 0f else if (alpha.isNaN()) 1f else alpha
            )
        }

        val paint = Paint().asFrameworkPaint()
        paint.apply {
            isAntiAlias = true
            textSize = (width.toPx() * 0.06f) * minMaxTemperatureTextScale
            textAlign = android.graphics.Paint.Align.CENTER
            color = android.graphics.Color.WHITE
        }
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.apply {

                fun drawText(degree: Float, index: Int) {

                    val text = if (viewModel.temperatureType.value == TemperatureType.Celsius || !isUsedFotTemeprature) {
                        "${degree.roundToInt()}$temperatureSymbol"
                    } else "${degree.toFahrenheit()}$temperatureSymbol"
                    val bound = Rect()
                    paint.getTextBounds(text, 0, text.length, bound)

                    val angle = startAngle + index * anglePerBar
                    val rotatedPoint = StaticMethods.rotate(center.x, center.y, center.x, -bound.height() * minMaxTemperatureTextMargin, angle)
                    drawText(text, rotatedPoint.x, rotatedPoint.y + bound.height() / 2f, paint)
                }

                if (minTemperature != maxTemperature) {
                    drawText(minTemperature, minColoredBarIndex)
                    if (animationValueColoredBarIndex >= maxColoredBarIndex) {
                        drawText(maxTemperature, maxColoredBarIndex)
                    }
                }
            }
        }
    }

    val fontSize = if (minTemperature == maxTemperature) {
        0.15.sw
    } else 0.24.sw

    Box(
        modifier = modifier
            .size(width, width)
    ) {
        Canvas(
            modifier = modifier
                .size(width, width),
            onDraw = onDraw
        )

        val temp = if (viewModel.temperatureType.value == TemperatureType.Celsius || !isUsedFotTemeprature) {
            temperatureForText
        } else temperatureForText.toFahrenheit()

        if (separateTextComponents) {
            val constrainCenter: ConstrainScope.() -> Unit = {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            val constrainCenterHorizontally: ConstrainScope.() -> Unit = {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            val constrainCenterVertically: ConstrainScope.() -> Unit = {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }

            ConstraintLayout(
                modifier = modifier
                    .size(width, width)
            ) {

                val tempText = if (minTemperature == maxTemperature) "N/A" else "${Math.abs(temp)}"
                val (signSymbol, text, degreeSymbol) = createRefs()
                Text(
                    fontFamily = RobotoFont,
                    text = if (temp < 0) "-" else "",
                    color = TextColor,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Thin,
                    modifier = Modifier.constrainAs(signSymbol) {
                        end.linkTo(text.start, margin = 0.01.dw)
                        constrainCenterVertically.invoke(this)
                    }
                )
                Text(
                    fontFamily = RobotoFont,
                    text = tempText,
                    color = TextColor,
                    textAlign = TextAlign.Center,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Thin,
                    modifier = Modifier.constrainAs(text, constrainBlock = constrainCenter)
                )
                Text(
                    fontFamily = RobotoFont,
                    text = "$temperatureSymbol",
                    color = TextColor,
                    fontSize = fontSize.times(0.65),
                    fontWeight = FontWeight.Thin,
                    modifier = Modifier
                        .constrainAs(degreeSymbol) {
                            start.linkTo(text.end)
                            top.linkTo(text.top, 0.02.dw)
                        }
                )
            }
        } else {

            val tempText = if (minTemperature == maxTemperature) {
                "N/A"
            } else {
                val sign = if (temp < 0) "-" else ""
                "$sign${Math.abs(temp)}$temperatureSymbol"
            }
            Text(
                fontFamily = RobotoFont,
                text = tempText,
                color = TextColor,
                fontSize = fontSize.times(0.3f),
                fontWeight = FontWeight.Thin,
                modifier = Modifier.align(Alignment.Center)
            )
        }

    }

}

@Composable
fun NexDaysBox(viewModel: CityWeatherViewModel) {

    val temperatureType = viewModel.temperatureType.value
    val dailyInfo = viewModel.weatherApiResponseApiResponseInfo.value.weatherApiResponseInfo.next4DaysForecastInfo

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.wrapContentWidth()
    ) {
        Spacer(modifier = Modifier.height(0.03.dw))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 0.1.dw)
        ) {
            NextDayComponent(dailyInfo[0], temperatureType)
            NexDaysBoxDivider()
            NextDayComponent(dailyInfo[1], temperatureType)
            NexDaysBoxDivider()
            NextDayComponent(dailyInfo[2], temperatureType)
            NexDaysBoxDivider()
            NextDayComponent(dailyInfo[3], temperatureType)
        }
        Spacer(modifier = Modifier.height(0.07.dw))
    }
}

@Composable
fun NexDaysBoxDivider() {
    Divider(
        color = TextSemiTransparentColor,
        modifier = Modifier
            .height(0.21.dw)
            .width(1.dp)
    )
}

@Composable
fun NextDayComponent(dailyInfo: DailyInfo, temperatureType: TemperatureType) {

    val fontSize = 0.035.sw
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(0.03.dw))
        Image(
            painter = painterResource(id = dailyInfo.weatherIconResId),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .width(0.11.dw)
        )
        Spacer(modifier = Modifier.height(0.02.dw))
        Text(
            fontFamily = RobotoFont,
            text = dailyInfo.getMinMaxTemperatureAsString(temperatureType),
            color = TextColor,
            textAlign = TextAlign.Center,
            fontSize = fontSize,
            fontWeight = FontWeight.Light
        )
        Spacer(modifier = Modifier.height(0.01.dw))
        Text(
            fontFamily = RobotoFont,
            text = dailyInfo.dayShortFormatted,
            color = TextColor,
            textAlign = TextAlign.Center,
            fontSize = fontSize,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
fun Next24HourForecastBox(viewModel: CityWeatherViewModel) {

    val scrollState = rememberScrollState()
    val fontSize = 0.038.sw
    val height = 0.5.dw
    val data = viewModel.weatherApiResponseApiResponseInfo.value.weatherApiResponseInfo.next24HoursForecastInfo

    val path by remember {
        mutableStateOf(Path())
    }
    val onDraw: DrawScope.() -> Unit = {

        val radius = 0.008.dw.toPx()
        val offsetStart = 0.1.dw.toPx()
        val offsetPerComponent = 0.2.dw.toPx()
        val offsetTopMin = 0.12.dw.toPx()
        val offsetTopMax = 0.37.dw.toPx()

        // find min/max temperature from all available hourly temps
        var minTemperature = Int.MAX_VALUE.toFloat()
        var maxTemperature = Int.MIN_VALUE.toFloat()
        data.forEach {
            it.temperature?.let {
                if (it < minTemperature) minTemperature = it
                if (it > maxTemperature) maxTemperature = it
            }
        }

        val points = ArrayList<Offset>()
        data.forEachIndexed { i, next24HourData ->

            val left = offsetStart + i * offsetPerComponent
            val top = offsetTopMax - (offsetTopMax - offsetTopMin) * (((next24HourData.temperature ?: 0f) - minTemperature) / (maxTemperature - minTemperature))
            val currentPoint = Offset(left, top)
            points.add(currentPoint)
        }

        val drawWithCurve = false
        if (drawWithCurve) {
            points.add(points.last())
            drawPath(path.cubicCurve(points), TextColor, 1f, Stroke(width = 1.dp.toPx()))

            points.forEachIndexed { i, point ->
                drawCircle(TextColor, radius, point)
            }
        } else {
            points.forEachIndexed { i, point ->
                if (i > 0) {
                    drawLine(TextColor, points[i - 1], point, 1.dp.toPx())
                }
                drawCircle(TextColor, radius, point)
            }
        }

        val textOffsetBottom = 0.03.dw.toPx()
        val paint = Paint().asFrameworkPaint()
        paint.apply {
            isAntiAlias = true
            textSize = 0.033.dw.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            color = android.graphics.Color.WHITE
        }
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.apply {
                points.forEachIndexed { i, point ->
                    if (i < data.size) {
                        drawText(data[i].getTemperatureAsString(viewModel.temperatureType.value), point.x, point.y - textOffsetBottom, paint)
                    }
                }
            }
        }
    }

    TabSeparator(R.string.forecast_for_next_24_hours)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {

        Column(
            modifier = Modifier
                .horizontalScroll(scrollState)
        ) {
            Row() {
                Spacer(modifier = Modifier.width(0.1.dw))
                Canvas(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(0.4.dw),
                    onDraw = onDraw
                )
            }
            Row() {
                Spacer(modifier = Modifier.width(0.1.dw))
                data.forEachIndexed { i, next24HourData ->
                    Next24HourComponent(next24HourData, viewModel.timeFormat.value)
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(0.06.dw))
}

@Composable
fun Next24HourComponent(hourlyInfo: HourlyInfo, timeFormat: TimeFormat) {

    val fontSize = 0.035.sw
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(0.2.dw)
    ) {
        Spacer(modifier = Modifier.height(0.03.dw))
        Image(
            painter = painterResource(id = hourlyInfo.weatherIconResId),
            contentDescription = null,
            Modifier.width(0.09.dw)
        )
        Spacer(modifier = Modifier.height(0.02.dw))
        Text(
            fontFamily = RobotoFont,
            text = hourlyInfo.getHour(timeFormat),
            color = TextColor,
            textAlign = TextAlign.Center,
            fontSize = fontSize,
            fontWeight = FontWeight.Light
        )
        Spacer(modifier = Modifier.height(0.01.dw))
    }
}

@Composable
fun ComfortLevelBox(viewModel: CityWeatherViewModel) {

    val fontSize = 0.038.sw
    val totalBars = 70
    val comfortLevelInfo = viewModel.weatherApiResponseApiResponseInfo.value.weatherApiResponseInfo.comfortLevelInfo

    TabSeparator(R.string.comfort_level)
    Spacer(modifier = Modifier.height(0.07.dw))
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(1.dw)
                .padding(horizontal = 0.1.dw)
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    fontFamily = RobotoFont,
                    text = stringResource(R.string.humidity),
                    color = TextColor,
                    textAlign = TextAlign.Center,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(0.02.dw))
                TemperatureCanvas(
                    viewModel = viewModel,
                    width = 0.3.dw,
                    totalBars = totalBars,
                    minTemperature = comfortLevelInfo.minHumidity,
                    maxTemperature = comfortLevelInfo.maxHumidity,
                    temperature = comfortLevelInfo.humidity,
                    temperatureAnimationSwitch = viewModel.weatherApiResponseApiResponseInfo.value,
                    temperatureSymbol = '%',
                    separateTextComponents = false,
                    minColoredBarIndexDefault = 0,
                    maxColoredBarIndexDefault = totalBars,
                    numBarsFor0Degrees = 0,
                    coloredBarIndex = ColoredBarIndex.CURRENT,
                    currentTemperatureCircleScale = 1.5f,
                    minMaxTemperatureTextMargin = 1.8f,
                    minMaxTemperatureTextScale = 1.4f,
                    isUsedFotTemeprature = false
                ) {
                    0xFF12D4DC.toInt()
                }

            }

            InfoContainer(listOf(R.string.feels_like, R.string.uv_index), comfortLevelInfo.getDataAsString(viewModel.temperatureType.value))
        }
    }
    Spacer(modifier = Modifier.height(0.11.dw))
}

@Composable
fun WindBox(viewModel: CityWeatherViewModel) {

    val windInfo = viewModel.weatherApiResponseApiResponseInfo.value.weatherApiResponseInfo.windInfo

    // 10 m/s Fresh Breeze => 0.3 rps
    val rps = (windInfo.speed ?: 10f) * (0.3f / 10f)  // m/s

    TabSeparator(R.string.wind)
    Spacer(modifier = Modifier.height(0.04.dw))
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(1.dw)
                .height(0.39.dw)
                .padding(horizontal = 0.1.dw)
        ) {

            Box(
                modifier = Modifier
                    .wrapContentSize()
            ) {
                Turbine(height = 0.27.dw, rps = rps)

                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(start = 0.15.dw)
                        .align(Alignment.BottomEnd)
                ) {
                    Turbine(height = 0.18.dw, rps = rps)
                }
            }

            InfoContainer(listOf(R.string.direction, R.string.speed), windInfo.getDataAsString(viewModel.temperatureType.value))
        }
    }
    Spacer(modifier = Modifier.height(0.09.dw))
}

@Composable
fun SunriseAndSunset(viewModel: CityWeatherViewModel) {

    val sunriseSunsetInfo = viewModel.weatherApiResponseApiResponseInfo.value.weatherApiResponseInfo.sunriseSunsetInfo
    val timeFormat = viewModel.timeFormat.value

    TabSeparator(R.string.sunrise_and_sunset)

    val fontSize = 0.038.sw
    val width = 1.dw
    val height = 0.45.dw

    Spacer(modifier = Modifier.height(0.04.dw))
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .width(width)
                .height(height)
                .padding(horizontal = 0.1.dw)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(bottom = 0.01.dw, end = 0.03.dw)
            ) {

                Image(
                    painter = painterResource(id = sunriseSunsetInfo.moonIconSrc),
                    contentDescription = null,
                    modifier = Modifier
                        .size(0.066.dw)
                )
                Spacer(modifier = Modifier.width(0.01.dw))
                Text(
                    fontFamily = RobotoFont,
                    text = stringResource(id = sunriseSunsetInfo.moonPhaseStrResId),
                    color = TextColor,
                    textAlign = TextAlign.Center,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Light
                )
            }

            // since we have horizontal padding of 0.1dw for each side
            SunriseAndSunsetComponent(sunriseSunsetInfo, timeFormat, width.times(0.8f), height)
        }
    }
    Spacer(modifier = Modifier.height(0.16.dw))
}

@Composable
fun SunriseAndSunsetComponent(
    sunriseSunsetInfo: SunriseSunsetInfo,
    format: TimeFormat,
    width: Dp, height: Dp
) {

    val widthPx = with(LocalDensity.current) { width.toPx() }
    val heightPx = with(LocalDensity.current) { height.toPx() }
    val heightAddedPx = with(LocalDensity.current) { height.times(1.3f).toPx() }
    val imageSize = with(LocalDensity.current) { (0.14.dw).toPx() }
    val halfImageSize = imageSize / 2f
    val centerX = widthPx / 2f
    val centerY = heightPx
    val sunImageInitialX = ((widthPx / 2f) - heightAddedPx / 2f) - halfImageSize
    val sunImageInitialY = heightPx - halfImageSize
    val imageSizeObject = Size(imageSize, imageSize)

    val vector = ImageVector.vectorResource(id = R.drawable.ic_weather7)
    val painter = rememberVectorPainter(image = vector)

    val infiniteTransition = rememberInfiniteTransition()
    val pivotAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    var targetDegrees by remember { mutableStateOf(0f) }
    var angle by remember { mutableStateOf(0f) }
    LaunchedEffect(sunriseSunsetInfo) {

        angle = 0f
        val unsafeAngle = sunriseSunsetInfo.sunAngle()
        targetDegrees = when {
            unsafeAngle <= 0f -> 0f
            unsafeAngle >= 180f -> 180f
            else -> unsafeAngle
        }

        val animation = TargetBasedAnimation(
            animationSpec = tween(3000, 0, LinearOutSlowInEasing),
            typeConverter = Float.VectorConverter,
            initialValue = 0f,
            targetValue = targetDegrees
        )

        val startTime = withFrameNanos { it }
        do {
            val playTime = withFrameNanos { it } - startTime
            angle = animation.getValueFromNanos(playTime)

        } while (angle < targetDegrees)
    }


    val onDraw: DrawScope.() -> Unit = {

        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.015.dw.toPx(), 0.01.dw.toPx()), 0f)
        drawRect(
            color = TextColor,
            topLeft = Offset(0f, heightPx),
            size = Size(widthPx, 1.6.dp.toPx())
        )

        drawArc(
            color = TextColor,
            startAngle = 0f,
            sweepAngle = -180f,
            useCenter = false,
            size = Size(heightAddedPx, heightAddedPx),
            topLeft = Offset((widthPx - heightAddedPx) / 2, (heightPx) / 2f - ((heightAddedPx - heightPx) / 2f)),
            style = Stroke(width = 1.5.dp.toPx(), pathEffect = pathEffect)
        )

        translate(sunImageInitialX, sunImageInitialY) {
            rotate(angle, Offset(centerX - sunImageInitialX, halfImageSize)) {
                rotate(pivotAngle, Offset(halfImageSize, halfImageSize)) {
                    with(painter) {
                        draw(imageSizeObject)
                    }
                }
            }
        }

        val paint = Paint().asFrameworkPaint()
        paint.apply {
            isAntiAlias = true
            textSize = 0.035.dw.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            color = android.graphics.Color.WHITE
        }
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.apply {
                val textX = sunImageInitialX + imageSize / 2
                val textY = centerY + imageSize / 2f
                drawText(sunriseSunsetInfo.getSunriseHourAsString(format), textX, textY, paint)
                drawText(sunriseSunsetInfo.getSunsetHourAsString(format), widthPx - textX, textY, paint)
            }
        }
    }

    Canvas(
        modifier = Modifier
            .width(width)
            .height(height),
        onDraw = onDraw
    )
}

@Composable
fun Turbine(height: Dp, rps: Float = 0.1f) {

    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween((1000f / rps).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
    ) {
        Box(modifier = Modifier.height(height)) {
            Image(
                contentScale = ContentScale.FillHeight,
                painter = painterResource(id = R.drawable.ic_turbine_propeller),
                contentDescription = null,
                modifier = Modifier
                    .height(height)
                    .rotate(angle)
            )
        }
        Box(modifier = Modifier.padding(top = height.times(0.4743f))) {
            Image(
                contentScale = ContentScale.FillHeight,
                painter = painterResource(id = R.drawable.ic_turbine_body),
                contentDescription = null,
                modifier = Modifier
                    .height(height.times(0.8f))
            )
        }
    }
}


@Composable
fun InfoContainer(names: List<Int>, values: List<String>) {

    val fontSize = 0.038.sw
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .width(0.4.dw)
            .wrapContentHeight()
    ) {

        names.forEachIndexed { i, resId ->
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    fontFamily = RobotoFont,
                    text = "${stringResource(id = resId)}:",
                    color = TextColor,
                    textAlign = TextAlign.Center,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Light
                )
                Text(
                    fontFamily = RobotoFont,
                    text = values[i],
                    color = TextColor,
                    textAlign = TextAlign.Center,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Light
                )
            }
            if (i < names.size - 1) {
                Spacer(modifier = Modifier.height(0.01.dw))
                Divider(color = TextColor, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(0.01.dw))
            }
        }
    }
}

@Composable
fun TabSeparator(textResId: Int) {

    val fontSize = 0.04.sw
    val height = 0.5.dw

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            fontFamily = RobotoFont,
            text = stringResource(id = textResId),
            color = TextSemiTransparentColor,
            textAlign = TextAlign.Start,
            fontSize = fontSize,
            fontWeight = FontWeight.Light, modifier = Modifier
                .align(Alignment.Start)
                .padding(horizontal = 0.08.dw)
        )
        Spacer(modifier = Modifier.height(0.007.dw))
        Divider(
            color = TextSemiTransparentColor, thickness = 1.dp, modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 0.08.dw)
        )
        Spacer(modifier = Modifier.height(0.02.dw))
    }
}

enum class ColoredBarIndex {
    MAX,
    MIN,
    CURRENT
}


/**
 * Set up the path points by setting cubic curve, using the array
 * with points, it has two properties factor and tension. Factor
 * controls curve sharpness, and tension controls the smoothness.
 */
fun Path.cubicCurve(
    points: ArrayList<Offset>, numPoints: Int = points.size, factor: Float = 0.3f,
    tension: Float = 0.5f
): Path {
    this.reset()

    var m: Float
    var dx1 = 0f
    var dy1 = 0f
    var dx2: Float
    var dy2: Float
    var previous = points[0]
    this.moveTo(previous.x, previous.y)
    for (i in 0 until numPoints - 1) {
        val current = points[i]
        val next = points[i + 1]
        m = (next.y - previous.y) / (next.x - previous.x)
        dx2 = (next.x - current.x) * -factor
        dy2 = dx2 * m * tension

        this.cubicTo(
            previous.x - dx1,
            previous.y - dy1,
            current.x + dx2,
            current.y + dy2,
            current.x,
            current.y
        )
        dx1 = dx2
        dy1 = dy2
        previous = current
    }
    return this
}
