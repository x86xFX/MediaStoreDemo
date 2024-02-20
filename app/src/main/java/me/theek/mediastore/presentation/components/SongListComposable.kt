package me.theek.mediastore.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.theek.mediastore.R
import me.theek.mediastore.domain.model.Song

internal fun LazyListScope.songListComposable(
    songs: List<Song>,
    songRetriever: suspend (String) -> ByteArray?,
) {
    items(
        items = songs,
         key = { it.id }
    ) {
        SongRow(
            song = it,
            songRetriever = songRetriever
        )
    }
}

@Composable
private fun SongRow(
    song: Song,
    songRetriever: suspend (String) -> ByteArray?,
    modifier: Modifier = Modifier
) {
    var image by remember { mutableStateOf<ByteArray?>(null) }

    LaunchedEffect(key1 = song) {
        image = songRetriever(song.path)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp)),
            model = image,
            error = painterResource(id = R.drawable.placeholder),
            contentDescription = null
        )

        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = song.songName ?: "Unknown",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
            Text(
                text = song.artistName ?: "Unknown",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}