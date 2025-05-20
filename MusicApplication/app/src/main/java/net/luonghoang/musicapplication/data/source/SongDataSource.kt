package net.braniumacademy.musicapplication.data.source

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.song.Song

interface SongDataSource {
    interface Local {
        val songs: List<Song>

        val favoriteSongs: Flow<List<Song>>

        val top15MostHeardSongs: Flow<List<Song>>

        val top40MostHeardSongs: Flow<List<Song>>

        val top15ForYouSongs: Flow<List<Song>>

        val top40ForYouSongs: Flow<List<Song>>

        suspend fun getSongById(id: String): Song?

        suspend fun insert(vararg songs: Song)

        suspend fun delete(song: Song)

        suspend fun update(song: Song)

        suspend fun updateFavorite(id: String, favorite: Boolean)
    }

    interface Remote {
        // todo
    }
}