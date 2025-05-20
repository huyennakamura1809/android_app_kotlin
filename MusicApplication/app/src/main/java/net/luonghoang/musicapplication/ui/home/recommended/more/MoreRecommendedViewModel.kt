package net.braniumacademy.musicapplication.ui.home.recommended.more

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.braniumacademy.musicapplication.data.model.song.Song

class MoreRecommendedViewModel : ViewModel() {
    private val _recommendedSongs = MutableLiveData<List<Song>>()
    val recommendedSongs: LiveData<List<Song>>
        get() = _recommendedSongs

    fun setRecommendedSongs(songs: List<Song>) {
        _recommendedSongs.value = songs
    }
}