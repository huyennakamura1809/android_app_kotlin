package net.braniumacademy.musicapplication.ui.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.braniumacademy.musicapplication.MusicApplication
import net.braniumacademy.musicapplication.databinding.FragmentDiscoveryBinding
import net.braniumacademy.musicapplication.ui.discovery.artist.ArtistViewModel
import net.braniumacademy.musicapplication.ui.discovery.foryou.ForYouViewModel
import net.braniumacademy.musicapplication.ui.discovery.mostheard.MostHeardViewModel

class DiscoveryFragment : Fragment() {
    private lateinit var binding: FragmentDiscoveryBinding
    private val artistViewModel: ArtistViewModel by activityViewModels()
    private val mostHeardViewModel: MostHeardViewModel by activityViewModels {
        val application = requireActivity().application as MusicApplication
        MostHeardViewModel.Factory(application.songRepository)
    }
    private val discoveryViewModel: DiscoveryViewModel by activityViewModels {
        val application = requireActivity().application as MusicApplication
        DiscoveryViewModel.Factory(application.artistRepository, application.songRepository)
    }
    private val forYouViewModel: ForYouViewModel by activityViewModels {
        val application = requireActivity().application as MusicApplication
        ForYouViewModel.Factory(application.songRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiscoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun observeData() {
        discoveryViewModel.artists.observe(viewLifecycleOwner) { artists ->
            discoveryViewModel.saveArtistToDB()
            discoveryViewModel.saveArtistSongCrossRef(artists)
        }
        discoveryViewModel.top15Artists.observe(viewLifecycleOwner) { artists ->
            artistViewModel.setArtists(artists)
        }
        discoveryViewModel.top15MostHeardSongs.observe(viewLifecycleOwner) { songs ->
            mostHeardViewModel.setSongs(songs)
        }
        discoveryViewModel.top15ForYouSongs.observe(viewLifecycleOwner) { songs ->
            forYouViewModel.setSongs(songs)
        }
    }
}