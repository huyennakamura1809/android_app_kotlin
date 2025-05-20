package net.braniumacademy.musicapplication.data.repository.recent

import kotlinx.coroutines.flow.Flow
import net.braniumacademy.musicapplication.data.model.RecentSong
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.source.local.recent.LocalRecentSongDataSource

class RecentSongRepositoryImpl(
    private val localDataSource: LocalRecentSongDataSource
) : RecentSongRepository.Local, RecentSongRepository.Remote {
    override val recentSongs: Flow<List<Song>>
        get() = localDataSource.recentSongs

    override suspend fun insert(vararg recentSongs: RecentSong) {
        localDataSource.insert(*recentSongs)
    }
}