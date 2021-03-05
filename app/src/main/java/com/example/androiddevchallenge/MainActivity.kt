/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    val state = remember { mutableStateOf(State()) }
    var job: Job? = null

    fun reset() {
        GlobalScope.launch {
            job?.cancelAndJoin()
            job = null
            state.value = state.value.copy(
                running = false,
                current = state.value.max
            )
        }
    }

    val backgroundColor = remember { Animatable(Color.White) }

    Surface(color = backgroundColor.value) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = state.value.current.let { if (it < 0) "" else it.toString() },
                    enabled = !state.value.running,
                    textStyle = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, textAlign = Center),
                    onValueChange = {
                        val newValue = it.toIntOrNull() ?: -1
                        state.value = state.value.copy(
                            max = newValue,
                            current = newValue
                        )
                    },
                    modifier = Modifier.wrapContentSize()
                )
                Spacer(modifier = Modifier.size(16.dp))
                Row {
                    Button(
                        enabled = !state.value.running,
                        onClick = {
                            val currentState = state.value
                            state.value = currentState.copy(
                                running = true
                            )
                            job = GlobalScope.launch {
                                while (isActive && state.value.current > 0) {
                                    delay(1000)
                                    state.value = state.value.copy(
                                        current = state.value.current - 1
                                    )

                                    if (state.value.current == 0) {
                                        job?.cancel()
                                        job = null
                                        reset()

                                        // animation
                                        GlobalScope.launch {
                                            backgroundColor.animateTo(Color.Red)
                                            backgroundColor.animateTo(Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        Text(text = "Start")
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(
                        onClick = ::reset,
                    ) {
                        Text(text = "Reset")
                    }
                }
            }
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

// @Preview("Dark Theme", widthDp = 360, heightDp = 640)
// @Composable
// fun DarkPreview() {
//  MyTheme(darkTheme = true) {
//    MyApp()
//  }
// }
