package com.gotenna.android.rsdksample.ui.radiomanagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotenna.android.rsdksample.utils.ScreenBackground
import com.gotenna.radio.sdk.common.models.RadioModel

data class RadioManagementState(
    val radios: State<List<RadioModel>> = mutableStateOf(emptyList()),
    val onClickScanBle: () -> Unit = {},
    val onClickScanUsb: () -> Unit = {},
    val onClickConnect: (radio: RadioModel) -> Unit = {},
)

@Composable
fun RadioManagementScreen(state: RadioManagementState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(top = 16.dp)
    ) {
        // Scan buttons
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
            ,
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth().weight(1F)
                ,
                onClick = state.onClickScanBle,
            ) {
                Text(text = "Scan BLE")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth().weight(1F)
                ,
                onClick = state.onClickScanUsb
            ) {
                Text(text = "Scan USB")
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        // Radio list
        val radios = state.radios.value
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(radios.size) { index ->
                val radio = radios[index]
                RadioManagementRadioItem(
                    radioName = "Radio ${index + 1}",
                    radioAddress = radio.address,
                    onClick = { state.onClickConnect(radio) },
                )
            }
        }
    }
}

@Composable
fun RadioManagementRadioItem(radioName: String, radioAddress: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ,
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
            ,
            onClick = onClick,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                ,
            ) {
                BasicText(
                    text = radioName,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                BasicText(
                    text = radioAddress,
                    style = TextStyle(
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun RadioManagementScreenPreview() {
    RadioManagementScreen(
        RadioManagementState()
    )
}

@Preview
@Composable
fun RadioManagementRadioItemPreview() {
    Column {
        RadioManagementRadioItem(
            radioName = "Radio 1",
            radioAddress = "asdf",
            onClick = {},
        )
    }
}