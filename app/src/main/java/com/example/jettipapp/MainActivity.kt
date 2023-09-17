@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.components.InputField
import com.example.jettipapp.widgets.RoundIconButton
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetTipAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        MainContent()
                    }
                }
            }
        }
    }
}

@Composable
fun TopHeader(totalPerPerson: Double) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(15.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))), color = Color(0xFFE9D7F7)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Total per Person", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "$$totalPerPerson",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainContent() {

    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        val splitByState = remember {
            mutableStateOf(1)
        }
        val tipAmountState = remember {
            mutableStateOf(0.0)
        }
        val totalPerPersonState = remember {
            mutableStateOf(0.0)
        }
        BillForm(
            splitByState = splitByState,
            totalPerPersonState = totalPerPersonState,
            tipAmountState = tipAmountState,
            onValChange = {
                Log.i("TEXTO", it)
            })
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {}
) {
    Column(
    ) {
        val totalBillState = remember {
            mutableStateOf("")
        }
        val validState = remember(totalBillState.value) {
            totalBillState.value.trim().isNotEmpty()
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        var sliderPosition by remember {
            mutableStateOf(0.0)
        }

        val tipPercentage = (sliderPosition * 100).roundToInt()

        TopHeader(totalPerPerson = totalPerPersonState.value)

        InputField(modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp, end = 10.dp, start = 10.dp),
            value = totalBillState,
            enabled = true,
            imeAction = ImeAction.Default,
            keyboardType = KeyboardType.Decimal,
            isSingleLine = true,
            labelId = "Enter Bill",
            onAction = KeyboardActions {
                if (!validState) {
                    return@KeyboardActions
                }
                onValChange(totalBillState.value)
                keyboardController?.hide()
            })
        if (validState) {
            Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start) {
                Text(
                    text = "Split",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(120.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    RoundIconButton(imageVector = Icons.Default.Refresh, onClick = {
                        if (splitByState.value > range.first)
                            splitByState.value -= 1
                        totalPerPersonState.value =
                            calculateTotalPerPerson(
                                totalBillState.value.toDouble(),
                                splitByState.value,
                                (sliderPosition * 100).roundToInt()
                            )
                    })
                    Text(
                        text = splitByState.value.toString(),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 9.dp)
                    )
                    RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                        if (splitByState.value < range.last)
                            splitByState.value += 1
                        totalPerPersonState.value =
                            calculateTotalPerPerson(
                                totalBillState.value.toDouble(),
                                splitByState.value,
                                (sliderPosition * 100).roundToInt()
                            )
                    })
                }
            }

            Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                Text(
                    text = "Tip",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(200.dp))
                Text(
                    text = "${tipAmountState.value}",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "$tipPercentage %")
                Spacer(modifier = Modifier.height(14.dp))
                Slider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    steps = 5,
                    value = sliderPosition.toFloat(),
                    onValueChange = {
                        sliderPosition = it.toDouble()
                        tipAmountState.value =
                            calculateTotalTip(
                                totalBillState.value.toDouble(),
                                (sliderPosition * 100).roundToInt()
                            )
                        totalPerPersonState.value =
                            calculateTotalPerPerson(
                                totalBillState.value.toDouble(),
                                splitByState.value,
                                (sliderPosition * 100).roundToInt()
                            )
                    })
            }
        }
    }
}

fun calculateTotalTip(totalBill: Double, tipPercentage: Int): Double {
    Log.i("TOTAL", "$totalBill  $tipPercentage ")
    return if (totalBill > 1 && totalBill.toString().isNotEmpty()) {
        (totalBill * tipPercentage) / 100
    } else {
        0.0
    }
}

fun calculateTotalPerPerson(totalBill: Double, splitBy: Int, tipPercentage: Int): Double {
    val bill = calculateTotalTip(totalBill, tipPercentage) + totalBill
    return (bill / splitBy)
}
