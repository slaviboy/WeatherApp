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

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.slaviboy.composeunits.adh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.weather.R
import com.slaviboy.weather.core.util.Language
import com.slaviboy.weather.core.util.StaticMethods.findActivity
import com.slaviboy.weather.core.util.TemperatureType
import com.slaviboy.weather.core.util.TimeFormat
import com.slaviboy.weather.core.util.UpdateIntervals
import com.slaviboy.weather.features.weather.presentation.CityWeatherViewModel
import com.slaviboy.weather.ui.theme.RobotoFont
import com.slaviboy.weather.ui.theme.TextColor
import com.slaviboy.weather.ui.theme.TextSemiTransparentColor
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(viewModel: CityWeatherViewModel) {

    val scrollState = rememberScrollState()
    val weatherApiResponseInfo = viewModel.weatherApiResponseApiResponseInfo.value.weatherApiResponseInfo

    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is CityWeatherViewModel.UIEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.resources.getString(event.messageResId)
                    )
                }
            }
        }
    }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState, enabled = true)
            .padding(top = 0.2.dw)
    ) {

        SearchBox(viewModel)
        MainBox(viewModel)
        OtherBox(viewModel)
    }

    TopSettingsBar(gradientColors) {

        // go back to home screen
        viewModel.switchHomeToSettings()

        viewModel.clearedSearchedCity()

        // request new API data for the selected city or location
        if (viewModel.wasCityOrLocationUpdatedFromSettings.value) {

            // delay request, since we have to wait for the animation to finish
            viewModel.requestWeatherByCity(delayTime = 1500L)
        }
    }
}

@Composable
fun MainBox(viewModel: CityWeatherViewModel) {

    val context = LocalContext.current

    BoxDivider(R.string.main)
    BoxWithDropDownMenu(
        imageResId = R.drawable.ic_settings_temperature,
        textResId = R.string.temperature,
        items = listOf(R.string.celsius, R.string.fahrenheit),
        defaultIndex = (viewModel.temperatureType.value).value
    ) {
        viewModel.setTemperatureType(TemperatureType.fromInt(it))
    }
    CustomHorizontalDivider()

    BoxWithDropDownMenu(
        imageResId = R.drawable.ic_settings_clock,
        textResId = R.string.time_format,
        items = listOf(R.string.am_pm, R.string.hour_24),
        defaultIndex = (viewModel.timeFormat.value).value
    ) {
        viewModel.setTimeFormat(TimeFormat.fromInt(it))
    }
    CustomHorizontalDivider()

    BoxWithDropDownMenu(
        imageResId = R.drawable.ic_settings_refresh,
        textResId = R.string.refresh_intervals,
        items = listOf(R.string.every_6_hours, R.string.every_12_hours, R.string.every_24_hours),
        defaultIndex = (viewModel.updateIntervals.value).value
    ) {
        viewModel.setUpdateIntervals(UpdateIntervals.fromInt(it))
    }
    CustomHorizontalDivider()

    BoxWithSwitch(
        imageResId = R.drawable.ic_settings_gps,
        textResId = R.string.use_current_location,
        isSwitchOn = viewModel.useCurrentLocation.value
    ) {
        viewModel.switchUseCurrentLocation()
    }
    CustomHorizontalDivider()

    BoxWithText(
        imageResId = R.drawable.ic_settings_town,
        textResId = R.string.your_selected_city,
        selectedCityName = (viewModel.resultCityById.value)?.name
    )
    CustomHorizontalDivider()

    BoxWithDropDownMenu(
        imageResId = R.drawable.ic_settings_globe,
        textResId = R.string.language,
        items = listOf(R.string.engligh, R.string.bulgarian),
        defaultIndex = (viewModel.language.value).value
    ) {
        viewModel.setLanguage(Language.fromInt(it))

        // force recreating of the activity
        context.findActivity()?.recreate()

        /*context.findActivity()?.apply {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            startActivity(intent)
        }*/
    }
}

