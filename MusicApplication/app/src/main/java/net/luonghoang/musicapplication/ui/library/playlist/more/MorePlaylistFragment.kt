package net.braniumacademy.musicapplication.ui.library.playlist.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.braniumacademy.musicapplication.MusicApplication
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.databinding.FragmentMorePlaylistBinding
import net.braniumacademy.musicapplication.ui.library.playlist.PlaylistAdapter
import net.braniumacademy.musicapplication.ui.library.playlist.PlaylistViewModel
import net.braniumacademy.musicapplication.ui.library.playlist.detail.PlaylistDetailFragment
import net.braniumacademy.musicapplication.ui.library.playlist.detail.PlaylistDetailViewModel
import net.braniumacademy.musicapplication.ui.viewmodel.SharedViewModel

class MorePlaylistFragment : Fragment() {
    private lateinit var binding: FragmentMorePlaylistBinding
    private lateinit var adapter: PlaylistAdapter
    private val morePlaylistViewModel: MorePlaylistViewModel by activityViewModels()
    private val playlistDetailViewModel: PlaylistDetailViewModel by activityViewModels()
    private val playlistViewModel: PlaylistViewModel by activityViewModels {
        val application = requireActivity().application as MusicApplication
        PlaylistViewModel.Factory(application.getPlaylistRepository())
    }
    private var isNavigateToPlaylistDetail = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMorePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        binding.toolbarMorePlaylist.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        adapter = PlaylistAdapter(
            object : PlaylistAdapter.OnPlaylistClickListener {
                override fun onPlaylistClick(playlist: Playlist) {
                    playlistViewModel.getPlaylistWithSongByPlaylistId(playlist.id)
                    isNavigateToPlaylistDetail = true
                }

                override fun onPlaylistMenuOptionClick(playlist: Playlist) {
                    // todo
                }
            }
        )
        binding.rvMorePlaylist.adapter = adapter
    }

    private fun observeData() {
        morePlaylistViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            adapter.updatePlaylists(playlists)
        }
        playlistViewModel.playlistWithSongs.observe(viewLifecycleOwner) { playlistWithSongs ->
            if (isNavigateToPlaylistDetail) {
                playlistWithSongs.playlist?.let {
                    it.updateSongList(playlistWithSongs.songs)
                    SharedViewModel.instance.addPlaylist(it)
                }
                playlistDetailViewModel.setPlaylistWithSongs(playlistWithSongs)
                navigateToPlaylistDetail()
                isNavigateToPlaylistDetail = false
            }
        }
    }

    private fun navigateToPlaylistDetail() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, PlaylistDetailFragment::class.java, null)
            .addToBackStack(null)
            .commit()
    }
}