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
package com.slaviboy.weather

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.slaviboy.composeunits.ScaleFactor
import com.slaviboy.composeunits.adw
import com.slaviboy.composeunits.initSize
import com.slaviboy.weather.core.util.Language
import com.slaviboy.weather.core.util.StaticMethods.hideSystemBars
import com.slaviboy.weather.features.weather.presentation.CityWeatherViewModel
import com.slaviboy.weather.ui.composable.main.CheckPermissionGPS
import com.slaviboy.weather.ui.composable.screens.HomeScreen
import com.slaviboy.weather.ui.composable.screens.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // GPS require
    private var cancellationTokenSource = CancellationTokenSource()
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSize()
        hideSystemBars()

        val metrics: DisplayMetrics = resources.displayMetrics
        val scaleFactor = metrics.density
        val widthPixels: Int = metrics.widthPixels
        val heightPixels: Int = metrics.heightPixels
        val widthDp = widthPixels / scaleFactor
        val heightDp = heightPixels / scaleFactor
        ScaleFactor = if (widthDp > 600f) {
            0.55f
        } else 1f

        setContent {

            val viewModel: CityWeatherViewModel = hiltViewModel()
            val context = LocalContext.current

            // change the language
            val locale = Language.getAsLocale(viewModel.language.value)
            Locale.setDefault(locale)
            resources.apply {
                configuration.setLocale(locale)
                updateConfiguration(configuration, resources.displayMetrics)
            }

            val scaffoldState = rememberScaffoldState()
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

            // prevent the scroll bounce effect
            CompositionLocalProvider(
                LocalOverScrollConfiguration provides null
            ) {

                // for displaying snackbar for messages
                Scaffold(
                    scaffoldState = scaffoldState,
                    modifier = Modifier
                        .fillMaxSize()
                    /*.clickable {
                        viewModel.showSnackbarWithScope(R.string.could_not_determine_location)
                    }*/,
                    snackbarHost = {
                        SnackbarHost(it) { data ->
                            Snackbar(
                                backgroundColor = Color.White,
                                actionColor = Color.Blue,
                                contentColor = Color.DarkGray,
                                snackbarData = data
                            )
                        }
                    }
                ) {

                    val density = LocalDensity.current
                    val width by remember {
                        mutableStateOf(with(density) { (1.adw).toPx() })
                    }

                    val animatedOffsetX: Float by animateFloatAsState(
                        targetValue = if (!viewModel.switchHomeToSetting.value) 0f else width,
                        animationSpec = tween(1200)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset { IntOffset((-width + animatedOffsetX).toInt(), 0) }
                    ) {
                        HomeScreen(viewModel = viewModel)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset { IntOffset(animatedOffsetX.toInt(), 0) }
                    ) {
                        SettingsScreen(viewModel = viewModel)
                    }
                }

                if (viewModel.useCurrentLocation.value &&
                    (viewModel.currentLocationLat.value == -1f && viewModel.currentLocationLon.value == -1f)
                ) {
                    CheckPermissionGPS(viewModel, cancellationTokenSource, fusedLocationClient,
                        onAcceptButtonClick = {},
                        onRejectButtonClick = {}
                    )
                }
            }

        }

    }

    override fun onStop() {
        super.onStop()
        cancellationTokenSource.cancel()
    }

}