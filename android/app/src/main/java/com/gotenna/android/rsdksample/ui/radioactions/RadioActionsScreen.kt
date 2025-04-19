package com.gotenna.android.rsdksample.ui.radioactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gotenna.android.rsdksample.utils.ScreenBackground

data class RadioActionsState(
    val onClickFlashLed: () -> Unit = {},
    val onClickSendPli: () -> Unit = {},
    val onClickSendBroadcastChat: () -> Unit = {},
    val onClickSendPrivateChat: () -> Unit = {},
)

@Composable
fun RadioActionsScreen(state: RadioActionsState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(top = 16.dp)
    ) {
        RadioActionsButton("Flash LED", state.onClickFlashLed)
        RadioActionsButton("Send PLI", state.onClickSendPli)
        RadioActionsButton("Send Chat Message (Broadcast)", state.onClickSendBroadcastChat)
        RadioActionsButton("Send Chat Message (Private)", state.onClickSendPrivateChat)
    }
}

@Composable
fun RadioActionsButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ,
        onClick = onClick,
    ) {
        Text(text = text)
    }
}

@Preview
@Composable
fun RadioActionsScreenPreview() {
    RadioActionsScreen(
        RadioActionsState()
    )
}