@Composable
fun OtherBox(viewModel: CityWeatherViewModel) {

    BoxDivider(R.string.other)

    BoxWithSwitch(
        imageResId = R.drawable.ic_settings_turbine,
        textResId = R.string.show_wind_box,
        isSwitchOn = viewModel.showWindBox.value
    ) {
        viewModel.switchShowWindBox()
    }
    CustomHorizontalDivider()

    BoxWithSwitch(
        imageResId = R.drawable.ic_settings_moon_sun,
        textResId = R.string.show_sunrise_sunset_box,
        isSwitchOn = viewModel.showSunriseAndSunsetBox.value
    ) {
        viewModel.switchShowSunriseAndSunsetBox()
    }
    CustomHorizontalDivider()

    BoxWithSwitch(
        imageResId = R.drawable.ic_settings_humidity,
        textResId = R.string.show_comfort_level_box,
        isSwitchOn = viewModel.showComfortLevelBox.value
    ) {
        viewModel.switchShowComfortLevelBox()
    }
    CustomHorizontalDivider()

    BoxWithSwitch(
        imageResId = R.drawable.ic_settings_next_4_days,
        textResId = R.string.show_next_4_days_forecast_box,
        isSwitchOn = viewModel.showNext4DaysForecastBox.value
    ) {
        viewModel.switchShowNext4DaysForecastBox()
    }
    CustomHorizontalDivider()

    BoxWithSwitch(
        imageResId = R.drawable.ic_settings_24_hours,
        textResId = R.string.show_next_24_hours_forecast_box,
        isSwitchOn = viewModel.showNext24HoursForecastBox.value
    ) {
        viewModel.switchShowNext24HoursForecastBox()
    }
    CustomHorizontalDivider()

    BoxWithSwitch(
        imageResId = R.drawable.ic_settings_speed,
        textResId = R.string.enable_animation,
        isSwitchOn = viewModel.enableAnimation.value
    ) {
        viewModel.switchEnableAnimation()
    }

}

@Composable
fun CustomHorizontalDivider(modifier: Modifier = Modifier, addPadding: Boolean = true) {
    Divider(
        modifier = modifier
            .padding(horizontal = if (addPadding) 0.07.dw else 0.dp)
            .fillMaxWidth(),
        color = TextSemiTransparentColor, thickness = 1.dp
    )
}

@Composable
fun BoxWithText(imageResId: Int, @StringRes textResId: Int, selectedCityName: String?) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .height(0.19.dw)
            .fillMaxWidth()
            .padding(horizontal = 0.1.dw)
    ) {
        ImageWithText(
            modifier = Modifier
                .weight(1f)
                .height(0.2.dw), imageResId, textResId
        )

        if (selectedCityName != null) {
            TextWithImage(R.drawable.ic_settings_checked, selectedCityName)
        } else TextWithImage(R.drawable.ic_settings_checked, R.string.none)
    }
}

@Composable
fun BoxWithSwitch(
    imageResId: Int,
    textResId: Int,
    isSwitchOn: Boolean = true,
    onSwitchChange: () -> Unit = {}
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .height(0.19.dw)
            .fillMaxWidth()
            .padding(horizontal = 0.1.dw)
    ) {

        ImageWithText(
            modifier = Modifier
                .weight(1f)
                .height(0.2.dw),
            imageResId, textResId
        )
        CustomSwitch(
            isSwitchOn = isSwitchOn,
            onSwitchChange = onSwitchChange
        )
    }

}

@Composable
fun CustomSwitch(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    width: Dp = 0.145.dw,
    height: Dp = 0.057.dw,
    strokeWidth: Dp = 1.dp,
    checkedTrackColor: Color = Color(0xFFFFFFFF),
    uncheckedTrackColor: Color = Color(0x3CFFFFFF),
    gapBetweenThumbAndTrackEdge: Dp = 2.dp,
    isSwitchOn: Boolean = true,
    onSwitchChange: () -> Unit = {}
) {

    val thumbRadius = (height / 2) - gapBetweenThumbAndTrackEdge
    val animatePosition = animateFloatAsState(
        targetValue = if (isSwitchOn)
            with(LocalDensity.current) { (width - thumbRadius - gapBetweenThumbAndTrackEdge).toPx() }
        else
            with(LocalDensity.current) { (thumbRadius + gapBetweenThumbAndTrackEdge).toPx() }
    )
    Canvas(
        modifier = modifier
            .size(width = width, height = height)
            .scale(scale = scale)
            .padding()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // This is called when the user taps on the canvas (which is nothing but switch)
                        onSwitchChange.invoke()
                    }
                )
            }
    ) {
        // Track
        drawRoundRect(
            color = if (isSwitchOn) checkedTrackColor else uncheckedTrackColor,
            cornerRadius = CornerRadius(x = 10.dp.toPx(), y = 10.dp.toPx()),
            style = Stroke(width = strokeWidth.toPx())
        )
        // Thumb
        drawCircle(
            color = if (isSwitchOn) checkedTrackColor else uncheckedTrackColor,
            radius = thumbRadius.toPx(),
            center = Offset(
                x = animatePosition.value,
                y = size.height / 2
            )
        )
    }
}

