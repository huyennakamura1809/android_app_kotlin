package net.braniumacademy.musicapplication.data.repository.song

import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.model.song.SongList
import net.braniumacademy.musicapplication.data.source.Result

interface SongRepository {
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
        suspend fun loadSongs(callback: ResultCallback<Result<SongList>>)
    }
}