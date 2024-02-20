package me.theek.mediastore.presentation

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.theek.mediastore.R
import me.theek.mediastore.presentation.components.PermissionAlert
import me.theek.mediastore.presentation.components.songListComposable

@Composable
fun MediaScreen(viewModel: MediaScreenViewModel = hiltViewModel()) {

    val uiState by viewModel.songStream.collectAsStateWithLifecycle()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = viewModel::onPermissionCheck
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    permissionLauncher.launch(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Manifest.permission.READ_MEDIA_AUDIO
                        } else {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        }
                    )
                }
            ) {
                Text(text = "Request Permission and fetch songs")
            }
        }

        when (val state = uiState) {
            UiState.Idle -> {
                Log.d("MediaScreen", "Idle State")
            }
            is UiState.Progress -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier.size(100.dp),
                        strokeWidth = 5.dp,
                        strokeCap = StrokeCap.Round,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                        text = state.message ?: "Scanning...",
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                }
            }
            is UiState.Success -> {
                if (state.songs.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(150.dp),
                            painter = painterResource(id = R.drawable.headphone),
                            contentDescription = null 
                        )
                        Text(
                            text = "No Songs",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    }
                }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    songListComposable(
                        state.songs,
                        songRetriever = viewModel::onRetrieveSongCover
                    )
                }
            }
        }
    }

    PermissionAlert(
        shouldShowPermissionAlert = viewModel.shouldShowPermissionAlert,
        onDismissAlert = viewModel::onPermissionAlertDismiss
    )
}