@Composable
fun BoxWithDropDownMenu(
    imageResId: Int,
    @StringRes textResId: Int,
    @StringRes items: List<Int>,
    defaultIndex: Int,
    onElementSelected: (elementIndex: Int) -> Unit = {}
) {

    val default = items[defaultIndex]
    var expanded by remember { mutableStateOf(false) }
    var selectedTextResId by remember { mutableStateOf(default) }
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .height(0.19.dw)
            .fillMaxWidth()
            .padding(horizontal = 0.1.dw)
    ) {

        ImageWithText(
            modifier = Modifier
                .weight(1f)
                .height(0.2.dw),
            imageResId, textResId
        )

        Box(contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .wrapContentWidth()
                .height(0.1.dw)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    expanded = !expanded
                }) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = selectedTextResId),
                    modifier = Modifier
                        .padding(horizontal = 0.02.dw)
                        .wrapContentSize(),
                    color = TextColor,
                    fontSize = 0.033.sw
                )
                Image(
                    modifier = Modifier.width(0.02.dw),
                    contentScale = ContentScale.FillWidth,
                    painter = painterResource(id = R.drawable.ic_dropdownmenu_arrow),
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .height(0.3.dw)
                    .wrapContentWidth()
            ) {

                items.forEachIndexed { i, resId ->
                    DropdownMenuItem(onClick = {
                        selectedTextResId = resId
                        expanded = false
                        onElementSelected.invoke(i)
                    }) {
                        Text(
                            stringResource(id = resId),
                            fontSize = 0.028.sw
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BoxDivider(textResId: Int) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {

        CustomHorizontalDivider(addPadding = false)
        Spacer(modifier = Modifier.height(0.017.dw))
        Text(
            text = stringResource(id = textResId),
            color = TextColor,
            fontSize = 0.04.sw,
            fontWeight = FontWeight.Light,
            fontFamily = RobotoFont,
            modifier = Modifier
                .offset(x = 0.1.dw)
                .align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(0.017.dw))
        CustomHorizontalDivider(addPadding = false)
    }
}

@Composable
fun ImageWithText(modifier: Modifier = Modifier, imageResId: Int, @StringRes textResId: Int) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            modifier = Modifier.width(0.105.dw),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(id = imageResId),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(0.01.dw))

        Text(
            text = stringResource(id = textResId),
            color = TextColor,
            fontSize = 0.037.sw,
            fontWeight = FontWeight.Light,
            fontFamily = RobotoFont,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TextWithImage(imageResId: Int, text: String) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentWidth()
            .height(0.2.dw)
    ) {
        Text(
            text = text,
            color = TextColor,
            fontSize = 0.035.sw,
            fontWeight = FontWeight.Light,
            fontFamily = RobotoFont
        )
        Spacer(modifier = Modifier.width(0.01.dw))
        Image(
            modifier = Modifier.width(0.04.dw),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(id = imageResId),
            contentDescription = null
        )
    }
}

@Composable
fun TextWithImage(imageResId: Int, textResId: Int) {
    TextWithImage(imageResId, stringResource(id = textResId))
}

@Composable
fun SearchBox(viewModel: CityWeatherViewModel) {

    val focusManager = LocalFocusManager.current
    val allCities = viewModel.resultAllCities.value.allCities
    val scrollState = rememberScrollState()
    val height = when (allCities.size) {
        0 -> 0.16.dw
        1 -> 0.26.dw
        2 -> 0.36.dw
        3 -> 0.46.dw
        else -> 0.56.dw
    }

    var isCloseButtonVisible by remember {
        mutableStateOf(false)
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .wrapContentHeight()
    ) {

        Spacer(modifier = Modifier.height(0.03.dw))
        CustomOutlineTextField(
            modifier = Modifier
                .width(0.7.dw)
                .height(0.1.dw)
                .border(1.dp, TextColor, RoundedCornerShape(0.02.dw)),
            placeholderResId = R.string.search_for_city,
            value = viewModel.searchedCityName.value,
            textSize = 0.04.sw,
            isCloseButtonVisible = isCloseButtonVisible,
            onValueChange = {
                viewModel.getAllCitiesByName(it)
                isCloseButtonVisible = (it.isNotEmpty())
            },
            onCloseButtonClick = {

                focusManager.clearFocus()
                viewModel.clearedSearchedCity()
                isCloseButtonVisible = false
            }
        )

        Spacer(modifier = Modifier.height(0.015.dw))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 0.15.dw)
                .padding(bottom = 0.03.dw)
                .verticalScroll(state = scrollState, enabled = true)
                .animateContentSize()
        ) {
            allCities.forEachIndexed { index, city ->
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.1.dw)
                        .clickable {
                            viewModel.setSelectedCityId(city.id)

                            focusManager.clearFocus()
                            isCloseButtonVisible = true
                        }
                ) {
                    Image(
                        modifier = Modifier.width(0.014.dw),
                        contentScale = ContentScale.FillWidth,
                        painter = painterResource(id = R.drawable.ic_circle),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(0.02.dw))
                    Text(
                        text = city.name, color = TextColor,
                        fontSize = 0.04.sw,
                        fontWeight = FontWeight.Light, fontFamily = RobotoFont
                    )

                    if (viewModel.selectedCityId.value == city.id) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Image(
                                modifier = Modifier
                                    .width(0.045.dw)
                                    .align(Alignment.CenterEnd),
                                contentScale = ContentScale.FillWidth,
                                painter = painterResource(id = R.drawable.ic_settings_checked),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomOutlineTextField(
    modifier: Modifier = Modifier,
    placeholderResId: Int,
    textSize: TextUnit,
    value: String,
    isCloseButtonVisible: Boolean,
    onValueChange: (String) -> Unit,
    onCloseButtonClick: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        val focusManager = LocalFocusManager.current
        BasicTextField(
            modifier = Modifier
                .padding(vertical = 0.02.dw)
                .padding(horizontal = 0.04.dw)
                .fillMaxWidth(),
            cursorBrush = SolidColor(Color.White),
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            textStyle = TextStyle(
                color = TextColor, fontSize = textSize
            ),
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }),
            decorationBox = { innerTextField ->

                if (value.isEmpty()) {
                    Text(
                        text = stringResource(id = placeholderResId),
                        style = TextStyle(
                            color = Color(0xABFFFFFF), fontSize = textSize
                        )
                    )
                }
                innerTextField()
            }
        )

        if (isCloseButtonVisible) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_settings_close),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .width(0.1.dw)
                        .padding(0.02.dw)
                        .align(Alignment.CenterEnd)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onCloseButtonClick.invoke()
                        }
                )
            }
        }
    }
}

