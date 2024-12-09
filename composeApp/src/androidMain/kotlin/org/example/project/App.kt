package org.example.project

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                Text("AR support:")
                if (uiState.isArSupported) {
                    Text("Enabled")
                } else {
                    Text("Disabled")
                }
            }
            Row {
                Text("Depth support:")

                if (uiState.isDepthSupported) {
                    Text("Enabled")
                } else {
                    Text("Disabled")
                }
            }
        }

    }
}