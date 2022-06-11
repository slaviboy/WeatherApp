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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import com.slaviboy.composeunits.adh
import com.slaviboy.composeunits.adw
import com.slaviboy.composeunits.dw
import com.slaviboy.weather.R
import com.slaviboy.weather.ui.composable.other.SnowFlake
import kotlinx.coroutines.android.awaitFrame
import java.util.*
import kotlin.math.roundToInt

@Composable
fun Clouds() {

}

@Composable
fun SnowFall(numberOfParticles: Int = 60) {

    val maxWidth = with(LocalDensity.current) { 1.adw.toPx() }
    val maxHeight = with(LocalDensity.current) { 1.adh.toPx() }
    val flakeSizeBound = with(LocalDensity.current) { 0.0035.dw.toPx() }
    val random by remember {
        mutableStateOf(Random())
    }

    val snowFlakeImages = arrayListOf(
        ImageBitmap.imageResource(id = R.drawable.snow_particle_1),
        ImageBitmap.imageResource(id = R.drawable.snow_particle_2),
        ImageBitmap.imageResource(id = R.drawable.snow_particle_3),
        ImageBitmap.imageResource(id = R.drawable.snow_particle_4),
    )
    val snowFlakes by remember {
        mutableStateOf(Array(numberOfParticles) {
            val snowFlakeType = (random.nextFloat() * 3).roundToInt()
            SnowFlake.create(
                random, maxWidth, maxHeight, flakeSizeBound,
                5f, 15f,
                1f, 2f,
                snowFlakeType, snowFlakeImages
            )
        })
    }


    var isActive by remember {
        mutableStateOf(true)
    }
    var frameCount by remember {
        mutableStateOf(0)
    }
    LaunchedEffect(Unit) {
        var lastFrame = 0L
        while (isActive) {
            val nextFrame = awaitFrame() / 100_000L
            if (lastFrame != 0L) {
                val period = nextFrame - lastFrame
                snowFlakes.forEach {
                    it.move(random, period, maxWidth, maxHeight)
                }
                frameCount += 1
            }
            lastFrame = nextFrame
        }
    }

    if (frameCount > 0) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize(),
                onDraw = {
                    snowFlakes.forEach {
                        it.draw(this, snowFlakeImages)
                    }
                }
            )
        }
    }
}