package net.braniumacademy.musicapplication.ui.discovery.artist.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import net.braniumacademy.musicapplication.MusicApplication
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.FragmentArtistDetailBinding
import net.braniumacademy.musicapplication.ui.home.recommended.SongAdapter

class ArtistDetailFragment : Fragment() {
    private lateinit var binding: FragmentArtistDetailBinding
    private lateinit var adapter: SongAdapter
    private val artistDetailViewModel: ArtistDetailViewModel by activityViewModels {
        val application = requireActivity().application as MusicApplication
        ArtistDetailViewModel.Factory(application.artistRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArtistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        binding.toolbarArtistDetail.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        adapter = SongAdapter(
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
        binding.includeDetailArtistSongList.rvSongList.adapter = adapter
    }

    private fun observeData() {
        artistDetailViewModel.artistWithSongs.observe(viewLifecycleOwner) { artistWithSongs ->
            adapter.updateSongs(artistWithSongs.songs)
        }
        artistDetailViewModel.artist.observe(viewLifecycleOwner) { artist ->
            showArtistInfo(artist)
        }
    }

    private fun showArtistInfo(artist: Artist) {
        binding.textDetailArtistName.text = getString(R.string.text_artist_name, artist.name)
        binding.textDetailInterested.text =
            getString(R.string.text_number_subscriber, artist.interested)
        val interested = if (artist.isCareAbout) "YES" else "NO"
        binding.textArtistDetailYourInterest.text = getString(R.string.text_artist_name, interested)
        Glide.with(binding.root)
            .load(artist.avatar)
            .error(R.drawable.ic_artist)
            .circleCrop()
            .into(binding.imageArtistDetailAvatar)
    }
}