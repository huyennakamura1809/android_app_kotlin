package net.braniumacademy.musicapplication.ui

import android.content.Intent
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.ui.dialog.SongOptionMenuDialogFragment
import net.braniumacademy.musicapplication.ui.dialog.SongOptionMenuViewModel
import net.braniumacademy.musicapplication.ui.playing.NowPlayingActivity
import net.braniumacademy.musicapplication.ui.viewmodel.AnimationViewModel
import net.braniumacademy.musicapplication.ui.viewmodel.PermissionViewModel
import net.braniumacademy.musicapplication.ui.viewmodel.SharedViewModel

open class PlayerBaseFragment : Fragment() {
    protected fun playSong(song: Song, index: Int, playlistName: String) {
        val isPermissionGranted = PermissionViewModel.instance.permissionGranted.value
        if (isPermissionGranted != null && isPermissionGranted) {
            doNavigate(index, playlistName)
        } else if (!PermissionViewModel.isRegistered) {
            PermissionViewModel.instance
                .permissionGranted
                .observe(requireActivity()) {
                    if (it) {
                        doNavigate(index, playlistName)
                    }
                }
            PermissionViewModel.isRegistered = true
        }
    }

    private fun doNavigate(index: Int, playlistName: String) {
        val sharedViewModel = SharedViewModel.instance
        sharedViewModel.setCurrentPlaylist(playlistName)
        sharedViewModel.setIndexToPlay(index)
        Intent(requireContext(), NowPlayingActivity::class.java).apply {
            val options = ActivityOptionsCompat
                .makeCustomAnimation(requireContext(), R.anim.slide_up, R.anim.fade_out)
            startActivity(this, options.toBundle())
        }
    }

    protected fun showOptionMenu(song: Song) {
        val menuDialogFragment = SongOptionMenuDialogFragment.newInstance
        val menuDialogViewMode: SongOptionMenuViewModel by activityViewModels()
        menuDialogViewMode.setSong(song)
        menuDialogFragment.show(
            requireActivity().supportFragmentManager,
            SongOptionMenuDialogFragment.TAG
        )
    }
}