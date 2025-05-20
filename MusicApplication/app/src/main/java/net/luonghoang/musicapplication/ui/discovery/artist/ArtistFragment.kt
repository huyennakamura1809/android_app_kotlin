package net.braniumacademy.musicapplication.ui.discovery.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.braniumacademy.musicapplication.MusicApplication
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.databinding.FragmentArtistBinding
import net.braniumacademy.musicapplication.ui.discovery.artist.detail.ArtistDetailFragment
import net.braniumacademy.musicapplication.ui.discovery.artist.detail.ArtistDetailViewModel
import net.braniumacademy.musicapplication.ui.discovery.artist.more.MoreArtistFragment

class ArtistFragment : Fragment() {
    private lateinit var binding: FragmentArtistBinding
    private val artistViewModel: ArtistViewModel by activityViewModels()
    private val artistDetailViewModel: ArtistDetailViewModel by activityViewModels {
        val application = requireActivity().application as MusicApplication
        ArtistDetailViewModel.Factory(application.artistRepository)
    }
    private lateinit var adapter: ArtistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        adapter = ArtistAdapter(
            object : ArtistAdapter.ArtistListener {
                override fun onClick(artist: Artist) {
                    artistDetailViewModel.setArtist(artist)
                    artistDetailViewModel.getArtistWithSongs(artist.id)
                    navigateToArtistDetail()
                }
            }
        )
        binding.includeArtistList.rvArtist.adapter = adapter
        binding.btnMoreArtist.setOnClickListener {
            navigateToMoreArtist()
        }
        binding.textTitleArtist.setOnClickListener {
            navigateToMoreArtist()
        }
    }

    private fun navigateToArtistDetail() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, ArtistDetailFragment::class.java, null)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToMoreArtist() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, MoreArtistFragment::class.java, null)
            .addToBackStack(null)
            .commit()
    }

    private fun observeData() {
        artistViewModel.artists.observe(viewLifecycleOwner) { artists ->
            adapter.updateArtists(artists)
        }
    }
}