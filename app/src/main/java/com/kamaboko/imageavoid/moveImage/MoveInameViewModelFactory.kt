package com.kamaboko.imageavoid.moveImage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MoveInameViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoveImageViewModel::class.java)) {
            return MoveImageViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}