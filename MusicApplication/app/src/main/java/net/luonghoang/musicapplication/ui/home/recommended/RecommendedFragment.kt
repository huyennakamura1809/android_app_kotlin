package net.braniumacademy.musicapplication.ui.home.recommended

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentRecommendedBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.detail.DetailFragment
import net.braniumacademy.musicapplication.ui.detail.DetailViewModel
import net.braniumacademy.musicapplication.ui.home.recommended.more.MoreRecommendedViewModel
import net.braniumacademy.musicapplication.ui.playing.MiniPlayerViewModel
import net.braniumacademy.musicapplication.ui.viewmodel.SharedViewModel
import net.braniumacademy.musicapplication.utils.MusicAppUtils

class RecommendedFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentRecommendedBinding
    private val recommendedViewModel: RecommendedViewModel by activityViewModels()
    private val moreRecommendedViewModel: MoreRecommendedViewModel by activityViewModels()
    private val miniPlayerViewModel: MiniPlayerViewModel by activityViewModels()
    private lateinit var adapter: SongAdapter
    private val detailViewModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecommendedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        adapter = SongAdapter(
            object : SongAdapter.OnSongClickListener {
                override fun onClick(song: Song, index: Int) {
                    val playlistName = MusicAppUtils.DefaultPlaylistName.RECOMMENDED.value
                    playSong(song, index, playlistName)
                }
            },
            object : SongAdapter.OnSongOptionMenuClickListener {
                override fun onClick(song: Song) {
                    showOptionMenu(song)
                }
            }
        )
        binding.includeSongList.rvSongList.adapter = adapter
        binding.btnMoreRecommended.setOnClickListener {
            navigateToDetailScreen()
        }
        binding.textTitleRecommended.setOnClickListener {
            navigateToDetailScreen()
        }
    }

    private fun navigateToDetailScreen() {
        val playlistName = MusicAppUtils.DefaultPlaylistName.RECOMMENDED.value
        val screenName = getString(R.string.title_recommended)
        detailViewModel.setScreenName(screenName)
        detailViewModel.setPlaylistName(playlistName)
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, DetailFragment::class.java, null)
            .addToBackStack(null)
            .commit()
    }

    private fun setupViewModel() {
        recommendedViewModel.songs.observe(viewLifecycleOwner) { songs ->
            detailViewModel.setSongs(songs)
            adapter.updateSongs(songs.subList(0, 16))
            moreRecommendedViewModel.setRecommendedSongs(songs)
            val playlistName = MusicAppUtils.DefaultPlaylistName.RECOMMENDED.value
            SharedViewModel.instance.setupPlaylist(songs, playlistName)
        }
    }

//    private fun navigateToMoreRecommended() {
//        requireActivity().supportFragmentManager
//            .beginTransaction()
//            .replace(
//                R.id.nav_host_fragment_activity_main,
//                MoreRecommendedFragment::class.java,
//                null
//            )
//            .addToBackStack(null)
//            .commit()
//    }
}