package net.braniumacademy.musicapplication.ui.playing

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.bumptech.glide.Glide
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.song.Song
import net.braniumacademy.musicapplication.databinding.ActivityNowPlayingBinding
import net.braniumacademy.musicapplication.ui.viewmodel.AnimationViewModel
import net.braniumacademy.musicapplication.ui.viewmodel.MediaPlayerViewModel
import net.braniumacademy.musicapplication.ui.viewmodel.SharedViewModel

class NowPlayingActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityNowPlayingBinding
    private var mediaController: MediaController? = null
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var pressedAnimator: Animator
    private lateinit var seekBarHandler: Handler
    private lateinit var seekbarCallback: Runnable
    private lateinit var rotationAnimator: ObjectAnimator
    private val nowPlayingViewModel: NowPlayingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNowPlayingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupViewModel()
        setupMediaController()
        setupAnimator()
    }

    override fun onResume() {
        super.onResume()
        mediaController?.let {
            if (!it.isPlaying) {
                it.play()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.fade_in, R.anim.slide_down)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        seekBarHandler.removeCallbacks(seekbarCallback)
    }

    override fun onClick(v: View) {
        pressedAnimator.setTarget(v)
        pressedAnimator.start()
        when (v) {
            binding.btnPlayPauseNowPlaying -> setupPlayPauseAction()
            binding.btnShuffle -> setupShuffleAction()
            binding.btnSkipPrevNowPlaying -> setupSkipPrevious()
            binding.btnSkipNextNowPlaying -> setupSkipNext()
            binding.btnRepeat -> setupRepeatAction()
            binding.btnShareNowPlaying -> {}
            binding.btnFavoriteNowPlaying -> setupFavoriteAction()
            else -> {}
        }
    }

    private fun setupPlayPauseAction() {
        mediaController?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    private fun setupShuffleAction() {
        mediaController?.let {
            val isShuffle = it.shuffleModeEnabled
            it.shuffleModeEnabled = !isShuffle
            showShuffleState()
        }
    }

    private fun setupSkipPrevious() {
        mediaController?.let {
            if (it.hasPreviousMediaItem()) {
                it.seekToPreviousMediaItem()
                rotationAnimator.end()
            }
        }
    }

    private fun setupSkipNext() {
        mediaController?.let {
            if (it.hasNextMediaItem()) {
                it.seekToNextMediaItem()
                rotationAnimator.end()
            }
        }
    }

    private fun setupRepeatAction() {
        mediaController?.let {
            val repeatMode = it.repeatMode
            when (repeatMode) {
                Player.REPEAT_MODE_OFF -> it.repeatMode = Player.REPEAT_MODE_ONE
                Player.REPEAT_MODE_ONE -> it.repeatMode = Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> it.repeatMode = Player.REPEAT_MODE_OFF
            }
            showRepeatMode()
        }
    }

    private fun setupFavoriteAction() {
        val playingSong = sharedViewModel.playingSong.value
        playingSong?.let {
            val song = it.song
            if (song != null) {
                song.favorite = !song.favorite
                showFavoriteState(song)
                sharedViewModel.updateFavoriteStatus(song)
            }
        }
    }

    private fun setupView() {
        binding.btnPlayPauseNowPlaying.setOnClickListener(this)
        binding.btnShuffle.setOnClickListener(this)
        binding.btnSkipPrevNowPlaying.setOnClickListener(this)
        binding.btnSkipNextNowPlaying.setOnClickListener(this)
        binding.btnRepeat.setOnClickListener(this)
        binding.btnShareNowPlaying.setOnClickListener(this)
        binding.btnFavoriteNowPlaying.setOnClickListener(this)
        binding.toolbarNowPlaying.setNavigationOnClickListener {
            AnimationViewModel.instance.setFraction(rotationAnimator.animatedFraction)
            onBackPressedDispatcher.onBackPressed()
        }
        binding.seekBarNowPlaying.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaController?.seekTo(progress.toLong())
                }
                binding.textLabelCurrentDuration.text =
                    nowPlayingViewModel.getTimeLabel(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun setupViewModel() {
        sharedViewModel = SharedViewModel.instance
        nowPlayingViewModel.isPlaying.observe(this) { isPlaying ->
            val iconId = if (isPlaying) {
                R.drawable.ic_pause_circle
            } else {
                R.drawable.ic_play_circle
            }
            binding.btnPlayPauseNowPlaying.setImageResource(iconId)
            if (isPlaying) {
                if (rotationAnimator.isPaused) {
                    rotationAnimator.resume()
                } else {
                    rotationAnimator.start()
                }
            } else {
                rotationAnimator.pause()
            }
        }

        AnimationViewModel.instance.fraction.observe(this) { fraction ->
            rotationAnimator.setCurrentFraction(fraction)
        }
    }

    private fun setupMediaController() {
        MediaPlayerViewModel.instance.mediaController.observe(this) { controller ->
            mediaController = controller
            sharedViewModel.playingSong.observe(this) { playingSong ->
                mediaController?.let {
                    nowPlayingViewModel.setIsPlaying(it.isPlaying)
                }
                setupSeekBar()
                showSongInfo(playingSong.song)
            }
            setupMediaListener()
        }
    }

    private fun setupSeekBar() {
        seekBarHandler = Looper.myLooper()?.let { Handler(it) }!!
        seekbarCallback = object : Runnable {
            override fun run() {
                if (mediaController != null) {
                    val currentPosition = mediaController!!.currentPosition
                    binding.seekBarNowPlaying.progress = currentPosition.toInt()
                }
                seekBarHandler.postDelayed(this, 1000)
            }
        }
        seekBarHandler.post(seekbarCallback)
    }

    private fun showSongInfo(song: Song?) {
        if (song != null) {
            updateSeekBarMaxValue()
            updateDuration()
            binding.textAlbumNowPlaying.text = song.album
            binding.textSongTitleNowPlaying.text = song.title
            binding.textSongArtistNowPlaying.text = song.artist
            Glide.with(this)
                .load(song.image)
                .error(R.drawable.ic_album)
                .circleCrop()
                .into(binding.imageArtworkNowPlaying)
            showRepeatMode()
            showShuffleState()
            showFavoriteState(song)
        }
    }

    private fun updateSeekBarMaxValue() {
        val currentPos = mediaController?.currentPosition ?: 0
        binding.seekBarNowPlaying.progress = currentPos.toInt()
        binding.seekBarNowPlaying.max = nowPlayingViewModel.getDuration(mediaController)
    }

    private fun updateDuration() {
        val durationLabel = nowPlayingViewModel.getTimeLabel(mediaController?.duration ?: 0)
        binding.textTotalDuration.text = durationLabel
    }

    private fun showRepeatMode() {
        mediaController?.let {
            val iconId = when (it.repeatMode) {
                Player.REPEAT_MODE_OFF -> R.drawable.ic_repeat_off
                Player.REPEAT_MODE_ALL -> R.drawable.ic_repeat_all
                Player.REPEAT_MODE_ONE -> R.drawable.ic_repeat_one
                else -> R.drawable.ic_repeat_off
            }
            binding.btnRepeat.setImageResource(iconId)
        }
    }

    private fun showShuffleState() {
        mediaController?.let {
            val isShuffle = it.shuffleModeEnabled
            val iconId = if (isShuffle) {
                R.drawable.ic_shuffle_on
            } else {
                R.drawable.ic_shuffle
            }
            binding.btnShuffle.setImageResource(iconId)
        }
    }

    private fun showFavoriteState(song: Song) {
        val favoriteIcon = if (song.favorite) {
            R.drawable.ic_favorite_on
        } else {
            R.drawable.ic_favorite_off
        }
        binding.btnFavoriteNowPlaying.setImageResource(favoriteIcon)
    }

    private fun setupMediaListener() {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                nowPlayingViewModel.setIsPlaying(isPlaying)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (mediaController!!.isPlaying) {
                    updateSeekBarMaxValue()
                    updateDuration()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    updateSeekBarMaxValue()
                    updateDuration()
                }
            }
        }
        mediaController?.addListener(listener)
    }

    private fun setupAnimator() {
        pressedAnimator = AnimatorInflater.loadAnimator(this, R.animator.button_pressed)
        rotationAnimator = ObjectAnimator
            .ofFloat(binding.imageArtworkNowPlaying, "rotation", 0f, 360f)
        rotationAnimator.interpolator = LinearInterpolator()
        rotationAnimator.duration = 12000
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        rotationAnimator.repeatMode = ObjectAnimator.RESTART
    }
}