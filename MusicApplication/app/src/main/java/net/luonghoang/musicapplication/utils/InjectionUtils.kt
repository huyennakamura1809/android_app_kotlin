package net.braniumacademy.musicapplication.utils

import android.content.Context
import net.braniumacademy.musicapplication.data.repository.artist.ArtistRepositoryImpl
import net.braniumacademy.musicapplication.data.repository.playlist.PlaylistRepositoryImpl
import net.braniumacademy.musicapplication.data.repository.recent.RecentSongRepositoryImpl
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import net.braniumacademy.musicapplication.data.source.ArtistDataSource
import net.braniumacademy.musicapplication.data.source.PlaylistDataSource
import net.braniumacademy.musicapplication.data.source.SongDataSource
import net.braniumacademy.musicapplication.data.source.local.AppDatabase
import net.braniumacademy.musicapplication.data.source.local.artist.LocalArtistDataSource
import net.braniumacademy.musicapplication.data.source.local.playlist.LocalPlaylistDataSource
import net.braniumacademy.musicapplication.data.source.local.recent.LocalRecentSongDataSource
import net.braniumacademy.musicapplication.data.source.local.song.LocalSongDataSource

object InjectionUtils {
    fun provideRecentSongDataSource(
        context: Context
    ): LocalRecentSongDataSource {
        val database = AppDatabase.getInstance(context)
        return LocalRecentSongDataSource(database.recentSongDao())
    }

    fun provideRecentSongRepository(
        dataSource: LocalRecentSongDataSource
    ): RecentSongRepositoryImpl {
        return RecentSongRepositoryImpl(dataSource)
    }

    fun provideSongDataSource(context: Context): SongDataSource.Local {
        val database = AppDatabase.getInstance(context)
        return LocalSongDataSource(database.songDao())
    }

    fun provideSongRepository(
        dataSource: SongDataSource.Local
    ): SongRepositoryImpl {
        return SongRepositoryImpl(dataSource)
    }

    fun providePlaylistDataSource(context: Context): PlaylistDataSource.Local {
        val database = AppDatabase.getInstance(context)
        return LocalPlaylistDataSource(database.playlistDao())
    }

    fun providePlaylistRepository(dataSource: PlaylistDataSource.Local): PlaylistRepositoryImpl {
        return PlaylistRepositoryImpl(dataSource)
    }

    fun provideArtistDataSource(context: Context): ArtistDataSource.Local {
        val database = AppDatabase.getInstance(context)
        return LocalArtistDataSource(database.artistDao())
    }

    fun provideArtistRepository(dataSource: ArtistDataSource.Local): ArtistRepositoryImpl {
        return ArtistRepositoryImpl(dataSource)
    }
}