package net.braniumacademy.musicapplication.ui.library.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.braniumacademy.musicapplication.MusicApplication
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.playlist.Playlist
import net.braniumacademy.musicapplication.databinding.FragmentPlaylistBinding
import net.braniumacademy.musicapplication.ui.library.playlist.creation.DialogPlaylistCreationFragment
import net.braniumacademy.musicapplication.ui.library.playlist.detail.PlaylistDetailFragment
import net.braniumacademy.musicapplication.ui.library.playlist.detail.PlaylistDetailViewModel
import net.braniumacademy.musicapplication.ui.library.playlist.more.MorePlaylistFragment
import net.braniumacademy.musicapplication.ui.library.playlist.more.MorePlaylistViewModel
import net.braniumacademy.musicapplication.ui.viewmodel.SharedViewModel

class PlaylistFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var adapter: PlaylistAdapter
    private val morePlaylistViewModel: MorePlaylistViewModel by activityViewModels()
    private val playlistDetailViewModel: PlaylistDetailViewModel by activityViewModels()
    private val playlistViewModel: PlaylistViewModel by activityViewModels {
        val application = requireActivity().application as MusicApplication
        PlaylistViewModel.Factory(application.getPlaylistRepository())
    }
    private var shouldNavigateToDetail = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        adapter = PlaylistAdapter(
            object : PlaylistAdapter.OnPlaylistClickListener {
                override fun onPlaylistClick(playlist: Playlist) {
                    SharedViewModel.instance.addPlaylist(playlist)
                    playlistViewModel.getPlaylistWithSongByPlaylistId(playlist.id)
                    shouldNavigateToDetail = true
                }

                override fun onPlaylistMenuOptionClick(playlist: Playlist) {
                    // todo
                }
            }
        )
        binding.rvPlaylist.adapter = adapter
        binding.includeButtonCreatePlaylist.btnItemCreatePlaylist.setOnClickListener {
            showDialogToCreatePlaylist()
        }
        binding.includeButtonCreatePlaylist.textItemCreatePlaylist.setOnClickListener {
            showDialogToCreatePlaylist()
        }
        binding.btnMorePlaylist.setOnClickListener {
            navigateToMorePlaylist()
        }
        binding.textTitlePlaylist.setOnClickListener {
            navigateToMorePlaylist()
        }
    }

    private fun navigateToPlaylistDetail() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, PlaylistDetailFragment::class.java, null)
            .addToBackStack(null)
            .commit()
    }

    private fun showDialogToCreatePlaylist() {
        val listener = object : DialogPlaylistCreationFragment.OnClickListener {
            override fun onClick(playlistName: String) {
                playlistViewModel.createNewPlaylist(playlistName)
            }
        }
        val textChangeListener = object : DialogPlaylistCreationFragment.OnTextChangeListener {
            override fun onTextChange(playlistName: String) {
                playlistViewModel.findPlaylistByName(playlistName)
            }
        }
        val dialog = DialogPlaylistCreationFragment(listener, textChangeListener)
        val tag = DialogPlaylistCreationFragment.TAG
        dialog.show(requireActivity().supportFragmentManager, tag)
    }

    private fun navigateToMorePlaylist() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, MorePlaylistFragment::class.java, null)
            .addToBackStack(null)
            .commit()
    }

    private fun observeData() {
        playlistViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            adapter.updatePlaylists(playlists)
        }
        playlistViewModel.playlists.observe(viewLifecycleOwner) {
            morePlaylistViewModel.setPlaylists(it)
        }
        playlistViewModel.playlistWithSongs.observe(viewLifecycleOwner) { playlistWithSongs ->
            if (shouldNavigateToDetail) {
                playlistWithSongs.playlist?.let {
                    it.updateSongList(playlistWithSongs.songs)
                    SharedViewModel.instance.addPlaylist(it)
                }
                playlistDetailViewModel.setPlaylistWithSongs(playlistWithSongs)
                navigateToPlaylistDetail()
                shouldNavigateToDetail = false
            }
        }
    }
}