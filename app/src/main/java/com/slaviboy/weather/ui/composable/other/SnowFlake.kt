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
package com.slaviboy.weather.ui.composable.other

import android.graphics.PointF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.IntSize
import java.util.*

class SnowFlake(
    var snowParticleType: Int = SNOW_FLAKE_TYPE_1,
    var position: PointF = PointF(0f, 0f),
    var directionAngle: Float = 0f,
    var increment: Float = 0f,
    var flakeSize: Float = 100f,
    var flakeSizeBound: IntSize,
    var pivotAngle: Float = 0f,
    var opacity: Float = 1f
) {

    fun move(random: Random, period: Long, width: Float, height: Float) {

        val x = position.x + increment * Math.cos(directionAngle.toDouble()).toFloat()
        val y = position.y + increment * Math.sin(directionAngle.toDouble()).toFloat()
        directionAngle += random.floatInRange(-ANGLE_SEED, ANGLE_SEED) / ANGLE_DIVISOR
        position.set(x, y)
        if (!isInside(width, height)) {
            reset(random, width)
        }
    }

    private fun isInside(width: Float, height: Float): Boolean {
        val x = position.x
        val y = position.y
        return x >= -flakeSizeBound.width - 1 &&
                x + flakeSizeBound.width <= width &&
                y >= -flakeSizeBound.width - 1 &&
                y - flakeSizeBound.width < height
    }

    private fun reset(random: Random, width: Float) {
        position.x = random.floatInRange(width)
        position.y = -flakeSize - 1f
        directionAngle = random.floatInRange(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE
    }

    fun draw(drawScope: DrawScope, snowFlakeImage: ArrayList<ImageBitmap>) {
        with(drawScope) {
            translate(position.x, position.y) {
                rotate(pivotAngle, Offset(flakeSizeBound.width / 2f, flakeSizeBound.height / 2f)) {
                    drawImage(image = snowFlakeImage[snowParticleType], dstSize = flakeSizeBound, alpha = opacity)
                }
            }
        }
    }


    companion object {

        const val SNOW_FLAKE_TYPE_1: Int = 0
        const val SNOW_FLAKE_TYPE_2: Int = 1
        const val SNOW_FLAKE_TYPE_3: Int = 2
        const val SNOW_FLAKE_TYPE_4: Int = 3

        private const val ANGE_RANGE = 0.1f
        private const val HALF_ANGLE_RANGE = ANGE_RANGE / 2f
        private const val HALF_PI = Math.PI.toFloat() / 2f
        private const val ANGLE_SEED = 125f
        private const val ANGLE_DIVISOR = 10000f
        private const val INCREMENT_LOWER = 1f
        private const val INCREMENT_UPPER = 2f
        private const val FLAKE_SIZE_LOWER = 5f
        private const val FLAKE_SIZE_UPPER = 30f

        fun Random.floatInRange(min: Float, max: Float): Float {
            // [min, max]
            return nextFloat() * (max - min) + min
        }

        fun Random.floatInRange(max: Float): Float {
            // [0, max]
            return nextFloat() * max
        }

        fun create(
            random: Random,
            width: Float,
            height: Float,
            snowFlakeSizeBound: Float,
            snowFlakeSizeMin: Float = FLAKE_SIZE_LOWER,
            snowFlakeSizeMax: Float = FLAKE_SIZE_UPPER,
            snowFlakeSpeedMin: Float = INCREMENT_LOWER,
            snowFlakeSpeedMax: Float = INCREMENT_UPPER,
            snowFlakeType: Int,
            snowFlakeImages: ArrayList<ImageBitmap>
        ): SnowFlake {

            val x = random.floatInRange(width)
            val y = random.nextFloat() * height
            val angle: Float = random.floatInRange(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE
            val increment: Float = random.floatInRange(snowFlakeSpeedMin, snowFlakeSpeedMax)
            val flakeSize: Float = random.floatInRange(snowFlakeSizeMin, snowFlakeSizeMax)

            val imageBitmap = snowFlakeImages[snowFlakeType]
            val flakeWidth = flakeSize * snowFlakeSizeBound
            val flakeHeight = (imageBitmap.height / imageBitmap.width.toFloat()) * flakeWidth
            return SnowFlake(
                snowFlakeType, PointF(x, y),
                angle, increment, flakeSize,
                IntSize(flakeWidth.toInt(), flakeHeight.toInt()),
                random.floatInRange(180f),
                random.floatInRange(0.4f, 0.8f)
            )
        }
    }

}