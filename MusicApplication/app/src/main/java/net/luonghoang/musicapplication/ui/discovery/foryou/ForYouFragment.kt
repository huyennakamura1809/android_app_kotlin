package net.braniumacademy.musicapplication.ui.discovery.foryou

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.braniumacademy.musicapplication.MusicApplication
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentForYouBinding
import net.braniumacademy.musicapplication.ui.detail.DetailFragment
import net.braniumacademy.musicapplication.ui.detail.DetailViewModel
import net.braniumacademy.musicapplication.ui.home.recommended.SongAdapter
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.FOR_YOU

class ForYouFragment : Fragment() {
    private lateinit var binding: FragmentForYouBinding
    private lateinit var songAdapter: SongAdapter
    private val forYouViewModel: ForYouViewModel by activityViewModels {
        val application = requireActivity().application as MusicApplication
        ForYouViewModel.Factory(application.songRepository)
    }
    private val detailViewModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForYouBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        songAdapter = SongAdapter(
            object : SongAdapter.OnSongClickListener {
                override fun onClick(song: Song, index: Int) {
                    // todo
                }
            },
            object : SongAdapter.OnSongOptionMenuClickListener {
                override fun onClick(song: Song) {
                    // todo
                }
            }
        )
        binding.includeForYouSong.rvSongList.adapter = songAdapter
        binding.btnMoreForYou.setOnClickListener {
            navigateToDetailScreen()
        }
        binding.textTitleForYou.setOnClickListener {
            navigateToDetailScreen()
        }
    }

    private fun navigateToDetailScreen() {
        val playlistName = FOR_YOU.value
        val screenName = getString(R.string.title_for_you)
        detailViewModel.setScreenName(screenName)
        detailViewModel.setPlaylistName(playlistName)
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, DetailFragment::class.java, null)
            .addToBackStack(null)
            .commit()
    }

    private fun observeData() {
        forYouViewModel.songs.observe(viewLifecycleOwner) { songs ->
            songAdapter.updateSongs(songs)
        }
        forYouViewModel.top40ForYouSongs.observe(viewLifecycleOwner) { songs ->
            detailViewModel.setSongs(songs)
        }
    }
}