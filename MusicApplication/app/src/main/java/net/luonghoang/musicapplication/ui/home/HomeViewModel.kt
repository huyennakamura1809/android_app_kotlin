package net.braniumacademy.musicapplication.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.album.Album
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.data.model.song.SongList
import net.braniumacademy.musicapplication.data.repository.AlbumRepositoryImpl
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import net.braniumacademy.musicapplication.data.source.Result

class HomeViewModel(
    private val songRepository: SongRepositoryImpl
) : ViewModel() {
    private val albumRepository = AlbumRepositoryImpl()
    private val _albums = MutableLiveData<List<Album>>()
    private val _songs = MutableLiveData<List<Song>>()
    private val _localSongs = MutableLiveData<List<Song>>()

    val albums: LiveData<List<Album>>
        get() = _albums
    val songs: LiveData<List<Song>>
        get() = _songs
    val localSongs: LiveData<List<Song>>
        get() = _localSongs

    init {
        loadSongs()
        loadAlbums()
        loadLocalSongs()
    }

    private fun loadAlbums() {
        viewModelScope.launch(Dispatchers.IO) {
            albumRepository.loadAlbums(object : ResultCallback<Result<List<Album>>> {
                override fun onResult(result: Result<List<Album>>) {
                    if (result is Result.Success) {
                        _albums.postValue(result.data)
                    } else {
                        _albums.postValue(emptyList())
                    }
                }
            })
        }
    }

    private fun loadLocalSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            val songList = songRepository.songs
            _localSongs.postValue(songList)
        }
    }

    private fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            songRepository.loadSongs(object : ResultCallback<Result<SongList>> {
                override fun onResult(result: Result<SongList>) {
                    if (result is Result.Success) {
                        _songs.postValue(result.data.songs)
                    } else {
                        _songs.postValue(emptyList())
                    }
                }
            })
        }
    }

    fun saveSongsToDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val songToSave = extractSongs()
            if (songToSave.isNotEmpty()) {
                val songArray = songToSave.toTypedArray()
                songRepository.insert(*songArray)
            }
        }
    }

    private fun extractSongs(): List<Song> {
        val results: MutableList<Song> = mutableListOf()
        val localSongs = localSongs.value
        if (localSongs == null) {
            _songs.value?.let { results.addAll(it) }
        } else if (_songs.value != null) {
            for (song in _songs.value!!) {
                if (!localSongs.contains(song)) {
                    results.add(song)
                }
            }
        }
        return results
    }

    class Factory(
        private val songRepository: SongRepositoryImpl
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(songRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}