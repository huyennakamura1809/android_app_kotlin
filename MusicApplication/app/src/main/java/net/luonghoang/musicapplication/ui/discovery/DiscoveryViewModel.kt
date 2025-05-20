package net.braniumacademy.musicapplication.ui.discovery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.braniumacademy.musicapplication.ResultCallback
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.model.artist.ArtistList
import net.braniumacademy.musicapplication.data.model.artist.ArtistSongCrossRef
import net.braniumacademy.musicapplication.data.repository.artist.ArtistRepositoryImpl
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import net.braniumacademy.musicapplication.data.source.Result

class DiscoveryViewModel(
    private val artistRepository: ArtistRepositoryImpl,
    private val songRepository: SongRepositoryImpl
) : ViewModel() {
    private val _artists = MutableLiveData<List<Artist>>()
    val artists: LiveData<List<Artist>>
        get() = _artists

    val localArtists = artistRepository.artists.asLiveData()
    val top15Artists = artistRepository.top15Artists.asLiveData()
    val top15MostHeardSongs = songRepository.top15MostHeardSongs.asLiveData()
    val top15ForYouSongs = songRepository.top15ForYouSongs.asLiveData()

    init {
        loadArtists()
    }

    private fun loadArtists() {
        viewModelScope.launch(Dispatchers.IO) {
            artistRepository.loadArtists(object : ResultCallback<Result<ArtistList>> {
                override fun onResult(result: Result<ArtistList>) {
                    if (result is Result.Success) {
                        _artists.postValue(result.data.artists)
                    } else {
                        _artists.postValue(emptyList())
                    }
                }
            })
        }
    }

    fun saveArtistToDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val artists = extractArtistsNotInDB()
            val artistToInsert = artists.toTypedArray()
            artistRepository.insert(*artistToInsert)
        }
    }

    private fun extractArtistsNotInDB(): List<Artist> {
        val results = mutableListOf<Artist>()
        val localArtistList = localArtists.value ?: emptyList()
        val remoteArtistList = artists.value ?: emptyList()
        if (remoteArtistList.isNotEmpty()) {
            if (localArtistList.isNotEmpty()) {
                results.addAll(remoteArtistList)
            } else {
                for (artist in remoteArtistList) {
                    if (!localArtistList.contains(artist)) {
                        results.add(artist)
                    }
                }
            }
        }
        return results
    }

    fun saveArtistSongCrossRef(artists: List<Artist>) {
        viewModelScope.launch(Dispatchers.IO) {
            val localSongs = songRepository.songs
            val crossRefs = mutableListOf<ArtistSongCrossRef>()
            if (artists.isNotEmpty()) {
                for (artist in artists) {
                    for (song in localSongs) {
                        val key = ".*" + artist.name.lowercase() + ".*"
                        if (song.artist.lowercase().matches(key.toRegex())) {
                            crossRefs.add(ArtistSongCrossRef(song.id, artist.id))
                        }
                    }
                }
            }
            val crossRefToInsert = crossRefs.toTypedArray()
            artistRepository.insertArtistSongCrossRef(*crossRefToInsert)
        }
    }

    class Factory(
        private val artistRepository: ArtistRepositoryImpl,
        private val songRepository: SongRepositoryImpl
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DiscoveryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DiscoveryViewModel(artistRepository, songRepository) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}