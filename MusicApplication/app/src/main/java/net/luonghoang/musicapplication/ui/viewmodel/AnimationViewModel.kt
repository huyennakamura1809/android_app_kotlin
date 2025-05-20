package net.braniumacademy.musicapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AnimationViewModel private constructor() : ViewModel() {
    private val _fraction = MutableLiveData<Float>()
    val fraction: LiveData<Float> get() = _fraction

    fun setFraction(fraction: Float) {
        _fraction.value = fraction
    }

    companion object {
        val instance = AnimationViewModel()
    }
}