@Composable
fun TopSettingsBar(gradientColor: ArrayList<Color>, onCloseSettingsPage: () -> Unit) {

    val interactionSource = remember { MutableInteractionSource() }
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier
            .fillMaxWidth()
            .height(0.2.dw)
            .background(
                Brush.verticalGradient(
                    gradientColor,
                    startY = 0f,
                    endY = with(LocalDensity.current) { 1.adh.toPx() }
                )
            )
    ) {

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painterResource(R.drawable.ic_settings_settings),
                contentDescription = null,
                modifier = Modifier
                    .width(0.07.dw)
                    .alpha(1f),
                colorFilter = ColorFilter.tint(TextColor, BlendMode.SrcIn)
            )

            Text(
                fontFamily = RobotoFont,
                text = stringResource(R.string.settings),
                modifier = Modifier
                    .padding(start = 0.01.dw),
                textAlign = TextAlign.Center,
                fontSize = 0.052.sw,
                color = Color(0xFFFFFFFF)
            )
        }

        Image(
            painterResource(R.drawable.ic_settings_close),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxHeight()
                .offset(x = 0.02.dw)
                .padding(0.016.dw)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    onCloseSettingsPage.invoke()
                }
                .padding(0.05.dw),
            colorFilter = ColorFilter.tint(TextColor, BlendMode.SrcIn)
        )
        CustomHorizontalDivider(
            Modifier
                .align(Alignment.BottomCenter), false
        )
    }
}



