package net.braniumacademy.musicapplication.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import net.braniumacademy.musicapplication.MusicApplication
import net.braniumacademy.musicapplication.databinding.FragmentLibraryBinding
import net.braniumacademy.musicapplication.ui.library.favorite.FavoriteViewModel
import net.braniumacademy.musicapplication.ui.library.playlist.PlaylistViewModel
import net.braniumacademy.musicapplication.ui.library.recent.RecentViewModel
import net.braniumacademy.musicapplication.ui.viewmodel.SharedViewModel
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.FAVORITES
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.RECENT

class LibraryFragment : Fragment() {
    private lateinit var binding: FragmentLibraryBinding
    private val libraryViewModel: LibraryViewModel by viewModels {
        val application = requireActivity().application as MusicApplication
        LibraryViewModel.Factory(
            application.getRecentSongRepository(),
            application.songRepository,
            application.getPlaylistRepository()
        )
    }
    private val recentSongViewModel: RecentViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteViewModel by activityViewModels()
    private val playlistViewModel: PlaylistViewModel by activityViewModels {
        val application = requireActivity().application as MusicApplication
        PlaylistViewModel.Factory(application.getPlaylistRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        if (savedInstanceState != null) {
            val scrollPosition = savedInstanceState.getInt(SCROLL_POSITION)
            binding.scrollViewLibrary.post {
                binding.scrollViewLibrary.scrollTo(0, scrollPosition)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCROLL_POSITION, binding.scrollViewLibrary.scrollY)
    }

    private fun observeData() {
        val sharedViewModel = SharedViewModel.instance
        libraryViewModel.recentSongs.observe(viewLifecycleOwner) { recentSongs ->
            recentSongViewModel.setRecentSongs(recentSongs)
            sharedViewModel.setupPlaylist(recentSongs, RECENT.value)
        }
        libraryViewModel.favoriteSongs.observe(viewLifecycleOwner) { favoriteSongs ->
            favoriteViewModel.setSongs(favoriteSongs)
            sharedViewModel.setupPlaylist(favoriteSongs, FAVORITES.value)
        }
    }

    companion object {
        const val SCROLL_POSITION = "net.braniumacademy.musicapplication.ui.library.SCROLL_POSITION"
    }
}