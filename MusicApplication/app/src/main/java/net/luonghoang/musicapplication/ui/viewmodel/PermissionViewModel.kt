package net.braniumacademy.musicapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionViewModel private constructor() : ViewModel() {
    private val _permissionAsked = MutableLiveData<Boolean>()
    private val _permissionGranted = MutableLiveData<Boolean>()

    val permissionAsked: LiveData<Boolean> = _permissionAsked
    val permissionGranted: LiveData<Boolean> = _permissionGranted


    fun askPermission() {
        _permissionAsked.value = true
    }

    fun grantPermission() {
        _permissionGranted.value = true
    }

    companion object {
        var isRegistered: Boolean = false
        val instance = PermissionViewModel()
    }
}