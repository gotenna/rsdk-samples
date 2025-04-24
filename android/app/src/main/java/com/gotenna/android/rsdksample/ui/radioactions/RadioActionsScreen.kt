package com.gotenna.android.rsdksample.ui.radioactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gotenna.android.rsdksample.RadioManager
import com.gotenna.android.rsdksample.utils.ScreenBackground

data class RadioActionsState(
    val onClickFlashLed: () -> Unit = {},
    val onClickSendPli: () -> Unit = {},
    val onClickSendBroadcastChat: () -> Unit = {},
    val onClickSendPrivateChat: (callsign: String) -> Unit = {},
)

@Composable
fun RadioActionsScreen(state: RadioActionsState) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        Text(
            text = "Callsign: ${RadioManager.sessionCallsign}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color.White
        )

        RadioActionsButton("Flash LED", state.onClickFlashLed)
        RadioActionsButton("Send PLI", state.onClickSendPli)
        RadioActionsButton("Send Chat Message (Broadcast)", state.onClickSendBroadcastChat)
        RadioActionsButton("Send Chat Message (Private)", { showDialog = true })

        if (showDialog) {
            RadioActionsContactsListDialog(
                onDismissDialog = { showDialog = false },
                onSelectItem = { callsign ->
                    state.onClickSendPrivateChat(callsign)
                },
            )
        }
    }
}

@Composable
fun RadioActionsContactsListDialog(onDismissDialog: () -> Unit, onSelectItem: (callsign: String) -> Unit) {
    AlertDialog(
        backgroundColor = ScreenBackground,
        contentColor = Color.White,
        onDismissRequest = onDismissDialog,
        title = { Text("Select a Contact") },
        text = {
            LazyColumn {
                items(RadioManager.contacts.keys.toList()) { callsign ->
                    Text(
                        text = callsign,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectItem(callsign)
                                onDismissDialog()
                            }
                            .padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismissDialog) {
                Text("Cancel")
            }
        }
    )
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

@Preview
@Composable
fun RadioActionsContactsListDialogPreview() {
    RadioActionsContactsListDialog(
        onDismissDialog = {},
        onSelectItem = {},
    )
}