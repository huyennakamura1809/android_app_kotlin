package net.braniumacademy.musicapplication.data.source.local

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import net.braniumacademy.musicapplication.data.model.RecentSong
import net.braniumacademy.musicapplication.data.model.album.Album
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.model.artist.ArtistSongCrossRef
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.data.model.playlist.PlaylistSongCrossRef
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.source.local.artist.ArtistDao
import net.braniumacademy.musicapplication.data.source.local.playlist.PlaylistDao
import net.braniumacademy.musicapplication.data.source.local.recent.RecentSongDao
import net.braniumacademy.musicapplication.data.source.local.song.SongDao

@Database(
    entities = [
        Song::class,
        Playlist::class,
        Album::class,
        RecentSong::class,
        PlaylistSongCrossRef::class,
        Artist::class,
        ArtistSongCrossRef::class
    ],
    version = 6,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 5, to = 6, spec = AppDatabase.MigrationSpec::class)
    ]
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun albumDao(): AlbumDao
    abstract fun recentSongDao(): RecentSongDao
    abstract fun artistDao(): ArtistDao

    @RenameColumn(
        tableName = "artists",
        fromColumnName = "id",
        toColumnName = "artist_id"
    )
    internal class MigrationSpec : AutoMigrationSpec

    companion object {
        @Volatile
        private var _instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (_instance == null) {
                synchronized(AppDatabase::class.java) {
                    if (_instance == null) {
                        _instance = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "music.db"
                        ).build()
                    }
                }
            }
            return _instance!!
        }
    }
}