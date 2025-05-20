package net.braniumacademy.musicapplication.ui.discovery.artist.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import net.braniumacademy.musicapplication.MusicApplication
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.artist.Artist
import net.braniumacademy.musicapplication.databinding.FragmentMoreArtistBinding
import net.braniumacademy.musicapplication.ui.discovery.artist.ArtistAdapter
import net.braniumacademy.musicapplication.ui.discovery.artist.detail.ArtistDetailFragment
import net.braniumacademy.musicapplication.ui.discovery.artist.detail.ArtistDetailViewModel

class MoreArtistFragment : Fragment() {
    private lateinit var binding: FragmentMoreArtistBinding
    private lateinit var adapter: ArtistAdapter
    private val moreArtistViewModel: MoreArtistViewModel by viewModels {
        val application = requireActivity().application as MusicApplication
        MoreArtistViewModel.Factory(application.artistRepository)
    }
    private val artistDetailViewModel: ArtistDetailViewModel by activityViewModels {
        val application = requireActivity().application as MusicApplication
        ArtistDetailViewModel.Factory(application.artistRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeData()
    }

    private fun setupView() {
        binding.toolbarMoreArtist.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        adapter = ArtistAdapter(
            object : ArtistAdapter.ArtistListener {
                override fun onClick(artist: Artist) {
                    artistDetailViewModel.setArtist(artist)
                    artistDetailViewModel.getArtistWithSongs(artist.id)
                    navigateToArtistDetail()
                }
            }
        )
        binding.includeArtist.rvArtist.adapter = adapter
    }

    private fun navigateToArtistDetail() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, ArtistDetailFragment::class.java, null)
            .addToBackStack(null)
            .commit()
    }

    private fun observeData() {
        moreArtistViewModel.artists.observe(viewLifecycleOwner) { artists ->
            adapter.updateArtists(artists)
        }
    }
}