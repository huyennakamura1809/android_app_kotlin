package net.braniumacademy.musicapplication.ui.home.album.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.braniumacademy.musicapplication.data.model.album.Album
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.data.model.song.Song

class DetailAlbumViewModel : ViewModel() {
    private val _album = MutableLiveData<Album>()
    private val _songs = MutableLiveData<List<Song>>()
    private val _playlist = MutableLiveData<Playlist>()
    val album: LiveData<Album>
        get() = _album
    val songs: LiveData<List<Song>>
        get() = _songs
    val playlist: LiveData<Playlist>
        get() = _playlist

    fun setAlbum(album: Album) {
        _album.value = album
    }

    fun extractSongs(album: Album, songs: List<Song>?) {
        songs?.let {
            val currentPlaylist = Playlist(name = album.name)
            currentPlaylist.id = -1
            val songsList = mutableListOf<Song>()
            for (songId in album.songs) {
                val songIndex = songs.indexOfFirst { it.id == songId }
                if (songIndex != -1) {
                    songsList.add(songs[songIndex])
                }
            }
            _songs.value = songsList
            currentPlaylist.updateSongList(songsList)
            _playlist.value = currentPlaylist
        }
    }
}