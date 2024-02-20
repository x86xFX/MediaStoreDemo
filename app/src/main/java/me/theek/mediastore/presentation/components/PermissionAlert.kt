package me.theek.mediastore.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun PermissionAlert(
    shouldShowPermissionAlert: Boolean,
    onDismissAlert: () -> Unit
) {
    if (shouldShowPermissionAlert) {
        AlertDialog(
            onDismissRequest = onDismissAlert,
            confirmButton = {
                FilledTonalButton(onClick = onDismissAlert) {
                    Text(text = "Close")
                }
            },
            title = {
                Text(text = "Permission Required")
            },
            text = {
                Text(text = "To read local audio files, this app requires relevant permission. Please grant it manually by navigating app's settings permission page.")
            }
        )
    }
}