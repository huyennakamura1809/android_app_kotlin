package net.braniumacademy.musicapplication.ui.home.recommended.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentMoreRecommendedBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.home.recommended.SongAdapter
import net.braniumacademy.musicapplication.utils.MusicAppUtils

class MoreRecommendedFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentMoreRecommendedBinding
    private lateinit var adapter: SongAdapter
    private val moreRecommendedViewModel: MoreRecommendedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreRecommendedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObserver()
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
        binding.includeMoreRecommended.rvSongList.adapter = adapter
        binding.toolbarMoreRecommended.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupObserver() {
        moreRecommendedViewModel.recommendedSongs.observe(viewLifecycleOwner) { songs ->
            adapter.updateSongs(songs)
        }
    }
}