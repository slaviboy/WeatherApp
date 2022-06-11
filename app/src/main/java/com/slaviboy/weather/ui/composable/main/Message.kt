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

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MessageBox(
    icon: Int, title: Int, message: Int, rejectButtonText: Int, acceptButtonText: Int,
    onAcceptButtonClick: () -> Unit = {}, onRejectButtonClick: () -> Unit = {}
) {

    val fontSize = 0.053.sw
    var isVisible by remember {
        mutableStateOf(true)
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn() +
                fadeIn(),
        exit = fadeOut(animationSpec = tween(500, 0, LinearOutSlowInEasing))
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(if (isVisible) Color(0x41000000) else Color.Transparent)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .width(0.89.dw)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(0.03.dw))
                    .background(Color(0xE8FFFFFF))
            ) {

                Spacer(modifier = Modifier.height(0.08.dw))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = "",
                        modifier = Modifier.width(0.12.dw),
                        contentScale = ContentScale.FillWidth
                    )
                    Text(
                        text = stringResource(id = title),
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1C1C),
                        modifier = Modifier.padding(horizontal = 0.04.dw)
                    )
                }

                Spacer(modifier = Modifier.height(0.05.dw))
                Text(
                    text = stringResource(id = message),
                    fontSize = fontSize.times(0.68f),
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF3F5B84),
                    modifier = Modifier.padding(horizontal = 0.14.dw)
                )
                Spacer(modifier = Modifier.height(0.09.dw))

                Divider(
                    color = Color(0xFF707070), thickness = 1.dp, modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.13.dw)
                ) {
                    TextButton(
                        onClick = {
                            isVisible = false
                            onRejectButtonClick.invoke()
                        },
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxHeight()
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = stringResource(id = rejectButtonText),
                            fontSize = fontSize.times(0.9f),
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF1569FF),
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.CenterVertically)
                        )
                    }
                    Divider(
                        color = Color(0xFF707070), modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .width(1.dp)
                            .fillMaxHeight()
                    )
                    TextButton(
                        onClick = {
                            isVisible = false
                            onAcceptButtonClick.invoke()
                        },
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxHeight()
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = stringResource(id = acceptButtonText).uppercase(),
                            fontSize = fontSize.times(0.9f),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF1569FF),
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}