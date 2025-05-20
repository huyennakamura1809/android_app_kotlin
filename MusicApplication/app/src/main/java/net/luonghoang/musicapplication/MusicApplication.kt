package net.braniumacademy.musicapplication

import android.app.Application
import android.content.ComponentName
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import net.braniumacademy.musicapplication.data.repository.artist.ArtistRepositoryImpl
import net.braniumacademy.musicapplication.data.repository.playlist.PlaylistRepositoryImpl
import net.braniumacademy.musicapplication.data.repository.recent.RecentSongRepositoryImpl
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import net.braniumacademy.musicapplication.ui.playing.PlaybackService
import net.braniumacademy.musicapplication.ui.viewmodel.MediaPlayerViewModel
import net.braniumacademy.musicapplication.utils.InjectionUtils
import java.util.concurrent.ExecutionException

class MusicApplication : Application() {
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private var mediaController: MediaController? = null
    private lateinit var recentSongRepository: RecentSongRepositoryImpl
    private lateinit var _songRepository: SongRepositoryImpl
    private lateinit var playlistRepository: PlaylistRepositoryImpl
    private lateinit var _artistRepository: ArtistRepositoryImpl

    val songRepository: SongRepositoryImpl
        get() = _songRepository
    val artistRepository: ArtistRepositoryImpl
        get() = _artistRepository

    override fun onCreate() {
        super.onCreate()
        createMediaPlayer()
        setupComponents()
    }

    private fun createMediaPlayer() {
        val sessionToken = SessionToken(
            applicationContext,
            ComponentName(applicationContext, PlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(applicationContext, sessionToken).buildAsync()
        controllerFuture.addListener({
            if (controllerFuture.isDone && !controllerFuture.isCancelled) {
                try {
                    mediaController = controllerFuture.get()
                    mediaController?.let {
                        MediaPlayerViewModel.instance.setMediaController(it)
                    }
                } catch (ignored: ExecutionException) {
                } catch (ignored: InterruptedException) {
                }
            } else {
                mediaController = null
            }
        }, MoreExecutors.directExecutor())
    }

    private fun setupComponents() {
        val songDataSource =
            InjectionUtils.provideSongDataSource(applicationContext)
        _songRepository = InjectionUtils.provideSongRepository(songDataSource)

        val recentSongDataSource =
            InjectionUtils.provideRecentSongDataSource(applicationContext)
        recentSongRepository = InjectionUtils.provideRecentSongRepository(recentSongDataSource)

        val playlistDataSource =
            InjectionUtils.providePlaylistDataSource(applicationContext)
        playlistRepository = InjectionUtils.providePlaylistRepository(playlistDataSource)

        val artistDataSource = InjectionUtils.provideArtistDataSource(applicationContext)
        _artistRepository = InjectionUtils.provideArtistRepository(artistDataSource)
    }

    fun getRecentSongRepository(): RecentSongRepositoryImpl {
        return recentSongRepository
    }

    fun getPlaylistRepository(): PlaylistRepositoryImpl {
        return playlistRepository
    }
}