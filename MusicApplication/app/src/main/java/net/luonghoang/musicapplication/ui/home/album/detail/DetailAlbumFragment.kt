package net.braniumacademy.musicapplication.ui.home.album.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentDetailAlbumBinding
import net.braniumacademy.musicapplication.ui.PlayerBaseFragment
import net.braniumacademy.musicapplication.ui.home.recommended.SongAdapter
import net.braniumacademy.musicapplication.ui.viewmodel.SharedViewModel

class DetailAlbumFragment : PlayerBaseFragment() {
    private lateinit var binding: FragmentDetailAlbumBinding
    private lateinit var adapter: SongAdapter
    private val detailAlbumViewModel: DetailAlbumViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupViewModel()
    }

    private fun setupView() {
        binding.includeAlbumDetail.toolbarPlaylistDetail.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        adapter = SongAdapter(
            object : SongAdapter.OnSongClickListener {
                override fun onClick(song: Song, index: Int) {
                    detailAlbumViewModel.playlist.observe(viewLifecycleOwner) { playlist ->
                        SharedViewModel.instance.addPlaylist(playlist)
                        playSong(song, index, playlist.name)
                    }
                }
            },
            object : SongAdapter.OnSongOptionMenuClickListener {
                override fun onClick(song: Song) {
                    showOptionMenu(song)
                }
            }
        )
        binding.includeAlbumDetail.includeSongList.rvSongList.adapter = adapter
    }

    private fun setupViewModel() {
        detailAlbumViewModel.album.observe(viewLifecycleOwner) { album ->
            binding.includeAlbumDetail.textPlaylistDetailTitle.text = album.name
            val text = getString(R.string.text_playlist_num_of_song, album.size)
            binding.includeAlbumDetail.textPlaylistDetailNumOfSong.text = text
            Glide.with(binding.root)
                .load(album.artwork)
                .error(R.drawable.ic_album)
                .into(binding.includeAlbumDetail.imagePlaylistArtwork)
        }
        detailAlbumViewModel.songs.observe(viewLifecycleOwner) { songs ->
            adapter.updateSongs(songs)
        }
    }
}