package me.theek.mediastore.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.theek.mediastore.domain.model.Song
import me.theek.mediastore.domain.repository.FlowEvent
import me.theek.mediastore.domain.repository.SongRepository
import javax.inject.Inject

@HiltViewModel
class MediaScreenViewModel @Inject constructor(private val songRepository: SongRepository) : ViewModel() {

    private val _songStream: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)
    val songStream = _songStream.asStateFlow()

    var shouldShowPermissionAlert by mutableStateOf(false)
        private set

    fun onPermissionCheck(isGranted: Boolean) {
        if (isGranted.not()) {
            shouldShowPermissionAlert = true

        } else {
            fetchSongs()
        }
    }

    fun onPermissionAlertDismiss() {
        shouldShowPermissionAlert = false
    }

    suspend fun onRetrieveSongCover(songPath: String) : ByteArray? {
        return songRepository.getSongCover(songPath)
    }

    private fun fetchSongs() {
        viewModelScope.launch(Dispatchers.Main) {
            songRepository.getSongs().collect { state ->
                when (state) {
                    is FlowEvent.Progress -> {
                        _songStream.value = UiState.Progress(
                            progress = state.asFloat(),
                            message = state.message
                        )
                    }
                    is FlowEvent.Success -> {
                        _songStream.value = UiState.Success(state.data)
                    }
                }
            }
        }
    }

}

sealed interface UiState {
    data object Idle : UiState
    data class Progress(
        val progress: Float,
        val message: String?
    ) : UiState
    data class Success(
        val songs: List<Song>
    ) : UiState
}