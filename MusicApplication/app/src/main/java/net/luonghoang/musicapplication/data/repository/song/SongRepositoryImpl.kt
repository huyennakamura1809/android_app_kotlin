package net.braniumacademy.musicapplication.data.repository.song

import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.model.song.SongList
import net.braniumacademy.musicapplication.data.source.Result
import net.braniumacademy.musicapplication.data.source.SongDataSource
import net.braniumacademy.musicapplication.data.source.remote.RemoteSongDataSource

class SongRepositoryImpl(
    private val localSongDataSource: SongDataSource.Local
) : SongRepository.Remote, SongRepository.Local {
    private val remoteSongDataSource = RemoteSongDataSource()
    override suspend fun loadSongs(callback: ResultCallback<Result<SongList>>) {
        remoteSongDataSource.loadSongs(callback)
    }

    override val songs: List<Song>
        get() = localSongDataSource.songs

    override val favoriteSongs: Flow<List<Song>>
        get() = localSongDataSource.favoriteSongs

    override val top15MostHeardSongs: Flow<List<Song>>
        get() = localSongDataSource.top15MostHeardSongs

    override val top40MostHeardSongs: Flow<List<Song>>
        get() = localSongDataSource.top40MostHeardSongs

    override val top15ForYouSongs: Flow<List<Song>>
        get() = localSongDataSource.top15ForYouSongs

    override val top40ForYouSongs: Flow<List<Song>>
        get() = localSongDataSource.top40ForYouSongs

    override suspend fun getSongById(id: String): Song? {
        return localSongDataSource.getSongById(id)
    }

    override suspend fun insert(vararg songs: Song) {
        localSongDataSource.insert(*songs)
    }

    override suspend fun delete(song: Song) {
        localSongDataSource.delete(song)
    }

    override suspend fun update(song: Song) {
        localSongDataSource.update(song)
    }

    override suspend fun updateFavorite(id: String, favorite: Boolean) {
        localSongDataSource.updateFavorite(id, favorite)
    }
}