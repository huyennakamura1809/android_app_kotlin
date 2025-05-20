package net.braniumacademy.musicapplication.ui.discovery.artist.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import net.braniumacademy.musicapplication.data.repository.artist.ArtistRepositoryImpl

class MoreArtistViewModel(
    artistRepository: ArtistRepositoryImpl
) : ViewModel() {
    val artists = artistRepository.artists.asLiveData()

    class Factory(
        private val artistRepository: ArtistRepositoryImpl
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MoreArtistViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MoreArtistViewModel(artistRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}