package net.braniumacademy.musicapplication.ui.discovery.mostheard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.braniumacademy.musicapplication.MusicApplication
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentMostHeardBinding
import net.braniumacademy.musicapplication.ui.detail.DetailFragment
import net.braniumacademy.musicapplication.ui.detail.DetailViewModel
import net.braniumacademy.musicapplication.ui.home.recommended.SongAdapter
import net.braniumacademy.musicapplication.utils.MusicAppUtils.DefaultPlaylistName.MOST_HEARD

class MostHeardFragment : Fragment() {
    private lateinit var binding: FragmentMostHeardBinding
    private lateinit var songAdapter: SongAdapter
    private val mostHeardViewModel: MostHeardViewModel by activityViewModels {
        val application = requireActivity().application as MusicApplication
        MostHeardViewModel.Factory(application.songRepository)
    }
    private val detailViewModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMostHeardBinding.inflate(inflater, container, false)
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
        binding.includeMostHeardSong.rvSongList.adapter = songAdapter
        binding.btnMoreMostHeard.setOnClickListener {
            navigateToDetailScreen()
        }
        binding.textTitleMostHeard.setOnClickListener {
            navigateToDetailScreen()
        }
    }

    private fun navigateToDetailScreen() {
        val playlistName = MOST_HEARD.value
        val screenName = getString(R.string.title_most_heard)
        detailViewModel.setScreenName(screenName)
        detailViewModel.setPlaylistName(playlistName)
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, DetailFragment::class.java, null)
            .addToBackStack(null)
            .commit()
    }

    private fun observeData() {
        mostHeardViewModel.songs.observe(viewLifecycleOwner) { songs ->
            songAdapter.updateSongs(songs)
        }
        mostHeardViewModel.top40MostHeardSongs.observe(viewLifecycleOwner) { songs ->
            detailViewModel.setSongs(songs)
        }
    }
}