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
package com.slaviboy.weather.ui.composable.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.slaviboy.weather.R
import com.slaviboy.weather.features.weather.presentation.CityWeatherViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermissionGPS(
    viewModel: CityWeatherViewModel,
    cancellationTokenSource: CancellationTokenSource,
    fusedLocationClient: FusedLocationProviderClient,
    onAcceptButtonClick: () -> Unit,
    onRejectButtonClick: () -> Unit
) {
    // track if the user doesn't want to see the rationale any more
    val doNotShowRationale by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    fun onPermissionGranted() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {

            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token
            )

            currentLocationTask.addOnCompleteListener { task: Task<Location> ->
                if (task.isSuccessful) {
                    val result: Location = task.result
                    viewModel.setCurrentLocationLat(result.latitude.toFloat())
                    viewModel.setCurrentLocationLon(result.longitude.toFloat())
                } else {
                    viewModel.showSnackbarWithScope(R.string.could_not_determine_location)
                }
            }
        }
    }

    fun navigateToAndroidAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", context.packageName, null)
        }
        startActivity(context, intent, null)
    }

    val gpsPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    PermissionRequired(
        permissionState = gpsPermissionState,
        permissionNotGrantedContent = {
            if (doNotShowRationale) {
                // feature not available
                viewModel.showSnackbarWithScope(R.string.feature_not_available)
            } else {
                // please grant the permission
                MessageBox(
                    R.drawable.ic_warning,
                    R.string.allow_permission,
                    R.string.allow_gps_permission_msg,
                    R.string.dont_allow,
                    R.string.ok,
                    onAcceptButtonClick = {
                        onAcceptButtonClick.invoke()
                        gpsPermissionState.launchPermissionRequest()
                    },
                    onRejectButtonClick = {
                        viewModel.setUseCurrentLocation(false)
                        onRejectButtonClick.invoke()
                    }
                )
            }
        },
        permissionNotAvailableContent = {
            // camera permission Denied
            navigateToAndroidAppSettings()
        }
    ) {
        // camera permission Granted
        onPermissionGranted()
    }
}